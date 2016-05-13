'use strict';

angular.module("services").factory("WebSocketsListener", ['$q', '$timeout', 'MessageService', '$interval',
    function ($q, $timeout, messageService, $interval) {

        var service = {}, listener = $q.defer(), socket = {
            client: null,
            stomp: null
        }, messageIds = [];

        service.RECONNECT_TIMEOUT = 30000;
        service.SOCKET_URL = "/arkcase/stomp";
        service.LISTEN_TOPIC = "/topic/message";
        service.LISTEN_TOPIC_OBJECTS = "/topic/objects/*";
        service.MESSAGE_BROKER = "/app/print-message";

        service.receive = function () {
            return listener.promise;
        };

        service.send = function (message,destination) {
            var id = Math.floor(Math.random() * 1000000);
            socket.stomp.send(destination, {
                priority: 9
            }, JSON.stringify(message));
            messageIds.push(id);
        };

        var reconnect = function () {
            $timeout(function () {
                initialize();
            }, this.RECONNECT_TIMEOUT);
        };

        var startListener = function (frame) {
            messageService.info("CONNECTED " + frame);
            //subscribe to the topic
            socket.stomp.subscribe(service.LISTEN_TOPIC, function (data) {
                messageService.info(data);
            });
            socket.stomp.subscribe(service.LISTEN_TOPIC_OBJECTS, function (data) {
                messageService.info(data);
            });
            //create timer for sending messaged to the broker
            $interval(function () {
                var message = {};
                message.text = "Just a sample text with current time " + new Date();
                service.send(message)
            }, 10000, 0);
        };

        var initialize = function () {
            socket.client = new SockJS(service.SOCKET_URL);
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.connect({}, startListener);
            socket.stomp.onclose = reconnect;
        };

        initialize();
        return service;
    }]).run(function (WebSocketsListener) {
    //this how we are bootstrap this servis to be started when everything starts
});
