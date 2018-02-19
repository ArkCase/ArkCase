'use strict';

angular.module('directives').directive('objectAuthorizationRolesFilter', [ 'UtilService', function(Util) {
    return {
        restrict : 'E',
        scope : {
            controllerAlias : "@?",
            filter : "&"
        },
        templateUrl : 'directives/object-authorization-filter/object.authorization.roles.filter.html',
        link : function(scope) {
            scope.isSearchValid = true;
            scope.data = {};

            scope.onChangeFilterWord = function() {
                if (scope.data.filterWord == "") {
                    scope.isSearchValid = true;
                    scope.filter({
                        data : {}
                    });
                }
                scope.isSearchValid = false;
            };

            scope.filterObjects = function() {
                scope.filter({
                    data : scope.data
                });
            };
        }
    }
} ]);