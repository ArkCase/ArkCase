'use strict';

angular.module('tasks').controller('Tasks.SignaturesController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.SignatureService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectSignatureService, HelperUiGridService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        ConfigService.getComponentConfig("tasks", "signatures").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });

        $scope.retrieveGridData = function () {
            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryAudit = ObjectSignatureService.findSignatures(ObjectService.ObjectTypes.TASK, currentObjectId);

                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                    var signatures = data[0];
                    $scope.gridOptions.data = signatures;
                    $scope.gridOptions.totalItems = signatures.length;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };
    }
]);
