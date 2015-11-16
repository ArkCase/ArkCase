'use strict';

angular.module('tasks').controller('Tasks.ESignaturesController', ['$scope', '$stateParams', '$q', 'UtilService', 'HelperService', 'ConstantService', 'CallObjectsService',
    function ($scope, $stateParams, $q, Util, Helper, Constant, CallObjectsService) {
        $scope.$emit('req-component-config', 'esignatures');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('esignatures' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });


        var promiseUsers = Helper.Grid.getUsers($scope);

        $scope.currentId = $stateParams.id;

        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                var promiseQueryAudit = CallObjectsService.findSignatures(Constant.ObjectTypes.TASK, $scope.currentId);

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var signatures = data[0];
                    $scope.gridOptions.data = signatures;
                    $scope.gridOptions.totalItems = signatures.length;
                    Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                });
            }
        };
    }
]);
