/**
 * @author sasko.tanaskoski
 *
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.CMMergeFieldsService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/cm.mergefields.client.service.js modules/admin/services/cm.mergefields.client.service.js}
 *
 * The Admin.CMMergeFieldsService provides correspondence Management calls functionality
 */
angular.module('admin').service('Admin.CMMergeFieldsService', ['$http',
    function ($http) {
        return ({
            retrieveMergeFieldsList: retrieveMergeFieldsList
        });

        /**
         * @ngdoc method
         * @name retrieveMergeFieldsList
         * @methodOf admin.service:Admin.CMMergeFieldsService
         *
         * @description
         * Performs retrieving correspondence management merge fields.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveMergeFieldsList() {
            return $http({
                method: "GET",
                url: "api/latest/plugin/admin/mergefields",
                cache: false
            });
        };

    }]);
