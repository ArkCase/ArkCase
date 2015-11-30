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
 * @param {String} permission-properties Data object that shhould be passed to permissions service
 * @param {String} [permission-action=disable] Defines action that should be applied to disabled UI control ('disable', 'hide')
 *
 * @example
 <example>
 <file name="index.html">
 <button permission="createCase"  permission-properties="orderInfo" permission-action="disable" ng-click="createCase()">Create Case</button>
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
angular.module('directives').directive('permission', ['$q', '$log', 'PermissionsService',
    function ($q, $log, PermissionsService) {
        return {
            priority: 100,
            restrict: 'A',
            scope: {
                permission: '@',
                permissionAction: '@',
                permissionProperties: '='
            },

            link: {
                pre: function (scope, element, attrs) {
                    element.on('click', {
                            actionName: scope.permission,
                            element: element
                        },
                        onElementClick
                    );
                },

                post: function (scope, element, attrs) {
                    var actionName = scope.permission;
                    var permissionAction = scope.permissionAction;

                    scope.$watchCollection('permissionProperties', function (newValue, oldValue) {
                        $q.when(newValue).then(function (permissionProperties) {
                            setPermission(element, actionName, permissionProperties, permissionAction);
                        }, true);
                    });

                    attrs.$observe('disabled', function () {
                        setPermission(element, actionName, scope.permissionProperties, permissionAction);
                    });

                }
            }
        };

        function setPermission(element, actionName, permissionProperties, permissionAction) {
            PermissionsService.getActionPermission(actionName, permissionProperties)
                .then(
                    function success(enabled) {

                        if (enabled === false) {
                            // Hide element if
                            if (permissionAction == 'hide') {
                                element.css({'display': 'none'});
                            } else {
                                element.attr('disabled', true);
                            }
                            element.attr('permission-disabled', true);
                        } else {
                            // Hide element if
                            if (permissionAction == 'hide') {
                                element.css({'display': ''});
                            } else {
                                element.attr('disabled', false);
                            }
                            element.attr('permission-disabled', false);
                        }
                    }, function error() {
                        $log.error('Can\'t get permission info for action ' + actionName);
                    }
                );
        };

        function onElementClick(e) {
            if (e.data.element.attr('permission-disabled') ==='true') {
                e.stopImmediatePropagation();
                e.preventDefault();
            }
        }
    }
]);
