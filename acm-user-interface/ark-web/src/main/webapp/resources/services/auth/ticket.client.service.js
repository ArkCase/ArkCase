'use strict';

angular.module('services').factory('TicketService', ['$http',
    function ($http) {
        return {
            /**
              * @ngdoc method
              * @name getArkCaseTicket
              * @methodOf TicketService
              *
              * @description
              * Retrieves a new acm authentication ticket for the currently logged in user
              */
            getArkCaseTicket: function () {
                return $http({
                    url: 'proxy/arkcase/api/v1/authenticationtoken/',
                    method: 'GET',
                    data: ''
                });
            },
        }
    }
]);