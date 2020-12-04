'use strict';

angular.module('admin').controller('Admin.ReportsScheduleListController',
    ['$scope', '$modal', '$q', '$translate', '$filter', 'LookupService', 'Util.DateService', 'Admin.ScheduleReportService', 'MessageService', 'Helper.UiGridService', 'UtilService',
        function ($scope, $modal, $q, $translate, $filter, LookupService, UtilDateService, ScheduleReportService, MessageService, HelperUiGridService, UtilService) {

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
                gridHelper.addEditButton(columnDefs, "grid.appScope.editRow(row.entity)");
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
                    $scope.gridOptions.totalItems = !UtilService.isEmpty(data.job) ? data.job.length : [];
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

            $scope.editRow = function(rowEntity) {
                var emailAddresses = _.find(rowEntity.jobParams.jobParams, function(row) {
                    if (row.name == '_SCH_EMAIL_TO') {
                        return row.value;
                    }
                });
                var startDate = _.find(rowEntity.jobParams.jobParams, function(row) {
                    if (row.name == 'startDate') {
                        return row.value;
                    }
                });
                var endDate = _.find(rowEntity.jobParams.jobParams, function(row) {
                    if (row.name == 'endDate') {
                        return row.value;
                    }
                });
                var report = {
                    reportFile: rowEntity.jobName,
                    reportRecurrence: rowEntity.jobTrigger.uiPassParam,
                    reportStartDate: UtilDateService.isoToDate(rowEntity.jobTrigger.startTime),
                    reportTime: rowEntity.jobTrigger.startTime,
                    reportEndDate: rowEntity.jobTrigger.endTime ? UtilDateService.isoToDate(rowEntity.jobTrigger.endTime) : "",
                    filterStartDate: UtilDateService.isoToDate(startDate.value),
                    filterEndDate: UtilDateService.isoToDate(endDate.value),
                    outputFormat: '',
                    reportEmailAddresses: emailAddresses ? emailAddresses.value : "",
                    jobId: rowEntity.jobId
                };
                $scope.showModal(report, true);

            };

            $scope.showModal = function(reportSchedule, isEdit) {
                var params = {};
                params.reportSchedule = reportSchedule;
                params.isEdit = isEdit;

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/admin/views/components/reports.schedule.modal.client.view.html',
                    controller: 'Admin.ReportsScheduleModalController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function(data) {
                    var objectToSubmit = {
                        jobName: data.reportSchedule.reportFile,
                        reportFile: data.reportSchedule.reportFile,
                        uiPassParam: data.reportSchedule.reportRecurrence,
                        startTime: moment(data.reportSchedule.reportStartDate).format("YYYY-MM-DDTHH:mm:ss.SSS") + "Z",
                        endTime: data.reportSchedule.reportEndDate ? moment(data.reportSchedule.reportEndDate).format("YYYY-MM-DDTHH:mm:ss.SSS") + "Z" : "", // schedule end date is optional
                        outputFileType: data.reportSchedule.outputFormat,
                        emails: data.reportSchedule.reportEmailAddresses,
                        filterStartDate: UtilDateService.goodIsoDate(data.reportSchedule.filterStartDate),
                        filterEndDate: UtilDateService.goodIsoDate(data.reportSchedule.filterEndDate)
                    };
                    var jsonToSubmit = JSON.stringify(objectToSubmit);

                    if (!data.isEdit) {
                        ScheduleReportService.scheduleReport(jsonToSubmit).then(function(data) {
                            $scope.reportEndDate = undefined;
                            $scope.reportEmailAddresses = '';
                            MessageService.info($translate.instant("admin.reports.schedule.createScheduleSuccess"));
                            $scope.$emit('created-report-schedule', data);
                        }, function(error) {
                            MessageService.error($translate.instant(!UtilService.isEmpty(error.data.error) ? "admin.reports.schedule." + error.data.error : "admin.reports.schedule.createScheduleFailure"));
                        });

                    } else {
                        ScheduleReportService.deleteSchedule(data.reportSchedule.jobId).then(function(data) {
                            gridHelper.deleteRow(data);
                            ScheduleReportService.scheduleReport(objectToSubmit).then(function(data) {
                                $scope.$emit('created-report-schedule', data);
                                MessageService.info($translate.instant("admin.reports.schedule.update.success"));
                            }, function(error) {
                                MessageService.error($translate.instant(!UtilService.isEmpty(error.data.error) ? "admin.reports.schedule." + error.data.error : "admin.reports.schedule.createScheduleFailure"));
                            })
                        }, function(errorData) {
                            MessageService.error($translate.instant(!UtilService.isEmpty(errorData.data.error) ? "admin.reports.schedule." + errorData.data.error : "admin.reports.schedule.createScheduleFailure"));
                        });
                    }
                });

            };

        } ]);