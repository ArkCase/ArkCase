'use strict';

/**
 * @ngdoc service
 * @name services.service:PermissionsService
 *
 * @description
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/auth/permissions.client.service.js services/auth/permissions.client.service.js}
 *
 * The Permissions service. Performs checking user permissions for action depends on user roles, and objectProperties, like orderInfo, queueInfo
 */
angular.module('services').factory('PermissionsService', [ '$q', '$http', '$log', '$interpolate', 'Authentication', 'Object.ModelService', 'LookupService', 'UtilService', function($q, $http, $log, $interpolate, Authentication, ObjectModelService, LookupService, Util) {
    // Initial rules loading
    var rules = queryRules();
    var userProfile = Authentication.queryUserInfo();
    var parentPermissions = [];
    LookupService.getConfig('dacService').then(function(data) {
        parentPermissions = [ {
            expr: "(" + data["dac.fallbackExpression.getObject"] + ").*",
            parentActionName: "getObject"
        }, {
            expr: "(" + data["dac.fallbackExpression.editObject"] + ").*",
            parentActionName: "editObject"
        }, {
            expr: "(" + data["dac.fallbackExpression.insertObject"] + ").*",
            parentActionName: "insertObject"
        }, {
            expr: "(" + data["dac.fallbackExpression.deleteObject"] + ").*",
            parentActionName: "deleteObject"
        } ];
    });

    return {
        /**
         * @ngdoc method
         * @name getActionPermission
         * @methodOf services.service:PermissionsService
         *
         * @param {String} actionName Name of action, for example 'printOrderUI'
         * @param {Object} objectProperties Object representing current state of application, like orderInfo, queueInfo
         * @param {Object} opts other info to be passed to permission delegate e.g, objectType, objectSubType
         *
         * @returns {Promise} Future result of permission : true (enabled) or false (disabled)
         * @description
         * Retrieves a new acm authentication ticket for the currently logged in user
         */
        getActionPermission: function(actionName, objectProperties, opts) {
            if (!actionName) {
                $log.error('Permission Action name is undefined');
                return $q.resolve(null);
            }

            if (rules && rules.data && rules.data.accessControlRuleList && userProfile && userProfile.$resolved) {
                return $q.resolve(processAction(actionName, objectProperties, opts));
            } else {
                var deferred = $q.defer();
                var userProfilePromise = Authentication.queryUserInfo();

                $q.all([ rules, userProfilePromise ]).then(function success(result) {
                    rules = result[0];
                    userProfile = result[1];
                    var permissionResult = processAction(actionName, objectProperties, opts);
                    deferred.resolve(permissionResult);
                }, function error() {
                    deferred.reject();
                });
                return deferred.promise;
            }
        },

        /**
         * @ngdoc method
         * @name getActionsByRoles
         * @methodOf services.service:PermissionsService
         *
         * @param {String} actionName Name of action, for example 'printOrderUI'
         * @param {Array} roles Roles that are to used for validation
         *
         * @returns {Promise} Action objects
         * @description
         * Retrieves available actions for the currently logged in user
         */
        getActionsByRoles: function(actionName, roles) {
            if (!roles) {
                $log.error('Permission roles are undefined');
                return $q.resolve(null);
            }

            if (rules && rules.data && rules.data.accessControlRuleList && userProfile && userProfile.$resolved) {
                return $q.resolve(processActionsByRoles(actionName, roles));
            } else {
                var deferred = $q.defer();
                var userProfilePromise = Authentication.queryUserInfo();

                $q.all([ rules, userProfilePromise ]).then(function success(result) {
                    rules = result[0];
                    userProfile = result[1];
                    var actionsList = (processActionsByRoles(actionName, roles));
                    deferred.resolve(actionsList);
                }, function error() {
                    deferred.reject();
                });
                return deferred.promise;
            }
        }
    };

    function findParentPermission(rules, actionName, objectType) {
        for ( var index in parentPermissions) {
            if (actionName.toLowerCase().match(parentPermissions[index].expr)) {
                return _.filter(rules.data.accessControlRuleList, {
                    actionName: parentPermissions[index].parentActionName,
                    objectType: objectType
                });
            }
        }
        return [];
    }

    /**
     *
     * @param {String }actionName
     * @param {Object} objectProperties
     * @param {Obejct} opts Other info to be passed to permission delegate e.g, objectType, objectSubType
     * @returns {Boolean} true if action is enabled, or false if action is disabled
     */
    function processAction(actionName, objectProperties, opts) {
        var isEnabled = true;
        if (opts && opts.objectType) {
            //check if can find permission
            var actions = _.filter(rules.data.accessControlRuleList, {
                actionName: actionName,
                objectType: opts.objectType
            });
            //if not found check for parent fallback permission
            if (actions.length <= 0) {
                actions = findParentPermission(rules, actionName, opts.objectType);
            }
        } else {
            var actions = _.filter(rules.data.accessControlRuleList, {
                actionName: actionName
            });
        }
        // If actions found
        if (actions.length > 0) {
            // Process all found actions objects
            _.forEach(actions, function(action) {

                isEnabled = true;

                // Check ALL authorities
                if (isEnabled && _.isArray(userProfile.authorities) && action.userRolesAll) {
                    _.forEach(action.userRolesAll, function(role) {
                        isEnabled = hasAuthorityRole(userProfile.authorities, role, objectProperties);
                        return isEnabled;
                    });
                }

                // Check ANY authorities
                if (isEnabled && _.isArray(userProfile.authorities) && action.userRolesAny) {
                    if (action.userRolesAny.length > 0) {
                        var anyEnabled = false;
                        _.forEach(action.userRolesAny, function(role) {
                            anyEnabled = anyEnabled || hasAuthorityRole(userProfile.authorities, role, objectProperties);
                        });
                        isEnabled = anyEnabled;
                    }
                }

                // Check objectProperties
                if (isEnabled && action.objectProperties) {
                    _.forEach(action.objectProperties, function(value, key) {
                        isEnabled = isEnabled && ((_.indexOf(value, _.get(objectProperties, key)) != -1) || (_.get(objectProperties, key) === value));
                        // exit from loop if properties are not equal
                        return isEnabled;
                    });
                }

                // Check userIsParticipantTypeAny... This code checks every partipant of the given type.
                // If the object has four 'co-owner' participants, this code will check all of them, to see if
                // the user matches any of them.
                if (isEnabled && action.userIsParticipantTypeAny && action.userIsParticipantTypeAny.length > 0) {
                    var isUserParticipant = false;
                    _.forEach(action.userIsParticipantTypeAny, function(value) {
                        _.forEach(Util.goodMapValue(objectProperties, "participants", []), function(prt) {
                            if ( prt.participantType === value ) {
                                isUserParticipant = (prt.participantLdapId  == userProfile.userId) || (_.includes(userProfile.authorities, prt.participantLdapId));
                                if (isUserParticipant) {
                                    return false;
                                }
                            }
                        });
                    });
                    isEnabled = isEnabled && isUserParticipant;
                }
                // Return if action object passed
                if (isEnabled) {
                    return false;
                }
            });
        } else {
            $log.warn('Action ' + actionName + ' was not found in rules list');
        }
        return isEnabled;
    }

    /**
     *
     * @param {String }actionName
     * @param {Array} roles
     * @returns {Array} actionsList
     */
    function processActionsByRoles(actionName, roles) {
        var actionsList = [];
        var actions = [];
        if (actionName) {
            actions = _.filter(rules.data.accessControlRuleList, {
                actionName: actionName
            });
        } else {
            if (rules.data && rules.data.accessControlRuleList) {
                actions = rules.data.accessControlRuleList;
            }
        }

        // If actions found
        if (actions.length > 0) {
            // Process all found actions objects
            _.forEach(actions, function(action) {

                // Check ALL authorities
                if (action.userRolesAll) {
                    _.forEach(action.userRolesAll, function(role) {
                        if (hasAuthorityRole(roles, role)) {
                            if (_.indexOf(actionsList, action) == -1) {
                                actionsList.push(action);
                            }
                        }
                    });
                }

                // Check ANY authorities
                if (action.userRolesAny) {
                    _.forEach(action.userRolesAny, function(role) {
                        if (hasAuthorityRole(roles, role)) {
                            if (_.indexOf(actionsList, action) == -1) {
                                actionsList.push(action);
                            }
                        }
                    });
                }
            });
        } else {
            $log.warn('Action ' + actionName + ' was not found in rules list');
        }
        return actionsList;
    }

    /**
     * Load permissions rules from server
     *
     * @returns {HttpPromise} Future rules object
     */
    function queryRules() {
        return $http({
            method: 'GET',
            url: 'api/v1/service/dataaccess/rules',
            cache: true
        });
    }

    /**
     * Interpolate ROLE name if required
     * @param role
     * @param objectProperties
     * @returns {*}
     */
    function processRole(role, objectProperties) {
        var exp = $interpolate(role);
        return exp(objectProperties);
    }

    /**
     * Check if the required role is in user authority roles.
     * If this role contains wildcard "@*", check if the string before "@" matches
     * some of the user authority roles without domain.
     * eg. role: ACM_ADMIN@*, userAuthorities: ACM_ADMIN@X.COM
     * ACM_ADMIN matches ACM_ADMIN without domain
     * @param userAuthorities array of user roles
     * @param role required role
     * @param objectProperties
     * @returns boolean  true if match found, otherwise false
     */
    function hasAuthorityRole(userAuthorities, role, objectProperties) {
        var processedRole = objectProperties ? processRole(role, objectProperties) : role;
        var authorities = angular.copy(userAuthorities);
        if (_.endsWith(processedRole, '@*')) {
            processedRole = processedRole.substring(0, processedRole.lastIndexOf('@*'));
            authorities = _.map(authorities, function(authority) {
                var authIndexOfDomain = authority.lastIndexOf('@');
                if (authIndexOfDomain === -1) {
                    return authority;
                }
                return authority.substring(0, authIndexOfDomain);
            });
        }
        return _.indexOf(authorities, processedRole) !== -1;
    }

} ]);
