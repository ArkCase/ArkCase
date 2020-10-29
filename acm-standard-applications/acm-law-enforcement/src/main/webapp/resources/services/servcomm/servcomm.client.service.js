'use strict';

/**
 * @ngdoc service
 * @name services.service:ServCommService
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/servcomm/servcomm.client.service.js services/servcomm/servcomm.client.service.js}
 *
 * Server communication. Pardon the incomplete ng doc. For now, it is just temporary code. Complete ng doc will be added after real implementation of UI and backend communication
 */
angular.module('services').factory('ServCommService', [ '$rootScope', '$timeout', 'Acm.StoreService', 'UtilService', function($rootScope, $timeout, Store, Util) {
    var Service = {
        CacheNames: {
            SERVCOMM_DATA: "ServCommData"
        }

        ,
        ServComm: function(scope, channel, topic) {
            this.scope = scope;
            this.channel = channel;
            this.topic = topic;
        }

        ,
        request: function(scope, channel, topic, data) {
            scope.$emit("rootScope:servcomm-request", {
                channel: channel,
                topic: topic,
                data: data
            });
        }

        ,
        handleRequest: function() {
            $rootScope.$on("rootScope:servcomm-request", function(e, request) {
                var cache = new Store.CacheFifo(Service.CacheNames.SERVCOMM_DATA);
                var key = request.channel + "." + request.topic;
                cache.put(key, request.data);

                //set up listener and send data to server, fake it with a timer for testing
                $timeout(function() {
                    Service.response({
                        channel: request.channel,
                        topic: request.topic,
                        data: request.data
                    });
                }, 8000);
            });
        }

        ,
        response: function(channel, topic, data) {
            $rootScope.$broadcast("rootScope:servcomm-response", {
                channel: channel,
                topic: topic,
                data: data
            });
        }

        ,
        handleResponse: function(scope) {
            scope.$on('rootScope:servcomm-response', function(event, data) {
                console.log("ServCommService, rootScope:servcomm-response");

                //$scope.$emit('report-object-refreshed', $stateParams.id);
            });
        }

        ,
        popRequest: function(channel, topic) {
            var cache = new Store.CacheFifo(Service.CacheNames.SERVCOMM_DATA);
            var key = channel + "." + topic;
            var data = cache.get(key);
            cache.remove(key);
            return data;
        }

        ,
        setStateToGo: function(stateName) {
            var stateNameVariable = new Store.Variable("NextStateName");
            stateNameVariable.set(stateName);
        }

        ,
        getStateToGo: function() {
            var stateNameVariable = new Store.Variable("NextStateName");
            var stateName = stateNameVariable.get();
            return stateName;
        }
    };
    Service.ServComm.prototype = {};

    return Service;
} ]);