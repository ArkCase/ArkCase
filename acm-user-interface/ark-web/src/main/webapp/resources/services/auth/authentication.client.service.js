'use strict';
/**
 * @ngdoc service
 * @name services.service:Authentication
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/auth/authentication.client.service.js services/auth/authentication.client.service.js}
 *
 * The Authentication service retrieves user information from server
 */

angular.module('services').factory('Authentication', ['$resource', 'ValidationService',
    function ($resource, Validator) {
        return $resource('proxy/arkcase/api/v1/users/info', {}, {
            /**
             * @ngdoc method
             * @name queryUserInfo
             * @methodOf services.service:Authentication
             *
             * @description
             * Returns User info object
             *
             * @returns {HttpPromise} Future user info object
             */
            queryUserInfo: {
                method: 'GET',
                cache: true,
                url: 'proxy/arkcase/api/v1/users/info',
                isArray: false,
                transformResponse: function (data, headers) {
                    var userInfo = {};
                    if (Validator.validateUserInfo(JSON.parse(data))) {
                        userInfo = JSON.parse(data);
                    }
                    return userInfo;
                }
            }
        });
    }
]);