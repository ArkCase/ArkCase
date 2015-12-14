'use strict';

angular.module('tasks').controller('Tasks.SignaturesController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.SignatureService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, HelperUiGridService, ObjectService, ObjectSignatureService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        ConfigService.getComponentConfig("tasks", "signatures").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryAudit = ObjectSignatureService.findSignatures(ObjectService.ObjectTypes.TASK, $stateParams.id);

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var signatures = data[0];
                    $scope.gridOptions.data = signatures;
                    $scope.gridOptions.totalItems = signatures.length;
                    gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };
    }
]);
