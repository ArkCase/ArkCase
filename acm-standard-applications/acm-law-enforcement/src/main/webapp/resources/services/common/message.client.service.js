'use strict';

/**
 * @ngdoc service
 * @name services.service:MessageService

 * @description
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/common/message.client.service.js services/common/message.client.service.js}
 *
 * The MessageService displays notify messages
 *
 */
angular.module('services').factory('MessageService', ['$injector', '$translate', 'Acm.StoreService', 'UtilService', 'Util.TimerService',
    function ($injector, $translate, Store, Util, TimerService) {
        var MSG_SHELF_LIFE = 5000; //5 seconds
        var notify = null;
        var ConfigService = null;
        var defaultOptions = {};
        var msgCache = new Store.CacheFifo("AcmNotifyMessages");

        /**
         * Displays notify message
         * @param msg
         * @param customOptions
         */
        function showMessage(msg, customOptions) {
            if (notify) {
                var notifyData = _.extend({}, defaultOptions, customOptions);

                if (Util.goodValue(notifyData.allowDuplicate, false)) {
                    notifyData.message = msg;
                    notify(notifyData);

                } else {
                    var nowDate = new Date();
                    var nowTimestamp = nowDate.getTime();
                    var lastTimestamp = Util.goodNumber(msgCache.get(msg), 0);
                    if (MSG_SHELF_LIFE < nowTimestamp - lastTimestamp) {
                        msgCache.put(msg, nowTimestamp);
                        notifyData.message = msg;
                        notify(notifyData);
                    }
                }
            }
        }

        /**
         * Initialise services and read configuration. We use $injector to avoid 'circular dependency issue'
         */
        function init() {
            if (!ConfigService) {
                ConfigService = $injector.get('ConfigService');
                ConfigService.getModule({moduleId: 'common'}, function (config) {
                    defaultOptions = Util.goodMapValue(config, "notifyMessage", {});
                });
            }

            if (!notify) {
                notify = $injector.get('notify');
            }
        }

        TimerService.useTimer("initMessageService"
            , 50     //delay 50 milliseconds
            , function () {
                init();
                return false;
            }
        );


        return {

            /**
             * @ngdoc method
             * @name error
             * @methodOf services.service:MessageService
             *
             * @param {String} message Displayed error message
             *
             * @description
             * This method displays error message in notify popup window.
             */
            error: function (message) {
                showMessage(message);
            },

            /**
             * @ngdoc method
             * @name httpError
             * @methodOf services.service:MessageService
             *
             * @param {HttpResponse} httpResponse Http response
             *
             * @description
             * This method takes information from httpResponse and displays notify error message
             */
            httpError: function (response) {
                var defaultOptions = {};
                if (response && response.config) {
                    var msg = '';
                    if (response.status == 503) {
                        msg = $translate.instant('common.service.messageService.authorizationError');
                    } else {
                        // TODO: Use templates for different types of errors
                        msg = [
                            'ERROR: ',
                            response.config.url,
                            ' ',
                            response.status
                        ].join('');
                    }

                    showMessage(msg);
                }
            },

            /**
             * @ngdoc method
             * @name info
             * @methodOf services.service:MessageService
             *
             * @param {String} message Displayed info message
             *
             * @description
             * This method displays info message in notify popup window.
             */
            info: function (message) {
                // TODO: create templates for info and error notify windows
                showMessage(message, {position: 'left'});
            },
            
            /**
             * @ngdoc method
             * @name succsessAction
             * @methodOf services.service:MessageService
             *
             * @description
             * This method displays succcess action message in notify popup window.
             */
            succsessAction: function(){
            	showMessage($translate.instant('common.service.messageService.actionMessages.success'), {position: 'left'});
            },
            
            /**
             * @ngdoc method
             * @name errorAction
             * @methodOf services.service:MessageService
             *
             * @description
             * This method displays error action message in notify popup window.
             */
            errorAction: function(){
            	showMessage($translate.instant('common.service.messageService.actionMessages.error'), {position: 'left'});
            },
        };
    }
]);
