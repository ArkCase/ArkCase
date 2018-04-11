'use strict';

/**
 * @ngdoc directive
 * @name global.directive:objectAuthorizationRoles
 * @restrict E
 *
 * @description
 *
 ** {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/object-authorization/object.authorization.roles.directive.js directives/object-authorization/object.authorization.roles.directive.js}
 *
 * The object authorization roles directive is used for listing objects and change their roles to be authorized or unauthorized.
 *
 * @param {array} data objects data for the left panel.
 * @param {function} onObjectSelected callback function when object is selected. It's used to fill the roles in the other two lists. callback function should look like this: function (selectedObject, authorized, notAuthorized).
 * @param {function} onAuthRoleChange callback function when role(s) are changed. Callback function should look like this: function (selectedObject, authorized, notAuthorized).
 * @param {string} objectDisplayName name of the field which we want to display in the list.
 * @param {string} roleDisplayName name of the field which we want to display in the list.
 * @param {string} objectTitle value for the title Choose {{objectTitle}} in selecting objects.
 * @param {boolean} hideFilter true/false, on this variable depends the visibility of the directive "objectAuthorizationRolesFilter", default value is false,
 *                  usage: <object-authorization-roles-filter ng-hide="hideFilter" />
 * @param {object} paginationDataControl - An object that contains functions for pagination
 * @param {object} filterDataControl - An object that contains functions for search/filter
 * @param {object} selectedObject - An object that has information for the pre-selected/selected user
 * @param {boolean} member - An object that has information if an adhoc group is a member of another adhoc group
 *
 * @scope
 *
 * @example
 <example module="ngAppDemo">
 <file name="index.html">
 <div ng-controller="ngAppDemoController">
 <object-authorization-roles data="widgets" object-display-name="name" on-object-selected="onObjSelect" role-display-name="name"
 on-auth-role-change="onAuthRoleSelected"/>
 </div>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', [])
 .controller('ngAppDemoController', function ($scope) {
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
angular.module('directives').directive(
        'objectAuthorizationRoles',
        [
                'Menus',
                'MessageService',
                '$http',
                'UtilService',
                '$injector',
                function(Menus, messageService, $http, Util, $injector) {
                    return {
                        restrict : 'E',
                        scope : {
                            data : "=",
                            onObjectSelected : "=",
                            onAuthRoleChange : "=",
                            objectDisplayName : "@",
                            roleDisplayName : "@",
                            objectTitle : "@",
                            hideFilter : "=?",
                            paginationDataControl : "=?",
                            filterDataControl : "=?",
                            selectedObject : "=?",
                            member : "=?"
                        },
                        templateUrl : 'directives/object-authorization/object.authorization.roles.html',
                        link : function(scope) {
                            //authorize button is clicked
                            scope.authorize = function() {
                                //don't do anything if array null or empty
                                if (scope.selectedNotAuthorized && scope.selectedNotAuthorized.length > 0) {
                                    angular.forEach(scope.selectedNotAuthorized, function(sel) {
                                        var indexOf = scope.data.selectedNotAuthorized.indexOf(sel);
                                        scope.data.selectedNotAuthorized.splice(indexOf, 1);
                                        scope.data.selectedAuthorized.push(sel);
                                    });
                                    scope.authRoleChange();
                                }
                            };

                            //unauthorize button is clicked
                            scope.unAuthorize = function() {
                                //don't do anything if array null or empty
                                if (scope.selectedAuthorized && scope.selectedAuthorized.length > 0) {
                                    angular.forEach(scope.selectedAuthorized, function(sel) {
                                        var indexOf = scope.data.selectedAuthorized.indexOf(sel);
                                        scope.data.selectedAuthorized.splice(indexOf, 1);
                                        scope.data.selectedNotAuthorized.push(sel);
                                    });
                                    scope.authRoleChange();
                                }
                            };

                            //object is selected event, call callback function
                            scope.selectObject = function() {
                                scope.data.selectedAuthorized = [];
                                scope.data.selectedNotAuthorized = [];
                                if (scope.selectedObject) {
                                    scope.resetScrollOnSelect = true;
                                    scope.onObjectSelected(scope.selectedObject, scope.data.selectedAuthorized,
                                            scope.data.selectedNotAuthorized);
                                }
                            };

                            //roles has been changed, call callback function with changed values
                            scope.authRoleChange = function() {
                                scope.onAuthRoleChange(scope.selectedObject, scope.data.selectedAuthorized,
                                        scope.data.selectedNotAuthorized).then(function() {
                                    //success save
                                    messageService.succsessAction();
                                }, function() {
                                    //error save
                                    messageService.errorAction();
                                });

                                var allMenuObj = [];
                                angular.forEach(Menus.allMenuObjects, function(menuO) {
                                    allMenuObj.push(menuO);
                                });
                                Menus.allMenuObjects.splice(0, Menus.allMenuObjects.length);
                                Menus.menus.leftnav.items.splice(0, Menus.menus.leftnav.items.length);
                                Menus.menus.topbar.items.splice(0, Menus.menus.topbar.items.length);
                                Menus.menus.usermenu.items.splice(0, Menus.menus.usermenu.items.length);
                                for (var i = 0; i < allMenuObj.length; i++) {
                                    var mO = [];
                                    mO.push(allMenuObj[i]);
                                    Menus.addMenuItems(mO);
                                }
                                scope.$bus.publish('refreshLeftMenu', null);
                            };

                        }
                    };
                } ]);
