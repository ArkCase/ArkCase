'use strict';

angular.module('admin').controller('Admin.SequenceManagement',
    [ '$scope', '$modal', 'Helper.UiGridService', 'MessageService', 'UtilService', 'Dialog.BootboxService', '$translate', 'Admin.SequenceManagementService'
        , function($scope, $modal, HelperUiGridService, MessageService, Util, DialogService, $translate, AdminSequenceManagementService) {

        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });

        $scope.config.$promise.then(function(config) {
            var config = angular.copy(_.find(config.components, {
                id: 'sequenceManagementConfiguration'
            }));

            $scope.config = config;

            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            gridHelper.addButton(config, "parts","fa fa-cog","showAddPartsModal");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);

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
                data: []
            };
        });

        $scope.sequenceList = {};

        var reloadGrid = function(data) {
            $scope.gridOptions.data = data;
        };

        $scope.loadPage = function() {
            AdminSequenceManagementService.getSequences().then(function(response) {
                if (!Util.isEmpty(response.data)) {
                    reloadGrid(response.data);
                }
            });
        };
        $scope.loadPage();

        var deleteSequence = function(sequenceConf) {
            var sequenceConfiguration = {
                "sequence": sequenceConf
            };
            saveConfig(sequenceConfiguration);
        };


        $scope.save = function() {
            var sequenceConfig = {
                "sequence": $scope.gridOptions.data
            };
            saveConfig(sequenceConfig);
        };
        var saveConfig = function(sequenceConfiguration) {
            AdminSequenceManagementService.saveSequences(sequenceConfiguration).then(function(data) {
                MessageService.succsessAction();
                reloadGrid(data.config.data);
            }, function() {
                MessageService.errorAction();
            });
        };

        function showModal(sequence, isEdit) {
            var params = {};
            params.sequence = sequence;
            params.isEdit = isEdit;

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/application-sequence-management.config.modal.client.view.html',
                controller: 'Admin.SequenceManagementModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });
            return modalInstance.result;
        }

        $scope.showAddPartsModal= function(sequence) {
            console.log(sequence);

            var params = {};
            params.config = $scope.config.sequenceManagementPartsConfig;
            params.sequenceParts = sequence.sequenceParts;
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/application-sequence-management-parts.config.modal.client.view.html',
                controller: 'Admin.SequenceManagementPartsModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });
            return modalInstance.result;
        };

        $scope.addNew = function() {
            var sequence = {};
            showModal(sequence, false).then(function(data) {
                var element = data;
                var sequenceConfig = $scope.gridOptions.data;
                var itemExist = _.find(sequenceConfig, function (sequence) {
                    return element.sequenceName === sequence.sequenceName;
                })
                if(itemExist === undefined){
                    sequenceConfig.push(element);
                    $scope.save();
                } else {
                    DialogService.alert($translate.instant('admin.application.sequenceManagementParts.config.message'));
                }
            });
        };

        $scope.editRow = function(rowEntity) {

            var entity = angular.copy(rowEntity);
            showModal(entity, true).then(function(data) {
                var element = data;

                var itemExist = _.find($scope.gridOptions.data, function(sequence) {
                    return (element.sequenceName === sequence.sequenceName && element.sequenceDescription === element.sequenceDescription
                        && element.sequenceEnabled === sequence.sequenceEnabled);
                });

                if (!itemExist) {
                    rowEntity.sequenceName = data.sequenceName;
                    rowEntity.sequenceDescription = data.sequenceDescription;
                    rowEntity.sequenceEnabled = data.sequenceEnabled;
                    $scope.save();
                } else {
                    DialogService.alert($translate.instant('admin.application.sequenceManagementParts.config.message'));
                }
            });
        };

        $scope.deleteRow = function(rowEntity) {
            var sequenceConfig = angular.copy($scope.gridOptions.data);
            _.remove(sequenceConfig, function(item) {
                return item.sequenceName === rowEntity.sequenceName;
            });
            deleteSequence(sequenceConfig);
        };

    } ]);