/**
 * Created by nebojsha on 11/19/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.StateOfArkcase
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/state-of-arkcase.client.service.js modules/admin/services/state-of-arkcase.client.service.js}
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
     * @methodOf admin.service:Admin.BrandingCustomCssService
     *
     * @description
     * Performs retrieving css data
     *
     * @returns {HttpPromise} Future info about Custom CSS data
     */
    function getStateOfArkcase(date) {
        return $http({
            method : "GET",
            url : "api/v1/plugin/state-of-arkcase",
            params : {
                date : date
            }
        });
    }
});
