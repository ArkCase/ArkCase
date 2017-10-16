'use strict';

/**
 * @ngdoc service
 * @name profile.service:Profile.UserInfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/profile/services/profile.userInfo.js}
 *
 * Profile.UserInfoService provides functions for accessing profile info
 *
 * @example
 <example>
 <file name="my-module.js">
 angular.module('my-module').controller('MyModuleController', ['$scope', '$log', 'Profile.UserInfoService',
 function ($scope, $log, UserInfoService) {
    //Retrieve currently signed in user's profile information
    UserInfoService.getUserInfo().then(function(userInfo) {
        $scope.currentUsersInfo = userInfo;
    }, function(error) {
        $log.error("Unable to retrieve current user's profile info: " + error);
    });

    //Retrieve profile information for 'ann-acm'
    UserInfoService.getUserInfo('ann-acm').then(function(userInfo) {
        $scope.profileForAnn = userInfo;
    }, function(error) {
        $log.error("Unable to retrieve profile for 'ann-acm': " + error);
    });
   }
 ]);
 </file>
 <file name="my-module.html">
 <div ng-controller="MyModuleController">
 Current User's ID: {{currentUsersInfo.userId}}
 Ann's User ID: {{profileForAnn.userId}}
 </div>
 </file>
 </example>
 */
angular.module('profile').factory('Profile.UserInfoService', ['$resource', '$q', 'UtilService', 'Authentication', '$translate',
    function ($resource, $q, Util, Authentication, $translate) {
        var Service = $resource('api/latest/plugin/profile', {}, {
            /**
             * @ngdoc method
             * @name _getUserInfoById
             * @methodOf profile.service:Profile.UserInfoService
             *
             * @description
             * Query given user's information
             *
             * @returns {Object} Returned by $resource
             */
            _getUserInfoById: {
                method: 'GET',
                url: 'api/latest/plugin/profile/get/:user',
                cache: false
            }
            /**
             * @ngdoc method
             * @name _updateUserInfo
             * @methodOf profile.service:Profile.UserInfoService
             *
             * @description
             * Save updates to profile info
             *
             * @returns {Object} Returned by $resource
             */
            , _updateUserInfo: {
                method: 'POST',
                url: 'api/latest/plugin/profile/userOrgInfo/set',
                cache: false
            }
        });

        /**
         * @ngdoc method
         * @name getUserInfoById
         * @methodOf profile.service:Profile.UserInfoService
         *
         * @description
         * Retrieve user info for specified userId
         *
         * @param {String} userId  id of user to fetch
         *
         * @returns {Promise} User info of specified user
         */
        Service.getUserInfoById = function (userId) {
            if (!Util.goodValue(userId)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"))
            }

            return Util.serviceCall({
                service: Service._getUserInfoById
                , param: {user: userId}
                , data: {}
                , onSuccess: function (data) {
                    return data;
                }
                , onError: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name getUserInfo
         * @methodOf profile.service:Profile.UserInfoService
         *
         * @description
         * Retrieve user info for current user
         *
         * @returns {Promise} User info of current user
         */
        Service.getUserInfo = function () {
            var rc = $q.defer();
            Authentication.queryUserInfo().then(function (userInfo) {
                var userId = Util.goodMapValue(userInfo, 'userId');
                if (userId) {
                    return Service.getUserInfoById(userId);
                } else {
                    return $q.reject([]);
                }
            }).then(function (userInfo) {
                rc.resolve(userInfo);
            }, function (err) {
                rc.reject(err);
            });
            return rc.promise;
        };

        /**
         * @ngdoc method
         * @name _updateUserInfo
         * @methodOf profile.service:Profile.UserInfoService
         *
         * @description
         * Save updates to profile info
         *
         * @returns {Object} Returned by $resource
         */
        Service.updateUserInfo = function (data) {
            if (!Util.goodValue(data)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"))
            }

            return Util.serviceCall({
                service: Service._updateUserInfo
                , param: {}
                , data: data
                , onSuccess: function (data) {
                    return data;
                }
                , onError: function (data) {
                    return data;
                }
            })
        };

        return Service;
    }
]);