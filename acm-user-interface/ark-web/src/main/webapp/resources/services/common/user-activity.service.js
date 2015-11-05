'use strict';

/**
 * @ngdoc service
 * @name services.service:UserActivityService
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/auth/session.client.service.js services/auth/session.client.service.js}
 *
 * The UserActivity handles user's mouse and keyboard events and fire event if no activity more that specified period.
 * Only one userActivity service can be active at same time
 *
 * @example
 <example>
     <file name="my-module.js">
         angular.module('my-module').controller('MyModuleController', ['$scope', '$log', 'UserActivityService',
         function ($scope, $log, UserActivityService) {

                // Be sure that service is stopped after module destroyed
                $scope.$on('$destroy', function () {
                    UserActivityService.stop();
                });

                // Set user activity timeout to 10 seconds
                UserActivityService.start(10000, userActivityTimeout);


                function userActivityTimeout () {
                    $log.error('User activity timeout expired');
                }
           }
         ]);
     </file>
     <file name="my-module.html">
         <div ng-controller="MyModuleController"></div>
     </file>
 </example>
 */
angular.module('services').factory('UserActivityService', ['$document', '$timeout',
    function ($document, $timeout) {

        // Events handling by service
        var userEvents = [
            'keydown',
            'keyup',
            'click',
            'mousemove',
            'DOMMouseScroll',
            'mousewheel',
            'mousedown',
            'touchstart',
            'touchmove',
            'scroll',
            'focus'
        ];

        var timeout = 0;
        var timer = null;
        var callbacFunc = null;

        var api = {

            /**
             * @ngdoc method
             * @name start
             * @methodOf services.service:UserActivityService
             *
             * @param {Number} timeout User activity timeout (milliseconds)
             * @param {Function} callback Callback function that executes after timeout expired
             *
             * @description
             * Start user activity monitoring
             */
            start: function (newTimeout, newCallback) {
                this.stop();

                _.forEach(userEvents, function (event) {
                    $document.bind(event, userAction);
                });

                timeout = newTimeout;
                callbacFunc = newCallback;

                updateTimer();
            },

            /**
             * @ngdoc method
             * @name stop
             * @methodOf services.service:UserActivityService
             *
             * @description
             * Stop user activity monitoring
             */
            stop: function () {
                _.forEach(userEvents, function (event) {
                    $document.unbind(event, userAction);
                });

                $timeout.cancel(timer);
                timer = null;
            }
        };

        return api;


        /**
         * Executed every time when user action events fired
         */
        function userAction() {
            updateTimer();
        }

        /**
         * Reset timer every time when user perform any action
         */
        function updateTimer() {
            $timeout.cancel(timer);
            timer = $timeout(timeoutCallback, timeout);
        }

        /**
         * Performs timeout reset and execute callback function on timeout
         */
        function timeoutCallback(){
            api.stop();
            callbacFunc();
        }
    }
]);