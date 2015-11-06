'use strict';

angular.module('complaints').controller('Cases.HistoryController', ['$scope', '$stateParams', '$q', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, Store, Util, Validator, Helper, LookupService, CasesService) {
        var z = 1;
        return;
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
                var cacheCaseHistoryData = new Store.CacheFifo(Helper.CacheNames.CASE_HISTORY_DATA);
                var cacheKey = Helper.ObjectTypes.COMPLAINT + "." + $scope.currentId;
                var historyData = cacheCaseHistoryData.get(cacheKey);
                var promiseQueryAudit = Util.serviceCall({
                    service: CasesService.queryAudit
                    , param: Helper.Grid.withPagingParams($scope, {
                        id: $scope.currentId
                    })
                    , onSuccess: function (data) {
                        if (Validator.validateHistory(data)) {
                            historyData = data;
                            cacheCaseHistoryData.put(cacheKey, historyData);
                            return historyData;

                        }
                    }
                });

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var historyData = data[0];
                    $scope.gridOptions.data = historyData.resultPage;
                    $scope.gridOptions.totalItems = historyData.totalCount;
                    Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                });
            }
        };
    }
]);