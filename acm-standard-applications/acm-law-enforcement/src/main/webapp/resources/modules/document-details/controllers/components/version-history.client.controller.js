'use strict';

angular.module('document-details').controller('Document.VersionHistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', '$timeout',
    function ($scope, $stateParams, $q, Util, ConfigService, HelperUiGridService, $timeout) {

        $scope.$on('document-data', updateVersionHistory);
        $scope.versions = [];
        $scope.activeVersionTag = {};
        $scope.selectedRows = [];

        function updateVersionHistory(event, documentDetails) {
            if (documentDetails.versions && documentDetails.versions.length) {
                $scope.versions = documentDetails.versions;
                $scope.activeVersionTag = documentDetails.activeVersionTag;
            }
        }

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        
        $scope.gridOptions = {
                enableRowSelection: true,
                enableFiltering: false,
                enableRowHeaderSelection: true,
                enableFullRowSelection: true,
                data: [],
                onRegisterApi: function(gridApi) {
                    $scope.gridApi = gridApi;
                    gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                        $scope.selectedRows = gridApi.selection.getSelectedRows();
                        $scope.updateViewerOpenDocuments();
                    });
     
                    gridApi.selection.on.rowSelectionChangedBatch($scope, function (rows) {
                        $scope.selectedRows = gridApi.selection.getSelectedRows();
                        $scope.updateViewerOpenDocuments();
                    });
               }
            };
        
        $scope.updateViewerOpenDocuments = function () {
            $scope.$emit('update-viewer-opened-versions', $scope.selectedRows);
        };
        
        $scope.$watchCollection('versions', function (newValue, oldValue) {
            if (newValue && newValue.length) {
                gridHelper.getUsers().then(function (promiseUsers) {
                    ConfigService.getComponentConfig("document-details", "versionHistory").then(function (data) {
                        gridHelper.setUserNameFilterToConfig(promiseUsers, data);
                        $scope.gridOptions.columnDefs = data.columnDefs;
                        $scope.gridOptions.paginationPageSizes = data.paginationPageSizes;
                        $scope.gridOptions.paginationPageSize = data.paginationPageSize;
                        $scope.retrieveGridData();
                        return data;
                    });
                });
            }

        })

        $scope.retrieveGridData = function () {
            if ($scope.versions && $scope.versions.length) {
                $scope.gridOptions = $scope.gridOptions ||
                    {};
                $scope.gridOptions.data = $scope.versions;
                _.forEach($scope.gridOptions.data, function (versionData) {
                    if (versionData.versionTag == $scope.activeVersionTag) {
                        $timeout(function () {
                            $scope.gridApi.selection.selectRow(versionData);
                        }, 100);
                    }
                });
                $scope.gridOptions.totalItems = $scope.versions.length;
            }
        }
    }
]);