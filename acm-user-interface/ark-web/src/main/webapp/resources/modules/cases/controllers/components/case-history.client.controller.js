'use strict';

angular.module('cases').controller('Cases.HistoryController', ['$scope', '$stateParams', '$q', 'UtilService', 'HelperService', 'ConstantService', 'CallObjectsService',
    function ($scope, $stateParams, $q, Util, Helper, Constant, CallObjectsService) {
        $scope.$emit('req-component-config', 'history');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('history' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setExternalPaging($scope, config, $scope.retrieveGridData);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });

        var promiseUsers = Helper.Grid.getUsers($scope);

        $scope.currentId = $stateParams.id;

        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                var promiseQueryAudit = CallObjectsService.queryAudit(Constant.ObjectTypes.CASE_FILE
                    , $scope.currentId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodMapValue($scope.sort, "by")
                    , Util.goodMapValue($scope.sort, "dir")
                );

                //var cacheCaseHistoryData = new Store.CacheFifo(Helper.CacheNames.CASE_HISTORY_DATA);
                //var cacheKey = Helper.ObjectTypes.CASE_FILE + "." + $scope.currentId;
                //var historyData = cacheCaseHistoryData.get(cacheKey);
                //var promiseQueryAudit = Util.serviceCall({
                //    service: CasesService.queryAudit
                //    , param: Helper.Grid.withPagingParams($scope, {
                //        id: $scope.currentId
                //    })
                //    , onSuccess: function (data) {
                //        if (Validator.validateHistory(data)) {
                //            historyData = data;
                //            cacheCaseHistoryData.put(cacheKey, historyData);
                //            return historyData;
                //
                //        }
                //    }
                //});

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var auditData = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = auditData.resultPage;
                    $scope.gridOptions.totalItems = auditData.totalCount;
                    Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                });
            }
        };
    }
]);