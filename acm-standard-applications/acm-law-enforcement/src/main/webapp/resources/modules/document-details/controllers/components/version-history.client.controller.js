'use strict';

angular.module('document-details').controller('Document.VersionHistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService',
    function ($scope, $stateParams, $q, Util, ConfigService, HelperUiGridService) {

        $scope.$on('document-data', updateVersionHistory);
        $scope.versions = [];

        function updateVersionHistory(event, documentDetails) {
            if (documentDetails.versions && documentDetails.versions.length) {
                $scope.versions = documentDetails.versions;
            }
        }

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        $scope.$watchCollection('versions', function (newValue, oldValue) {
            if (newValue && newValue.length) {
                var promiseConfig = ConfigService.getComponentConfig("document-details", "versionHistory").then(function (config) {
                    gridHelper.setColumnDefs(config);
                    gridHelper.setBasicOptions(config);
                    gridHelper.disableGridScrolling(config);
                    gridHelper.setExternalPaging(config, $scope.retrieveGridData);
                    gridHelper.setUserNameFilter(promiseUsers);

                    $scope.retrieveGridData();
                    return config;
                });
            }

        })


        $scope.retrieveGridData = function () {
            if ($scope.versions && $scope.versions.length) {
                $scope.gridOptions = $scope.gridOptions ||
                    {};
                $scope.gridOptions.data = $scope.versions;
                $scope.gridOptions.totalItems = $scope.versions.length;
            }
        }
    }
]);