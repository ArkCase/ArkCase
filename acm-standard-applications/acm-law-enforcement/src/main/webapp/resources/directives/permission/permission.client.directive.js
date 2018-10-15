'use strict';

/**
 * @ngdoc directive
 * @name global.directive:permission
 * @restrict A
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/permission/permission.client.directive.js directives/permission/permission.client.directive.js}
 *
 * The permission directive controls User Interface control availability depends on security settings.
 * Action name consists of 2 parts: module name and actionName, for example: 'cases.createCase'
 *
 * @param {string} permission Acion name that includes module name, for example: 'cases.createCase' ( can also be included multiple action for a single element ex: 'cases.createCase | cases.editCase'
 * @param {String} permission-object-type Object type to be passed to permission service
 * @param {string} permission-properties Data object that shhould be passed to permissions service
 * @param {string} [permission-action=disable] Defines action that should be applied to disabled UI control ('disable', 'hide')
 *
 * @example
 <example>
 <file name="index.html">
 <button permission="createCase"  permission-object-type="CASE_FILE" permission-properties="orderInfo" permission-action="disable" ng-click="createCase()">Create Case</button>
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
angular.module('directives').directive('permission', [ '$q', '$log', 'PermissionsService', function($q, $log, PermissionsService) {
    return {
        priority: 100,
        restrict: 'A',

        link: {
            pre: function(scope, element, attrs) {
                element.on('click', {
                    actionName: attrs.permission,
                    objectType: attrs.permissionObjectType,
                    element: element
                }, onElementClick);
            },

            post: function(scope, element, attrs) {

                // We use access to attributes value instead of isolated scope
                // to avoid "isolated scopes conflicts" when few directives with isolated scopes are applied to element
                var actionName = attrs.permission;
                var objectType = attrs.permissionObjectType;
                var permissionAction = attrs.permissionAction;
                var permissionProperties = null;

                scope.$watch(attrs.permissionProperties, function(value) {
                    permissionProperties = value;
                    setPermission(element, actionName, objectType, permissionProperties, permissionAction);
                });

                attrs.$observe('disabled', function() {
                    setPermission(element, actionName, objectType, permissionProperties, permissionAction);
                });
            }
        }
    };

    function setPermission(element, actionName, objectType, permissionProperties, permissionAction) {
        actionName = actionName.split("|");
        for(var i=0;i < actionName.length; i++){
            var action = actionName[i];
        PermissionsService.getActionPermission(action, permissionProperties, {
            objectType: objectType
        }).then(function success(enabled) {

            if (enabled === false) {
                if (permissionAction == 'hide') {
                    element.css({
                        'display': 'none'
                    });
                } else if (permissionAction == 'show') {
                    element.css({
                        'display': ''
                    });
                } else {
                    element.attr('disabled', true);
                }
                element.attr('permission-disabled', true);
            } else {
                if (permissionAction == 'hide') {
                    element.css({
                        'display': ''
                    });
                } else if (permissionAction == 'show') {
                    element.css({
                        'display': 'none'
                    });
                } else {
                    element.attr('disabled', false);
                }
                element.attr('permission-disabled', false);
            }
        }, function error() {
            $log.error('Can\'t get permission info for action ' + action);
        });
        }
    }
    ;

    function onElementClick(e) {
        if (e.data.element.attr('permission-disabled') === 'true') {
            e.stopImmediatePropagation();
            e.preventDefault();
        }
    }
} ]);
