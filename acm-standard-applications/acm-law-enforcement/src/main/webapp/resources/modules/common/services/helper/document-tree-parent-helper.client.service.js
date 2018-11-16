"use strict";

/**
 * @ngdoc service
 * @name services:Helper.DocumentListTreeHelper
 *
 * @description
 * Helper.DocumentListTreeHelper provide common help for 'doc-tree' directive. Uses the HelperObjectBrowserService Component.
 * Helper.DocumentListTreeHelper uses all the method needed for showing a functional doc-tree component.
 * Every function is overridable by the controller's scope if it's defined otherwise will just take the default helper function.
 *
 * DocumentTreeComponent takes the same arguments as Helper.ObjectBrowserService component plus have additional:
 * - enableEmailButton for enabling the email button can also be enabled after initialization of the DocumentTreeComponent with enableSendEmailButton function.
 * - enableNewTaskButton for enabling the new task button can also be enabled after initialization of the DocumentTreeComponent with enableNewTaskButton function.
 * - Required commit function at the end after everything is defined from the scope and can send request for getting the object information and the config.
 *
 */

angular.module('common')
        .factory(
                'Helper.DocumentListTreeHelper',
                [
                        '$q',
                        'UtilService',
                        'Helper.ObjectBrowserService',
                        'Object.LookupService',
                        'Admin.CMTemplatesService',
                        'Config.LocaleService',
                        'ObjectService',
                        'DocTreeService',
                        'DocTreeExt.WebDAV',
                        'DocTreeExt.Checkin',
                        'DocTreeExt.Email',
                        'Object.ModelService',
                        'ModalDialogService',
                        'Admin.EmailSenderConfigurationService',
                        'PermissionsService',
                        function($q, Util, HelperObjectBrowserService, ObjectLookupService, CorrespondenceService, LocaleService, ObjectService, DocTreeService, DocTreeExtWebDAV, DocTreeExtCheckin, DocTreeExtEmail, ObjectModelService, ModalDialogService, EmailSenderConfigurationService,
                                PermissionsService) {

                            var Service = {

                                DocumentTreeComponent: function(arg) {
                                    var that = this;
                                    that.arg = arg;
                                    that.scope = arg.scope;
                                    that.scope.objectType = arg.objectType || that.scope.objectType;
                                    that.scope.enableEmailButton = arg.enableEmailButton || that.scope.enableEmailButton;
                                    that.scope.enableNewTaskButton = arg.enableNewTaskButton || that.scope.enableNewTaskButton;

                                    that.scope.afterObjectInfo = that.scope.afterObjectInfo || function() {
                                        if (that.scope.objectType === ObjectService.ObjectTypes.COSTSHEET) {
                                            that.scope.parentObject = that.scope.objectInfo.costsheetNumber;
                                            that.scope.objectInfo.number = that.scope.objectInfo.costsheetNumber;
                                        } else if (that.scope.objectType === ObjectService.ObjectTypes.CASE_FILE) {
                                            that.scope.objectInfo.number = that.scope.objectInfo.caseNumber;
                                            that.scope.parentObject = that.scope.objectInfo.caseNumber;
                                        } else if (that.scope.objectType === ObjectService.ObjectTypes.COMPLAINT) {
                                            that.scope.parentObject = that.scope.objectInfo.complaintNumber;
                                            that.scope.objectInfo.number = that.scope.objectInfo.complaintNumber;
                                            that.scope.objectInfo.title = that.scope.objectInfo.complaintTitle;
                                        }
                                    };

                                    var promiseFormTypes = ObjectLookupService.getFormTypes(that.scope.objectType);
                                    var promiseFileTypes = ObjectLookupService.getFileTypes();
                                    var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(that.scope.objectType);
                                    var promiseFileLanguages = LocaleService.getSettings();

                                    that.scope.onConfigRetrieved = arg.onConfigRetrieved || function(config) {
                                        that.scope.treeConfig = config.docTree;
                                        that.scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

                                        $q.all([ promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages ]).then(function(data) {
                                            that.scope.treeConfig.formTypes = data[0];
                                            that.scope.treeConfig.fileTypes = data[1];
                                            that.scope.treeConfig.correspondenceForms = data[2];
                                            that.scope.treeConfig.fileLanguages = data[3];
                                            if (!Util.isEmpty(that.scope.treeControl)) {
                                                that.scope.treeControl.refreshTree();
                                            }
                                        });
                                    };

                                    // that.scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
                                    that.scope.onObjectInfoRetrieved = arg.onObjectInfoRetrieved || function(objectInfo) {
                                        that.scope.objectInfo = objectInfo;
                                        that.scope.objectId = objectInfo.id;
                                        that.scope.assignee = ObjectModelService.getAssignee(objectInfo);
                                        if (that.scope.afterObjectInfo) {
                                            that.scope.afterObjectInfo();
                                        }
                                    };

                                    that.scope.uploadForm = function(type, folderId, onCloseForm) {
                                        var fileTypes = Util.goodArray(that.scope.treeConfig.fileTypes);
                                        fileTypes = fileTypes.concat(Util.goodArray(that.scope.treeConfig.formTypes));
                                        return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, that.scope.objectInfo, fileTypes);
                                    };

                                    that.scope.onInitTree = function(treeControl) {

                                        that.scope.treeControl = treeControl;
                                        DocTreeExtCheckin.handleCheckout(treeControl, that.scope);
                                        DocTreeExtCheckin.handleCheckin(treeControl, that.scope);
                                        DocTreeExtCheckin.handleCancelEditing(treeControl, that.scope);
                                        DocTreeExtWebDAV.handleEditWithWebDAV(treeControl, that.scope);
                                        //
                                        that.scope.treeControl.addCommandHandler({
                                            name: "declare",
                                            onAllowCmd: function(nodes) {
                                                return that.scope.getActionPermission('declareAsRecords', that.scope.objectInfo, that.scope.objectType);
                                            }
                                        });

                                        that.scope.treeControl.addCommandHandler({
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

                                                return that.scope.getActionPermission(action, node.data, objectType);
                                            }
                                        });

                                    };

                                    that.scope.getActionPermission = function(action, object, objectType) {
                                        return PermissionsService.getActionPermission(action, object, {
                                            objectType: objectType
                                        }).then(function success(enabled) {
                                            return enabled ? 'enable' : 'disable';
                                        }, function error() {
                                            $log.error('Can\'t get permission info for action ' + action + '. The menu item will be disabled.');
                                            return 'disable';
                                        });
                                    };

                                    that.scope.onClickRefresh = function() {
                                        that.scope.treeControl.refreshTree();
                                    };

                                    that.scope.selectedDocuments = []; //todo: check if needed

                                    that.scope.onCheckNode = arg.onCheckNode || function(node) {
                                        if (!node.folder) {
                                            var idx = _.findIndex(that.scope.selectedDocuments, function(d) {
                                                return d.data.objectId == node.data.objectId;
                                            });

                                            if (idx > -1) {
                                                that.scope.selectedDocuments.splice(idx, 1);
                                            } else {
                                                that.scope.selectedDocuments.push(node);
                                            }
                                        }
                                    };

                                    that.scope.onToggleAllNodesChecked = arg.onToggleAllNodesChecked || function(nodes) {
                                        that.scope.selectedDocuments = _.filter(nodes, function(node) {
                                            return !node.folder;
                                        });
                                    };

                                    that.scope.removeSearchFilter = arg.removeSearchFilter || function() {
                                        that.scope.searchFilter = null;
                                    };

                                    that.scope.$bus.subscribe('docTreeNodeChecked', that.scope.onCheckNode);

                                    that.scope.$bus.subscribe('toggleAllNodesChecked', that.scope.onToggleAllNodesChecked);

                                    that.scope.$bus.subscribe('removeSearchFilter', that.scope.removeSearchFilter);

                                    that.scope.onFilter = arg.onFilter || function() {
                                        that.scope.$bus.publish('onFilterDocTree', {
                                            filter: that.scope.filter
                                        });
                                    };

                                    that.scope.onSearch = arg.onSearch || function() {
                                        that.scope.$bus.publish('onSearchDocTree', {
                                            searchFilter: that.scope.searchFilter
                                        });
                                    };

                                    if (that.scope.enableEmailButton) {
                                        that.enableSendEmailButton();
                                    }

                                    if (that.scope.enableNewTaskButton) {
                                        that.enableNewTaskButton();
                                    }

                                }
                            };

                            Service.DocumentTreeComponent.prototype.enableSendEmailButton = function() {
                                var that = this;
                                EmailSenderConfigurationService.getEmailSenderConfiguration().then(function(emailData) {
                                    that.scope.enableEmailButton = emailData.data.allowDocuments;
                                });
                                this.scope.sendEmail = that.arg.sendEmail || function() {
                                    var nodes = that.scope.treeControl.getSelectedNodes();
                                    var DocTree = that.scope.treeControl.getDocTreeObject();
                                    DocTreeExtEmail.openModal(DocTree, nodes);
                                }
                            };

                            Service.DocumentTreeComponent.prototype.enableNewTaskButton = function(arg) {
                                var that = this;
                                that.scope.enableNewTaskButton = true;
                                if (!arg) {
                                    arg = {};
                                }
                                this.scope.createNewTask = that.arg.createNewTask || function() {
                                    var modalMetadata = {
                                        moduleName: 'tasks',
                                        templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                                        controllerName: 'Tasks.NewTaskController',
                                        params: {
                                            parentType: arg.parentType || that.scope.objectType,
                                            parentObject: arg.parentObject || that.scope.parentObject,
                                            parentTitle: arg.parentTitle || that.scope.objectInfo.title,
                                            parentId: arg.parentId || that.scope.objectId,
                                            documentsToReview: that.scope.selectedDocuments,
                                            taskType: 'REVIEW_DOCUMENT'
                                        }
                                    };
                                    ModalDialogService.showModal(modalMetadata);
                                };

                            };

                            Service.DocumentTreeComponent.prototype.commit = function() {
                                var that = this;
                                this.arg.onConfigRetrieved = this.arg.onConfigRetrieved || function(componentConfig) {
                                    return that.scope.onConfigRetrieved(componentConfig);
                                };
                                this.arg.onObjectInfoRetrieved = this.arg.onObjectInfoRetrieved || function(objectInfo) {
                                    return that.scope.onObjectInfoRetrieved(objectInfo);
                                };
                                this.scope.componentHelper = new HelperObjectBrowserService.Component(this.arg);
                            };

                            return Service;

                        } ]);