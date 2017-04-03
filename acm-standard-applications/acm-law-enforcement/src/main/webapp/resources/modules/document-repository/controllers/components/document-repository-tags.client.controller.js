'use strict';

angular.module('document-repository').controller('DocumentRepository.TagsController', ['$scope', '$q', '$stateParams'
    , '$translate', 'UtilService', 'ObjectService', 'DocumentRepository.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.TagsService', '$modal', 'MessageService'
    , function ($scope, $q, $stateParams, $translate
        , Util, ObjectService, DocumentRepositoryInfoService
        , HelperUiGridService, HelperObjectBrowserService, ObjectTagsService, $modal, messageService) {

        $scope.tags = [];

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , componentId: "tags"
            , retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        $scope.addNew = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/document-repository/views/components/document-repository-tags-modal.client.view.html',
                controller: 'DocumentRepository.TagsModalController',
                size: 'lg'
            });

            modalInstance.result.then(function (tags) {
                _.forEach(tags, function (tag) {
                    tag.object_id_s = tag.id.split("-")[0];
                    tag.tags_s = tag.title_parseable;
                    if (tag.id) {
                        if (tag.object_id_s) {
                            var tagsFound = _.filter($scope.tags, function (tagAss) {
                                return tagAss.id == tag.object_id_s;
                            });
                            if (tagsFound.length == 0) {
                                ObjectTagsService.associateTag(componentHelper.currentObjectId
                                    , ObjectService.ObjectTypes.DOC_REPO, $scope.objectParentTitle, tag.object_id_s)
                                    .then(
                                        function (returnedTag) {
                                            var tagToAdd = angular.copy(returnedTag);
                                            tagToAdd.tagName = tag.tags_s;
                                            tagToAdd.id = returnedTag.tagId;
                                            $scope.tags.push(tagToAdd);
                                            $scope.gridOptions.data = $scope.tags;
                                            $scope.gridOptions.totalItems = $scope.tags.length;
                                        }
                                    );
                            }
                            else {
                                messageService.info(tag.tags_s + " " +
                                    $translate.instant('document-repository.comp.tags.message.tagAssociated'));
                                _.remove(tagsFound, function () {
                                    return tag;
                                });
                            }
                        }
                        else {
                            ObjectTagsService.associateTag(componentHelper.currentObjectId
                                , ObjectService.ObjectTypes.DOC_REPO, $scope.objectParentTitle, tag.id)
                                .then(
                                    function () {
                                        $scope.tags.push(tag);
                                        $scope.gridOptions.data = $scope.tags;
                                        $scope.gridOptions.totalItems = $scope.tags.length;
                                    }
                                );
                        }
                    }
                });

            }, function () {
                // Cancel button was clicked.
            });
        };

        var onConfigRetrieved = function (config) {
            // $scope.config = config;
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

        };

        var onObjectInfoRetrieved = function (objectInfo) {
            //    $scope.objectInfo = objectInfo;
            $scope.objectParentTitle = $scope.objectInfo.name;

            var currentObjectId = Util.goodMapValue(objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryTags = ObjectTagsService.getAssociateTags(currentObjectId, ObjectService.ObjectTypes.DOC_REPO);
                $q.all([promiseQueryTags]).then(function (data) {
                    _.forEach(data[0], function (tag) {
                        var tmp = tag.tagName;
                        tag.tagName = tag.tagText;
                        tag.tagText = tmp;
                    });

                    $scope.tags = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.tags;
                    $scope.gridOptions.totalItems = $scope.tags.length;
                });
            }
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                ObjectTagsService.removeAssociateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.DOC_REPO
                    , rowEntity.id).then(function () {
                    messageService.info($translate.instant('document-repository.comp.tags.message.delete.success'));
                }, function () {
                    messageService.error($translate.instant('cases.comp.tags.message.delete.error'));
                });
            }
        };
    }
]);