'use strict';

angular.module('admin').controller('Admin.ManageScheduledJobsController',
    ['$scope', '$translate', 'Helper.UiGridService', 'MessageService',
        'UtilService', 'Util.DateService', 'Admin.ScheduledJobsManagementService',
        function ($scope, $translate, HelperUiGridService, MessageService, Util, UtilDateService, AdminScheduledJobsManagementService) {

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            $scope.config.$promise.then(function (config) {
                $scope.config = angular.copy(_.find(config.components, {
                    id: 'scheduledJobs'
                }));

                gridHelper.setColumnDefs($scope.config);
                gridHelper.setBasicOptions($scope.config);
                gridHelper.disableGridScrolling($scope.config);

                var runBtnTitle = $translate.instant('admin.application.scheduled.jobs.btn.run');
                var spinBtnTitle = $translate.instant('admin.application.scheduled.jobs.btn.spin');
                var pauseBtnTitle = $translate.instant('admin.application.scheduled.jobs.btn.pause');
                var resumeBtnTitle = $translate.instant('admin.application.scheduled.jobs.btn.resume');

                gridHelper.addButton($scope.config, "Run", "fa fa-play", "runJob", "hideRun", runBtnTitle);
                gridHelper.addButton($scope.config, "Spinner", "fa fa-circle-o-notch fa-spin",
                    null, "hideSpin", spinBtnTitle);
                gridHelper.addButton($scope.config, "Pause", "fa fa-pause", "pauseJob",
                    "hidePause", pauseBtnTitle);
                gridHelper.addButton($scope.config, "Resume", "fa fa-forward", "resumeJob",
                    "hideResume", resumeBtnTitle);
            });

            var jobStateMap = {};

            function getScheduledJobs() {
                AdminScheduledJobsManagementService.getScheduledJobs().then(function (response) {
                    jobStateMap = response.data;
                    $scope.gridOptions.data = _.values(jobStateMap);
                });
            }

            getScheduledJobs();

            $scope.$bus.subscribe("scheduled-jobs-status-update", function (message) {
                var jobState = message.jobState;
                $scope.$apply(function () {
                    var oneTimeJobDone = !jobState.nextRun && !jobState.running && !jobState.paused;
                    var deletedJob = !jobState.triggerName;
                    if (deletedJob || oneTimeJobDone) {
                        delete jobStateMap[jobState.triggerName];
                    } else {
                        var job = jobStateMap[jobState.triggerName];
                        if (!job) {
                            jobStateMap[jobState.triggerName] = jobState;
                        } else {
                            job.running = jobState.running;
                            job.paused = jobState.paused;
                            job.lastRun = jobState.lastRun;
                            job.nextRun = jobState.nextRun;
                        }
                    }
                    $scope.gridOptions.data = _.values(_.sortBy(jobStateMap, function (job) {
                        return job.jobName;
                    }));
                });
            });

            $scope.hideRun = function (rowEntity) {
                return rowEntity.running || rowEntity.paused;
            };

            $scope.hidePause = function (rowEntity) {
                var oneTimeJobRunning = rowEntity.running && !rowEntity.nextRun;
                return rowEntity.paused || oneTimeJobRunning;
            };

            $scope.hideResume = function (rowEntity) {
                return !rowEntity.paused;
            };

            $scope.hideSpin = function (rowEntity) {
                return !rowEntity.running;
            };

            $scope.runJob = function (rowEntity) {
                AdminScheduledJobsManagementService.runJob(rowEntity.jobName);
            };

            $scope.pauseJob = function (rowEntity) {
                AdminScheduledJobsManagementService.pauseJob(rowEntity.jobName);
            };

            $scope.resumeJob = function (rowEntity) {
                AdminScheduledJobsManagementService.resumeJob(rowEntity.jobName);
            };

        } ]);