'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.FormConfigService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/form-configuration.service.js modules/admin/services/form-configuration.service.js}
 *
 * The Admin.FormConfigService provides Plain Forms Config REST calls functionality
 */
angular.module('admin').service('Admin.FormConfigService', function ($http) {
    return ({
        retrievePlainForms: retrievePlainForms,
        deletePlainForm: deletePlainForm
    });

    /**
     * @ngdoc method
     * @name retrievePlainForms
     * @methodOf admin.service:Admin.FormConfigService
     *
     * @description
     * Performs retrieving all data
     *
     * @returns {HttpPromise} Future info about plain forms
     */
    function retrievePlainForms() {
        return $http({
            method: "GET",
            url: "proxy/arkcase/api/latest/plugin/admin/plainforms"
        });
    }

    /**
     * @ngdoc method
     * @name deletePlainForm
     * @methodOf admin.service:Admin.FormConfigService
     *
     * @description
     * Delete  plain form by key and target
     *
     * @param {object} key key name to be deleted
     * @param {object} target target type to be deleted
     *
     * @returns {HttpPromise} Future info about http delete
     */
    function deletePlainForm(key, target) {
        var url = 'proxy/arkcase/api/latest/plugin/admin/plainforms/' + key + "/" + target;
        return $http({
            method: "DELETE",
            url: url
        });
    }
});
