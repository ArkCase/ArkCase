'use strict';

angular.module('admin').factory('Admin.ScheduledJobsManagementService', [ '$http', function($http) {
    return {
        getScheduledJobs: getScheduledJobs,
        runJob: runJob,
        pauseJob: pauseJob,
        resumeJob: resumeJob
    };

    function getScheduledJobs() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/admin/scheduler/jobs"
        });
    }

    function runJob(name) {
        return $http({
            method: "PUT",
            url: "api/latest/plugin/admin/scheduler/jobs/" + name + "/run"
        });
    }

    function pauseJob(name) {
        return $http({
            method: "PUT",
            url: "api/latest/plugin/admin/scheduler/jobs/" + name + "/pause"
        });
    }

    function resumeJob(name) {
        return $http({
            method: "PUT",
            url: "api/latest/plugin/admin/scheduler/jobs/" + name + "/resume"
        });
    }

} ]);