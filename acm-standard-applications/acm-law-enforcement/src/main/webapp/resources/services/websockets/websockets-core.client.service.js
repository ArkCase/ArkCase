'use strict';

angular.module("services").factory("WebSocketsListener", ['$q', '$timeout', 'Websockets.MessageHandler',
    function ($q, $timeout, messageHandler) {
  
        var service = {

            RECONNECT_TIMEOUT : 500,
            SOCKET_URL : "/arkcase/stomp",
            LISTEN_TOPIC_OBJECTS : "/topic/objects/changed",
            MESSAGE_BROKER : "/app/print-message",
            shouldStart : true,
            listener : $q.defer(),
            
            socket : {
                client: null,
                stomp: null
            },
            
            connect : function () {
                console.log("WS init");
                this.socket.client = new SockJS(this.SOCKET_URL);
                this.socket.stomp = Stomp.over(this.socket.client);
                this.socket.stomp.connect({}, connectCallback, errorCallback);
                this.socket.stomp.ws.onclose = this.connect;
            },
            
            send : function (message, destination) {
                if(!this.socket.stomp.connected) {
                    return;
                }
                var id = Math.floor(Math.random() * 1000000);
                this.socket.stomp.send(destination, {
                    priority: 9
                }, JSON.stringify(message));
            },
            
            receive : function () {
                return this.listener.promise;
            },
            
            disconnect : function() {
                if(service.socket.stomp && service.socket.stomp.connected) {
                    console.log("WS close");
                    this.socket.stomp.disconnect();
                }
            }
        };

        var connectCallback = function (frame) {
            service.socket.stomp.subscribe(service.LISTEN_TOPIC_OBJECTS, function (data) {
                var message = JSON.parse(data.body);
                $timeout(function () {
                    messageHandler.handleMessage(message);
                    //4 seconds delay so solr can index the object
                }, 4000);
            });
        };
        
        var errorCallback = function (error) {
            console.log("WS error",error);
            if(!service.socket.stomp.connected) {
                service.connect(); // reconnect on error
            }
        }

        return service;

    }]).run(
        function (WebSocketsListener) {
            if(WebSocketsListener.shouldStart) {
                WebSocketsListener.connect();
            } 
        }
    );
