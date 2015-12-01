'use strict';

angular.module('complaints').controller('Complaints.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', '$translate'
    , 'UtilService', 'HelperService', 'ConstantService', 'LookupService', 'Object.LookupService', 'Object.CorrespondenceService', 'Complaint.InfoService'
    , function ($scope, $stateParams, $q, $window, $translate, Util, Helper, Constant, LookupService, ObjectLookupService
        , ObjectCorrespondenceService, ComplaintInfoService) {

        $scope.$emit('req-component-config', 'correspondence');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'correspondence') {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setExternalPaging($scope, config, $scope.retrieveGridData);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });

        var promiseUsers = Helper.Grid.getUsers($scope);

        var promiseObjectTypes = ObjectLookupService.getObjectTypes().then(
            function (objectTypes) {
                $scope.objectTypes = objectTypes;
                return objectTypes;
            }
        );


        $scope.correspondenceForms = [{"value": "noop", "name": $translate.instant("common.select.option.none")}];
        $scope.correspondenceForm = {"value": "noop", "name": $translate.instant("common.select.option.none")};
        var promiseCorrespondenceForms = ObjectLookupService.getCorrespondenceForms().then(
            function (correspondenceForms) {
                $scope.correspondenceForms = correspondenceForms;
                $scope.correspondenceForms.unshift({
                    "value": "noop",
                    "name": $translate.instant("common.select.option.none")
                });
                return correspondenceForms;
            }
        );

        $scope.$on('complaint-updated', function (e, data) {
            if (ComplaintInfoService.validateComplaintInfo(data)) {
                $scope.complaintInfo = data;
            }
        });

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            var promiseCorrespondence = ObjectCorrespondenceService.queryCorrespondences(Constant.ObjectTypes.COMPLAINT
                , $scope.currentId
                , Util.goodValue($scope.start, 0)
                , Util.goodValue($scope.pageSize, 10)
                , Util.goodValue($scope.sort.by)
                , Util.goodValue($scope.sort.dir)
            );

            $q.all([promiseCorrespondence, promiseUsers]).then(function (data) {
                var correspondenceData = data[0];
                $scope.gridOptions.data = correspondenceData.children;
                $scope.gridOptions.totalItems = Util.goodValue(correspondenceData.totalChildren, 0);
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
            });
        };

        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            promiseObjectTypes.then(function (data) {
                var found = _.find($scope.objectTypes, {type: Constant.ObjectTypes.FILE});
                if (found) {
                    var url = Util.goodValue(found.url);
                    var id = Util.goodMapValue(rowEntity, "objectId");
                    url = url.replace(":id", id);
                    $window.location.href = url;
                }
            });
        };

        $scope.addNew = function () {
            var complaintId = Util.goodValue($scope.complaintInfo.id, 0);
            var folderId = Util.goodMapValue($scope.complaintInfo, "container.folder.cmisFolderId", "");
            var template = $scope.correspondenceForm.value;
            var promiseCreateCorrespondence = ObjectCorrespondenceService.createCorrespondence(template, Constant.ObjectTypes.COMPLAINT, $scope.currentId, folderId);

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
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
            });
        };

    }
]);