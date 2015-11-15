'use strict';

/**
 * @ngdoc service
 * @name services.service:MessageService

 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/message.client.service.js services/common/message.client.service.js}
 *
 * The MessageService displays notify messages
 *
 */

// Authentication service for user variables
angular.module('services').factory('MessageService', ['$injector', '$translate',
    function ($injector, $translate) {
        var notify = null;
        var ConfigService = null;
        var config = null;
        var notifyOptions = {};


        /**
         * Displays notify message
         * @param msg
         * @param notifyOptions
         */
        function showMessage(msg, customOptions) {
            if (notify) {
                var notifyData = {} || customOptions;
                notifyData = _.extend(notifyOptions, customOptions);
                notifyData.message = msg;
                notify(notifyData);
            }
        }

        /**
         * Inject config and notify dependencies and show Message
         * @param msg
         * @param notifyOptions
         */
        function showNotifyMessage(msg, customOptions) {
            // We use injector to avoid 'circular dependency issue'
            if (!ConfigService) {
                ConfigService = $injector.get('ConfigService');
                config = ConfigService.getModule({moduleId: 'common'}, function (config) {
                    if (config.notifyMessage) {
                        notifyOptions = config.notifyMessage;

                        if (!notify) {
                            notify = $injector.get('notify');
                            showMessage(msg, customOptions);
                        }
                    }
                });
            } else {
                showMessage(msg, customOptions);
            }
        }

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
                showNotifyMessage(message);
            },

            /**
             * @ngdoc method
             * @name httpError
             * @methodOf services.service:MessageService
             *
             * @param {HttpResponse} httpResponse Http resposne
             *
             * @description
             * This method takes information from httpResponse and displays notify error message
             */
            httpError: function (response) {
                var notifyOptions = {};
                if (response && response.config) {
                    var msg = '';
                    if (response.status == 503) {
                        // TODO find way to preload common resources
                        //msg = $translate.instant('common.service.messageService.authorizationError');
                        msg = 'You are not authorized to take this action';
                    } else {
                        // TODO: Use templates for different types of errors
                        msg = [
                            'ERROR: ',
                            response.config.url,
                            ' ',
                            response.status
                        ].join('');
                    }

                    showNotifyMessage(msg);
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
                showNotifyMessage(message, {position: 'left'});
            }
        };
    }
]);