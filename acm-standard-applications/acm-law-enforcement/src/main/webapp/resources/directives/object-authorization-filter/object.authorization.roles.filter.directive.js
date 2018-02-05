'use strict';

angular.module('directives').directive('objectAuthorizationRolesFilter', [ 'UtilService', function(Util) {
    return {
        restrict : 'E',
        scope : {
            controllerName : "@"
        },
        templateUrl : 'directives/object-authorization-filter/object.authorization.roles.filter.html',
        link : function(scope) {
            scope.filterWord = "";
            scope.noFilterData = true;
            scope.onChangeFilterWord = function() {
                if (scope.filterWord == "") {
                    scope.noFilterData = true;
                    scope.$bus.publish(scope.controllerName + "Filter", "");
                } else {
                    scope.noFilterData = false;
                }
            };

            scope.filterObjects = function() {
                var data = {};
                data.filterWord = scope.filterWord;
                scope.$bus.publish(scope.controllerName + "Filter", data);
            };

        }
    }
} ]);