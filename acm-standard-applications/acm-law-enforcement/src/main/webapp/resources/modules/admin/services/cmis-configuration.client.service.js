/**
 * Created by nick.ferguson on 3/8/2017.
 */
'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.CmisConfigService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/cmis-configuration.client.service.js modules/admin/services/cmis-configuration.client.service.js}
 *
 * The Admin.CmisConfigService provides CMIS Config REST call functionality
 */
angular.module('admin').service('Admin.CmisConfigService', function ($http) {
    return ({
        retrieveCmisConfigurations: retrieveCmisConfigurations,
        createCmisConfiguration: createCmisConfiguration,
        deleteCmisConfiguration: deleteCmisConfiguration,
        updateCmisConfiguration: updateCmisConfiguration
    });

    /**
     * @ngdoc method
     * @name createCmisConfiguration
     * @methodOf admin.service:Admin.CmisConfigService
     *
     * @description
     * Create new CMIS configuration
     *
     * @param {object} cmisConfig row data to send to the server
     *
     * @returns {HttpPromise} Future info about http post
     */
    function createCmisConfiguration(cmisConfig) {
        return $http({
            method: "POST",
            url: "api/latest/plugin/admin/cmisconfiguration/config",
            data: angular.toJson(cmisConfig),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    /**
     * @ngdoc method
     * @name deleteCmisConfiguration
     * @methodOf admin.service:Admin.CmisConfigService
     *
     * @description
     * Delete config in CMIS configuration directory
     *
     * @param {object} cmisId config id to be deleted
     *
     * @returns {HttpPromise} Future info about http delete
     */
    function deleteCmisConfiguration(cmisId) {
        var url = 'api/latest/plugin/admin/cmisconfiguration/config/' + cmisId;
        return $http({
            method: "DELETE",
            url: url
        });
    };

    /**
     * @ngdoc method
     * @name updateCmisConfiguration
     * @methodOf admin.service:Admin.CmisConfigService
     *
     * @description
     * Updates config in CMIS configuration directory
     *
     * @param {object} cmisId config data row to be updated
     *
     * @returns {HttpPromise} Future info about http post
     */
    function updateCmisConfiguration(config) {
        var url = 'api/latest/plugin/admin/cmisconfiguration/config/' + config["cmis.id"];
        return $http({
            method: "PUT",
            url: url,
            data: angular.toJson(config),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    /**
     * @ngdoc method
     * @name retrieveCmisConfigurations
     * @methodOf admin.service:Admin.CmisConfigService
     *
     * @description
     * Performs retrieving all configs in CMIS configuration directory
     *
     * @returns {HttpPromise} Future info about cmis configurations
     */
    function retrieveCmisConfigurations() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/admin/cmisconfiguration/config"
        });
    };
});