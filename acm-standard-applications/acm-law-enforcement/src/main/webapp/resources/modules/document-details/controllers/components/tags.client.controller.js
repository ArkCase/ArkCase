'use strict';

angular.module('document-details').controller('Document.TagsController', ['$scope', '$filter', '$stateParams', '$q'
    , '$modal', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.TagsService'
    , 'MessageService', '$translate', 'EcmService'
    , function ($scope, $filter, $stateParams, $q, $modal, Util, ConfigService, HelperUiGridService, ObjectService
        , ObjectTagsService, messageService, $translate, EcmService) {

        $scope.tags = [];

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        ConfigService.getComponentConfig("document-details", "tags").then(function (config) {
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

            $scope.retrieveGridData();
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryTags = ObjectTagsService.getAssociateTags($stateParams.id, ObjectService.ObjectTypes.FILE);
                $q.all([promiseQueryTags]).then(function (data) {
                    $scope.tags = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.tags;
                    $scope.gridOptions.totalItems = $scope.tags.length;
                });
            }
        };

        EcmService.getFile({fileId: $stateParams.id}).$promise.then(function (ecmFileInfo) {
            $scope.parentTitle = ecmFileInfo.fileName;
        });

        $scope.addNew = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/document-details/views/components/tags-modal.client.view.html',
                controller: 'Document.TagsModalController',
                size: 'lg'
            });

            modalInstance.result.then(function (tags) {
                _.forEach(tags, function (tag) {
                    if (tag.id) {
                        if (tag.object_id_s) {
                            var tagsFound = _.filter($scope.tags, function (tagAss) {
                                return tagAss.id == tag.object_id_s;
                            });
                            if (tagsFound.length == 0) {
                                ObjectTagsService.associateTag($stateParams.id, ObjectService.ObjectTypes.FILE,
                                    $scope.parentTitle, tag.object_id_s).then(
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
                                messageService.info(tag.tags_s + " " + $translate.instant('documentDetails.comp.tags.message.tagAssociated'));
                                _.remove(tagsFound, function () {
                                    return tag;
                                });
                            }
                        }
                        else {
                            ObjectTagsService.associateTag($stateParams.id, ObjectService.ObjectTypes.FILE,
                                $scope.parentTitle, tag.id).then(
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

        $scope.deleteRow = function (rowEntity) {
            ObjectTagsService.removeAssociateTag($stateParams.id, ObjectService.ObjectTypes.FILE, rowEntity.id).then(function () {
                gridHelper.deleteRow(rowEntity);
                messageService.info($translate.instant('documentDetails.comp.tags.message.delete.success'));
            }, function () {
                messageService.error($translate.instant('documentDetails.comp.tags.message.delete.error'));
            });
        };

    }
]);