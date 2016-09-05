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
            if(!socket.stomp.connected) {
            return;
            }
            var id = Math.floor(Math.random() * 1000000);
            socket.stomp.send(destination, {
                priority: 9
            }, JSON.stringify(message));
            messageIds.push(id);
        };
        
        var connectCallback = function (frame) {
            socket.stomp.subscribe(service.LISTEN_TOPIC_OBJECTS, function (data) {
                var message = JSON.parse(data.body);
                $timeout(function () {
                    messageHandler.handleMessage(message);
                    //4 seconds delay so solr can index the object
                }, 4000);
            });
        };
        
        var errorCallback = function (error) {
            console.log("WS error",error);
            if(!socket.stomp.connected) {
                initialize(); // reconnect on error
            }
        };
        
        var connect = function () {
            console.log("WS init");
            socket.client = new SockJS(service.SOCKET_URL);
            socket.stomp = Stomp.over(socket.client);
            //socket.stomp.debug = null; //frequently need to disable stomp msg for debugging, please do not delete this line
            socket.stomp.connect({}, connectCallback, errorCallback);
            socket.stomp.ws.onclose = connect;
        };


        if (service.shouldStart) {
            setTimeout(connect, 2000); // give it time to load UI
        }
        
        return service;

    }]).run(function (WebSocketsListener) {
});
