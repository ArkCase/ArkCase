'use strict';

angular.module('admin').controller('Admin.ObjectTitleConfigurationController', ['$scope','$translate', '$modal', 'Helper.UiGridService', 'Admin.ObjectTitleConfigurationService', 'MessageService', 'Object.LookupService', 'UtilService', 'Dialog.BootboxService',
    function ($scope, $translate, $modal, HelperUiGridService, AdminObjectTitleConfigurationService, MessageService, ObjectLookupService, Util, DialogService) {


        var gridHelper = new HelperUiGridService.Grid({
            scope: $scope
        });

        $scope.config.$promise.then(function(config) {
            var config = angular.copy(_.find(config.components, {
                id: 'objectTitleConfiguration'
            }));

            $scope.config = config;
            $scope.radioButtonTitle = config.radioButtonTitle;
            $scope.objectTypeValue = config.objectTypeValue;

            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
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

        ObjectLookupService.getObjectTitleTypes().then(function(objectTitleTypes) {
            $scope.objectTitleTypesDropdownOptions = objectTitleTypes;

        });
        $scope.objectTitleConfiguration = {
            objectTitleTypes: []
        };
        var reloadGrid = function() {
            $scope.gridOptions.data = $scope.objectTitleConfiguration.objectTitleTypes;
        };
        AdminObjectTitleConfigurationService.getObjectTitleConfiguration().then(function(response) {
            if (!Util.isEmpty(response.data)) {
                angular.forEach(response.data, function (value, key){
                    $scope.objectTitleConfiguration.objectTitleTypes.push({
                        objectType: key,
                        enableTitleField: value.enableTitleField,
                        title: value.title
                    })
                });
                reloadGrid();
            }
        });

        var saveConfig = function() {
            var model = {
                objectTitleConfig: {}
            };
            angular.forEach($scope.objectTitleConfiguration.objectTitleTypes, function (object) {
                model.objectTitleConfig[object.objectType] = {
                    enableTitleField: object.enableTitleField,
                    title: object.title
                };
            });

            AdminObjectTitleConfigurationService.saveObjectTitleConfiguration(model.objectTitleConfig).then(function(response) {
                MessageService.succsessAction($translate.instant("admin.application.objectTitleConfiguration.message.success"));
            }, function(error) {
                MessageService.errorAction($translate.instant("admin.application.objectTitleConfiguration.message.error"));
            });
        };

        function showModal( objectTitleTypes, isEdit, objectTitleTypesDropdownOptions) {
            var params = {};
            params.objectTitleTypes = objectTitleTypes;
            params.isEdit = isEdit;
            params.objectTitleTypesDropdownOptions = objectTitleTypesDropdownOptions;

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/application-object-title-configuration-modal.client.view.html',
                controller: 'Admin.ObjectTitleConfigurationModalController',
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

        $scope.addNew = function() {
            var objectTitleTypes = {
                objectType: '',
                enableTitleField: true,
                title: 'objectId'
            };

            showModal(objectTitleTypes, false, $scope.objectTitleTypesDropdownOptions).then(function(data) {
                var itemExist = false;
                var objectTitleElement;
                for (var i = 0; i < $scope.objectTitleConfiguration.objectTitleTypes.length; i++) {
                    objectTitleElement = $scope.objectTitleConfiguration.objectTitleTypes[i];
                    if (objectTitleElement.objectType === data.objectTitleTypes.objectType) {
                        itemExist = true;
                    } else if (objectTitleElement.objectType === data.objectTitleTypes.objectType) {

                    }
                }

                if (itemExist == false) {
                    $scope.objectTitleConfiguration.objectTitleTypes.push(data.objectTitleTypes);
                    reloadGrid();
                    saveConfig();
                } else {
                    DialogService.alert($translate.instant("admin.application.objectTitleConfiguration.message.exist1") + $translate.instant($scope.getObjectTypeValue(data.objectTitleTypes.objectType)) + $translate.instant("admin.application.objectTitleConfiguration.message.exist2"));
                }
            });
        };
        $scope.editRow = function(rowEntity) {
            showModal(rowEntity, true, $scope.objectTitleTypesDropdownOptions).then(function(data) {

                rowEntity.objectType = data.objectTitleTypes.objectType;
                rowEntity.enableTitleField = data.objectTitleTypes.enableTitleField;
                rowEntity.title = data.objectTitleTypes.title;

                reloadGrid();
                saveConfig();

            });
        };
        $scope.deleteRow = function(rowEntity) {
            _.remove($scope.gridOptions.data, function(item) {
                return item === rowEntity;
            });
            reloadGrid();
            saveConfig();
        };

        $scope.getTitleValue = function (key) {
            return $scope.radioButtonTitle[key];
        }

        $scope.getObjectTypeValue = function (key) {
            return $scope.objectTypeValue[key];
        }


    }]);