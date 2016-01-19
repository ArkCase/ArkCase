'use strict';

angular.module('document-details').controller('Document.TagsController', ['$scope', '$filter', '$stateParams', '$q'
    , '$modal', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.TagsService', 'MessageService', '$translate',
    function ($scope, $filter, $stateParams, $q, $modal, Util, ConfigService, HelperUiGridService, ObjectService, ObjectTagsService, messageService, $translate) {

        $scope.selectedTag = {};
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseTypes = ObjectTagsService.getTags().then(
            function (tags) {
                $scope.createdTags = tags;
                return tags;
            }
        );

        var promiseConfig = ConfigService.getComponentConfig("document-details", "tags").then(function (config) {
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

            $scope.retrieveGridData();
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryTags = ObjectTagsService.getAssociateTags($stateParams.id, ObjectService.ObjectTypes.FILE);
                $q.all([promiseQueryTags, promiseConfig]).then(function (data) {
                    $scope.tags = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.tags;
                    $scope.gridOptions.totalItems = $scope.tags.length;
                });
            }
        };

        $scope.addNew = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/document-details/views/components/tags-modal.client.view.html',
                controller: 'Document.TagsModalController',
                size: 'lg',
                resolve: {
                    scopeTag: function () {
                        return $scope;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if ($scope.selectedTag) {
                    var tagsFound = _.filter($scope.tags, function (tag) {
                        return tag.id == $scope.selectedTag.id;
                    });
                    if (tagsFound.length == 0) {
                        ObjectTagsService.associateTag($stateParams.id, ObjectService.ObjectTypes.FILE, $scope.selectedTag.id).then(
                            function () {
                                $scope.tags.push($scope.selectedTag);
                                $scope.gridOptions.data = $scope.tags;
                                $scope.gridOptions.totalItems = $scope.tags.length;
                            }
                        );
                    }
                    else {
                        messageService.info($translate.instant('documentDetails.comp.tags.message.tagAssociated'));
                    }
                }
                else {
                    var tagsCreated = _.filter($scope.createdTags, function (tag) {
                        return tag.tagName == data.tag.name || tag.tagDescription == data.tag.description
                            || tag.tagText == data.tag.text;
                    });
                    if (tagsCreated.length == 0) {
                        ObjectTagsService.createTag(data.tag.name, data.tag.description, data.tag.text).then(
                            function (tagCreated) {
                                ObjectTagsService.associateTag($stateParams.id, ObjectService.ObjectTypes.FILE, tagCreated.id).then(
                                    function () {
                                        $scope.tags.push(tagCreated);
                                        $scope.gridOptions.data = $scope.tags;
                                        $scope.gridOptions.totalItems = $scope.tags.length;
                                    }
                                );
                            }
                        )
                    }
                    else {
                        messageService.info($translate.instant('documentDetails.comp.tags.message.tagExists'));
                    }
                }

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