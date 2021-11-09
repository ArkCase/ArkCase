'use strict';

angular.module('admin').factory('Admin.PortalConfigurationService', [ '$http', 'Upload', function($http, Upload) {

    var getPortalConfig = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/admin/portal/config'
        });
    };

    var getPortalConfigUser = function () {
        return $http({
            method: 'GET',
            url: 'api/latest/service/portalgateway/config'
        });
    }

    var savePortalConfig = function (portal) {
        return $http({
            method: 'PUT',
            url: 'api/latest/service/portalgateway/admin/portal/config',
            data: portal,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    /**
     * @ngdoc method
     * @name uploadLogo
     * @methodOf admin.service:Admin.PortalConfigurationService
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
    var uploadLogo = function (files, formNames) {
        return Upload.upload({
            url: 'api/latest/plugin/admin/portal/branding/customlogos',
            method: 'POST',
            fileFormDataName: formNames,
            file: files
        });
    }
    ;

    return {
        getPortalConfig: getPortalConfig,
        getPortalConfigUser:getPortalConfigUser,
        savePortalConfig: savePortalConfig,
        uploadLogo: uploadLogo
    };

} ]);
