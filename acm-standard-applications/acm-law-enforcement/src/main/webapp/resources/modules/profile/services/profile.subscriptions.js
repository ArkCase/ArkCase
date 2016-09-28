'use strict';
angular.module('profile').factory('Profile.SubscriptionService', ['$http', '$q', 'Authentication', 'Acm.StoreService'
    , function ($http, $q, Authentication, Store) {
        return ({
            getSubscriptions: getSubscriptions,
            removeSubscriptions: removeSubscriptions
        });
        function getSubscriptions() {
            var deferred = $q.defer();
            Authentication.queryUserInfo().then(
                function (userInfo) {
                    var user = userInfo.userId;
                    if (user) {
                        var request = $http({
                            method: "GET",
                            url: "api/v1/service/subscription/" + user
                        }).then(
                            function successCallback(response) {
                                deferred.resolve(response.data);
                            },
                            function errorCallback(response) {
                                if (!angular.isObject(response.data) || !response.data.message) {
                                    deferred.reject("An unknown error occurred.");
                                }
                                deferred.reject(response.data.message);
                            }
                        );
                    }
                    return userInfo;
                }
            );
            //Authentication.queryUserInfo({}
            //    , function (userInfo) {
            //        var user = userInfo.userId;
            //        if(user){
            //            var request = $http({
            //                method: "GET",
            //                url: "api/v1/service/subscription/" + user
            //            }).then(
            //                function successCallback(response) {
            //                    deferred.resolve(response.data);
            //                },
            //                function errorCallback(response) {
            //                    if (!angular.isObject(response.data) || !response.data.message) {
            //                        deferred.reject("An unknown error occurred.");
            //                    }
            //                    deferred.reject(response.data.message);
            //                }
            //            );
            //        }
            //    }
            //);
            return deferred.promise;
        };
        function removeSubscriptions(userId, parentType, parentId) {
            var deferred = $q.defer();
            $http({
                method: "DELETE",
                url: "api/v1/service/subscription/" + userId + "/" + parentType + "/" + parentId
            }).then(
                function successCallback(response) {
                    var cacheSubscriptions = new Store.CacheFifo("SubscriptionData");
                    var cacheKey = userId + "." + parentType + "." + parentId;
                    cacheSubscriptions.remove(cacheKey);
                    deferred.resolve(response.data);
                },
                function errorCallback(response) {
                    if (!angular.isObject(response.data) || !response.data.message) {
                        deferred.reject("An unknown error occurred.");
                    }
                    deferred.reject(response.data.message);
                }
            );
            return deferred.promise;
        };
    }
]);
