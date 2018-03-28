'use strict';

angular
        .module('admin')
        .controller(
                'Admin.Holiday',
                [
                        '$scope',
                        '$modal',
                        'Helper.UiGridService',
                        'Admin.HolidayService',
                        'MessageService',
                        'UtilService',
                        'Dialog.BootboxService',
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

                            $scope.holidayConfig = {
                                holidays : []
                            };

                            var reloadGrid = function() {
                                $scope.gridOptions.data = $scope.holidayConfig.holidays;
                            };

                            AdminHolidayService.getHolidays().then(function(response) {
                                if (!Util.isEmpty(response.data)) {
                                    $scope.holidayConfig = response.data;

                                    reloadGrid();
                                }
                            });

                            var saveConfig = function() {
                                AdminHolidayService.saveHolidays($scope.holidayConfig).then(function() {
                                    MessageService.succsessAction();
                                    reloadGrid();
                                }, function() {
                                    MessageService.errorAction();
                                });
                            };

                            function showModal(holidays, isEdit) {
                                var params = {};
                                params.holidays = holidays;
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
                                var holidays = {};
                                showModal(holidays, false).then(function(data) {
                                    var element = data.holidays;
                                    var itemExist = _.find($scope.holidayConfig.holidays, function(holiday) {
                                        return element.holidayDate === holiday.holidayDate;
                                    });
                                    if (itemExist === undefined) {
                                        $scope.holidayConfig.holidays.push(element);
                                        saveConfig();
                                    } else {
                                        DialogService.alert($translate.instant('admin.application.holiday.message'));
                                    }
                                });
                            };

                            $scope.editRow = function(rowEntity) {
                                showModal(rowEntity, true)
                                        .then(
                                                function(data) {
                                                    var itemExist = false;
                                                    var element = $scope.holidayConfig.holidays;

                                                    var itemExist = _
                                                            .find(
                                                                    $scope.holidayConfig.holidays,
                                                                    function(holiday) {
                                                                        if (element.holidayDate === holiday.holidayDate
                                                                                && !(rowEntity.holidayName === holiday.holidayName && rowEntity.holidayDate === holiday.holidayDate)) {
                                                                            return true;
                                                                        }

                                                                    });

                                                    if (itemExist === false) {
                                                        rowEntity.holidayName = data.holidays.holidayName;
                                                        rowEntity.holidayDate = data.holidays.holidayDate;
                                                        saveConfig();
                                                    } else {
                                                        DialogService.alert($translate.instant('admin.application.holiday.message'));
                                                    }
                                                });
                            };

                            $scope.deleteRow = function(rowEntity) {
                                _.remove($scope.gridOptions.data, function(item) {
                                    return item === rowEntity;
                                });
                                saveConfig();
                            };

                        } ]);