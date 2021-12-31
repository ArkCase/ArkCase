'use strict';

angular.module('tasks').controller(
        'Tasks.AttachmentsController',
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
                'DocTreeExt.Email',
                'Admin.CMTemplatesService',
                function($scope, $stateParams, $q, $modal, $translate, Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService, Authentication, DocTreeService, PermissionsService, DocTreeExtWebDAV, DocTreeExtCheckin, DocTreeExtEmail, CorrespondenceService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.user = userInfo.userId;
                        return userInfo;
                    });


                    var componentHelper = new HelperObjectBrowserService.Component({
                        moduleId: "tasks",
                        componentId: "attachments",
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
                    var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.TASK);
                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        $scope.treeConfig = config.docTree;

                        $q.all([ promiseFormTypes, promiseFileTypes, promiseFileLanguages, promiseCorrespondenceForms ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = data[1];
                            $scope.treeConfig.fileLanguages = data[2];
                            $scope.treeConfig.correspondenceForms = data[3];
                            if (!Util.isEmpty($scope.treeControl)) {
                                $scope.treeControl.refreshTree();
                            }
                        });
                    };

                    $scope.objectType = ObjectService.ObjectTypes.TASK;
                    $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.objectId = objectInfo.taskId;
                        PermissionsService.getActionPermission('editAttachments', objectInfo, {
                            objectType: ObjectService.ObjectTypes.TASK
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

                        treeControl.addCommandHandler({
                            name: "remove",
                            onAllowCmd: function(nodes) {
                                var len = 0;
                                if (Util.isArray(nodes[0].children)) {
                                    len = nodes[0].children.length;
                                }
                                if (0 != len) {
                                    return 'disable';
                                } else {
                                    return $scope.isReadOnly ? 'disable' : '';
                                }
                            }
                        });
                    };

                    $scope.onClickRefresh = function() {
                        $scope.treeControl.refreshTree();
                    };


                    $scope.correspondenceForms = {};

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