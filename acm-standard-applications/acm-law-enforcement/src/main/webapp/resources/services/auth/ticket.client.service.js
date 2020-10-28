'use strict';

/**
 * @ngdoc service
 * @name services.service:TicketService
 *
 * @description
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/auth/ticket.client.service.js services/auth/ticket.client.service.js}
 *
 * The Ticket management service
 */
angular.module('services').factory('TicketService', [ '$http', function($http) {
    return {
        /**
         * @ngdoc method
         * @name getArkCaseTicket
         * @methodOf services.service:TicketService
         *
         * @description
         * Retrieves a new acm authentication ticket for the currently logged in user
         */
        getArkCaseTicket: function() {
            return $http({
                url: 'api/v1/authenticationtoken/',
                method: 'GET',
                data: ''
            });
        }
    }
} ]);