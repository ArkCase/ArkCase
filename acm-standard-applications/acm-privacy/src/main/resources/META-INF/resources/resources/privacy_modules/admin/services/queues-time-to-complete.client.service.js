angular.module('admin').service('Admin.QueuesTimeToCompleteService', function($http) {
    return ({
        saveQueueConfig: saveQueueConfig,
        getQueuesConfig: getQueuesConfig
    });

    function saveQueueConfig(timeToComplete) {
        return $http({
            method: "POST",
            url: "api/latest/service/queues/time-to-complete",
            data: timeToComplete,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    function getQueuesConfig() {
        return $http({
            method: "GET",
            url: "api/latest/service/queues/time-to-complete"
        });
    }
});