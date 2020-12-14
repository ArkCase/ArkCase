'use strict';

/**
 * @ngdoc service
 * @name services:Task.AlertsService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/tasks/services/task-alerts.client.service.js modules/tasks/services/task-alerts.client.service.js}
 *
 * This service is used to make calculations for overdue or deadline information about tasks
 */

angular.module('tasks').factory('Task.AlertsService', function() {

    var Service = {

        AlertsConfig: {
            DEADLINE_ALERT_DAYS: 1
        }

        /**
         * @ngdoc method
         * @name calculateOverdue
         * @methodOf services:Task.AlertsService
         *
         * @description
         * Return if date is overdue.
         */
        ,
        calculateOverdue: function(dueDate) {
            //for all tasks that suspense date was before today we will show overdue alert
            var today = new Date();
            today.setHours(0, 0, 0, 0);

            if (dueDate < today) {
                return true;
            }

            return false;
        }
        /**
         * @ngdoc method
         * @name calculateDeadline
         * @methodOf services:Task.AlertsService
         *
         * @description
         * Return if date is approaching deadline.
         */
        ,
        calculateDeadline: function(dueDate) {
            return Service.deadlineCalculate(dueDate, Service.AlertsConfig.DEADLINE_ALERT_DAYS);
        },

        deadlineCalculate: function(dueDate, days) {
            var today = new Date();
            today.setHours(0, 0, 0, 0);
            var deadline = new Date();
            deadline.setDate(today.getDate() + days + 1);
            deadline.setHours(0, 0, 0, 0);

            if (dueDate >= today && dueDate < deadline) {
                return true;
            }

            return false;
        }

    };

    return Service;
});