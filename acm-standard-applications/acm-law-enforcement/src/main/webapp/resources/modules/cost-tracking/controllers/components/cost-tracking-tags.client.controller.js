'use strict';

angular.module('cost-tracking').controller('CostTracking.TagsController', ['$scope', '$q', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'CostTracking.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.TagsService', '$modal', 'MessageService'
    , function ($scope, $q, $stateParams, $translate
        , Util, ObjectService, CostTrackingInfoService
        , HelperUiGridService, HelperObjectBrowserService, ObjectTagsService, $modal, messageService) {

        $scope.tags = [];

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , componentId: "tags"
            , retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo
            , validateObjectInfo: CostTrackingInfoService.validateCostsheet
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
                templateUrl: 'modules/cost-tracking/views/components/cost-tracking-tags-modal.client.view.html',
                controller: 'CostTracking.TagsModalController',
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
                                ObjectTagsService.associateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.COSTSHEET, tag.object_id_s).then(
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
                                messageService.info(tag.tags_s + " " + $translate.instant('costTracking.comp.tags.message.tagAssociated'));
                                _.remove(tagsFound, function () {
                                    return tag;
                                });
                            }
                        }
                        else {
                            ObjectTagsService.associateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.COSTSHEET, tag.id).then(
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

            $scope.config = config;
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            var currentObjectId = Util.goodMapValue(objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryTags = ObjectTagsService.getAssociateTags(currentObjectId, ObjectService.ObjectTypes.COSTSHEET);
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
                ObjectTagsService.removeAssociateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.COSTSHEET, rowEntity.id).then(function () {
                    messageService.info($translate.instant('costTracking.comp.tags.message.delete.success'));
                }, function () {
                    messageService.error($translate.instant('costTracking.comp.tags.message.delete.error'));
                });
            }
        };
    }
]);