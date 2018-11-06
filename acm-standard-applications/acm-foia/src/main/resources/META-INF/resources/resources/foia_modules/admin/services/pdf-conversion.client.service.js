'use strict';

angular.module('admin').factory('Admin.PDFConversionService', [ '$http', function($http) {

    var _getProperties = function() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/pdfConversion/load'
        });
    };

    var _saveProperties = function(costsheetProperties) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/admin/pdfConversion/save',
            data: costsheetProperties,
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