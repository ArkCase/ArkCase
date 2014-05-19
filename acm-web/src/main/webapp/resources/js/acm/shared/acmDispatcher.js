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

    // events mapped to listeners
    events:[],


    addEventListener: function(event,callback) {
        this.events[event] = this.events[event] || [];
        if ( this.events[event] ) {
            this.events[event].push(callback);
        }
    },

    initialize: function() {

    },

    removeEventListener: function(event,callback) {
        if ( this.events[event] ) {
            var listeners = this.events[event];
            for ( var i = listeners.length-1; i>=0; --i ){
                if ( listeners[i] === callback ) {
                    listeners.splice( i, 1 );
                    return true;
                }
            }
        }
        return false;
    },

    triggerEvent:function(event, data) {
        if ( this.events[event] ) {
            var listeners = this.events[event], len = listeners.length;
            while ( len-- ) {
                listeners[len](this, data);
            }
        }
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
                if ( listeners[i] === callback ) {
                    return true;
                }
            }
        }
        return false;
    }
};