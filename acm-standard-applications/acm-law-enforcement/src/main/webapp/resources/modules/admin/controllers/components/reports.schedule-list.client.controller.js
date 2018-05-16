'use strict';

angular.module('admin').controller('Admin.ReportsScheduleListController',
        [ '$scope', '$q', '$translate', '$filter', 'LookupService', 'Util.DateService', 'Admin.ScheduleReportService', 'MessageService', 'Helper.UiGridService', function($scope, $q, $translate, $filter, LookupService, UtilDateService, ScheduleReportService, MessageService, HelperUiGridService) {

            $scope.$on('new-report-schedule', onCreatedReportSchedule);

            function onCreatedReportSchedule(e, data) {
                reloadGrid();
            }

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                enableSorting: false,
                enableColumnMenus: false,
                multiSelect: false,
                noUnselect: false,
                columnDefs: [],
                totalItems: 0,
                data: []
            };

            $scope.config.$promise.then(function(config) {
                var scheduleReportConfig = _.find(config.components, {
                    id: 'scheduleReportConfig'
                });
                var columnDefs = scheduleReportConfig.columnDefs;
                gridHelper.addDeleteButton(columnDefs, "grid.appScope.deleteRow(row.entity)");
                $scope.gridOptions.columnDefs = columnDefs;
                $scope.gridOptions.paginationPageSizes = scheduleReportConfig.paginationPageSizes;
                $scope.gridOptions.paginationPageSize = scheduleReportConfig.paginationPageSize;
                reloadGrid();
            });

            function reloadGrid() {
                ScheduleReportService.getSchedules().then(function(data) {
                    _.remove(data.job, function(reportSchedule) {
                        return ('PentahoSystemVersionCheck' === reportSchedule.jobName);
                    });
                    $scope.gridOptions.data = data.job;
                    $scope.gridOptions.totalItems = data.job.length;
                });
            }

            $scope.extractEmailAddresses = function(rowEntity) {
                var emailAddresses = "";
                var emailParam = _.find(rowEntity.jobParams.jobParams, {
                    name: '_SCH_EMAIL_TO'
                });
                if (emailParam && emailParam.value) {
                    emailAddresses = emailParam.value.replace(/;/g, "; ");
                }
                return emailAddresses;
            };

            $scope.deleteRow = function(rowEntity) {
                ScheduleReportService.deleteSchedule(rowEntity.jobId).then(function(data) {
                    gridHelper.deleteRow(rowEntity);
                    MessageService.info($translate.instant("admin.reports.schedule.delete.success"));
                }, function(errorData) {
                    MessageService.error($translate.instant("admin.reports.schedule.delete.error"));
                });
            };
        } ]);