'use strict';

/**
 * @ngdoc service
 * @name services.MessageService

 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/message.client.service.js services/common/message.client.service.js}
 *
 * The MessageService displays notify messages
 *
 */

// Authentication service for user variables
angular.module('services').factory('MessageService', ['$injector',
    function ($injector) {
        var notify = null;
        var ConfigService = null;
        var config = null;
        var notifyOptions = {};

        return {

            /**
             * @ngdoc method
             * @name httpError
             * @methodOf services.MessageService
             *
             * @param {HttpResponse} httpResponse Http resposne
             *
             * @description
             * This method takes information from httpResponse and displays Notify error message
             */
            httpError: function (response) {
                var showNotifyMessage = function (msg) {
                    if (notify) {
                        var notifyData = _.clone(notifyOptions);
                        notifyData.message = msg;
                        notify(notifyData);
                    }
                };

                if (response && response.config) {

                    // TODO: Use templates for different types of errors
                    var msg = [
                        'ERROR: ',
                        response.config.url,
                        ' ',
                        response.status
                    ].join('');


                    // We use injector to avoid 'circular dependency issue'
                    if (!ConfigService) {
                        ConfigService = $injector.get('ConfigService');
                        config = ConfigService.getModule({moduleId: 'common'}, function (config) {
                            if (config.notifyMessage) {
                                notifyOptions = config.notifyMessage;

                                if (!notify) {
                                    notify = $injector.get('notify');
                                    showNotifyMessage(msg);
                                }
                            }
                        });
                    }

                    showNotifyMessage(msg);
                }
            }
        };
    }
]);