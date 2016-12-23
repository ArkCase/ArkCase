'use strict';

angular.module('dashboard.details', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('details', {
                    title: 'Details',
                    description: 'Displays details',
                    controller: 'Dashboard.DetailsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/details-widget.client.view.html',
                    commonName: 'details'
                }
            );
    })
    .controller('Dashboard.DetailsController', ['$scope', '$stateParams', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService', 'Task.InfoService', 'CostTracking.InfoService', 'TimeTracking.InfoService'
        , 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $stateParams, Util, CaseInfoService, ComplaintInfoService, TaskInfoService
            , CostTrackingInfoService, TimeTrackingInfoService, HelperObjectBrowserService, HelperUiGridService) {

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    validateInfo: CaseInfoService.validateCaseInfo
                }
                , {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                }
                , {
                    name: "COSTSHEET",
                    configName: "cost-tracking",
                    getInfo: CostTrackingInfoService.getCostsheetInfo,
                    validateInfo: CostTrackingInfoService.validateCostsheet
                }
                , {
                    name: "TIMESHEET",
                    configName: "time-tracking",
                    getInfo: TimeTrackingInfoService.getTimesheetInfo,
                    validateInfo: TimeTrackingInfoService.validateTimesheet
                }
                , {
                    name: "TASK",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    validateInfo: TaskInfoService.validateTaskInfo
                }
                , {
                    name: "ADHOC",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    validateInfo: TaskInfoService.validateTaskInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };


            var gridHelper = new HelperUiGridService.Grid({scope: $scope});
            var promiseUsers = gridHelper.getUsers();

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.gridOptions.data = objectInfo.details ? [Util.omitNg(objectInfo)] : [];
                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "details";
                });
                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);

                $scope.gridOptions.columnDefs = widgetInfo.columnDefs;
            };

        }
    ]);