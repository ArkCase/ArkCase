'use strict';

angular.module('complaints').controller(
        'Complaints.DocumentsController',
        [
                '$scope',
                '$stateParams',
                '$modal',
                '$q',
                '$timeout',
                '$translate',
                'UtilService',
                'Config.LocaleService',
                'ConfigService',
                'ObjectService',
                'Object.LookupService',
                'Complaint.InfoService',
                'Helper.ObjectBrowserService',
                'DocTreeService',
                'Authentication',
                'PermissionsService',
                'Object.ModelService',
                'DocTreeExt.WebDAV',
                'DocTreeExt.Checkin',
                'Admin.CMTemplatesService',
                'DocTreeExt.Email',
                'ModalDialogService',
                'Admin.EmailSenderConfigurationService',
                function($scope, $stateParams, $modal, $q, $timeout, $translate, Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService, HelperObjectBrowserService, DocTreeService, Authentication, PermissionsService, ObjectModelService, DocTreeExtWebDAV,
                        DocTreeExtCheckin, CorrespondenceService, DocTreeExtEmail, ModalDialogService, EmailSenderConfigurationService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.user = userInfo.userId;
                        return userInfo;
                    });

                    EmailSenderConfigurationService.getEmailSenderConfiguration().then(function(emailData) {
                        $scope.sendEmailEnabled = emailData.data.allowDocuments;
                    });

                    $scope.uploadForm = function(type, folderId, onCloseForm) {
                        var fileTypes = Util.goodArray($scope.treeConfig.fileTypes);
                        fileTypes = fileTypes.concat(Util.goodArray($scope.treeConfig.formTypes));
                        return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, fileTypes);
                    };

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "documents",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.COMPLAINT);
                    var promiseFileTypes = ObjectLookupService.getFileTypes();
                    var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.COMPLAINT);
                    var promiseFileLanguages = LocaleService.getSettings();

                    var onConfigRetrieved = function(config) {
                        $scope.treeConfig = config.docTree;
                        $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

                        $q.all([ promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = data[1];
                            $scope.treeConfig.correspondenceForms = data[2];
                            $scope.treeConfig.fileLanguages = data[3];
                            $scope.treeControl.refreshTree();
                        });
                    };

                    $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
                    $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
                    var onObjectInfoRetrieved = function(objectInfo) {
                        objectInfo.number = objectInfo.complaintNumber;
                        objectInfo.title = objectInfo.complaintTitle;
                        $scope.objectInfo = objectInfo;
                        $scope.objectId = objectInfo.complaintId;
                        $scope.assignee = ObjectModelService.getAssignee(objectInfo);
                    };

                    $scope.onInitTree = function(treeControl) {
                        $scope.treeControl = treeControl;
                        DocTreeExtCheckin.handleCheckout(treeControl, $scope);
                        DocTreeExtCheckin.handleCheckin(treeControl, $scope);
                        DocTreeExtCheckin.handleCancelEditing(treeControl, $scope);
                        DocTreeExtWebDAV.handleEditWithWebDAV(treeControl, $scope);

                        $scope.treeControl.addCommandHandler({
                            name: "declare",
                            onAllowCmd: function(nodes) {
                                return $scope.getActionPermission('declareAsRecords', $scope.objectInfo, $scope.objectType);
                            }
                        });

                        $scope.treeControl.addCommandHandler({
                            name: "rename",
                            onAllowCmd: function(nodes) {
                                // There are multiple node selected. Rename is not possible for multiple nodes
                                if (Util.isArrayEmpty(nodes) || nodes.length > 1) {
                                    return 'disable';
                                }

                                var node = nodes[0];
                                var objectType = !Util.isEmpty(node.data) && !Util.isEmpty(node.data.objectType) ? node.data.objectType.toUpperCase() : '';
                                var action = '';

                                switch (objectType) {
                                case 'FILE':
                                    action = 'renameFile';
                                    break;
                                case 'FOLDER':
                                    action = 'renameFolder';
                                    break;
                                default:
                                    return 'disable';
                                }

                                return $scope.getActionPermission(action, node.data, objectType);
                            }
                        });
                    };

                    $scope.getActionPermission = function(action, object, objectType) {
                        return PermissionsService.getActionPermission(action, object, {
                            objectType: objectType
                        }).then(function success(enabled) {
                            return enabled ? 'enable' : 'disable';
                        }, function error() {
                            $log.error('Can\'t get permission info for action ' + action + '. The menu item will be disabled.');
                            return 'disable';
                        });
                    };

                    $scope.onClickRefresh = function() {
                        $scope.treeControl.refreshTree();
                    };

                    $scope.sendEmail = function() {
                        var nodes = $scope.treeControl.getSelectedNodes();
                        var DocTree = $scope.treeControl.getDocTreeObject();
                        DocTreeExtEmail.openModal(DocTree, nodes);
                    };

                    $scope.createNewTask = function() {
                        var modalMetadata = {
                            moduleName: 'tasks',
                            templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                            controllerName: 'Tasks.NewTaskController',
                            params: {
                                parentType: ObjectService.ObjectTypes.COMPLAINT,
                                parentObject: $scope.objectInfo.complaintNumber,
                                parentId: $scope.objectInfo.complaintId,
                                parentTitle: $scope.objectInfo.title,
                                documentsToReview: $scope.selectedDocuments,
                                taskType: 'REVIEW_DOCUMENT'
                            }
                        };
                        ModalDialogService.showModal(modalMetadata);
                    };

                    $scope.selectedDocuments = [];

                    $scope.onCheckNode = function(node) {
                        if (!node.folder) {
                            var idx = _.findIndex($scope.selectedDocuments, function(d) {
                                return d.data.objectId == node.data.objectId;
                            });

                            if (idx > -1) {
                                $scope.selectedDocuments.splice(idx, 1);
                            } else {
                                $scope.selectedDocuments.push(node);
                            }
                        }
                    };

                    $scope.onToggleAllNodesChecked = function(nodes) {
                        $scope.selectedDocuments = _.filter(nodes, function(node) {
                            return !node.folder;
                        });
                    };

                    $scope.$bus.subscribe('docTreeNodeChecked', function(node) {
                        $scope.onCheckNode(node);
                    });

                    $scope.$bus.subscribe('toggleAllNodesChecked', function(nodes) {
                        $scope.onToggleAllNodesChecked(nodes);
                    });

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
                } ]);
