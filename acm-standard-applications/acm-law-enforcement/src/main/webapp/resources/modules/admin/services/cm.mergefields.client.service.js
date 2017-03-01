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
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/cm.mergefields.client.service.js modules/admin/services/cm.mergefields.client.service.js}
 *
 * The Admin.CMMergeFieldsService provides correspondence Management calls functionality
 */
angular.module('admin').service('Admin.CMMergeFieldsService', ['$http',
    function ($http) {
        return ({
            retrieveMergeFieldsList: retrieveMergeFieldsList,
            retrieveMergeFieldsVersionsList : retrieveMergeFieldsVersionsList,
            retrieveMergeFieldsVersionsByType : retrieveMergeFieldsVersionsByType,
            retrieveActiveMergeFieldsByType : retrieveActiveMergeFieldsByType,
            saveMergeFieldsData : saveMergeFieldsData
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
                url: 'api/latest/plugin/admin/mergefields',
                cache: false
            });
        };

        /**
         * @ngdoc method
         * @name retrieveMergeFieldsVersionsList
         * @methodOf admin.service:Admin.CMMergeFieldsService
         *
         * @description
         * Performs retrieving correspondence management merge fields versions.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveMergeFieldsVersionsList() {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/mergefields/versions',
                cache: false
            });
        };

        /**
         * @ngdoc method
         * @name retrieveMergeFieldsVersionsByType
         * @methodOf admin.service:Admin.CMMergeFieldsService
         *
         * @description
         * Performs retrieving correspondence management merge fields versions for selected type.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveMergeFieldsVersionsByType(objectType) {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/mergefields/versions/' + objectType,
                cache: false
            });
        };
        
        /**
         * @ngdoc method
         * @name retrieveActiveMergeFieldsByType
         * @methodOf admin.service:Admin.CMMergeFieldsService
         *
         * @description
         * Performs retrieving correspondence management active version merge fields for selected type.
         *
         * @returns {HttpPromise} Future info about widgets
         */
        function retrieveActiveMergeFieldsByType(objectType) {
            return $http({
                method: "GET",
                url: 'api/latest/plugin/admin/mergefields/active/' + objectType,
                cache: false
            });
        };
        
        function saveMergeFieldsData(mergeFieldsData) {
            return $http({
                method: "PUT",
                url: 'api/latest/plugin/admin/mergefields',
                data: mergeFieldsData
            });
        }
        
    }]);
