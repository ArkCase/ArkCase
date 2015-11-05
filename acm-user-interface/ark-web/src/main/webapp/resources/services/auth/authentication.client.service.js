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
angular.module('services').factory('Authentication', ['$resource',
    function ($resource) {
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
                url: 'proxy/arkcase/api/v1/users/info',
                cache: true
            }
        });
    }
]);