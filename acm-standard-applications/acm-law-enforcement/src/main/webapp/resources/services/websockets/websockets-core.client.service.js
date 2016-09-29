'use strict';

angular.module("services").factory("WebSocketsListener", ['$q', '$timeout', 'Websockets.MessageHandler',
    function ($q, $timeout, messageHandler) {

        var service = {
            //The number of milliseconds to delay before attempting to reconnect.
            RECCONECT_INTERVAL: 1000,
            //The rate of increase of the reconnect delay. Allows reconnect attempts to back off when problems persist.
            RECCONECT_DECAY: 1.5,
            RECCONECT_ATTEMPTS: 0,
            MAX_RECCONECT_ATTEMPTS: 5,
            //The maximum number of milliseconds to delay a reconnection attempt
            MAX_RECCONECT_INTERVAL: 30000,
            SOCKET_URL: "/arkcase/stomp",
            LISTEN_TOPIC_OBJECTS: "/topic/objects/changed",
            MESSAGE_BROKER: "/app/print-message",
            shouldStart: true,
            listener: $q.defer(),
            socket: {
                client: null,
                stomp: null
            },

            connect: function () {
                if (this.isConnected()) {
                    return;
                }
                console.log("WS init");
                this.socket.client = new SockJS(this.SOCKET_URL);
                this.socket.stomp = Stomp.over(this.socket.client);
                //socket.stomp.debug = null; //frequently need to disable stomp msg for debugging, please do not delete this line
                this.socket.stomp.connect({}, connectCallback(this), errorCallback(this));
            },

            send: function (message, destination) {
                if (!this.isConnected()) {
                    return;
                }
                var id = Math.floor(Math.random() * 1000000);
                this.socket.stomp.send(destination, {
                    priority: 9
                }, JSON.stringify(message));
            },

            receive: function () {
                if (!this.isConnected()) {
                    return;
                }
                return this.listener.promise;
            },

            disconnect: function () {
                if (this.isConnected()) {
                    console.log("WS close");
                    this.socket.stomp.disconnect();
                }
            },

            isConnected: function () {
                return this.socket.stomp && this.socket.stomp.connected;
            }
        };

        var connectCallback = function (target) {
            return function (frame) {
                target.socket.stomp.subscribe(target.LISTEN_TOPIC_OBJECTS, function (data) {
                    var message = JSON.parse(data.body);
                    $timeout(function () {
                        messageHandler.handleMessage(message);
                        //4 seconds delay so solr can index the object
                    }, 4000);
                });
            }
        };

        var errorCallback = function (target) {
            return function (error) {
                console.log("WS error", error);
                if (target.RECCONECT_ATTEMPTS) {
                    if (target.MAX_RECCONECT_ATTEMPTS && target.RECCONECT_ATTEMPTS > target.MAX_RECCONECT_ATTEMPTS) {
                        return;
                    }
                }
                var timeout = target.RECCONECT_INTERVAL * Math.pow(target.RECCONECT_DECAY, target.RECCONECT_ATTEMPTS);
                setTimeout(function () {
                    target.RECCONECT_ATTEMPTS++;
                    target.connect();
                }, timeout > target.MAX_RECCONECT_INTERVAL ? target.MAX_RECCONECT_INTERVAL : timeout);
            }
        };

        return service;

    }]).run(
    function (WebSocketsListener) {
        if (WebSocketsListener.shouldStart) {
            WebSocketsListener.connect();
        }
    }
);
