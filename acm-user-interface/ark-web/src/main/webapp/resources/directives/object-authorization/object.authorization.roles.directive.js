'use strict';

/**
 * @ngdoc directive
 * @name global.directive:objectAuthorizationRoles
 * @restrict E
 *
 * @description
 *
 ** {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/object-authorization/object.authorization.roles.directive.js directives/object-authorization/object.authorization.roles.directive.js}
 *
 * The object authorization roles directive is used for listing some objects and change their roles to be authorized or unauthorized.
 *
 * @param {string} data objects data for the left panel.
 * @param {string} onObjectSelected callback function name when object is selected. It's used to fill the roles in the other two lists. callback function should look like this: function (selectedObject, authorized, notAuthorized).
 * @param {string} onAuthRoleChange callback function name when role(s) are changed. Callback function should look like this: function (selectedObject, authorized, notAuthorized).
 * @param {string} objectDisplayName name of the field which we want to display in the list.
 * @param {string} roleDisplayName name of the field which we want to display in the list.
 *
 * @scope
 *
 * @example
 <example>
 <file name="index.html">
 <object-authorization-roles data="widgets" object-display-name="name" on-object-selected="onObjSelect" role-display-name="name"
 on-auth-role-change="onAuthRoleSelected"/>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', [])
 .controller('DashboardConfigCtrl', function ($scope) {
         $scope.widgets = [{name:"widget Name1", widgetName:"widgetName1"}, {name:"widget Name2", widgetName:"widgetName2"}];
         $scope.widgetsMap = [];
        angular.forEach($scope.widgets, function (widget) {
            var element = new Object;
            element.name = widget.name;
            element.key = widget.widgetName;
            $scope.widgets.push(element);
            $scope.widgetsMap[widget.widgetName] = widget;
        });


        $scope.onObjSelect = function (selectedObject, authorized, notAuthorized) {
            angular.forEach($scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles, function (element) {
                authorized.push(element);
            });
            angular.forEach($scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles, function (element) {
                notAuthorized.push(element);
            });
        };

        $scope.onAuthRoleSelected = function (selectedObject, authorized, notAuthorized) {
            $scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles = authorized;
            $scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles = notAuthorized;
        };
     });
 </file>
 </example>
 */
angular.module('directives').directive('objectAuthorizationRoles', [
    function () {
        return {
            restrict: 'E',
            scope: {
                data: "=",
                onObjectSelected: "=",
                onAuthRoleChange: "=",
                objectDisplayName: "@",
                roleDisplayName: "@"
            },
            templateUrl: 'directives/object-authorization/object.authorization.roles.html',
            link: function (scope) {
                scope.$watch('data', function (newValue) {
                    if (newValue && newValue.length > 0) {
                        scope.selectedObject = scope.data[0];
                        scope.selectObject();
                    }
                });

                //initial setup
                scope.selectedNotAuthorized = "";
                scope.selectedAuthorized = "";
                scope.authorized = [];
                scope.notAuthorized = [];

                //authorize button is clicked
                scope.authorize = function () {
                    //don't do anything if array null or empty
                    if (scope.selectedNotAuthorized && scope.selectedNotAuthorized.length > 0) {
                        angular.forEach(scope.selectedNotAuthorized, function (sel) {
                            var indexOf = scope.notAuthorized.indexOf(sel);
                            scope.notAuthorized.splice(indexOf, 1);
                            scope.authorized.push(sel);
                        });
                        scope.authRoleChange();
                    }
                };

                //unauthorize button is clicked
                scope.unAuthorize = function () {
                    //don't do anything if array null or empty
                    if (scope.selectedAuthorized && scope.selectedAuthorized.length > 0) {
                        angular.forEach(scope.selectedAuthorized, function (sel) {
                            var indexOf = scope.authorized.indexOf(sel);
                            scope.authorized.splice(indexOf, 1);
                            scope.notAuthorized.push(sel);
                        });
                        scope.authRoleChange();
                    }
                };


                //object is selected event, call callback function
                scope.selectObject = function () {
                    scope.authorized = [];
                    scope.notAuthorized = [];
                    scope.onObjectSelected(scope.selectedObject, scope.authorized, scope.notAuthorized);
                };

                //roles has been changed, call callback function with changed values
                scope.authRoleChange = function () {
                    scope.onAuthRoleChange(scope.selectedObject, scope.authorized, scope.notAuthorized);
                };
            }
        };
    }
]);