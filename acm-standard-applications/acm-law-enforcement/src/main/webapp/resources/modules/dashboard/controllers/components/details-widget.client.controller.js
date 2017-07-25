'use strict';

angular.module('dashboard.details', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('details', {
                title: 'dashboard.widgets.details.title',
                description: 'dashboard.widgets.details.description',
                controller: 'Dashboard.DetailsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/details-widget.client.view.html',
                commonName: 'details'
            });
    })
    .controller('Dashboard.DetailsController', ['$scope', '$stateParams', '$translate',
        'UtilService', 'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService', 'CostTracking.InfoService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'DocumentRepository.InfoService', 'Person.InfoService', 'Organization.InfoService',
        function ($scope, $stateParams, $translate,
                  Util, CaseInfoService, ComplaintInfoService, TaskInfoService, CostTrackingInfoService, TimeTrackingInfoService, HelperObjectBrowserService, HelperUiGridService, DocumentRepositoryInfoService, PersonInfoService, OrganizationInfoService) {

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
                , {
                    name: "DOC_REPO",
                    configName: "document-repository",
                    getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                    validateInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
                }
                , {
                    name: "PERSON",
                    configName: "people",
                    getInfo: PersonInfoService.getPersonInfo,
                    validateInfo: PersonInfoService.validatePersonInfo
                }
                , {
                    name: "ORGANIZATION",
                    configName: "organizations",
                    getInfo: OrganizationInfoService.getOrganizationInfo,
                    validateInfo: OrganizationInfoService.validateOrganizationInfo
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
                if(!Util.isEmpty(objectInfo.details)) {
                    $scope.gridOptions.data = [Util.omitNg(objectInfo)];
                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                    $scope.gridOptions.noData = false;
                }
                else{
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.details.noDataMessage');
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "details";
                });
                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);

                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };

        }
    ]);