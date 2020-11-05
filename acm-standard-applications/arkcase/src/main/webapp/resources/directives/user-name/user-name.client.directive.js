'use strict';

/**
 * @ngdoc directive
 * @name global.directive:userName
 * @restrict E
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/directives/user-name/user-name.client.directive.js directives/user-name/user-name.client.directive.js}
 *
 * @param {Object} user object containing data for directive
 * @param {string} user.userId User Identifier that displayed if application property ApplicationConfigService.PROPERTIES.NAME  = 'userId'
 * @param {string} user.userName User Name that displayed if application property ApplicationConfigService.PROPERTIES.NAME  = 'userName'
 *
 * The userName directive displays userId or full user name depends on application configuration.
 * If configuration settings not defined then it displays Full user name by default.
 *
 *
 * @example
 <example>
 <file name="index.html">
 <user-name user"{{user}}"></user-name>
 </file>
 <file name="app.js">
 angular.module('examples').controller('appController', ['$scope'
 , function ($scope) {

 $scope.user = {
 userId: 'ann-acm',
 userName: 'Ann Administrator'
 }
 }
 ]);
 </file>
 </example>
 */
angular.module('directives').directive('userName', [ '$q', 'ApplicationConfigService', function($q, ApplicationConfigService) {
    return {
        restrict: 'E',
        transclude: true,
        scope: {
            user: '='
        },

        link: function(scope, element, attrs) {

            var user = null;
            scope.displayName = "";

            scope.$watch('user', function(newUser) {
                if (newUser != null) {
                    update(scope, newUser);
                }
            });
        },

        template: '{{displayName}}'
    };

    function update(scope, user) {
        $q.all([ ApplicationConfigService.getProperty(ApplicationConfigService.PROPERTIES.DISPLAY_USERNAME) ]).then(function(result) {
            var userNameProp = result[0];

            // Display user Id as userName property equals to 'userId'
            // otherwise display full user name
            if (userNameProp == 'userId') {
                scope.displayName = user.userId;
            } else {
                scope.displayName = user.userName;
            }
        });
    }
} ]);