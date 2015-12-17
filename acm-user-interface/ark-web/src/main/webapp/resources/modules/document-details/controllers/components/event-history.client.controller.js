'use strict';

angular.module('document-details').controller('Document.EventHistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.AuditService'
    , function ($scope, $stateParams, $q, Util, ConfigService, HelperUiGridService, ObjectService, ObjectAuditService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var promiseConfig = ConfigService.getComponentConfig("document-details", "eventHistory").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, $scope.retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryAudit = ObjectAuditService.queryAudit(ObjectService.ObjectTypes.FILE
                    , $stateParams.id
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodMapValue($scope.sort, "by")
                    , Util.goodMapValue($scope.sort, "dir")
                );

                $q.all([promiseQueryAudit, promiseUsers, promiseConfig]).then(function (data) {
                    var auditData = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = auditData.resultPage;
                    $scope.gridOptions.totalItems = auditData.totalCount;
                });
            }
        };
    }
]);