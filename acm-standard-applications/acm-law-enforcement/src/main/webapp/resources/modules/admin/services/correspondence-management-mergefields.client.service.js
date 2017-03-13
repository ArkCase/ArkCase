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
            retrieveMergeFieldsList: retrieveMergeFieldsList,
            retrieveMergeFieldsVersionsList : retrieveMergeFieldsVersionsList,
            retrieveMergeFieldsVersionsByType : retrieveMergeFieldsVersionsByType,
            retrieveActiveMergeFieldsByType : retrieveActiveMergeFieldsByType,
            saveMergeFieldsData : saveMergeFieldsData,
            setActiveMergingVersion : setActiveMergingVersion
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

        /**
         * @ngdoc method
         * @name saveMergeFieldsData
         * @methodOf admin.service:Admin.CMMergeFieldsService
         *
         * @description
         * Saving query and mapped fields for merge fields.
         *
         * @param {object} mergefieldsData Contains merge field data
         * @returns {HttpPromise} Future info about widgets
         */        
        function saveMergeFieldsData(mergeFieldsData) {
            return $http({
                method: "PUT",
                url: 'api/latest/plugin/admin/mergefields',
                data: mergeFieldsData
            });
        }

        /**
         * @ngdoc method
         * @name setActiveMergingVersion
         * @methodOf admin.service:Admin.CMMergeFieldsService
         *
         * @description
         * Set selected merge field version as active.
         *
         * @param {object} mergeFieldVersionData Contains merge field version data
         * @returns {HttpPromise} Future info about widgets
         */        
        function setActiveMergingVersion(mergeFieldVersionData) {
            return $http({
                method: "PUT",
                url: 'api/latest/plugin/admin/mergefields/version/active',
                data: mergeFieldVersionData
            });
        }
    }]);
