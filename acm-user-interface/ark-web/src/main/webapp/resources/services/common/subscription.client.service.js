'use strict';

/**
 * @ngdoc service
 * @name services.service:SubscriptionService
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/subscription.client.service.js services/common/subscription.client.service.js}
 *
 * This service includes methods for managing case file subscriptions and allows checking
 * the status of a subscription, subscribing, and unsubscribing
 */
angular.module('services').factory('SubscriptionService', [ '$http',
    function ($http) {

        console.log("SubscriptionService is phasing out. Please use OBject.SubscriptionService");

        return {

            ///**
            // * @ngdoc method
            // * @name getSubscribers
            // * @methodOf services.service:SubscriptionService
            // *
            // * @description
            // * This method retrieves an array of subscriptions for the supplied user to the given caseFile.
            // *
            // * @param {Object} contains the userId and caseFile objectId for the subscription to check
            // * @returns {HttpPromise} Future array of user subscriptions to the given caseFile
            // */
            //getSubscribers: function(options) {
            //    return $http({
            //        url: 'proxy/arkcase/api/v1/service/subscription/' + options.userId + '/CASE_FILE/' + options.objectId,
            //         method: 'GET',
            //         data: ''
            //    });
            //},
            //
            ///**
            // * @ngdoc method
            // * @name subscribe
            // * @methodOf services.service:SubscriptionService
            // *
            // * @description
            // * This method subscribes the given user to the specified caseFile
            // *
            // * @param {Object} contains the userId and caseFile objectId for the subscription to create
            // * @returns {HttpPromise} Future info about the created subscription of the user to the caseFile
            // */
            //subscribe: function(options) {
            //    return $http({
            //        url: 'proxy/arkcase/api/v1/service/subscription/' + options.userId + '/CASE_FILE/' + options.objectId,
            //        method: 'PUT',
            //        data: ''
            //    });
            //},
            //
            ///**
            // * @ngdoc method
            // * @name unsubscribe
            // * @methodOf services.service:SubscriptionService
            // *
            // * @description
            // * This method unsubscribes the given user from the specified caseFile
            // *
            // * @param {Object} contains the userId and caseFile objectId for the subscription to delete
            // * @returns {HttpPromise} Future object confirming that the subscription was deleted
            // */
            //unsubscribe: function(options) {
            //    return $http({
            //        url: 'proxy/arkcase/api/v1/service/subscription/' + options.userId + '/CASE_FILE/' + options.objectId,
            //        method: 'DELETE',
            //        data: ''
            //    });
            //},
            //
            ///**
            // * @ngdoc method
            // * @name isSubscribed
            // * @methodOf services.service:SubscriptionService
            // *
            // * @description
            // * This method determines if the specified user is subscribed to the given case file
            // *
            // * @param {String} userId uniquely identifies the user
            // * @param {Object} caseFile contains the metadata for the case file
            // * @param {Array} subscriptions array of objects containing the metadata for existing subscriptions
            // * @returns {Boolean} true if the user is subscribed to the given caseFile, false otherwise
            // */
            //isSubscribed: function(userId, caseFile, subscriptions) {
            //    var isSubscribed = false;
            //    if (userId && caseFile && subscriptions) {
            //        _.forEach(subscriptions, function(value) {
            //            if (value.objectId == caseFile.id &&
            //                value.userId == userId) {
            //                isSubscribed = true;
            //            }
            //        });
            //    }
            //    return isSubscribed;
            //}
        }
    }
]);