'use strict';

angular.module('complaints').controller('Complaints.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'Helper.UiGridService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectAuditService, ComplaintInfoService
        , HelperObjectBrowserService, HelperUiGridService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "history"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, $scope.retrieveGridData);
            gridHelper.showUserFullNames();

            $scope.retrieveGridData();
        };

        $scope.retrieveGridData = function () {
            if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                var promiseQueryAudit = ObjectAuditService.queryAudit(ObjectService.ObjectTypes.COMPLAINT
                    , componentHelper.currentObjectId
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