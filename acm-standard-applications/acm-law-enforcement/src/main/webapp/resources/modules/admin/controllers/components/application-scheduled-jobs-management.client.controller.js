'use strict';

angular.module('admin').controller('Admin.ManageScheduledJobsController',
        [ '$scope', 'Helper.UiGridService', 'MessageService', 'UtilService', 'Util.DateService', 'Admin.ScheduledJobsManagementService', '$interval',
            function($scope, HelperUiGridService, MessageService, Util, UtilDateService, AdminScheduledJobsManagementService, $interval) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            $scope.config.$promise.then(function(config) {
                $scope.config = angular.copy(_.find(config.components, {
                    id: 'scheduledJobs'
                }));

                gridHelper.setColumnDefs($scope.config);
                gridHelper.setBasicOptions($scope.config);
                gridHelper.disableGridScrolling($scope.config);
                gridHelper.addButton($scope.config, "Run", "fa fa-play", "runJob", "isRunHidden");
                gridHelper.addButton($scope.config, "Pause", "fa fa-pause", "pauseJob", "isPauseHidden");
                gridHelper.addButton($scope.config, "Resume", "fa fa-forward", "resumeJob", "isResumeHidden");
                gridHelper.addButton($scope.config, "Spinner", "fa fa-circle-o-notch fa-spin", null, "spin");
            });

            function getScheduledJobs() {
                AdminScheduledJobsManagementService.getScheduledJobs().then(function(response) {
                  $scope.gridOptions.data = response.data;
                });
            }

            getScheduledJobs();

            var interval;

            var start = function() {
                stop();
                interval = $interval(getScheduledJobs, 5000);
            };

            var stop = function() {
                $interval.cancel(interval);
            };

            $scope.$on('$destroy', function() {
                stop();
            });

            start();

            $scope.isRunHidden = function(rowEntity) {
                return rowEntity.running;
            };

            $scope.isPauseHidden = function(rowEntity) {
                return !rowEntity.running;
            };

            $scope.isResumeHidden = function(rowEntity) {
                return !rowEntity.paused;
            };

            $scope.spin = function(rowEntity) {
                return !rowEntity.running;
            };

            $scope.runJob = function(rowEntity) {
                AdminScheduledJobsManagementService.runJob(rowEntity.jobName);
            };

            $scope.pauseJob = function(rowEntity) {
                AdminScheduledJobsManagementService.pauseJob(rowEntity.jobName);
            };

            $scope.resumeJob = function(rowEntity) {
                AdminScheduledJobsManagementService.resumeJob(rowEntity.jobName);
            };

        } ]);