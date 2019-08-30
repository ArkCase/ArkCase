/**
 * Created by nebojsha on 11/19/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.BrandingLogoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/branding.logo.client.service.js modules/admin/services/branding.logo.client.service.js}
 *
 * The Admin.BrandingCustomCss provides Branding logo calls functionality
 */
angular.module('admin').service('Admin.BrandingLogoService', [ '$http', 'Upload', function(http, Upload) {
    return ({
        uploadLogo: uploadLogo
    });

    /**
     * @ngdoc method
     * @name uploadLogo
     * @methodOf admin.service:Admin.BrandingLogoService
     *
     * @description
     * Performs uploads logo files.
     *
     * @param {array} files files to be uploaded
     *
     * @param {array} formNames corresponding form names for each file
     *
     * @returns {HttpPromise} Future info about file upload progress
     */
    function uploadLogo(files, formNames) {
        return Upload.upload({
            url: 'api/latest/plugin/admin/branding/customlogos',
            method: 'POST',
            fileFormDataName: formNames,
            file: files
        });
    }
    ;

} ]);
