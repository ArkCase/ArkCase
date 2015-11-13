'use strict';

angular.module('services').factory('PermissionsService', ['$q', '$http', '$log',
    function ($q, $http) {
        var permissions = {};

        return {
            getActionPermission: function (actionName) {
                var deferred = $q.defer();
                if (actionName) {
                    // Get moduleId from action name.
                    var actionNameItems = actionName.split('.');
                    if (actionNameItems.length > 1) {
                        // Check module permissions availability
                        var moduleId = actionNameItems[0];
                        var actionId = actionNameItems[1];
                        if (permissions[moduleId]) {
                            deferred.resolve(permissions[moduleId][actionId]);
                        } else {
                            this.queryModulePermissions(moduleId)
                                .then(
                                    function success(modulePermissions){
                                        permissions = _.extend(permissions, modulePermissions);
                                        deferred.resolve(permissions[moduleId][actionId]);
                                    },
                                    function error(){
                                        $log.error('Module ' + moduleId + ' is absent');
                                        permissions[moduleId] = {};
                                        deferred.reject();
                                    }
                                );
                        }
                    } else {
                        deferred.reject();
                    }
                } else {
                    deferred.reject();
                }
                return deferred.promise;
            },

            queryModulePermissions: function (moduleId) {
                var deferred = $q.defer();

                $http.get('modules_config/config/modules/' + moduleId + '/permissions/permissions.json')
                    .then(
                        function success(result){
                            permissions[moduleId] = result.data;
                            deferred.resolve(result.data);
                        },
                        function error(){
                            deferred.reject();
                        }
                    );
                return deferred.promise;
            }
        }
    }
]);