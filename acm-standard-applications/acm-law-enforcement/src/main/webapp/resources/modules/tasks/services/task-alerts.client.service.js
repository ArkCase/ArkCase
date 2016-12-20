'use strict';

/**
 * @ngdoc service
 * @name services:Task.AlertsService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/jsapn/blob/develop/jsapn/jsap-user-interface/src/main/resources/META-INF/resources/resources/custom_services/jsap/task-alerts.client.service.js custom_services/jsap/task-alerts.client.service.js}
 *
 * This service is used to make calculations for overdue or deadline information about tasks
 */

angular.module('tasks').factory('Task.AlertsService', function () {

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
            , calculateOverdue: function (dueDate) {
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
            , calculateDeadline: function (dueDate) {
                var today = new Date();
                today.setHours(0, 0, 0, 0);
                var deadline = new Date();
                deadline.setDate(today.getDate() + Service.AlertsConfig.DEADLINE_ALERT_DAYS + 1);
                deadline.setHours(0, 0, 0, 0);

                if (dueDate >= today && dueDate < deadline) {
                    return true;
                }

                return false;
            }
        };

        return Service;
    }
);