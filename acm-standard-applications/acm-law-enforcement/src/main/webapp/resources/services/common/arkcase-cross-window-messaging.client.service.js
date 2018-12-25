'use strict';

/**
 * @ngdoc service
 * @name services.service:ArkCaseCrossWindowMessagingService

 * @description
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/common/arkcase-snowbound-messaging.client.service.js services/common/arkcase-cross-window-messaging.client.service.js}
 *
 * The ArkCaseCrossWindowMessagingService make communication between ArkCase and other component (iframe) on UI level
 *
 */
angular.module('services').factory('ArkCaseCrossWindowMessagingService', ['$interval', 'UtilService', function($interval, Util) {

    var _sender = null;
    var _receiver = null;
    var _messagingPromise = null;
    var _handlers = {};
    var _host = null;
    var _source = null;

    function start(source, host) {
        _source = source;
        _host = host;
        stop();
        _messagingPromise = $interval(init, 250);
    }

    function stop() {
        $interval.cancel(_messagingPromise);
    }

    function send(message) {
        if (!Util.isEmpty(_sender.send)) {
            _sender.send(message);
        }
    }

    function init() {
        var target = getTarget();
        if (!Util.isEmpty(target)) {
            stop();
            if (Util.isEmpty(_sender)) {
                _sender = {};
                _sender.target = target;
                _sender.send = function send(message) {
                    if (!Util.isEmpty(_sender.target)) {
                        _sender.target.postMessage(message, getTargetOrigin());
                    }
                };
            } else {
                _sender.target = target;
            }

            if (Util.isEmpty(_receiver)) {
                _receiver = {};
                _receiver.window = window;
                _receiver.receive = function receive(e) {
                    if (!Util.isEmpty(e) && !Util.isEmpty(e.data) && !Util.isEmpty(e.data.source) && e.data.source == _source && !Util.isEmpty(e.data.action)) {
                        var handler = _handlers[e.data.action];
                        if (!Util.isEmpty(handler)) {
                            handler(e.data.data);
                        }
                    }
                };

                _receiver.window.addEventListener("message", _receiver.receive);
            }
        }
    }

    function getTargetOrigin() {
        var targetOrigin = '*';
        if (!Util.isEmpty(_host)) {
            targetOrigin = _host;
        }
        return targetOrigin;
    }

    function getTarget() {
        var id = _source + 'Iframe';
        if (!Util.isEmpty(document) && !Util.isEmpty(document.getElementById(id)) && !Util.isEmpty(document.getElementById(id).contentWindow)) {
            return document.getElementById(id).contentWindow;
        }

        return null;
    }

    function addHandler(name, handler) {
        if (!Util.isEmpty(name) && !Util.isEmpty(handler)) {
            _handlers[name] = handler;
        }
    }

    return {

        /**
         * @ngdoc method
         * @name start
         * @methodOf services.service:ArkCaseCrossWindowMessagingService
         *
         * @param {String} source - The source from where ArkCase will receive messages
         * @param {String} host - The host of the iframe (if not defined, ‘*’ is default)
         *
         * @description
         * This method will start initiating ArkCase-Snowbound messaging
         */
        start: start,

        /**
         * @ngdoc method
         * @name send
         * @methodOf services.service:ArkCaseCrossWindowMessagingService
         *
         * @param {Object} message
         *
         * @description
         * This method will send message to snowbound instance
         */
        send: send,

        /**
         * @ngdoc method
         * @name addHandler
         * @methodOf services.service:ArkCaseCrossWindowMessagingService
         *
         * @param {String} name - name of the handler
         * @param {Method} handler - method definition with input parameter 'data' that represent object with data
         *
         * @description
         * This method will add handler in the list of handlers that will be used for catching messages sent from Snowbound to ArkCase by handler name
         */
        addHandler: addHandler
    };
} ]);
