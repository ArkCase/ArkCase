'use strict';

angular.module('admin').controller('Admin.CMISConfigurationController', ['$scope', '$modal', 'Helper.UiGridService'
    , 'Admin.CmisConfigService', 'UtilService', 'Admin.ModalDialogService', 'MessageService', '$translate'
    , function ($scope, $modal, HelperUiGridService, CmisConfigService, Util, modalDialogService, messageService, $translate) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        //get config and init grid settings
        $scope.config.$promise.then(function (config) {
            var componentConfig = _.find(config.components, {id: 'cmisConfiguration'});
            $scope.config = config;

            gridHelper.addButton(componentConfig, 'edit');

            //Deletion isn't working currently.
            // gridHelper.addButton(componentConfig, 'delete');

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: componentConfig.columnDefs,
                totalItems: 0,
                data: []
            };

            reloadGrid();
        });

        $scope.showModal = function (cmisConfig, isEdit, originalConfig) {
            var modalScope = $scope.$new();
            modalScope.cmisConfig = cmisConfig || {};
            modalScope.isEdit = isEdit || false;

            var modalInstance = $modal.open({
                scope: modalScope,
                templateUrl: 'modules/admin/views/components/cmis.configuration.addconfig.modal.html',
                backdrop: 'static',
                controller: function ($scope, $modalInstance) {
                    $scope.ok = function () {
                        $modalInstance.close({cmisConfig: $scope.cmisConfig, isEdit: $scope.isEdit});
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    }
                }
            });

            modalInstance.result.then(function (result) {
                addPrefixInKey(result.cmisConfig);
                if (result.isEdit) {
                    CmisConfigService.updateCmisConfiguration(result.cmisConfig).then(function () {
                        reloadGrid();
                        messageService.info($translate.instant('admin.documentManagement.cmisConfiguration.messages.update.success'));
                    }, function () {
                        messageService.error($translate.instant('admin.documentManagement.cmisConfiguration.messages.update.error'));
                    })
                } else {
                    CmisConfigService.createCmisConfiguration(result.cmisConfig).then(function () {
                        reloadGrid();
                        messageService.info($translate.instant('admin.documentManagement.cmisConfiguration.messages.insert.success'));
                    }, function () {
                        messageService.error($translate.instant('admin.documentManagement.cmisConfiguration.messages.insert.error'));
                    });
                }
            })
        };

        $scope.deleteRow = function (rowEntity) {
            //TODO: Delete configuration

            console.log("You clicked DELETE. Here's a cookie (::)");

            $scope.deleteDir = rowEntity;
            var modalOptions = {
                closeButtonText: $translate.instant('admin.documentManagement.cmisConfiguration.deleteDialog.cancelBtn'),
                actionButtonText: $translate.instant('admin.documentManagement.cmisConfiguration.deleteDialog.deleteBtn'),
                headerText: $translate.instant('admin.documentManagement.cmisConfiguration.deleteDialog.headerText'),
                bodyText: $translate.instant('admin.documentManagement.cmisConfiguration.deleteDialog.bodyText')
            };
            modalDialogService.showModal({}, modalOptions).then(function () {
                CmisConfigService.deleteCmisConfiguration($scope.deleteDir.id).then(function () {
                    gridHelper.deleteRow($scope.deleteDir);
                    messageService.info($translate.instant('admin.documentManagement.cmisConfiguration.messages.delete.success'));
                }, function () {
                    messageService.error($translate.instant('admin.documentManagement.cmisConfiguration.messages.delete.error'));
                });
            });
        };

        $scope.editRow = function (rowEntity) {
            $scope.showModal(angular.copy(rowEntity), true, rowEntity);
        };

        function reloadGrid() {
            var cmisConfigurationPromise = CmisConfigService.retrieveCmisConfigurations();
            cmisConfigurationPromise.then(function (configs) {
                if (Util.goodMapValue(configs, 'data')) {
                    removePrefixInKey(configs.data);
                    $scope.gridOptions.data = configs.data;
                }
            })
        }

        //we need this because key name contains '.'
        function removePrefixInKey(data) {
            angular.forEach(data, function (row, index) {
                angular.forEach(row, function (element, key) {
                    if (key.match('.') !== -1) {
                        delete row[key];
                        var newKey = key.replace(/[a-zA-Z]*?\./, '');
                        row[newKey] = element;
                    }
                });
            });
        }

        //we need this because backend expects keys with 'cmis.' prefix
        function addPrefixInKey(cmisConfig) {
            angular.forEach(cmisConfig, function (element, key) {
                if (key.match('.') !== -1) {
                    delete cmisConfig[key];
                    var newKey = 'cmis.' + key;
                    cmisConfig[newKey] = element;
                }
            });
        }
    }]);
