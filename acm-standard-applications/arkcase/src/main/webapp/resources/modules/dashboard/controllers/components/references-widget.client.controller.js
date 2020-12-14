'use strict';

angular.module('dashboard.references', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('references', {
        title: 'preference.overviewWidgets.references.title',
        description: 'dashboard.widgets.references.description',
        controller: 'Dashboard.ReferencesController',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/references-widget.client.view.html',
        commonName: 'references'
    });
}).controller(
        'Dashboard.ReferencesController',
        [ '$scope', '$stateParams', '$translate', 'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Helper.UiGridService', 'DocumentRepository.InfoService',
                function($scope, $stateParams, $translate, CaseInfoService, ComplaintInfoService, TaskInfoService, HelperObjectBrowserService, ObjectService, HelperUiGridService, DocumentRepositoryInfoService) {

                    var modules = [ {
                        name: "CASE_FILE",
                        configName: "cases",
                        getInfo: CaseInfoService.getCaseInfo,
                        validateInfo: CaseInfoService.validateCaseInfo
                    }, {
                        name: "COMPLAINT",
                        configName: "complaints",
                        getInfo: ComplaintInfoService.getComplaintInfo,
                        validateInfo: ComplaintInfoService.validateComplaintInfo
                    }, {
                        name: "TASK",
                        configName: "tasks",
                        getInfo: TaskInfoService.getTaskInfo,
                        validateInfo: TaskInfoService.validateTaskInfo
                    }, {
                        name: "ADHOC",
                        configName: "tasks",
                        getInfo: TaskInfoService.getTaskInfo,
                        validateInfo: TaskInfoService.validateTaskInfo
                    }, {
                        name: "DOC_REPO",
                        configName: "document-repository",
                        getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
                    }, {
                        name: "MY_DOC_REPO",
                        configName: "my-documents",
                        getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
                    } ];

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    $scope.gridOptions = {
                        enableColumnResizing: true,
                        columnDefs: []
                    };

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: module.configName,
                        componentId: "main",
                        retrieveObjectInfo: module.getInfo,
                        validateObjectInfo: module.validateInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved: function(componentConfig) {
                            onConfigRetrieved(componentConfig);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {

                        /**
                         * Complaints and CaseFiles return their references in a different way.
                         */
                        if (module.name == ObjectService.ObjectTypes.COMPLAINT) {
                            var references = [];
                            _.forEach(objectInfo.childObjects, function(childObject) {
                                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                                    references.push(childObject);
                                }
                            });
                            gridHelper.setWidgetsGridData(references);
                        } else {
                            gridHelper.setWidgetsGridData(objectInfo.references);
                        }

                    };

                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "references";
                        });
                        gridHelper.setColumnDefs(widgetInfo);
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                    };

                } ]);