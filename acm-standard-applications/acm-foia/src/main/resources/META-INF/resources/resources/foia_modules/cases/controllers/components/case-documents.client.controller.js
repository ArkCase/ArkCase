'use strict';

angular.module('cases').controller(
        'Cases.DocumentsController',
        [
                '$scope',
                '$state',
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
                'Case.InfoService',
                'DocTreeService',
                'Helper.ObjectBrowserService',
                'Authentication',
                'PermissionsService',
                'Object.ModelService',
                'DocTreeExt.WebDAV',
                'DocTreeExt.Checkin',
                'Admin.CMTemplatesService',
                'DocTreeExt.Email',
                'EcmService',
                'MessageService',
                'Admin.EmailSenderConfigurationService',
                'MultiCorrespondence.Service',
                'ModalDialogService',
                'Websockets.MessageHandler',
                'Case.FolderStructureService',
                function($scope, $state, $stateParams, $modal, $q, $timeout, $translate, Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService, HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService, DocTreeExtWebDAV,
                         DocTreeExtCheckin, CorrespondenceService, DocTreeExtEmail, Ecm, MessageService, EmailSenderConfigurationService, MultiCorrespondenceService, ModalDialogService, messageHandler, CaseFolderStructureService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.user = userInfo.userId;
                        return userInfo;
                    });

                    EmailSenderConfigurationService.isEmailSenderAllowDocuments().then(function(emailData) {
                        $scope.sendEmailEnabled = emailData.data;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "documents",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE);
                    var promiseFileTypes = ObjectLookupService.getFileTypes();
                    var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.CASE_FILE);
                    var promiseFileLanguages = LocaleService.getSettings();
                    var promiseFolderStructure = CaseFolderStructureService.getFolderStructure();

                    var onConfigRetrieved = function(config) {
                        $scope.treeConfig = config.docTree;
                        $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

                        $q.all([ promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages, promiseFolderStructure ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = data[1];
                            $scope.treeConfig.correspondenceForms = data[2];
                            $scope.treeConfig.fileLanguages = data[3];
                            $scope.treeConfig.folderStructure = data[4];
                            if (!Util.isEmpty($scope.treeControl)) {
                                $scope.treeControl.refreshTree();
                            }
                        });
                    };

                    $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
                    $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.objectId = objectInfo.id;
                        $scope.assignee = ObjectModelService.getAssignee(objectInfo);

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

                        //$scope.treeControl.addCommandHandler({
                        //    name: "sample"
                        //    , onAllowCmd: function(nodes) {
                        //        return "disable";
                        //    }
                        //    , onPreCmd: function(nodes, args) {
                        //        console.log("onPreCmd of sample command");
                        //        return false;
                        //    }
                        //    , onPostCmd: function(nodes, args) {
                        //        console.log("onPostCmd of sample command");
                        //    }
                        //    , execute: function(nodes, args) {
                        //        console.log("Possible to add onPreCmd code here");
                        //        var promise = this.prevHandler.execute(nodes, args);
                        //        $q.when(promise).then(function () {
                        //            console.log("Possible to add onPostCmd code here, too");
                        //        });
                        //        console.log("Possible to add onPostCmd code here");
                        //    }
                        //});
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
                                parentType: ObjectService.ObjectTypes.CASE_FILE,
                                parentObject: $scope.objectInfo.caseNumber,
                                parentTitle: $scope.objectInfo.title,
                                parentId: $scope.objectInfo.id,
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

                    $scope.$bus.subscribe('docTreeNodeChecked', function(node) {
                        $scope.onCheckNode(node);
                    });

                    $scope.$bus.subscribe('toggleAllNodesChecked', function(nodes) {
                        $scope.onToggleAllNodesChecked(nodes);
                    });

                    function handleOpen(treeControl) {

                        treeControl.addCommandHandler({
                            name: "open",
                            execute: function(nodes) {
                                var node = nodes[0];

                                $state.go('request-info', {
                                    id: $stateParams.id,
                                    fileId: node.data.objectId
                                }, true);
                            }
                        });
                    }

                    $scope.$bus.subscribe('multi-correspondence-requested', function(payload) {
                        var requestData = payload;
                        var names = [ requestData.args.label ];
                        var template = requestData.args.templateType;

                        var modalInstance = $modal.open({
                            animation: false,
                            templateUrl: 'modules/common/views/multi-correspondence.modal.client.view.html',
                            controller: 'Common.MultiCorrespondenceModalController',
                            size: 'lg',
                            backdrop: 'static'
                        });

                        modalInstance.result.then(function(modalResult) {
                            MultiCorrespondenceService.createMultiTemplateCorrespondence(requestData, names, template, modalResult.selectedTemplates, modalResult.multiCorrespondenceDocumentName);
                        });
                    });

                }]);
