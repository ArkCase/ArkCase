'use strict';

angular.module('complaints').controller('Complaints.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'ComplaintsService',
    function ($scope, $stateParams, $q, $window, $translate, Store, Util, Validator, Helper, LookupService, ComplaintsService) {
        var z = 1;
        $scope.gridOptions = {};
        return;
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

        var cacheObjectTypes = new Store.SessionData(Helper.SessionCacheNames.OBJECT_TYPES);
        var objectTypes = cacheObjectTypes.get();
        var promiseObjectTypes = Util.serviceCall({
            service: LookupService.getObjectTypes
            , result: objectTypes
            , onSuccess: function (data) {
                objectTypes = [];
                _.forEach(data, function (item) {
                    objectTypes.push(item);
                });
                cacheObjectTypes.set(objectTypes);
                return objectTypes;
            }
        }).then(
            function (objectTypes) {
                $scope.objectTypes = objectTypes;
                return objectTypes;
            }
        );


        $scope.correspondenceForms = [{"value": "noop", "name": $translate.instant("common.select.option.none")}];
        $scope.correspondenceForm = {"value": "noop", "name": $translate.instant("common.select.option.none")};
        var cacheCorrespondenceForms = new Store.SessionData(Helper.SessionCacheNames.COMPLAINT_CORRESPONDENCE_FORMS);
        var correspondenceForms = cacheCorrespondenceForms.get();
        var promiseCorrespondenceForms = Util.serviceCall({
            service: LookupService.getCorrespondenceForms
            , result: correspondenceForms
            , onSuccess: function (data) {
                correspondenceForms = Util.omitNg(Util.goodArray(data));
                correspondenceForms.unshift({
                    "value": "noop",
                    "name": $translate.instant("common.select.option.none")
                });
                cacheCorrespondenceForms.set(correspondenceForms);
                return correspondenceForms;
            }
        }).then(
            function (correspondenceForms) {
                $scope.correspondenceForms = correspondenceForms;
                return correspondenceForms;
            }
        );

        $scope.$on('complaint-updated', function (e, data) {
            if (Validator.validateComplaintFile(data)) {
                $scope.complaintInfo = data;
            }
        });

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            var cacheCorrespondenceData = new Store.CacheFifo(Helper.CacheNames.COMPLAINT_CORRESPONDENCE_DATA);
            var cacheKey = Helper.ObjectTypes.COMPLAINT + "." + $scope.currentId;
            var correspondenceData = cacheCorrespondenceData.get(cacheKey);
            var promiseCorrespondence = Util.serviceCall({
                service: ComplaintsService.queryCorrespondence
                , param: Helper.Grid.withPagingParams($scope, {
                    parentType: Helper.ObjectTypes.COMPLAINT,
                    parentId: $scope.currentId
                })
                , onSuccess: function (data) {
                    if (Validator.validateCorrespondences(data)) {
                        correspondenceData = data;
                        cacheCorrespondenceData.put(cacheKey, correspondenceData);
                        return correspondenceData;
                    }
                }
            });

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
                var found = _.find($scope.objectTypes, {type: Helper.ObjectTypes.FILE});
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
            var promiseCreateCorrespondence = Util.serviceCall({
                service: ComplaintsService.createCorrespondence
                , param: {
                    parentType: Helper.ObjectTypes.COMPLAINT,
                    parentId: $scope.currentId,
                    folderId: folderId,
                    template: template
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Validator.validateNewCorrespondence(data)) {
                        var newCorrespondence = data;
                        return newCorrespondence;
                    }
                }
            });
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

                //var lastPage = $scope.gridApi.pagination.getTotalPages();
                //$scope.gridApi.pagination.seek(lastPage);
            });
        };

    }
]);