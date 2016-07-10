'use strict';

angular.module("services").factory("WebSocketsListener", ['$q', '$timeout', 'Websockets.MessageHandler',
    function ($q, $timeout, messageHandler) {

        var service = {}, listener = $q.defer(), socket = {
            client: null,
            stomp: null
        }, messageIds = [];

        service.RECONNECT_TIMEOUT = 30000;
        service.SOCKET_URL = "/arkcase/stomp";
        service.LISTEN_TOPIC_OBJECTS = "/topic/objects/changed";
        service.MESSAGE_BROKER = "/app/print-message";
        service.shouldStart = true;

        service.receive = function () {
            return listener.promise;
        };

        service.send = function (message, destination) {
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

            socket.stomp.subscribe(service.LISTEN_TOPIC_OBJECTS, function (data) {
                var message = JSON.parse(data.body);
                $timeout(function () {
                    messageHandler.handleMessage(message);

                    //4 seconds delay so solr can index the object
                }, 4000);
            });

        };

        var initialize = function () {
            //console.log('initialize');
            socket.client = new SockJS(service.SOCKET_URL);
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.debug = null; //for those who want to disable the annoying debug messages
            //connect() with headers and success callback
            socket.stomp.connect({}, startListener);
            socket.stomp.onclose = reconnect;

        };

        //temp check to start/stop websockets, we should read it from config file
        if (service.shouldStart) {
            initialize();
        }

        return service;
    }]).run(function (WebSocketsListener) {
    //this how we bootstrap this service to be started when everything starts
    //we should check what is right approach to bootstrap the service
});
