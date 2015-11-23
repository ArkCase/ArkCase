'use strict';

angular.module('cases').controller('Cases.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', '$translate'
    , 'UtilService', 'HelperService', 'ConstantService', 'LookupService', 'Object.LookupService', 'Object.CorrespondenceService'
    , function ($scope, $stateParams, $q, $window, $translate, Util, Helper, Constant, LookupService, ObjectLookupService, ObjectCorrespondenceService) {

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

        $scope.$on('case-updated', function (e, data) {
            $scope.caseInfo = data;
        });

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            var promiseCorrespondence = ObjectCorrespondenceService.queryCorrespondences(Constant.ObjectTypes.CASE_FILE
                , $scope.currentId
                , $scope.start
                , $scope.pageSize
                , $scope.sort.by
                , $scope.sort.dir
            );

            //var cacheCorrespondenceData = new Store.CacheFifo(Helper.CacheNames.CASE_CORRESPONDENCE_DATA);
            //var cacheKey = Constant.ObjectTypes.CASE_FILE + "." + $scope.currentId;
            //var correspondenceData = cacheCorrespondenceData.get(cacheKey);
            //var promiseCorrespondence = Util.serviceCall({
            //    service: CasesService.queryCorrespondence
            //    , param: Helper.Grid.withPagingParams($scope, {
            //        parentType: Constant.ObjectTypes.CASE_FILE,
            //        parentId: $scope.currentId
            //    })
            //    , onSuccess: function (data) {
            //        if (Validator.validateCorrespondences(data)) {
            //            correspondenceData = data;
            //            cacheCorrespondenceData.put(cacheKey, correspondenceData);
            //            return correspondenceData;
            //        }
            //    }
            //});

            $q.all([promiseCorrespondence, promiseUsers]).then(function (data) {
                var correspondenceData = data[0];
                $scope.gridOptions = $scope.gridOptions || {};
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
            var caseId = Util.goodValue($scope.caseInfo.id, 0);
            var folderId = Util.goodMapValue($scope.caseInfo, "container.folder.cmisFolderId", "");
            var template = $scope.correspondenceForm.value;
            var promiseCreateCorrespondence = ObjectCorrespondenceService.createCorrespondence(template, Constant.ObjectTypes.CASE_FILE, $scope.currentId, folderId);
            //var promiseCreateCorrespondence = Util.serviceCall({
            //    service: CasesService.createCorrespondence
            //    , param: {
            //        parentType: Constant.ObjectTypes.CASE_FILE,
            //        parentId: $scope.currentId,
            //        folderId: folderId,
            //        template: template
            //    }
            //    , data: {}
            //    , onSuccess: function (data) {
            //        if (Validator.validateNewCorrespondence(data)) {
            //            var newCorrespondence = data;
            //            return newCorrespondence;
            //        }
            //    }
            //});
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