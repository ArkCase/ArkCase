'use strict';

angular.module('dashboard.references', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('references', {
                title: 'dashboard.widgets.references.title',
                description: 'dashboard.widgets.references.description',
                controller: 'Dashboard.ReferencesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/references-widget.client.view.html',
                commonName: 'references'
            });
    })
    .controller('Dashboard.ReferencesController', ['$scope', '$stateParams', '$translate',
        'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Helper.UiGridService', 'DocumentRepository.InfoService', 'UtilService',
            function ($scope, $stateParams, $translate,
                      CaseInfoService, ComplaintInfoService, TaskInfoService, HelperObjectBrowserService, ObjectService, HelperUiGridService, DocumentRepositoryInfoService, Util) {

                var modules = [
                    {
                        name: "CASE_FILE",
                        configName: "cases",
                        getInfo: CaseInfoService.getCaseInfo,
                        validateInfo: CaseInfoService.validateCaseInfo
                    }
                    ,
                    {
                        name: "COMPLAINT",
                        configName: "complaints",
                        getInfo: ComplaintInfoService.getComplaintInfo,
                        validateInfo: ComplaintInfoService.validateComplaintInfo
                    }
                    ,
                    {
                        name: "TASK",
                        configName: "tasks",
                        getInfo: TaskInfoService.getTaskInfo,
                        validateInfo: TaskInfoService.validateTaskInfo
                    }
                    ,
                    {
                        name: "ADHOC",
                        configName: "tasks",
                        getInfo: TaskInfoService.getTaskInfo,
                        validateInfo: TaskInfoService.validateTaskInfo
                    }
                    ,
                    {
                        name: "DOC_REPO",
                        configName: "document-repository",
                        getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
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

                    /**
                     * Complaints and CaseFiles return their references in a different way.
                     */
                    if (module.name == ObjectService.ObjectTypes.COMPLAINT) {
                        var references = [];
                        _.forEach(objectInfo.childObjects, function (childObject) {
                            if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                                references.push(childObject);
                            }
                        });
                        if(!Util.isArrayEmpty(references)) {
                            $scope.gridOptions.data = references;
                            $scope.gridOptions.noData = false;
                            $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                        }
                        else {
                            $scope.gridOptions.data = [];
                            $scope.gridOptions.noData = true;
                            $scope.gridOptions.totalItems = 0;
                            $scope.noDataMessage = $translate.instant('dashboard.widgets.references.noDataMessage');
                        }
                    } else {
                        if(!Util.isArrayEmpty(objectInfo.references)){
                            $scope.gridOptions.data = objectInfo.references;
                            $scope.gridOptions.noData = false;
                            $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                        }
                        else {
                            $scope.gridOptions.data = [];
                            $scope.gridOptions.noData = true;
                            $scope.gridOptions.totalItems = 0;
                            $scope.noDataMessage = $translate.instant('dashboard.widgets.references.noDataMessage');
                        }
                    }

                };

                var onConfigRetrieved = function (componentConfig) {
                    var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                        return widget.id === "references";
                    });
                    $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
                    gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                };

            }
    ]);