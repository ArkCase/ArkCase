'use strict';

/**
 * @ngdoc service
 * @name services.service:SessionService
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/auth/session.client.service.js services/auth/session.client.service.js}
 *
 * The Session service detects session expiration and shows warning message
 */
angular.module('services').factory('SessionService', ['$injector',
    function ($injector) {
        var SESSION_TIMEOUT_COOKIE = 'ARKCASE_SESSION';
        var SESSION_CHECK_PERIOD = 30000;
        var SESSION_TIMEOUT = 3600000;

        // Dependencies
        var $cookies = null;
        var $interval = null;
        var MessageService = null;

        // Inject dependencies
        injectDependencies();

        $interval(checkSession, SESSION_CHECK_PERIOD);

        return {
            /**
             * @ngdoc method
             * @name update
             * @methodOf services.service:SessionService
             *
             * @description
             * Update session cookie.
             *
             */
            update: function() {
                var lastActionTime = new Date().getTime();
                $cookies.put(SESSION_TIMEOUT_COOKIE, lastActionTime);
            }
        };

        /**
         * Inject service dependencies
         */
        function injectDependencies(){
            if (!$cookies) {
                $cookies = $injector.get('$cookies');
            }
            if (!$interval) {
                $interval = $injector.get('$interval');
            }
            if (!MessageService) {
                MessageService = $injector.get('MessageService');
            }
        }

        /**
         * Check session expiration
         */
        function checkSession() {
            var nowTime = new Date().getTime();
            var lastActionTime = parseInt($cookies.get(SESSION_TIMEOUT_COOKIE));
            if (!isNaN(lastActionTime)) {
                if ((nowTime - lastActionTime) > SESSION_TIMEOUT) {
                    // Display warning message about expired session
                }
            }
        }
    }
]);