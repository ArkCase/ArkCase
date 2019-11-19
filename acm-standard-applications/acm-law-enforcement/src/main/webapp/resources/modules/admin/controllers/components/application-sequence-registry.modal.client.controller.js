'uset strict';

angular.module('admin').controller('Admin.SequenceRegistryModalController', ['$scope', '$modalInstance', 'ConfigService', 'Helper.UiGridService', 'Admin.SequenceRegistryService', function ($scope, $modalInstance, ConfigService, HelperUiGridService, AdminSequenceRegistryService) {

    ConfigService.getModuleConfig("admin").then(function (moduleConfig) {
        $scope.sequenceRegistryConfig = _.find(moduleConfig.components, {
            id: "sequenceRegistry"
        });


        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            columnDefs: $scope.sequenceRegistryConfig.columnDefs,
            totalItems: 0,
            paginationPageSizes: $scope.sequenceRegistryConfig.paginationPageSizes,
            paginationPageSize: $scope.sequenceRegistryConfig.paginationPageSize,
            data: []
        };

        AdminSequenceRegistryService.getSequenceRegistry().then(function (res) {
            $scope.gridOptions.data = res.data;
        });


    });

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('Cancel');
    };


}]);