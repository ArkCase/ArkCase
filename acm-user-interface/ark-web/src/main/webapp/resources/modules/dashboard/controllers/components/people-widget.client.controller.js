'use strict';

angular.module('dashboard.people', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('people', {
                    title: 'People Widget',
                    description: 'Displays people',
                    controller: 'Dashboard.PeopleController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/people-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.PeopleController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ConfigService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: CaseInfoService.getCaseInfo}
                ,   {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo($stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "people";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;
                        $scope.gridOptions.data = info.personAssociations;
                        $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                    },
                    function (err) {

                    }
                );
            }

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[1].columnDefs; //widget[1] = people

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            CaseInfoService.getCaseInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = data.personAssociations;
                                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                                }
                                , function (error) {
                                    $scope.caseInfo = null;
                                    $scope.progressMsg = $translate.instant("cases.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else if ($stateParams.type == 'complaint') {
                            ComplaintInfoService.getComplaintInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = data.personAssociations;
                                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                                }
                                , function (error) {
                                    $scope.complaintInfo = null;
                                    $scope.progressMsg = $translate.instant("complaint.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }
        }
    ]);