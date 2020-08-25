'use strict';

angular.module('admin').factory('Admin.PDFConversionService', [ '$http', function($http) {

    var _getProperties = function() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/pdfConversion'
        });
    };

    var _saveProperties = function(pdfProperties) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/admin/pdfConversion',
            data: pdfProperties,
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