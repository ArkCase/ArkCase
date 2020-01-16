'use strict';

angular.module('tasks').controller(
        'Tasks.DocumentsUnderReview',
        [
                '$scope',
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
                'Admin.CMTemplatesService',
                'DocTreeExt.Email',
                'Admin.EmailSenderConfigurationService',
                'LookupService',
                function($scope, $stateParams, $q, $modal, $translate, Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService, Authentication, DocTreeService, PermissionsService, DocTreeExtWebDAV, DocTreeExtCheckin, CorrespondenceService,
                        DocTreeExtEmail, EmailSenderConfigurationService, LookupService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.user = userInfo.userId;
                        return userInfo;
                    });

                    EmailSenderConfigurationService.isEmailSenderAllowDocuments().then(function(emailData) {
                        $scope.sendEmailEnabled = emailData.data;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        moduleId: "tasks",
                        componentId: "documentsunderreview",
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

                    var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.TASK);
                    var promiseFileTypes = ObjectLookupService.getFileTypes();
                    var promiseFileLanguages = LocaleService.getSettings();
                    var promiseUsers = LookupService.getUserFullNames();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        $scope.treeConfig = config.docTree;

                        $q.all([ promiseFormTypes, promiseFileTypes, promiseFileLanguages ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = data[1];
                            $scope.treeConfig.fileLanguages = data[2];
                        });
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.objectId = objectInfo.taskId;
                        $scope.parentObjectId = objectInfo.parentObjectId;
                        $scope.parentObjectType = objectInfo.parentObjectType;
                        var documentsUnderReview = [];
                        if (Util.isArray($scope.objectInfo.documentsToReview) && !Util.isArrayEmpty($scope.objectInfo.documentsToReview)) {
                            documentsUnderReview = $scope.objectInfo.documentsToReview;
                        } else if (!Util.isArray($scope.objectInfo.documentsToReview) && !Util.isEmpty($scope.objectInfo.documentsToReview)) {
                            documentsUnderReview.push($scope.objectInfo.documentsToReview);
                        }
                        $scope.fqFilter = "";
                        angular.forEach(documentsUnderReview, function(document, index) {
                            var operation = '';
                            if (index !== documentsUnderReview.length - 1) {
                                operation = ' OR ';
                            }
                            $scope.fqFilter += 'object_id_s:' + document.fileId + operation;
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
                        $scope.config.docTree.nodeCacheKeyPrefix = $scope.config.docTree.nodeCacheKeyPrefix ? $scope.config.docTree.nodeCacheKeyPrefix + $scope.objectId : "" + $scope.objectId;
                        $scope.treeControl.getDocTreeObject().treeConfig.fqFilter = $scope.fqFilter;
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
                            searchFilter: $scope.fqFilter + (!Util.isEmpty($scope.searchFilter) ? ' AND name_partial: ' + $scope.searchFilter : '')
                        });
                    };

                    $scope.$bus.subscribe('removeSearchFilter', function() {
                        $scope.searchFilter = null;
                    });
                } ]);
