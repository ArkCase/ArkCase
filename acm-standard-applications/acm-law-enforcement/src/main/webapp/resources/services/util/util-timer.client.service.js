'use strict';

/**
 * @ngdoc service
 * @name services:Util.TimerService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/util/util-timer.client.service.js services/util/util-timer.client.service.js}
 *
 * Timer based on web worker.
 */

angular.module('services').factory('Util.TimerService', ['UtilService'
    , function (Util) {

        var Service = {
            _worker: null

            /**
             * @ngdoc method
             * @name startWorker
             * @methodOf services:Util.TimerService
             *
             * @param {String} workerUrl URL to a web worker script
             *
             * @description
             * Start a web worker. The worker is a singleton. It is created at first call. Subsequent calls returns
             * the web worker previously created.
             */
            ,startWorker: function(workerUrl) {
                if (null == this._worker) {
                    if(typeof(Worker) === "undefined") {
                        return null;
                    }

                    this._worker = new Worker(workerUrl);
                    this._worker.onmessage = function(event) {
                        //console.log("" + event.data);
                        Service.triggerEvent();
                    };
                }
                return this._worker;
            }

            /**
             * @ngdoc method
             * @name stopWorker
             * @methodOf services:Util.TimerService
             *
             * @param {String} workerUrl URL to a web worker script
             *
             * @description
             * Stop the web worker
             */
            ,stopWorker: function() {
                this._worker.terminate();
            }

            ,_listeners: []
            ,_listenerCount: 0

            /**
             * @ngdoc method
             * @name registerListener
             * @methodOf services:Util.TimerService
             *
             * @param {String} name Listener name
             * @param {number} interval Time interval in milliseconds that the callback is called
             * @param {Function} callback A function responses to triggered timer event. Return true to continue receive
             * timer events; return false means it is the last event, and the listener will be removed from lister list.
             *
             * @description
             * Register a timer listener
             */
            ,registerListener: function(name, interval, callback) { //interval in milliseconds
                var count = interval / 100;                          //timer pulse is 100 ms
                var i = this._findListener(name);
                if (0 > i) {    //not found; create new entry
                    this._listeners.push({name: name, callback: callback, count: count, countDown: count});
                    this._listenerCount++;
                } else {
                    var listener = this._listeners[i];
                    listener.callback = callback;
                    listener.count = count;
                }
            }

            /**
             * @ngdoc method
             * @name registerListener
             * @methodOf services:Util.TimerService
             *
             * @param {String} name Listener name
             *
             * @description
             * Remove a timer listener
             */
            ,removeListener: function(name) {
                var i = this._findListener(name);
                this._removeListener(i);
            }
            ,_removeListener: function(i) {
                if (0 <= i) {
                    this._listeners.splice(i, 1);
                    this._listenerCount--;
                }
            }
            ,_findListener: function(name) {
                for (var i = 0; i < this._listenerCount; i++) {
                    var listener = this._listeners[i];
                    if (listener.name == name) {
                        return i;
                    }
                }
                return -1;
            }

            /**
             * @ngdoc method
             * @name triggerEvent
             * @methodOf services:Util.TimerService
             *
             * @description
             * Trigger a timer event. It notifies all listeners and remove listeners when their event counts reach
             * limit or when callback function return false
             */
            ,triggerEvent: function() {
                //console.log("triggerEvent, this._listenerCount=" + this._listenerCount);

                //need to loop backwards because of possible item removed while looping
                for (var i = this._listenerCount - 1; 0 <= i; i--) {
                    var listener = this._listeners[i];
                    if (0 >= --listener.countDown) {
                        if (listener.callback(listener.name)) {
                            listener.countDown = listener.count;
                        } else {
                            this._removeListener(i);
                        }
                    }
                } //for i
            }


            /**
             * @ngdoc method
             * @name useTimer
             * @methodOf services:Util.TimerService
             *
             * @param {String} name Listener name
             * @param {number} interval Time interval in milliseconds that the callback is called
             * @param {Function} callback A function responses to triggered time event
             *
             * @description
             * Register a timer listener
             */
            ,useTimer: function(name, interval, callback) {
                Service.startWorker("assets/js/acmTimer.js");
                Service.registerListener(name, interval, callback);
            }
        };

        return Service;
    }
]);