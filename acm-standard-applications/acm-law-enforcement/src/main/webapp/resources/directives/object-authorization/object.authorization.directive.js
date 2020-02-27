'use strict';

/**
 * @ngdoc directive
 * @name global.directive:objectAuthorization
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
 * @param {boolean} hideFilter true/false, on this variable depends the visibility of the directive "objectAuthorizationRolesFilter", default value is false,
 *                  usage: <object-authorization-roles-filter ng-hide="hideFilter" />
 * @param {object} paginationDataControl - An object that contains functions for pagination
 * @param {object} filterDataControl - An object that contains functions for search/filter
 * @param {object} selectedObject - An object that has information for the pre-selected/selected user
 * @param {boolean} member - An object that has information if an adhoc group is a member of another adhoc group
 * @param {string} objectType string value for the object, to check whether it is, role or something else. This is used for filtering data in tree panel view
 * @param {boolean} showTooltip true/false, on this variable depends the visibility of the tooltip over the elements, default value is false,
 *                  usage: <object-authorization show-tooltip="true" />
 * @scope
 *
 * @example
 <example module="ngAppDemo">
 <file name="index.html">
 <div ng-controller="ngAppDemoController">
 <object-authorization data="widgets" object-display-name="name" on-object-selected="onObjSelect" role-display-name="name"
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
angular.module('directives').directive('objectAuthorization', [ 'Menus', 'MessageService', '$q', '$timeout', '$translate', function(Menus, messageService, $q, $timeout, $translate) {
    return {
        restrict: 'E',
        scope: {
            data: "=",
            onObjectSelected: "=",
            onAuthRoleChange: "=",
            objectDisplayName: "@",
            roleDisplayName: "@",
            objectTitle: "@",
            hideFilter: "=?",
            paginationDataControl: "=?",
            filterDataControl: "=?",
            selectedObject: "=?",
            member: "=?",
            objectType: '@',
            showTooltip: "=?"
        },
        templateUrl: 'directives/object-authorization/object.authorization.html',
        link: function(scope) {

            scope.firstSelectHide = scope.data.firstSelectHide;
            scope.hideFilter = scope.data.hideFilter;
            scope.showTooltip = scope.showTooltip;

            //authorize button is clicked
            scope.authorize = function() {
                //don't do anything if array null or empty
                if (scope.selectedNotAuthorized && scope.selectedNotAuthorized.length > 0) {
                    var toAdd = angular.copy(scope.selectedNotAuthorized);
                    _authorize(toAdd, scope.data);

                    scope.authRoleChange().then(function() {
                    }, function() {
                        //on error revert back changes
                        _unAuthorize(toAdd, scope.data);
                    });
                }
            };

           scope.showTooltipFunction = function() {
               if(scope.showTooltip) {
                   $timeout(function () {
                       $("#selectedNotAuthorized option").each(function (index, element) {
                           $(element).attr("title", $translate.instant(scope.data.selectedNotAuthorized[index].value));
                       });
                       $("#selectedAuthorized option").each(function (index, element) {
                           $(element).attr("title", $translate.instant(scope.data.selectedAuthorized[index].value));
                       });
                   });
               }
            };

            var _unAuthorize = function(toRemove, data) {
                angular.forEach(toRemove, function(sel) {
                    var indexOf = data.selectedAuthorized.map(function(obj) {
                        return obj.key + " " + obj.name;
                    }).indexOf(sel.key + " " + sel.name);
                    data.selectedAuthorized.splice(indexOf, 1);
                    data.selectedNotAuthorized.push(sel);
                });
            };

            var _authorize = function(toAdd, data) {
                angular.forEach(toAdd, function(sel) {
                    var indexOf = data.selectedNotAuthorized.map(function(obj) {
                        return obj.key + " " + obj.name;
                    }).indexOf(sel.key + " " + sel.name);
                    data.selectedNotAuthorized.splice(indexOf, 1);
                    data.selectedAuthorized.push(sel);
                });
            };

            //unauthorize button is clicked
            scope.unAuthorize = function() {
                //don't do anything if array null or empty
                if (scope.selectedAuthorized && scope.selectedAuthorized.length > 0) {
                    var toRemove = angular.copy(scope.selectedAuthorized);
                    _unAuthorize(toRemove, scope.data);

                    scope.authRoleChange().then(function() {
                    }, function() {
                        //on error revert back changes
                        _authorize(toRemove, scope.data);
                    });
                }
            };

            //object is selected event, call callback function
            scope.selectObject = function() {
                scope.data.selectedAuthorized = [];
                scope.data.selectedNotAuthorized = [];
                if (scope.selectedObject) {
                    scope.resetScrollOnSelect = true;
                    scope.onObjectSelected(scope.selectedObject);
                }
            };

            //roles has been changed, call callback function with changed values
            scope.authRoleChange = function() {
                var deferred = $q.defer();
                scope.onAuthRoleChange(scope.selectedObject, scope.data.selectedAuthorized, scope.data.selectedNotAuthorized).then(function(data) {
                    //success save
                    messageService.succsessAction();
                    deferred.resolve();
                }, function(error) {
                    //error save
                    deferred.reject();
                    if (error.data.message) {
                        messageService.error(error.data.message);
                    } else {
                        messageService.errorAction();
                    }
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
                return deferred.promise;
            };

        }
    };
} ]);
