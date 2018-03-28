'use strict';

angular.module('admin').controller(
        'Admin.Holiday',
        [ '$scope', '$modal', 'Helper.UiGridService', 'Admin.HolidayService', 'MessageService', 'UtilService', 'Dialog.BootboxService',
                '$translate',
                function($scope, $modal, HelperUiGridService, AdminHolidayService, MessageService, Util, DialogService, $translate) {

                    var gridHelper = new HelperUiGridService.Grid({
                        scope : $scope
                    });

                    $scope.config.$promise.then(function(config) {
                        var config = angular.copy(_.find(config.components, {
                            id : 'holiday'
                        }));

                        $scope.config = config;

                        gridHelper.addButton(config, "edit");
                        gridHelper.addButton(config, "delete");
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);

                        $scope.gridOptions = {
                            enableColumnResizing : true,
                            enableRowSelection : true,
                            enableRowHeaderSelection : false,
                            multiSelect : false,
                            noUnselect : false,
                            columnDefs : $scope.config.columnDefs,
                            paginationPageSizes : $scope.config.paginationPageSizes,
                            paginationPageSize : $scope.config.paginationPageSize,
                            totalItems : 0,
                            data : []
                        };
                    });

                    var reloadGrid = function(data) {
                        $scope.gridOptions.data = data;
                    };

                    AdminHolidayService.getHolidays().then(function(response) {
                        if (!Util.isEmpty(response.data)) {

                            reloadGrid(response.data);
                        }
                    });

                    var saveConfig = function(holidayConfig) {
                        AdminHolidayService.saveHolidays(holidayConfig).then(function(data) {
                            MessageService.succsessAction();
                            reloadGrid(data.config.data);
                        }, function() {
                            MessageService.errorAction();
                        });
                    };

                    function showModal(holiday, isEdit) {
                        var params = {};
                        params.holiday = holiday;
                        params.isEdit = isEdit;

                        var modalInstance = $modal.open({
                            animation : true,
                            templateUrl : 'modules/admin/views/components/application-holiday.config.modal.client.view.html',
                            controller : 'Admin.HolidayModalController',
                            size : 'md',
                            backdrop : 'static',
                            resolve : {
                                params : function() {
                                    return params;
                                }
                            }
                        });
                        return modalInstance.result;
                    }

                    $scope.addNew = function() {
                        var holiday = {};
                        showModal(holiday, false).then(function(data) {
                            var element = data;
                            var holidayConfig = $scope.gridOptions.data;
                            var itemExist = _.find(holidayConfig, function(holiday) {
                                return element.holidayDate === holiday.holidayDate;
                            });
                            if (itemExist === undefined) {
                                holidayConfig.push(element);
                                saveConfig(holidayConfig);
                            } else {
                                DialogService.alert($translate.instant('admin.application.holiday.message'));
                            }
                        });
                    };

                    $scope.editRow = function(rowEntity) {

                        var entity = angular.copy(rowEntity);
                        showModal(entity, true).then(function(data) {
                            var element = data;

                            var itemExist = _.find($scope.gridOptions.data, function(holiday) {
                                return (element.holidayDate === holiday.holidayDate && holiday.holidayName === element.holidayName);
                            });

                            if (!itemExist) {
                                rowEntity.holidayName = data.holidayName;
                                rowEntity.holidayDate = data.holidayDate;
                                var holidayConfig = $scope.gridOptions.data;
                                saveConfig(holidayConfig);
                            } else {
                                DialogService.alert($translate.instant('admin.application.holiday.message'));
                            }
                        });
                    };

                    $scope.deleteRow = function(rowEntity) {
                        var holidayConfig = angular.copy($scope.gridOptions.data);
                        _.remove(holidayConfig, function(item) {
                            return item.holidayDate === rowEntity.holidayDate;
                        });
                        saveConfig(holidayConfig);
                    };

                } ]);