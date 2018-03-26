'use strict';

angular
        .module('admin')
        .controller(
                'Admin.HolidaySchedule',
                [
                        '$scope',
                        '$modal',
                        'Helper.UiGridService',
                        'Admin.HolidayScheduleService',
                        'MessageService',
                        'UtilService',
                        'Dialog.BootboxService',
                        '$translate',
                        function($scope, $modal, HelperUiGridService, AdminHolidayScheduleService, MessageService, Util, DialogService,
                                $translate) {

                            var gridHelper = new HelperUiGridService.Grid({
                                scope : $scope
                            });

                            $scope.config.$promise.then(function(config) {
                                var config = angular.copy(_.find(config.components, {
                                    id : 'holidaySchedule'
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

                            $scope.holidayScheduleConfig = {
                                schedule : []
                            };

                            var reloadGrid = function() {
                                $scope.gridOptions.data = $scope.holidayScheduleConfig.schedule;
                            };

                            AdminHolidayScheduleService.getHolidaySchedule().then(function(response) {
                                if (!Util.isEmpty(response.data)) {
                                    $scope.holidayScheduleConfig = response.data;

                                    reloadGrid();
                                }
                            });

                            var saveConfig = function() {
                                AdminHolidayScheduleService.saveHolidaySchedule($scope.holidayScheduleConfig).then(function() {
                                    MessageService.succsessAction();
                                }, function() {
                                    MessageService.errorAction();
                                });
                            };

                            function showModal(schedule, isEdit) {
                                var params = {};
                                params.schedule = schedule;
                                params.isEdit = isEdit;

                                var modalInstance = $modal
                                        .open({
                                            animation : true,
                                            templateUrl : 'modules/admin/views/components/application-holiday-schedule.config.modal.client.view.html',
                                            controller : 'Admin.HolidayScheduleModalController',
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
                                var schedule = {
                                    holidayName : '',
                                    holidayDate : ''
                                };

                                showModal(schedule, false).then(
                                        function(data) {
                                            var itemExist = false;
                                            var elements;
                                            for (var i = 0; i < $scope.holidayScheduleConfig.schedule.length; i++) {
                                                elements = $scope.holidayScheduleConfig.schedule[i];
                                                if (elements.holidayName === data.schedule.holidayName
                                                        || elements.holidayDate === data.schedule.holidayDate) {
                                                    itemExist = true;
                                                    if (itemExist) {
                                                        break;
                                                    }
                                                }
                                            }

                                            if (itemExist === false) {
                                                $scope.holidayScheduleConfig.schedule.push(data.schedule);
                                                reloadGrid();
                                                saveConfig();
                                            } else {
                                                DialogService.alert($translate.instant('admin.application.holidaySchedule.message'));
                                            }
                                        });
                            };

                            $scope.editRow = function(rowEntity) {
                                showModal(rowEntity, true).then(
                                        function(data) {
                                            var itemExist = false;
                                            var elements;
                                            for (var i = 0; i < $scope.holidayScheduleConfig.schedule.length; i++) {
                                                elements = $scope.holidayScheduleConfig.schedule[i];
                                                if (rowEntity.holidayName === elements.holidayName) {
                                                } else {
                                                    if (elements.holidayName === data.schedule.holidayName
                                                            || elements.holidayDate === data.schedule.holidayDate) {
                                                        itemExist = true;
                                                        if (itemExist) {
                                                            break;

                                                        }
                                                    }
                                                }
                                            }
                                            if (itemExist === false) {
                                                rowEntity.holidayName = data.schedule.holidayName;
                                                rowEntity.holidayDate = data.schedule.holidayDate;

                                                reloadGrid();
                                                saveConfig();
                                            } else {
                                                DialogService.alert($translate.instant('admin.application.holidaySchedule.message'));
                                            }
                                        });
                            };

                            $scope.deleteRow = function(rowEntity) {
                                _.remove($scope.gridOptions.data, function(item) {
                                    return item === rowEntity;
                                });
                                reloadGrid();
                                saveConfig();
                            };

                        } ]);