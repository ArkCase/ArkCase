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
        
        $scope.$watchCollection('versions', function (newValue, oldValue) {
            if (newValue && newValue.length) {
            	$q.all([gridHelper.getUsers(), ConfigService.getComponentConfig("document-details", "versionHistory")]).then(function (data) {
                    gridHelper.setColumnDefs(data[1]);
                    gridHelper.setBasicOptions(data[1]);
                    gridHelper.disableGridScrolling(data[1]);
                    gridHelper.setExternalPaging(data[1], $scope.retrieveGridData);
                    gridHelper.setUserNameFilter(data[0]);

                    $scope.retrieveGridData();
                    return data[1];
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