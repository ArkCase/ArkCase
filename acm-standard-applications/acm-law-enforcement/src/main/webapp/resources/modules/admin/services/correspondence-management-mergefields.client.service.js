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
angular.module('admin').service('Admin.CMMergeFieldsService', [ '$http', function($http) {
    return ({
        retrieveActiveMergeFieldsByType: retrieveActiveMergeFieldsByType,
        saveMergeFieldsData: saveMergeFieldsData,
        deleteMergeFields: deleteMergeFields,
        addMergeField: addMergeField
    });

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
    }
    ;

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
     * @name deleteMergeFieldData
     * @methodOf admin.service:Admin.CMTemplatesService
     *
     * @description
     * Delete merge field.
     *
     * @param {string} mergeFieldId Id of the merge field
     * @returns {HttpPromise} Future info about widgets
     */
    function deleteMergeFields(mergeFieldId) {
        return $http({
            method: "DELETE",
            url: 'api/latest/plugin/admin/mergefields/' + mergeFieldId
        });
    }

    /**
     * @ngdoc method
     * @name addMergeField
     * @methodOf admin.service:Admin.CMTemplatesService
     *
     * @description
     * Add new merge field
     *
     * @param {string} mergeFieldId Id of the merge field
     * @returns {HttpPromise} Future info about widgets
     */
    function addMergeField(newMergeField) {
        return $http({
            method: "PUT",
            url: 'api/latest/plugin/admin/mergefields/addMergeField',
            data: newMergeField
        });
    }
} ]);
