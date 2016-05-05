'use strict';

/**
 * @ngdoc directive
 * @name global.directive:userName
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/user-name/user-name.client.directive.js directives/user-name/user-name.client.directive.js}
 *
 * @param {string} userId User Identifier that displayed if application property ApplicationConfigService.PROPERTIES.NAME  = 'userId'
 * @param {string} userName User Name that displayed if application property ApplicationConfigService.PROPERTIES.NAME  = 'userName'
 *
 * The userName directive displays userId or full user name depends on application configuration.
 * If configuration settings not defined then it displays Full user name by default.
 *
 *
 * @example
 <example>
 <file name="index.html">
 <user-name userId="{{profile.userId}}" userName="{{profile.fullName}}"></user-name>
 </file>
 </example>
 */
angular.module('directives').directive('userName', ['$q', 'ApplicationConfigService',
    function ($q, ApplicationConfigService) {
        return {
            restrict: 'E',
            transclude: true,
            scope: {
                userName: '=',
                userId: '='
            },

            link: function (scope, element, attrs) {

                var userName = null;
                var userId = null;

                scope.$watch('userName', function (newUserName) {
                    userName = newUserName;
                    update(scope, userName, userId);
                });

                scope.$watch('userId', function (newUserId) {
                    userId = newUserId;
                    update(scope, userName, userId);
                });
            },

            template: '{{displayName}}'
        };


        function update(scope, userName, userId) {
            $q.all([ApplicationConfigService.getProperty(ApplicationConfigService.PROPERTIES.DISPLAY_USERNAME)])
                .then(function (result) {
                    var userNameProp = result[0];

                    // Display user Id as userName property equals to 'userId'
                    // otherwise display full user name
                    if (userNameProp == 'userId') {
                        scope.displayName = userId;
                    } else {
                        scope.displayName = userName;
                    }
                });
        }

    }
]);