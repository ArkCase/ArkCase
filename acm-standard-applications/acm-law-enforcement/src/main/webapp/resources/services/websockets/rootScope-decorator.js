'use strict';

angular.module('services').config(function($provide) {
    $provide.decorator('$rootScope', function($delegate) {
        var rootScope = $delegate;

        Object.defineProperty(rootScope.constructor.prototype, '$bus', {
            get: function get() {
                var self = this;

                return {

                    publish: function publish(msg, data) {
                        data = data || {};
                        // emit goes to parents, broadcast goes down to children
                        // since rootScope has no parents, this is the least noisy approach
                        // however, with the caveat mentioned below
                        rootScope.$emit(msg, data);
                    },
                    subscribe: function subscribe(msg, func) {
                        // ignore the event.  Just want the data
                        var unbind = rootScope.$on(msg, function(event, data) {
                            return func(data);
                        });
                        // being able to enforce unbinding here is why decorating rootscope
                        // is preferred over DI of an explicit bus service
                        self.$on('$destroy', unbind);

                        return unbind;
                    },
                    unsubscribe: function(unbind) {
                        unbind();
                    }
                };
            }
        });

        return rootScope;
    });
});
