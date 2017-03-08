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
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/cmis.configuration.client.service.js modules/admin/services/cmis.configuration.client.service.js}
 *
 * The Admin.CmisConfigService provides CMIS Config REST call functionality
 */
angular.module('admin').service('Admin.CmisConfigService', function ($http) {
    return ({
        createCmisConfiguration: createCmisConfiguration
    });

    /**
     * @ngdoc method
     * @name createCmisConfiguration
     * @methodOf admin.service:Admin.CmisConfigService
     *
     * @description
     * Create new CMIS configuration
     *
     *
     * @param {object} cmisConfig row data to send to the server
     *
     * @returns {HttpPromise} Future info about http post
     */
    function createCmisConfiguration(cmisConfig) {
        return $http({
            method: "POST",
            url: "",
            data: angular.toJson(cmisConfig),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});