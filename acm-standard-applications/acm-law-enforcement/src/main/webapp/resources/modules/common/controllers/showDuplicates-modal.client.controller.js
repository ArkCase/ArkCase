'use strict';

angular.module('common').controller(
    'Common.ShowDuplicates',
    ['$scope', '$modal', '$translate', '$modalInstance', '$state', 'ConfigService', 'UtilService', 'params', 'EcmService', 'MessageService',
        function ($scope, $modal, $translate, $modalInstance, $state, ConfigService, Util, params, Ecm, MessageService) {

            $scope.modalInstance = $modalInstance;
            $scope.gridOptions = {};
            $scope.header = $translate.instant("common.duplicates.header");
            $scope.data = params.data;

            for (var i = 0; i < $scope.data.length; i++) {
                if ($scope.data[i].className === "gov.foia.model.FOIAFile") {
                    var activeVersion = parseInt($scope.data[i].activeVersionTag) - 1;
                    $scope.data[i].reviewStatus = $scope.data[i].versions[activeVersion].reviewStatus === "" ?
                        "No review Status" : $scope.data[i].versions[activeVersion].reviewStatus;
                    $scope.data[i].redactionStatus = $scope.data[i].versions[activeVersion].redactionStatus === "" ?
                        "Not Redacted" : $scope.data[i].versions[activeVersion].redactionStatus;
                }
            }
            $scope.moduleConfig = ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.config = Util.goodMapValue(moduleConfig, "showDuplicates");
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: true,
                    noUnselect: false,
                    columnDefs: $scope.config.columnDefs,
                    data: $scope.data,
                    onRegisterApi: function (gridApi) {
                        //set gridApi on scope
                        $scope.gridApi = gridApi;
                        gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                            $scope.selectedRows = gridApi.selection.getSelectedRows();
                        });
                    }
                };
            });

            $scope.onClickObjLink = function(event, rowEntity) {
                event.preventDefault();
                $state.go($scope.modalParams.returnState, {
                    id: rowEntity.container.containerObjectId,
                    fileId: rowEntity.fileId
                }, true);
            };

            $scope.openCaseFile = function(event, rowEntity) {
                event.preventDefault();
                $state.go('cases.main', {
                    id: rowEntity.container.containerObjectId,
                }, true);
            };

            $scope.deleteFile = function(rowEntity) {
                for (var i = 0; i < rowEntity.length; i++) {
                    var fileId = rowEntity[i].fileId;
                    Util.serviceCall({
                        service: Ecm.deleteFileTemporary,
                        param: {
                            fileId: fileId
                        },
                        data: {},
                        onSuccess: function() {
                            MessageService.succsessAction();
                            $modalInstance.close('done');
                        },
                        onError: function(error) {
                            MessageService.error(error.data);
                        }
                    })
                }
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

        }]);