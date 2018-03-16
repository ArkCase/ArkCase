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
                                        $scope.widgetsData.chooseObject.push(element);
                                        $scope.widgetsMap[widget.widgetName] = widget;
                                    }
                                });
                                $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
                            });

                            function filterArray(data, arrayFrom) {
                                var result = [];
                                if (!_.isEmpty(data)) {
                                    result = _.filter(arrayFrom, function(item) {
                                        return ($translate.instant(item.name).toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                                    });
                                } else {
                                    result = angular.copy(arrayFrom);
                                }
                                return result;
                            }

                            function chooseAppRoleFilter(data) {
                                $scope.widgetsData.chooseObject = filterArray(data, initWidgetsData.chooseObject);
                                if(_.isArray($scope.widgetsData.chooseObject)){
                                    $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
                                }
                            }

                            function appRoleUnauthorizedFilter(data) {
                                $scope.widgetsData.selectedNotAuthorized = filterArray(data, initWidgetsData.selectedNotAuthorized);
                            }

                            function appRoleAuthorizedFilter(data) {
                                $scope.widgetsData.selectedAuthorized = filterArray(data, initWidgetsData.selectedAuthorized, $scope.widgetsData.selectedAuthorized);
                            }

                            $scope.onObjSelect = function(selectedObject, authorized, notAuthorized) {
                                $scope.lastSelectedRole = [];
                                $scope.lastSelectedRole = selectedObject;

                                initWidgetsData.selectedAuthorized = angular
                                        .copy($scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles);
                                initWidgetsData.selectedNotAuthorized = angular
                                        .copy($scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles);

                                $scope.widgetsData.selectedAuthorized = $scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles;
                                $scope.widgetsData.selectedNotAuthorized = $scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles;
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