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
                
        $scope.updateViewerOpenDocuments = function () {
            $scope.$bus.publish('update-viewer-opened-versions', $scope.selectedRows);
        };
        
        $scope.$watchCollection('versions', function (newValue, oldValue) {
            if (newValue && newValue.length) {
                var promiseUsers = gridHelper.getUsers();
                ConfigService.getComponentConfig("document-details", "versionHistory").then(function (data) {
                    gridHelper.setColumnDefs(data);
                    gridHelper.setBasicOptions(data);
                    gridHelper.disableGridScrolling(data);
                    gridHelper.setUserNameFilterToConfig(promiseUsers, data);
                    
                    gridHelper.addGridApiHandler(function(gridApi) {
                        gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                            $scope.selectedRows = gridApi.selection.getSelectedRows();
                            $scope.updateViewerOpenDocuments();
                        });

                        gridApi.selection.on.rowSelectionChangedBatch($scope, function (rows) {
                            $scope.selectedRows = gridApi.selection.getSelectedRows();
                            $scope.updateViewerOpenDocuments();
                        });
                    });
                    
                    $scope.retrieveGridData();
                    return data;
                });
            }
        });

        $scope.retrieveGridData = function () {
            if ($scope.versions && $scope.versions.length) {
                $scope.gridOptions = $scope.gridOptions ||
                    {};
                $scope.gridOptions.data = $scope.versions;
                _.forEach($scope.gridOptions.data, function (versionData) {
                    if (versionData.versionTag == $scope.activeVersionTag) {
                        $timeout(function () {
                            $scope.gridApi.selection.selectRow(versionData);
                        });
                    }
                });
                $scope.gridOptions.totalItems = $scope.versions.length;
            }
        }
    }
]);