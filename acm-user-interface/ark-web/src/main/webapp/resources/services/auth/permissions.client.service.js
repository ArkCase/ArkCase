'use strict';

/**
 * @ngdoc service
 * @name services.service:PermissionsService
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/auth/permissions.client.service.js services/auth/permissions.client.service.js}
 *
 * The Permissions service. Performs checking user permissions for action depends on user roles, and objectProperties, like orderInfo, queueInfo
 */
angular.module('services').factory('PermissionsService', ['$q', '$http', '$log','$interpolate',  'Authentication',
    function ($q, $http, $log, $interpolate, Authentication) {
        // Iniital rules loading
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
                    var rulesPromise = queryRules();
                    var userProfilePromise = Authentication.queryUserInfo();

                    $q.all([rules, userProfilePromise])
                        .then(
                            function success(result) {
                                rules = result[0];
                                userProfile = result[1];
                                var permissionResult = processAction(actionName, objectProperties)
                                deferred.resolve(permissionResult);
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
                            return isEnabled
                        });
                    }

                    // Check ANY authorities
                    if (isEnabled && _.isArray(userProfile.authorities) && action.userRolesAny) {
                        var anyEnabled = false;
                        _.forEach(action.userRolesAny, function (role) {
                            var processedRole = processRole(role, objectProperties);
                            anyEnabled = anyEnabled || (_.indexOf(userProfile.authorities, processedRole) != -1);
                        });
                        isEnabled = anyEnabled;
                    }

                    // Check objectProperties
                    if (isEnabled && action.objectProperties) {
                        _.forEach(action.objectProperties, function (value, key) {
                            isEnabled = isEnabled && (_.get(objectProperties, key) === value );
                            // exit from loop if properties are not equal
                            return isEnabled;
                        });
                    }
                    // Return if action object passed
                    if (isEnabled) {
                        return false;
                    }
                });
            } else {
                $log.error('Action ' + actionName + ' was not found in rules list');
            }
            return isEnabled;
        }

        /**
         * Load permissions rules from server
         *
         * @returns {HttpPromise} Future rules object
         */
        function queryRules() {
            return $http({
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/dataaccess/rules',
                cache: true
            });
        }

        /**
         * Inerpolate ROLE name if required
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