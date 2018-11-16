'use strict';

angular.module('queues').factory('Queues.QueuesConstants', function() {
    return {
        INTAKE: 1,
        FULFILL: 2,
        APPROVE: 3,
        GENERAL_COUNSEL: 4,
        BILLING: 5,
        RELEASE: 6,
        SUSPEND: 7,
        HOLD: 8,
        APPEAL: 9,
    }
});