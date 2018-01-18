'use strict';

/**
 * @ngdoc directive
 * @name global.directive:objectAuthorizationRoles
 * @restrict E
 *
 * @description
 *
 ** {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/object-authorization/object.authorization.roles.directive.js directives/object-authorization/object.authorization.roles.directive.js}
 *
 * The object authorization roles directive is used for listing objects and change their roles to be authorized or unauthorized.
 *
 * @param {array} data objects data for the left panel.
 * @param {function} onObjectSelected callback function when object is selected. It's used to fill the roles in the other two lists. callback function should look like this: function (selectedObject, authorized, notAuthorized).
 * @param {function} onAuthRoleChange callback function when role(s) are changed. Callback function should look like this: function (selectedObject, authorized, notAuthorized).
 * @param {string} objectDisplayName name of the field which we want to display in the list.
 * @param {string} roleDisplayName name of the field which we want to display in the list.
 * @param {string} objectTitle value for the title Choose {{objectTitle}} in selecting objects.
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
angular.module('directives').directive('objectAuthorizationRoles',
        [ 'Menus', 'MessageService', '$http', 'UtilService', '$injector', function(Menus, messageService, $http, Util, $injector) {
            return {
                restrict : 'E',
                scope : {
                    data : "=",
                    onObjectSelected : "=",
                    onAuthRoleChange : "=",
                    objectDisplayName : "@",
                    roleDisplayName : "@",
                    controllerName : "@?",
                    objectTitle : "@"
                },
                templateUrl : 'directives/object-authorization/object.authorization.roles.html',
                link : function(scope) {
                    scope.$watch('data', function(newValue) {
                        if (newValue && newValue.length > 0) {
                            scope.selectedObject = scope.data[0];
                            scope.selectObject();
                        }
                    }, true);

                    scope.filterWord = "";
                    scope.noFilterData = true;
                    scope.onChangeFilterWord = function() {
                        if (Util.isEmpty(scope.filterWord)) {
                            scope.noFilterData = true;
                            scope.$bus.publish('onFilter-' + scope.controllerName, "");
                        } else {
                            scope.noFilterData = false;
                        }
                    };

                    scope.filterObjects = function() {
                        scope.$bus.publish('onFilter-' + scope.controllerName, scope.filterWord);
                    };

                    //initial setup
                    scope.selectedNotAuthorized = "";
                    scope.selectedAuthorized = "";
                    scope.authorized = [];
                    scope.notAuthorized = [];

                    document.getElementById("scrollTest").addEventListener("scroll", myFunction);
                    var temp = document.getElementById("scrollTest");

                    var maxScrolled = 0;

                    function myFunction() {
                        var temp = document.getElementById("scrollTest");
                        console.log(temp.scrollTop);
                        console.log(temp.offsetHeight + temp.scrollTop);
                        if ((temp.offsetHeight + temp.scrollTop) >= temp.scrollHeight) {
                            //                         maxScrolled = temp.scrollTop / 200;
                            //                         console.log(maxScrolled);

                            /*scope.externalMethods[scope.externalMethodTwo](scope.filterWord, scope.data.length * 2).then(
                                    function(response) {
                                        console.log(response);
                                        scope.data = [];
                                        if (!Util.isEmpty(response.data.response.docs)) {
                                            _.forEach(response.data.response.docs, function(user) {
                                                var element = {};
                                                element.name = user.name;
                                                element.key = user.object_id_s;
                                                element.directory = user.directory_name_s;
                                                scope.data.push(element);
                                            });
                                            scope.selectedObject = scope.data[0]
                                        }

                                    }, function() {
                                        console.log("error");
                                    });
                             */
                            // scope.$parent.$digest();
                        }
                    }

                    //authorize button is clicked
                    scope.authorize = function() {
                        //don't do anything if array null or empty
                        if (scope.selectedNotAuthorized && scope.selectedNotAuthorized.length > 0) {
                            angular.forEach(scope.selectedNotAuthorized, function(sel) {
                                var indexOf = scope.notAuthorized.indexOf(sel);
                                scope.notAuthorized.splice(indexOf, 1);
                                scope.authorized.push(sel);
                            });
                            scope.authRoleChange();
                        }
                    };

                    //object is selected event, call callback function
                    scope.selectObject = function() {
                        scope.authorized = [];
                        scope.notAuthorized = [];
                        if (scope.selectedObject) {
                            scope.onObjectSelected(scope.selectedObject, scope.authorized, scope.notAuthorized);
                        }
                    };

                    //roles has been changed, call callback function with changed values
                    scope.authRoleChange = function() {
                        scope.onAuthRoleChange(scope.selectedObject, scope.authorized, scope.notAuthorized).then(function() {
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
angular.module('directives').filter('orderObjectBy', function() {
    return function(input) {
        if (!angular.isObject(input))
            return input;

        var array = [];
        for ( var objectKey in input) {
            array.push(input[objectKey]);
        }

        array.sort(function(a, b) {
            a = a["name"];
            b = b["name"];
            return a > b ? 1 : a < b ? -1 : 0;
        });
        return array;
    }
});