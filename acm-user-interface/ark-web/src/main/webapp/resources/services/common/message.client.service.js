'use strict';

// Authentication service for user variables
angular.module('services').factory('MessageService', ['$injector',
    function ($injector) {
        var notify = null;
        var ConfigService = null;
        var config = null;
        var notifyOptions = {};

        return {
            /**
             * Display http error
             * @param response
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