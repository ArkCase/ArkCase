'use strict';

angular.module('cases').controller('Cases.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Case.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectAuditService, CaseInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "history"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, $scope.retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            //$scope.retrieveGridData();
        };

        //$scope.retrieveGridData = function () {
        //    if (Util.goodPositive(componentHelper.currentObjectId, false)) {
        //        var promiseQueryAudit = ObjectAuditService.queryAudit(ObjectService.ObjectTypes.CASE_FILE
        //            , componentHelper.currentObjectId
        //            , Util.goodValue($scope.start, 0)
        //            , Util.goodValue($scope.pageSize, 10)
        //            , Util.goodMapValue($scope.sort, "by")
        //            , Util.goodMapValue($scope.sort, "dir")
        //        );
        //
        //        $q.all([promiseQueryAudit, promiseUsers, componentHelper.promiseConfig]).then(function (data) {
        //            var auditData = data[0];
        //            $scope.gridOptions = $scope.gridOptions || {};
        //            $scope.gridOptions.data = auditData.resultPage;
        //            $scope.gridOptions.totalItems = auditData.totalCount;
        //            //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
        //        });
        //    }
        //};
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            var currentObjectId = Util.goodMapValue(objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryAudit = ObjectAuditService.queryAudit(ObjectService.ObjectTypes.CASE_FILE
                    , currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodMapValue($scope.sort, "by")
                    , Util.goodMapValue($scope.sort, "dir")
                );

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var auditData = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = auditData.resultPage;
                    $scope.gridOptions.totalItems = auditData.totalCount;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };

    }
]);