'use strict';


angular.module('services').factory('PermissionsService', ['$q', '$http', '$log', 'Authentication',
    function ($q, $http, $log, Authentication) {
        // Iniital rules loading
        var rules = queryRules();
        var userInfo = Authentication.queryUserInfo();

        return {
            getActionPermission: function (actionName, objectProperties) {
                if (!actionName) {
                    $log.error('Permission Action name is undefined');
                    return $q.resolve(null);
                }

                if (rules && rules.accessControlRuleList && userInfo && userInfo.$resolved) {
                    return $q.resolve(processAction(actionName, objectProperties));
                } else {
                    var deferred = $q.defer();
                    var rulesPromise = queryRules();
                    var userInfoPromise = Authentication.queryUserInfo();

                    $q.all([rules, userInfo])
                        .then(
                            function success(result) {
                                rules = result[0].data;
                                userInfo = result[1];
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
            var actions = _.filter(rules.accessControlRuleList, {actionName: actionName});
            // If actions found
            if (actions.length > 0) {
                // Process all found actions objects
                _.forEach(actions, function (action) {
                    // TODO Check roles


                    isEnabled = true;
                    // Check objectProperties
                    if (action.objectProperties) {
                        _.forEach(action.objectProperties, function (value, key) {
                            isEnabled = isEnabled && (_.get(objectProperties, key) === value );
                            // exit from loop if properties are not equal
                            return isEnabled;
                        });
                    }
                    // Return if action object passed
                    return !isEnabled;
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
                url: 'test-data/rules.json',
                cache: true
            });
        }
    }
]);