'use strict';

angular.module('complaints').controller('Complaints.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', '$translate'
    , 'UtilService', 'ConfigService', 'ObjectService', 'LookupService', 'Object.LookupService'
    , 'Object.CorrespondenceService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q, $window, $translate
        , Util, ConfigService, ObjectService, LookupService, ObjectLookupService
        , ObjectCorrespondenceService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "correspondence"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (complaintInfo) {
                $scope.complaintInfo = complaintInfo;
            }
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var promiseObjectTypes = ObjectLookupService.getObjectTypes().then(
            function (objectTypes) {
                $scope.objectTypes = objectTypes;
                return objectTypes;
            }
        );

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, $scope.retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
        };

        $scope.correspondenceForms = [{"value": "noop", "name": $translate.instant("common.select.option.none")}];
        $scope.correspondenceForm = {"value": "noop", "name": $translate.instant("common.select.option.none")};
        var promiseCorrespondenceForms = ObjectLookupService.getComplaintCorrespondenceForms().then(
            function (correspondenceForms) {
                $scope.correspondenceForms = correspondenceForms;
                $scope.correspondenceForms.unshift({
                    "value": "noop",
                    "name": $translate.instant("common.select.option.none")
                });
                return correspondenceForms;
            }
        );

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($scope.currentObjectId, false)) {
                var promiseCorrespondence = ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.COMPLAINT
                    , $scope.currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodValue($scope.sort.by)
                    , Util.goodValue($scope.sort.dir)
                );

                $q.all([promiseCorrespondence, promiseUsers]).then(function (data) {
                    var correspondenceData = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = correspondenceData.children;
                    $scope.gridOptions.totalItems = Util.goodValue(correspondenceData.totalChildren, 0);
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            promiseObjectTypes.then(function (data) {
                var found = _.find($scope.objectTypes, {type: ObjectService.ObjectTypes.FILE});
                if (found) {
                    var url = Util.goodValue(found.url);
                    var id = Util.goodMapValue(rowEntity, "objectId");
                    url = url.replace(":id", id);
                    $window.location.href = url;
                }
            });
        };

        $scope.addNew = function () {
            var complaintId = Util.goodValue($scope.complaintInfo.complaintId, 0);
            var folderId = Util.goodMapValue($scope.complaintInfo, "container.folder.cmisFolderId", "");
            var template = $scope.correspondenceForm.value;
            var promiseCreateCorrespondence = ObjectCorrespondenceService.createCorrespondence(template, ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, folderId);

            $q.all([promiseCreateCorrespondence, promiseUsers]).then(function (data) {
                var newCorrespondence = data[0];
                var correspondence = {};
                correspondence.objectId = Util.goodValue(newCorrespondence.fileId);
                correspondence.name = Util.goodValue(newCorrespondence.fileName);
                correspondence.creator = Util.goodValue(newCorrespondence.creator);
                correspondence.created = Util.goodValue(newCorrespondence.created);
                correspondence.objectType = "file";
                correspondence.category = "Correspondence";
                $scope.gridOptions.data.push(correspondence);
                $scope.gridOptions.totalItems++;
            });
        };

    }
]);