'use strict';

angular.module('dashboard.details', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('details', {
                    title: 'Details Widget',
                    description: 'Displays details',
                    controller: 'Dashboard.DetailsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/details-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.DetailsController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService', 'Task.InfoService', 'CostTracking.InfoService', 'TimeTracking.InfoService', 'Authentication'
        , 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, TaskInfoService
            , CostTrackingInfoService, TimeTrackingInfoService, Authentication, DashboardService, ConfigService) {

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };
            var promiseConfig;
            var promiseInfo;
            var modules = [
                    {name: "CASE_FILE", configName: "cases", getInfo: CaseInfoService.getCaseInfo}
                ,   {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getCaseInfo}
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
                        var widgetInfo = _.find(config.widgets, function(widget){
                            return widget.id === "details";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;
                        $scope.gridOptions.data = [Util.omitNg(info)];
                        $scope.gridOptions.totalItems = 1;
                    },
                    function (err) {
                        console.log("screwed");
                    }
                );
            }
            //-------------

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;


            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[0].columnDefs; //widgets[0] = details

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            CaseInfoService.getCaseInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = [Util.omitNg(data)];
                                    $scope.gridOptions.totalItems = 1;
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
                                    $scope.gridOptions.data = [Util.omitNg(data)];
                                    $scope.gridOptions.totalItems = 1;
                                }
                                , function (error) {
                                    $scope.complaintInfo = null;
                                    $scope.progressMsg = $translate.instant("complaint.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                            TaskInfoService.getTaskInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = [Util.omitNg(data)];
                                    $scope.gridOptions.totalItems = 1;
                                }
                                , function (error) {
                                    $scope.taskInfo = null;
                                    $scope.progressMsg = $translate.instant("task.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else if ($stateParams.type == 'cost') {
                            CostTrackingInfoService.getCostTrackingInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = [Util.omitNg(data)];
                                    $scope.gridOptions.totalItems = 1;
                                }
                                , function (error) {
                                    $scope.costsheetInfo = null;
                                    $scope.progressMsg = $translate.instant("cost.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else if ($stateParams.type == 'time') {
                            TimeTrackingInfoService.getTimeTrackingInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = [Util.omitNg(data)];
                                    $scope.gridOptions.totalItems = 1;
                                }
                                , function (error) {
                                    $scope.timesheetInfo = null;
                                    $scope.progressMsg = $translate.instant("time.progressError") + " " + id;
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