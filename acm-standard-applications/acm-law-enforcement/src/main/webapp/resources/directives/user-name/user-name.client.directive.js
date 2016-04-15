'use strict';

/**
 * @ngdoc directive
 * @name global.directive:userName
 * @restrict E
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/user-name/user-name.client.directive.js directives/user-name/user-name.client.directive.js}
 *
 * The userName directive displays userId or full user name depends on application configuration.
 *
 *
 * @example
 <example>
 <file name="index.html">
 <user-name></user-name>
 </file>
 </example>
 */
angular.module('directives').directive('userName', ['$q', 'Authentication', 'ApplicationConfigService',
    function ($q, Authentication, ApplicationConfigService) {
        return {
            restrict: 'E',
            transclude: true,

            link: function (scope, element, attrs) {
                $q.all([Authentication.queryUserInfo(), ApplicationConfigService.getConfiguration(ApplicationConfigService.PROP_NAME)])
                    .then(function(result){
                        var userInfo = result[0];
                        var nameConfig = result[1];

                        scope.userName = userInfo.userId;
                    });
            },

            template: '{{userName}}'
        };
    }
]);