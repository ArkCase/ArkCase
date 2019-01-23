'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.OcrManagementService
 *
 * @description
 * Contains REST calls for Admin OCR Configuration
 *
 * The Admin.OcrManagementService provides $http services for OCR Configuration.
 */
angular.module('admin').factory('Admin.OcrManagementService', [ '$http', function($http) {

    var _getProperties = function() {
        return $http({
            method: 'GET',
            url: 'api/v1/plugin/admin/ocr/configuration'
        });
    };

    var _saveProperties = function(ocrConfigDataModel) {
        return $http({
            method: 'POST',
            url: 'api/v1/plugin/admin/ocr/configuration',
            data: ocrConfigDataModel,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    return {
        getProperties: _getProperties,
        saveProperties: _saveProperties
    };

} ]);