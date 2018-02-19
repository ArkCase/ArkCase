'use strict';
/**
 * @ngdoc directive
 * @name global.directive:objectAuthorizationRolesFilter
 * @restrict A
 *
 * @description
 *
 *{@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/object-authorization-filter/object-authorization-filter.client.directive.js directives/object-authorization-filter/object-authorization-filter.client.directive.js}
 *
 * The "objectAuthorizationRolesFilter" directive search data using the search-input data
 *
 * @param {object} filter - An object(function) should be called when the user search/filter and to retrieve info for the search input data
 *
 * @example
 <example>
 <object-authorization-roles-filter ng-show="true" filter="exampleFunction(data)" />
 </example>
 **/

angular.module('directives').directive('objectAuthorizationRolesFilter', [ 'UtilService', function(Util) {
    return {
        restrict : 'E',
        scope : {
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