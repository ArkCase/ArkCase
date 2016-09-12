'use strict';

angular.module('complaints').controller('Complaints.TagsController', ['$scope', '$q', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.CostService', 'Complaint.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.TagsService', '$modal', 'MessageService'
    , function ($scope, $q, $stateParams, $translate
        , Util, ObjectService, ConfigService, ObjectCostService, ComplaintInfoService
        , HelperUiGridService, HelperObjectBrowserService, ObjectTagsService, $modal, messageService) {

        $scope.tags = [];

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "tags"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
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
                templateUrl: 'modules/complaints/views/components/complaint-tags-modal.client.view.html',
                controller: 'Complaints.TagsModalController',
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
                                ObjectTagsService.associateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.COMPLAINT, tag.object_id_s).then(
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
                                messageService.info(tag.tags_s + " " + $translate.instant('complaints.comp.tags.message.tagAssociated'));
                                _.remove(tagsFound, function () {
                                    return tag;
                                });
                            }
                        }
                        else {
                            ObjectTagsService.associateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.COMPLAINT, tag.id).then(
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

            var currentObjectId = Util.goodMapValue(objectInfo, "complaintId");
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryTags = ObjectTagsService.getAssociateTags(currentObjectId, ObjectService.ObjectTypes.COMPLAINT);
                $q.all([promiseQueryTags]).then(function (data) {
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
                ObjectTagsService.removeAssociateTag(componentHelper.currentObjectId, ObjectService.ObjectTypes.COMPLAINT, rowEntity.id).then(function () {
                    messageService.info($translate.instant('complaints.comp.tags.message.delete.success'));
                }, function () {
                    messageService.error($translate.instant('complaints.comp.tags.message.delete.error'));
                });
            }
        };
    }
]);