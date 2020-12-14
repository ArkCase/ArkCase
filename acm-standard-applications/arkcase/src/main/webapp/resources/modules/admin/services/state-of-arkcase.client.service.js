/**
 * Created by nebojsha on 02/04/2018.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.StateOfArkcase
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/admin/services/state-of-arkcase.client.service.js modules/admin/services/state-of-arkcase.client.service.js}
 *
 * The Admin.StateOfArkcaseService state of arkcase report file
 */
angular.module('admin').service('Admin.StateOfArkcaseService', function($http) {
    return ({
        getStateOfArkcase : getStateOfArkcase
    });
    /**
     * @ngdoc method
     * @name getStateOfArkcase
     * @methodOf admin.service:Admin.StateOfArkcaseService
     *
     * @description
     * Performs retrieving state of arkcase as zip
     *
     * @returns {HttpPromise} returns binary data
     */
    function getStateOfArkcase(date) {
        return $http({
            method : "GET",
            url : "api/v1/plugin/state-of-arkcase/generate",
            params : {
                date : date
            },
            responseType : "arraybuffer"
        });
    }
});
