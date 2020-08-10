'use strict';

angular.module('request-info').controller(
        'RequestInfo.DocumentsController',
        [
                '$scope',
                '$state',
                '$stateParams',
                '$modal',
                '$q',
                '$timeout',
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
                '$translate',
                'Request.InfoService',
                'Admin.EmailSenderConfigurationService',
                'DocTreeExt.Email',
                'EcmService',
                 function ($scope, $state, $stateParams, $modal, $q, $timeout, Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService, HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService, DocTreeExtWebDAV, DocTreeExtCheckin,
                             CorrespondenceService, $translate, RequestInfoService, EmailSenderConfigurationService, DocTreeExtEmail, EcmService) {

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
                        moduleId: "request-info",
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

                    var onConfigRetrieved = function(config) {
                        $scope.treeConfig = config.docTree;
                        $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

                        $q.all([ promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = data[1];
                            $scope.treeConfig.correspondenceForms = data[2];
                            $scope.treeConfig.fileLanguages = data[3];
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
                        handleChangeVersion();
                    };

                    $scope.onClickRefresh = function() {
                        $scope.treeControl.refreshTree();
                    };

                    $scope.sendEmail = function() {
                        var nodes = $scope.treeControl.getSelectedNodes();
                        var DocTree = $scope.treeControl.getDocTreeObject();
                        DocTreeExtEmail.openModal(DocTree, nodes);
                    };

                    RequestInfoService.registerFileUpdateHandler($scope, $scope.objectType, $scope.objectId, $scope.onClickRefresh);

                    function handleOpen(treeControl) {
                        treeControl.addCommandHandler({
                            name: "open",
                            execute: function(nodes) {
                                var node = nodes[0];
                                var files = [];
                                if (Util.isArray(nodes)) {
                                    angular.forEach(nodes, function (item, index) {
                                        if(item.data.link) {
                                            var params = {};
                                            params.fileId = node.data.objectId;
                                            EcmService.getLinkTargetEcmFile(params).then(function (ecmFileInfo) {
                                                files.push({
                                                    id: ecmFileInfo.parentObjectId,
                                                    fileId: ecmFileInfo.originalFileId,
                                                    removeOlderFileVersionFromSnowboundTabs: false
                                                });
                                                // open linked document in another tab if it comes from different CASE_FILE in order to reload the
                                                // the right doc-tree folder structure
                                                if ($scope.objectId != ecmFileInfo.parentObjectId) {
                                                    var parentObjectId = ecmFileInfo.parentObjectId;
                                                    var originalFileId = ecmFileInfo.originalFileId;

                                                    $state.go('request-info', {
                                                        id: parentObjectId,
                                                        fileId: originalFileId
                                                    }, true);
                                                } else {
                                                    $scope.$bus.publish('update-viewer-open-documents', files);

                                                    $scope.$bus.publish('reload-exemption-code-grid', {
                                                        id: files[0].id,
                                                        fileId: files[0].fileId
                                                    });
                                                }
                                            });
                                        }
                                        else {
                                            files.push({
                                                id: $stateParams.id,
                                                fileId: item.data.objectId,
                                                removeOlderFileVersionFromSnowboundTabs: false
                                            });
                                            $scope.$bus.publish('update-viewer-open-documents', files);

                                            $scope.$bus.publish('reload-exemption-code-grid', {
                                                id: files[0].id,
                                                fileId: files[0].fileId
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }

                    /**
                     * @ngdoc method
                     * @name handleChangeVersion
                     * @methodOf documents.controller:RequestInfo.DocumentsController
                     *
                     * @description
                     * Override 'DocTree.onChangeVersion' method. Before invoking the logic of this method, publish event just to know
                     * that we are changing the version manually
                     *
                     */
                    function handleChangeVersion() {
                        var DocTree = $scope.treeControl.getDocTreeObject();
                        DocTree.onChangeVersion = function(event) {
                            $scope.$bus.publish('update-viewer-open-documents-after-change-version');
                            var node = DocTree.tree.getActiveNode();
                            if (node) {
                                var parent = node.parent;
                                if (parent) {
                                    var cacheKey = DocTree.getCacheKeyByNode(parent);

                                    var verSelected = DocTree.Ui.getSelectValue($(this));
                                    var verCurrent = Util.goodValue(node.data.version, "0");
                                    if (verSelected != verCurrent) {
                                        if (verSelected < verCurrent) {
                                            DocTree.Ui.dlgConfirm($translate.instant("common.directive.docTree.confirmVersion"), function(
                                                result) {
                                                if (result) {
                                                    DocTree.Op.setActiveVersion(node, verSelected);
                                                } else {
                                                    node.renderTitle();
                                                }
                                            });
                                        } else {
                                            DocTree.Op.setActiveVersion(node, verSelected);
                                        }
                                    }
                                } //end if (parent)
                            }
                        }
                    }

                }

        ]);
