'use strict';

angular.module('dashboard.references', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('references', {
                    title: 'References Widget',
                    description: 'Displays references',
                    controller: 'Dashboard.ReferencesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/references-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.ReferencesController', ['$scope', '$translate', '$stateParams', '$q','UtilService'
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
                , {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo}
            ]

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
                            return widget.id === "references";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = info.references;
                        $scope.gridOptions.totalItems = info.references ? $scope.gridOptions.data.length : 0;
                    },
                    function (err) {

                    }
                );
            }
            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[6].columnDefs; //widget[6] = references
                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            CaseInfoService.getCaseInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = data.references;
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
                                    $scope.gridOptions.data = data.references;
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