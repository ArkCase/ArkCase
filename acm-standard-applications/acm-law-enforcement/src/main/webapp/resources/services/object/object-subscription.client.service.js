'use strict';

/**
 * @ngdoc service
 * @name services:Object.SubscriptionService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-subscription.client.service.js services/object/object-subscription.client.service.js}

 * Object.SubscriptionService includes methods for managing subscriptions
 */
angular.module('services').factory('Object.SubscriptionService', ['$resource', '$q', 'Acm.StoreService', 'UtilService', 'Authentication'
    , function ($resource, $q, Store, Util, Authentication) {
        var Service = $resource('api/latest/service', {}, {
            /**
             * @ngdoc method
             * @name _getSubscriptions
             * @methodOf services:Object.SubscriptionService
             *
             * @description
             * Query subscriptions for a user with an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.userId Subscription user
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _getSubscriptions: {
                method: 'GET',
                url: 'api/latest/service/subscription/:userId/:objectType/:objectId',
                isArray: true
            }

            /**
             * @ngdoc method
             * @name _getListOfSubscriptionsByUser
             * @methodOf services:Object.SubscriptionService
             *
             * @description
             * Query subscriptions for a given user with an object.
             *
             * @param {Object} params Map of input parameter
             * @param {Object} params.userId Subscription user
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _getListOfSubscriptionsByUser: {
                method: 'GET',
                url: 'api/latest/service/subscription/:userId',
                isArray: true
            }

            /**
             * @ngdoc method
             * @name _subscribe
             * @methodOf services:Object.SubscriptionService
             *
             * @description
             * Subscribe the given user to the specified object
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.userId Subscription user
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _subscribe: {
                method: 'PUT',
                url: 'api/latest/service/subscription/:userId/:objectType/:objectId'
            }

            /**
             * @ngdoc method
             * @name _unsubscribe
             * @methodOf services:Object.SubscriptionService
             *
             * @description
             * Unsubscribe the given user to the specified object
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.userId Subscription user
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _unsubscribe: {
                method: 'DELETE',
                url: 'api/latest/service/subscription/:userId/:objectType/:objectId'
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            SUBSCRIPTION_DATA: "SubscriptionData"
        };

        /**
         * @ngdoc method
         * @name getSubscriptions
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Query subscriptions for a user with an object.
         *
         * @param {String} userId Subscription user
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.getSubscriptions = function (userId, objectType, objectId) {
            var cacheSubscriptions = new Store.CacheFifo(Service.CacheNames.SUBSCRIPTION_DATA);
            var cacheKey = userId + "." + objectType + "." + objectId;
            var subscriptions = cacheSubscriptions.get(cacheKey);
            return Util.serviceCall({
                service: Service._getSubscriptions
                , param: {
                    userId: userId
                    , objectType: objectType
                    , objectId: objectId
                }
                , result: subscriptions
                , onSuccess: function (data) {
                    if (Service.validateSubscriptions(data)) {
                        subscriptions = data;
                        cacheSubscriptions.put(cacheKey, subscriptions);
                        return subscriptions;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name getListOfSubscriptionsByUser
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Query subscriptions by user without additional params.
         *
         * @returns {Object} list of subscriptions
         */
        Service.getListOfSubscriptionsByUser = function () {
            var result = $q.defer();
            var userId = null;

            return Authentication.queryUserInfo().then(
                function (userInfo) {
                    result.resolve(userInfo);
                    userId = userInfo.userId;
                    var cacheKey = userId;

                    var cacheSubscriptions = new Store.CacheFifo(Service.CacheNames.SUBSCRIPTION_DATA);
                    var listOfSubscriptions = cacheSubscriptions.get(cacheKey);

                    return Util.serviceCall({
                        service: Service._getListOfSubscriptionsByUser
                        , param: {
                            userId: userId
                        }
                        , result: listOfSubscriptions
                        , onSuccess: function (data) {
                            if (Service.validateSubscriptions(data)) {
                                listOfSubscriptions = data;
                                result.resolve(data);
                                cacheSubscriptions.put(cacheKey, listOfSubscriptions);
                                return listOfSubscriptions;
                            }
                        }
                    });

                }
            )
            return result;
        };

        /**
         * @ngdoc method
         * @name subscribe
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Subscribe the given user to the specified object
         *
         * @param {String} userId Subscription user
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.subscribe = function (userId, objectType, objectId) {
            return Util.serviceCall({
                service: Service._subscribe
                , param: {
                    userId: userId
                    , objectType: objectType
                    , objectId: objectId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateSubscription(data)) {
                        var subscription = data;
                        var cacheSubscriptions = new Store.CacheFifo(Service.CacheNames.SUBSCRIPTION_DATA);

                        //This particular key is introduced for getListOfSubscriptionsByUser function
                        var cacheKeyUser = userId;
                        cacheSubscriptions.get(cacheKeyUser);

                        var cacheKey = userId + "." + objectType + "." + objectId;
                        var subscriptions = cacheSubscriptions.get(cacheKey);
                        if (subscriptions == null)
                            subscriptions = [];
                        //update subscription into subscriptions
                        var index = _.findIndex(subscriptions, function (sub) {
                            return Util.compare(sub.id, subscription.id);
                        });
                        if (index < 0)
                            subscriptions.push(subscription);
                        else
                            subscriptions[index] = subscription;
                        cacheSubscriptions.put(cacheKeyUser, subscriptions);
                        cacheSubscriptions.put(cacheKey, subscriptions);
                        return subscription;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name unsubscribe
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Subscribe the given user to the specified object
         *
         * @param {String} userId Subscription user
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.unsubscribe = function (userId, objectType, objectId) {
            return Util.serviceCall({
                service: Service._unsubscribe
                , param: {
                    userId: userId
                    , objectType: objectType
                    , objectId: objectId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateUnsubscribe(data)) {
                        var cacheSubscriptions = new Store.CacheFifo(Service.CacheNames.SUBSCRIPTION_DATA);

                        //This particular key is introduced for getListOfSubscriptionsByUser function
                        var cacheKeyUser = userId;
                        cacheSubscriptions.remove(cacheKeyUser);

                        var cacheKey = userId + "." + objectType + "." + objectId;
                        cacheSubscriptions.remove(cacheKey);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateSubscriptions
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Validate list of subscriptions
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSubscriptions = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateSubscription(data[i])) {
                    return false;
                }
            }

            return true;
        };

        /**
         * @ngdoc method
         * @name validateSubscription
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Validate subscription data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSubscription = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.userId)) {
                return false;
            }
            if (Util.isEmpty(data.subscriptionObjectType)) {
                return false;
            }
            if (Util.isEmpty(data.objectId)) {
                return false;
            }
            if (Util.isEmpty(data.subscriptionId)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateUnsubscribe
         * @methodOf services:Object.SubscriptionService
         *
         * @description
         * Validate subscription data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateUnsubscribe = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedSubscriptionId)) {
                return false;
            }
            return true;
        };


        return Service;
    }
])
;
