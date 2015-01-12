/**
 * Acm.Dispatcher
 *
 * Handles dispatching events that are triggered to configured listeners
 *
 * Capabilities:
 *
 *   + addEventListener - add an event along with a callback to execute when fired
 *   + initialize - perform any initialization required, should only be called by parent on document.onload
 *   + removeEventListener - remove a configured event/callback
 *   + triggerEvent - fire an event, passes along sent data to the callback
 *
 *
 * @author dmcclure
 */
Acm.Dispatcher = {
    create: function() {
    }

    ,PRIORITY_LOW    : 1
    ,PRIORITY_NORMAL : 2
    ,PRIORITY_HIGH   : 3

    // events mappedto listeners
    ,events:[],

    addEventListener: function(event,callback, priority) {
        priority = priority || this.PRIORITY_NORMAL;

        this.events[event] = this.events[event] || [];
        if ( this.events[event] ) {
            this.events[event].push({callback:callback, priority:priority});
        }
    },

    removeEventListener: function(event,callback) {
        if ( this.events[event] ) {
            var listeners = this.events[event];
            for ( var i = listeners.length-1; i>=0; --i ){
                if ( listeners[i].callback === callback ) {
                    listeners.splice( i, 1 );
                    return true;
                }
            }
        }
        return false;
    },

    //phase out triggerEvent, use fireEvent instead
    triggerEvent:function(event, data) {
        if ( this.events[event] ) {
            var listeners = this.events[event], len = listeners.length;
            while ( len-- ) {
                listeners[len].callback(this, data);
            }
        }
    },

    fireEvent:function(event) {
        var responseCount = 0;

        if (!event) {
            return;
        }

        var args = [];
        Array.prototype.push.apply(args, arguments);
        args.shift();

        if ( this.events[event] ) {
            var listeners = this.events[event];
            var len = listeners.length;
            while ( len-- ) {
                var a = this.PRIORITY_HIGH;
                var b = Acm.Dispatcher.PRIORITY_HIGH;
                var c = listeners[len];

                if (this.PRIORITY_HIGH == listeners[len].priority) {
                    if (listeners[len].callback.apply(this, args)) {
                        responseCount++;
                    }
                }
            }
            var len = listeners.length;
            while ( len-- ) {
                if (this.PRIORITY_NORMAL == listeners[len].priority) {
                    if (listeners[len].callback.apply(this, args)) {
                        responseCount++;
                    }
                }
            }
            var len = listeners.length;
            while ( len-- ) {
                if (this.PRIORITY_LOW == listeners[len].priority) {
                    if (listeners[len].callback.apply(this, args)) {
                        responseCount++;
                    }
                }
            }
        }

        return responseCount;
    },

    numOfListeners: function(event) {
        if ( this.events[event] ) {
            return this.events[event].length;
        }
        return 0;
    },

    isListening: function(event, callback) {
        if ( this.events[event] ) {
            var listeners = this.events[event];
            for ( var i = listeners.length-1; i>=0; --i ){
                if ( listeners[i].callback === callback ) {
                    return true;
                }
            }
        }
        return false;
    }
};