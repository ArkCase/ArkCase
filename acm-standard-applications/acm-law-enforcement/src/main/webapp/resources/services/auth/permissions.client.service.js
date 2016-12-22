'use strict';

/**
 * @ngdoc service
 * @name services.service:PermissionsService
 *
 * @description
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/auth/permissions.client.service.js services/auth/permissions.client.service.js}
 *
 * The Permissions service. Performs checking user permissions for action depends on user roles, and objectProperties, like orderInfo, queueInfo
 */
angular.module('services').factory('PermissionsService', ['$q', '$http', '$log', '$interpolate', 'Authentication', 'Object.ModelService',
    function ($q, $http, $log, $interpolate, Authentication, ObjectModelService) {
        // Initial rules loading
        var rules = queryRules();
        var userProfile = Authentication.queryUserInfo();

        return {
            /**
             * @ngdoc method
             * @name getActionPermission
             * @methodOf services.service:PermissionsService
             *
             * @param {String} actionName Name of action, for example 'printOrderUI'
             * @param {Object} objectProperties Object representing current state of application, like orderInfo, queueInfo
             *
             * @returns {Promise} Future result of permission : true (enabled) or false (disabled)
             * @description
             * Retrieves a new acm authentication ticket for the currently logged in user
             */
            getActionPermission: function (actionName, objectProperties) {
                if (!actionName) {
                    $log.error('Permission Action name is undefined');
                    return $q.resolve(null);
                }

                if (rules && rules.data && rules.data.accessControlRuleList && userProfile && userProfile.$resolved) {
                    return $q.resolve(processAction(actionName, objectProperties));
                } else {
                    var deferred = $q.defer();
                    var userProfilePromise = Authentication.queryUserInfo();

                    $q.all([rules, userProfilePromise])
                        .then(
                            function success(result) {
                                rules = result[0];
                                userProfile = result[1];
                                var permissionResult = processAction(actionName, objectProperties);
                                deferred.resolve(permissionResult);
                            },
                            function error() {
                                deferred.reject();
                            }
                        );
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
            getActionsByRoles: function (actionName, roles) {
                if (!roles) {
                    $log.error('Permission roles are undefined');
                    return $q.resolve(null);
                }

                if (rules && rules.data && rules.data.accessControlRuleList && userProfile && userProfile.$resolved) {
                    return $q.resolve(processActionsByRoles(actionName, roles));
                } else {
                    var deferred = $q.defer();
                    var userProfilePromise = Authentication.queryUserInfo();

                    $q.all([rules, userProfilePromise])
                        .then(
                            function success(result) {
                                rules = result[0];
                                userProfile = result[1];
                                var actionsList = (processActionsByRoles(actionName, roles));
                                deferred.resolve(actionsList);
                            },
                            function error() {
                                deferred.reject();
                            }
                        );
                    return deferred.promise;
                }
            }
        };


        /**
         *
         * @param {String }actionName
         * @param {Object} objectProperties
         * @returns {Boolean} true if action is enabled, or false if action is disabled
         */
        function processAction(actionName, objectProperties) {
            var isEnabled = true;
            var actions = _.filter(rules.data.accessControlRuleList, {actionName: actionName});
            // If actions found
            if (actions.length > 0) {
                // Process all found actions objects
                _.forEach(actions, function (action) {

                    isEnabled = true;

                    // Check ALL authorities
                    if (isEnabled && _.isArray(userProfile.authorities) && action.userRolesAll) {
                        _.forEach(action.userRolesAll, function (role) {
                            var processedRole = processRole(role, objectProperties);
                            isEnabled = (_.indexOf(userProfile.authorities, processedRole) != -1);
                            return isEnabled;
                        });
                    }

                    // Check ANY authorities
                    if (isEnabled && _.isArray(userProfile.authorities) && action.userRolesAny) {
                        if (action.userRolesAny.length > 0) {
                            var anyEnabled = false;
                            _.forEach(action.userRolesAny, function (role) {
                                var processedRole = processRole(role, objectProperties);
                                anyEnabled = anyEnabled || (_.indexOf(userProfile.authorities, processedRole) != -1);
                            });
                            isEnabled = anyEnabled;
                        }
                    }

                    // Check objectProperties
                    if (isEnabled && action.objectProperties) {
                        _.forEach(action.objectProperties, function (value, key) {
                            isEnabled = isEnabled && ((_.indexOf(value, _.get(objectProperties, key)) != -1) || (_.get(objectProperties, key) === value));
                            // exit from loop if properties are not equal
                            return isEnabled;
                        });
                    }

                    // Check userIsParticipantTypeAny
                    if (isEnabled && action.userIsParticipantTypeAny
                        && action.userIsParticipantTypeAny.length > 0) {
                        var isUserParticipant = false;
                        _.forEach(action.userIsParticipantTypeAny, function (value) {
                            var participant = ObjectModelService.getParticipantByType(objectProperties, value);
                            if (participant) {
                                isUserParticipant = (participant == userProfile.userId) || (_.includes(userProfile.authorities, participant));
                                if (isUserParticipant) {
                                    // user found in participant's types
                                    return false;
                                }
                            }
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
                actions = _.filter(rules.data.accessControlRuleList, {actionName: actionName});
            } else {
                if (rules.data && rules.data.accessControlRuleList) {
                    actions = rules.data.accessControlRuleList;
                }
            }

            // If actions found
            if (actions.length > 0) {
                // Process all found actions objects
                _.forEach(actions, function (action) {

                    // Check ALL authorities
                    if (action.userRolesAll) {
                        _.forEach(action.userRolesAll, function (role) {
                            if (_.indexOf(roles, role) != -1) {
                                if (_.indexOf(actionsList, action) == -1) {
                                    actionsList.push(action);
                                }
                            }
                        });
                    }

                    // Check ANY authorities
                    if (action.userRolesAny) {
                        _.forEach(action.userRolesAny, function (role) {
                            if (_.indexOf(roles, role) != -1) {
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
    }
]);