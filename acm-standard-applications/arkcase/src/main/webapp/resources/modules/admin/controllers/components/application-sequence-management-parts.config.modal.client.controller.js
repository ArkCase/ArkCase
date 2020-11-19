'use strict';

angular.module('admin').controller('Admin.SequenceManagementPartsModalController',
    ['$rootScope', '$scope', '$modal', 'params', 'Helper.UiGridService', 'MessageService', 'UtilService', 'Dialog.BootboxService', '$translate',
        '$modalInstance', 'Object.LookupService', 'Admin.SequenceManagementService', 'Admin.SequenceManagementResetService',
        function ($rootScope, $scope, $modal, params, HelperUiGridService, MessageService, Util, DialogService, $translate,
                  $modalInstance, ObjectLookupService, AdminSequenceManagementService, SequenceManagementResetService) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            $scope.config = angular.copy(params.config);
            $scope.showAddPartsModalParams = angular.copy(params);
            $scope.sequenceName = angular.copy(params.sequenceName);

            gridHelper.addButton($scope.config, "edit");
            gridHelper.addButton($scope.config, "delete");
            gridHelper.addButton($scope.config, "reset", "fa fa-cog", "resetSequencePart","isResetDisabled");
            gridHelper.addButton($scope.config, "editSequence", "fa fa-eye", "editSequenceNumber","isResetDisabled");
            gridHelper.setColumnDefs($scope.config);
            gridHelper.setBasicOptions($scope.config);
            gridHelper.disableGridScrolling($scope.config);

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: $scope.config.columnDefs,
                paginationPageSizes: $scope.config.paginationPageSizes,
                paginationPageSize: $scope.config.paginationPageSize,
                totalItems: 0,
                data: params.sequenceParts
            };

            var reloadGrid = function (data) {
                $scope.gridOptions.data = data;
            };

            $scope.loadPage = function () {
                AdminSequenceManagementService.getSequences().then(function (response) {
                    if (!Util.isEmpty(response.data)) {

                        reloadGrid(response.data);
                    }
                });
            };
            $scope.save = function (sequenceConfig, index) {
                saveConfig(sequenceConfig, index);

            };
            var saveConfig = function (sequenceConfiguration, index) {
                AdminSequenceManagementService.updateSequences(sequenceConfiguration).then(function (data) {
                    MessageService.succsessAction();
                    reloadGrid(data.data[index].sequenceParts);
                    $rootScope.$broadcast("reloadParentConfig", data.data);
                    //$scope.$parent.reloadGrid(data.data);
                }, function () {
                    MessageService.errorAction();
                });
            };

            function showModal(selectedSequence, isEdit) {
                var params = {};
                //params.sequencePart = sequencePart;
                params.isEdit = isEdit;
                params.config = $scope.config;
                params.selectedSequence = selectedSequence;
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/application-sequence-management-parts-modification.config.modal.client.view.html',
                    controller: 'Admin.SequenceManagementPartsModalConfigController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (data) {

                    return modalInstance.result;
                })
            }

            $scope.addNew = function () {
                var selectedSequence = $scope.showAddPartsModalParams.selectedSequence;
                var params = {};
                params.isEdit = false;
                params.config = $scope.config;
                params.selectedSequence = selectedSequence;
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/application-sequence-management-parts-modification.config.modal.client.view.html',
                    controller: 'Admin.SequenceManagementPartsModalConfigController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (selectedSequence) {
                    var sequenceConfig = $scope.showAddPartsModalParams.sequences;
                    var selectedSequenceName = $scope.showAddPartsModalParams.selectedSequence.sequenceName;
                    var reqIndex;
                    sequenceConfig.forEach(function (sequence, index) {
                        if (sequence.sequenceName === selectedSequenceName) {
                            reqIndex = index;
                            return;
                        }
                    });
                    var itemExist = _.find(sequenceConfig[reqIndex].sequenceParts, function (sequence) {
                        return selectedSequence.sequencePartName === sequence.sequencePartName;
                    });
                    if (itemExist === undefined && reqIndex !== undefined) {
                        sequenceConfig[reqIndex].sequenceParts.push(selectedSequence);
                        $scope.save(sequenceConfig, reqIndex);
                    } else {
                        DialogService.alert($translate.instant('admin.application.sequenceManagementParts.config.message'));
                    }

                    return modalInstance.result;
                })
            };

            $scope.editRow = function (rowEntity) {

                var selectedSequence = $scope.showAddPartsModalParams.selectedSequence;
                //params.sequencePart = sequencePart;
                var params = {};
                params.isEdit = true;
                params.config = $scope.config;
                params.selectedSequence = selectedSequence;
                params.selectedSequencePart = angular.copy(rowEntity);
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/application-sequence-management-parts-modification.config.modal.client.view.html',
                    controller: 'Admin.SequenceManagementPartsModalConfigController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (selectedSequence) {
                    var sequenceConfig = $scope.showAddPartsModalParams.sequences;
                    var selectedSequenceName = $scope.showAddPartsModalParams.selectedSequence.sequenceName;
                    var reqIndex;
                    sequenceConfig.forEach(function (sequence, index) {
                        if (sequence.sequenceName === selectedSequenceName) {
                            reqIndex = index;
                            return;
                        }
                    });
                    var itemExist = _.find(sequenceConfig[reqIndex].sequenceParts, function (sequence) {
                        return selectedSequence.sequencePartName === sequence.sequencePartName;
                    });
                    var index = sequenceConfig[reqIndex].sequenceParts.indexOf(itemExist);

                    if (itemExist !== undefined && reqIndex !== undefined) {
                        sequenceConfig[reqIndex].sequenceParts[index] = selectedSequence;
                        $scope.save(sequenceConfig, reqIndex);
                    } else {
                        DialogService.alert($translate.instant('admin.application.sequenceManagementParts.config.message'));
                    }

                    return modalInstance.result;
                })
            };

            $scope.deleteRow = function (rowEntity) {
                var sequenceConfigParts = $scope.showAddPartsModalParams.sequenceParts;
                var sequenceConfig = $scope.showAddPartsModalParams.sequences;
                var selectedSequenceName = $scope.showAddPartsModalParams.selectedSequence.sequenceName;
                _.remove(sequenceConfigParts, function (item) {
                    return item.sequencePartName === rowEntity.sequencePartName;
                });
                var reqIndex;
                sequenceConfig.forEach(function (sequence, index) {
                    if (sequence.sequenceName === selectedSequenceName) {
                        reqIndex = index;
                        return;
                    }
                });

                deleteSequence(sequenceConfig, reqIndex);
                reloadGrid(sequenceConfig[reqIndex]);
                $rootScope.$broadcast("reloadParentConfig", sequenceConfig);
            };

            var deleteSequence = function (sequenceConf, index) {
                saveConfig(sequenceConf, index);
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close($scope.sequence);
            };

            $scope.sequencePartsCurrentlySelected = {};

            $scope.showRequiredFields = function () {
                $scope.sequencePartsCurrentlySelected = {};
                $scope.sequencePartType = $scope.selectedItem.key;
                $scope.sequencePartsCurrentlySelected[$scope.selectedItem.key] = true;
            };

            $scope.isResetDisabled = function(rowEntity){
                return rowEntity.sequencePartType !== "AUTOINCREMENT";
            };

            $scope.resetSequencePart = function(rowEntity) {
                var params = {};
                params.sequenceName = $scope.sequenceName;
                params.sequencePartName = rowEntity.sequencePartName;
                params.config = $scope.config.sequenceManagementResetConfig;
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/application-sequence-management-reset.modal.client.view.html',
                    controller: 'Admin.SequenceManagementResetController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });
                return modalInstance.result;
            };

            $scope.editSequenceNumber = function(rowEntity) {
                var params = {};
                params.sequenceName = $scope.sequenceName;
                params.sequencePartName = rowEntity.sequencePartName;

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/application-sequence-edit-sequence-number.modal.client.view.html',
                    controller: 'Admin.SequenceEditSequenceNumberController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });
            }

        }]);