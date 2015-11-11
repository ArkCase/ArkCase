'use strict';

/**
 * @ngdoc directive
 * @name global.directive:permission
 * @restrict A
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/permission/permission.client.directive.js directives/permission/permission.client.directive.js}
 *
 * The permission directive controls User Interface control availability depends on security settings.
 * Action name consists of 2 parts: module name and acrionName, for example: 'cases.createCase'
 *
 * @param {String} permission Acion name that includes module name, for example: 'cases.createCase'
 * @param {String} [disableaction=disable] Defines action that should be applied to disabled UI control ('disable', 'hide')
 *
 * @example
 <example>
 <file name="index.html">
 <button permission="cases.createCase"  permission.disableAction="disable" ng-click="createCase()">Create Case</button>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
        $scope.createCase = function(){
            $log.debug('Create case');
        };
     });
 </file>
 </example>
 */
angular.module('directives').directive('permission', ['$q', 'MessageService', 'PermissionsService',
    function ($q, MessageService, PermissionsService) {
        return {
            restrict: 'A',

            link: function (scope, element, attrs) {
                var actionName = attrs.permission;
                var disableAction = attrs.disableaction;

                attrs.$observe('disabled', function () {
                    setPermission(element, actionName, disableAction);
                });

                setPermission(element, actionName, disableAction);
                element.on('click', function(e) {
                    e.preventDefault();
                    MessageService.info('Action ' + actionName + ' is disabled');
                });
            }
        };

        function setPermission(element, actionName, disableAction) {
            PermissionsService.getActionPermission(actionName)
                .then(
                    function success(enabled) {

                        if (enabled === false) {
                            // Hide element if
                            if (disableAction == 'hide') {
                                element.css({'visibility': 'hidden'});
                            } else {
                                // Disable element by default
                                element.attr('disabled', true);
                            }
                        }
                    }, function error() {
                        debugger;
                    }
                );
        }
    }
]);
