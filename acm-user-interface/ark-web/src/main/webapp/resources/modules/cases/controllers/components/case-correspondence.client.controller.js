'use strict';

angular.module('cases').controller('Cases.CorrespondenceController', ['$scope', '$stateParams', '$q', '$window', '$translate'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'LookupService', 'Object.LookupService'
    , 'Object.CorrespondenceService', 'Case.InfoService'
    , function ($scope, $stateParams, $q, $window, $translate
        , Util, ConfigService, HelperUiGridService, ObjectService, LookupService, ObjectLookupService
        , ObjectCorrespondenceService, CaseInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var promiseObjectTypes = ObjectLookupService.getObjectTypes().then(
            function (objectTypes) {
                $scope.objectTypes = objectTypes;
                return objectTypes;
            }
        );

        //$scope.$emit('req-component-config', 'correspondence');
        //$scope.$on('component-config', function (e, componentId, config) {
        //    if (componentId == 'correspondence') {
        //        gridHelper.setColumnDefs(config);
        //        gridHelper.setBasicOptions(config);
        //        gridHelper.setExternalPaging(config, $scope.retrieveGridData);
        //        gridHelper.setUserNameFilter(promiseUsers);
        //
        //        $scope.retrieveGridData();
        //    }
        //});
        ConfigService.getComponentConfig("cases", "correspondence").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.setExternalPaging(config, $scope.retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });


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

        //$scope.$on('case-updated', function (e, data) {
        //    if (CaseInfoService.validateCaseInfo(data)) {
        //        $scope.caseInfo = data;
        //    }
        //});
        CaseInfoService.getCaseInfo($stateParams.id).then(function (caseInfo) {
            $scope.caseInfo = caseInfo;
            return caseInfo;
        });

        $scope.retrieveGridData = function () {
            var promiseCorrespondence = ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.CASE_FILE
                , $stateParams.id
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
                gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
            });
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
            var caseId = Util.goodValue($scope.caseInfo.id, 0);
            var folderId = Util.goodMapValue($scope.caseInfo, "container.folder.cmisFolderId", "");
            var template = $scope.correspondenceForm.value;
            var promiseCreateCorrespondence = ObjectCorrespondenceService.createCorrespondence(template, ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, folderId);

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
                gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);

                //var lastPage = $scope.gridApi.pagination.getTotalPages();
                //$scope.gridApi.pagination.seek(lastPage);
            });
        };

    }
]);