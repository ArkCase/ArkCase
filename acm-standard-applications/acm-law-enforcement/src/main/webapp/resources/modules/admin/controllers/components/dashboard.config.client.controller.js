/**
 * Created by nebojsha on 10/30/2015.
 */

angular.module('admin')
        .controller(
                'Admin.DashboardConfigController',
                [
                        '$scope',
                        'Admin.DashboardConfigService',
                        'ConfigService',
                        '$q',
                        'UtilService',
                        '$translate',
                        function($scope, dashboardConfigService, ConfigService, $q, Util, $translate) {

                            var deferred = $q.defer();
                            var tempWidgetsPromise = dashboardConfigService.getRolesByWidgets();
                            var tempDashboardModulePromise = ConfigService.getModuleConfig("dashboard");

                            $scope.filterArray = filterArray;
                            //filter functions
                            $scope.chooseAppRoleFilter = chooseAppRoleFilter;
                            $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
                            $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;
                            //scroll functions
                            $scope.widgetsScroll = widgetsScroll;
                            $scope.appRoleUnauthorizedScroll = appRoleUnauthorizedScroll;
                            $scope.appRoleAuthorizedScroll = appRoleAuthorizedScroll;

                            $scope.showFilter = true;
                            // Loaded data after the initialization
                            var initWidgetsData = {
                                "chooseObject" : [],
                                "selectedNotAuthorized" : [],
                                "selectedAuthorized" : []
                            };
                            // Data to be displayed
                            $scope.widgetsData = {
                                "chooseObject" : [],
                                "selectedNotAuthorized" : [],
                                "selectedAuthorized" : []
                            };
                            $scope.filterData = {
                                "objectsFilter" : $scope.chooseAppRoleFilter,
                                "unauthorizedFilter" : $scope.appRoleUnauthorizedFilter,
                                "authorizedFilter" : $scope.appRoleAuthorizedFilter
                            };
                            $scope.scrollLoadData = {
                                "loadObjectsScroll" : $scope.widgetsScroll,
                                "loadUnauthorizedScroll" : $scope.appRoleUnauthorizedScroll,
                                "loadAuthorizedScroll" : $scope.appRoleAuthorizedScroll
                            };
                            $scope.widgetsMap = [];

                            $q.all([ tempWidgetsPromise, tempDashboardModulePromise ]).then(function(payload) {
                                angular.forEach(payload[0].data, function(widget) {
                                    var cfg = _.find(payload[1].components, {
                                        id : widget.widgetName
                                    });

                                    if (!Util.isEmpty(cfg)) {
                                        var element = new Object;
                                        element.name = cfg.title;
                                        element.key = widget.widgetName;

                                        initWidgetsData.chooseObject.push(element);
                                        $scope.widgetsMap[widget.widgetName] = widget;
                                    }
                                });
                                $scope.widgetsData.chooseObject = initWidgetsData.chooseObject.slice(0, 18);
                                $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
                            });

                            function filterArray(data, arrayFrom, arrayTo) {
                                if (!_.isEmpty(data)) {
                                    arrayTo = _.filter(arrayFrom, function(item) {
                                        return ($translate.instant(item.name).toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                                    });
                                } else {
                                    arrayTo = arrayFrom.slice(0, 18);
                                }
                            }

                            function chooseAppRoleFilter(data) {
                                filterArray(data, initWidgetsData.chooseObject, $scope.widgetsData.chooseObject);
                                $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
                            }

                            function appRoleUnauthorizedFilter(data) {
                                filterArray(data, initWidgetsData.selectedNotAuthorized, $scope.widgetsData.selectedNotAuthorized);
                                $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
                            }

                            function appRoleAuthorizedFilter(data) {
                                filterArray(data, initWidgetsData.selectedAuthorized, $scope.widgetsData.selectedAuthorized);
                                $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
                            }

                            function widgetsScroll() {
                                $scope.widgetsData.chooseObject = initWidgetsData.chooseObject.slice(0,
                                        $scope.widgetsData.chooseObject.length * 2);
                                $scope.$digest();
                            }

                            function appRoleUnauthorizedScroll() {
                                $scope.widgetsData.selectedNotAuthorized = initWidgetsData.selectedNotAuthorized.slice(0,
                                        $scope.widgetsData.selectedNotAuthorized.length * 2);
                                $scope.$digest();
                            }

                            function appRoleAuthorizedScroll() {
                                $scope.widgetsData.selectedAuthorized = initWidgetsData.selectedAuthorized.slice(0,
                                        $scope.widgetsData.selectedAuthorized.length * 2);
                                $scope.$digest();
                            }

                            $scope.onObjSelect = function(selectedObject, authorized, notAuthorized) {
                                $scope.lastSelectedRole = [];
                                $scope.lastSelectedRole = selectedObject;

                                initWidgetsData.selectedAuthorized = angular
                                        .copy($scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles);
                                initWidgetsData.selectedNotAuthorized = angular
                                        .copy($scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles);

                                $scope.widgetsData.selectedAuthorized = initWidgetsData.selectedAuthorized.slice(0, 18);
                                $scope.widgetsData.selectedNotAuthorized = initWidgetsData.selectedNotAuthorized.slice(0, 18);
                            };

                            $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
                                $scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles = authorized;
                                $scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles = notAuthorized;
                                dashboardConfigService.authorizeRolesForWidget($scope.widgetsMap[selectedObject.key]).then(function() {
                                    deferred.resolve();
                                }, function() {
                                    deferred.reject();
                                });

                                return deferred.promise;
                            };
                        } ]);