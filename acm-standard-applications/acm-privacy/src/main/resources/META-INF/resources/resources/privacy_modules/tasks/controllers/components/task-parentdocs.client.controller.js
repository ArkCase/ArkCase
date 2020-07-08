'use strict';

angular.module('tasks').controller(
        'Tasks.ParentDocsController',
        [
                '$scope',
                '$state',
                '$stateParams',
                '$q',
                '$modal',
                '$translate',
                'UtilService',
                'Config.LocaleService',
                'ConfigService',
                'ObjectService',
                'Object.LookupService',
                'Task.InfoService',
                'Helper.ObjectBrowserService',
                'Authentication',
                'DocTreeService',
                'PermissionsService',
                'DocTreeExt.WebDAV',
                'DocTreeExt.Checkin',
                'Case.InfoService',
                'Complaint.InfoService',
                'CostTracking.InfoService',
                'TimeTracking.InfoService',
                'Admin.CMTemplatesService',
                'DocTreeExt.Email',
                function($scope, $state, $stateParams, $q, $modal, $translate, Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService, Authentication, DocTreeService, PermissionsService, DocTreeExtWebDAV, DocTreeExtCheckin,
                        CaseInfoService, ComplaintInfoService, CostTrackingInfoService, TimeTrackingInfoService, CorrespondenceService, DocTreeExtEmail) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.user = userInfo.userId;
                        return userInfo;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        moduleId: "tasks",
                        componentId: "parentdocs",
                        scope: $scope,
                        stateParams: $stateParams,
                        retrieveObjectInfo: TaskInfoService.getTaskInfo,
                        validateObjectInfo: TaskInfoService.validateTaskInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        $scope.treeConfig = config.docTree;
                    };

                    $scope.objectType = ObjectService.ObjectTypes.TASK;
                    $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.objectId = objectInfo.taskId;
                        $scope.parentObjectId = objectInfo.parentObjectId;
                        $scope.parentObjectType = objectInfo.parentObjectType;

                        var promiseFormTypes = ObjectLookupService.getFormTypes($scope.parentObjectType);
                        var promiseFileTypes = ObjectLookupService.getFileTypes();
                        var promiseFileLanguages = LocaleService.getSettings();
                        var promiseCorrespondenceForms;

                        switch ($scope.parentObjectType) {
                        case ObjectService.ObjectTypes.COMPLAINT:
                            ComplaintInfoService.getComplaintInfo($scope.objectInfo.parentObjectId).then(function(complaintInfo) {
                                $scope.parentInfo = complaintInfo;
                            });
                            promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.COMPLAINT);
                            $scope.treeConfig.email.emailSubject = "Complaint $complaintNumber";
                            break;
                        case ObjectService.ObjectTypes.CASE_FILE:
                            CaseInfoService.getCaseInfo($scope.objectInfo.parentObjectId).then(function(caseInfo) {
                                $scope.parentInfo = caseInfo;
                            });
                            promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.CASE_FILE);
                            $scope.treeConfig.email.emailSubject = "Request $caseNumber";
                            break;
                        case ObjectService.ObjectTypes.COSTSHEET:
                            CostTrackingInfoService.getCostsheetInfo($scope.objectInfo.parentObjectId).then(function(costsheetInfo) {
                                $scope.parentInfo = costsheetInfo;
                            });
                            promiseCorrespondenceForms = {};
                            break;
                        case ObjectService.ObjectTypes.TIMESHEET:
                            TimeTrackingInfoService.getTimesheetInfo($scope.objectInfo.parentObjectId).then(function(timesheetInfo) {
                                $scope.parentInfo = timesheetInfo;
                            });
                            promiseCorrespondenceForms = {};
                            break;
                        default:
                            $scope.parentInfo = null;
                            promiseCorrespondenceForms = {};
                        }

                        $q.all([ promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = [];
                            for (var i = 0; i < data[1].length; i++) {
                                $scope.treeConfig.fileTypes.push({
                                    "type": data[1][i].key,
                                    "label": $translate.instant(data[1][i].value)
                                });
                            }
                            $scope.treeConfig.correspondenceForms = data[2];
                            $scope.treeConfig.fileLanguages = data[3];
                            $scope.treeControl.refreshTree();
                        });

                        $scope.isreadOnly = false;
                        // Using the parentInfo and parentObjectType to enforce the editing permission
                        PermissionsService.getActionPermission('editAttachments', $scope.parentInfo, {
                            objectType: $scope.parentObjectType
                        }).then(function(result) {
                            $scope.isReadOnly = !result;
                        });
                    };

                    $scope.uploadForm = function(type, folderId, onCloseForm) {
                        var fileTypes = Util.goodArray($scope.treeConfig.fileTypes);
                        fileTypes = fileTypes.concat(Util.goodArray($scope.treeConfig.formTypes));
                        return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, fileTypes);
                    };

                    $scope.onInitTree = function(treeControl) {
                        $scope.treeControl = treeControl;
                        DocTreeExtCheckin.handleCheckout(treeControl, $scope);
                        DocTreeExtCheckin.handleCheckin(treeControl, $scope);
                        DocTreeExtCheckin.handleCancelEditing(treeControl, $scope);
                        DocTreeExtWebDAV.handleEditWithWebDAV(treeControl, $scope);

                        //overwrite execute functionality for open command
                        handleOpen(treeControl, $scope);
                    };

                    $scope.onClickRefresh = function() {
                        $scope.treeControl.refreshTree();
                    };

                    $scope.sendEmail = function() {
                        var nodes = $scope.treeControl.getSelectedNodes();
                        var DocTree = $scope.treeControl.getDocTreeObject();
                        DocTreeExtEmail.openModal(DocTree, nodes);
                    };

                    $scope.onFilter = function() {
                        $scope.$bus.publish('onFilterDocTree', {
                            filter: $scope.filter
                        });
                    };

                    $scope.onSearch = function() {
                        $scope.$bus.publish('onSearchDocTree', {
                            searchFilter: $scope.searchFilter
                        });
                    };

                    $scope.$bus.subscribe('removeSearchFilter', function() {
                        $scope.searchFilter = null;
                    });

                    function handleOpen(treeControl) {

                        treeControl.addCommandHandler({
                            name: "open",
                            execute: function(nodes) {
                                var node = nodes[0];

                                $state.go('request-info', {
                                    id: $scope.parentObjectId,
                                    fileId: node.data.objectId
                                }, true);
                            }
                        });
                    }
                } ]);
