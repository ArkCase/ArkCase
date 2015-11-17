'use strict';

angular.module('tasks').controller('Tasks.HistoryController', ['$scope', '$stateParams', '$q', 'UtilService', 'HelperService', 'ConstantService', 'Object.AuditService',
    function ($scope, $stateParams, $q, Util, Helper, Constant, ObjectAuditService) {
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
                var promiseQueryAudit = ObjectAuditService.queryAudit(Constant.ObjectTypes.TASK
                    , $scope.currentId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodMapValue($scope.sort, "by")
                    , Util.goodMapValue($scope.sort, "dir")
                );

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var auditData = data[0];
                    $scope.gridOptions.data = auditData.resultPage;
                    $scope.gridOptions.totalItems = auditData.totalCount;
                    Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                });
            }
        };
    }
]);