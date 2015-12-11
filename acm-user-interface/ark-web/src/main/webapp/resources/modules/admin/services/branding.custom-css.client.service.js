/**
 * Created by nebojsha on 11/19/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.BrandingCustomCssService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/branding.custom-css.client.service.js modules/admin/services/branding.custom-css.client.service.js}
 *
 * The Admin.BrandingCustomCss provides Branding custom css calls functionality
 */
angular.module('admin').service('Admin.BrandingCustomCssService', function ($http) {
    return ({
        getCustomCss: getCustomCss,
        saveCustomCss: saveCustomCss
    });
    /**
     * @ngdoc method
     * @name getCustomCss
     * @methodOf admin.service:Admin.BrandingCustomCssService
     *
     * @description
     * Performs retrieving css data
     *
     * @returns {HttpPromise} Future info about Custom CSS data
     */
    function getCustomCss() {
        return $http({
            method: "GET",
            url: "proxy/arkcase/branding/customcss"
        });
    };

    /**
     * @ngdoc method
     * @name saveCustomCss
     * @methodOf admin.service:Admin.BrandingCustomCssService
     *
     * @description
     * Performs saving css data.
     *
     * @param {object} data css data
     *
     * @returns {HttpPromise} Future info about Custom CSS saved
     */
    function saveCustomCss(data) {
        return $http({
            method: "PUT",
            url: "proxy/arkcase/api/latest/plugin/admin/branding/customcss",
            data: data,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});
