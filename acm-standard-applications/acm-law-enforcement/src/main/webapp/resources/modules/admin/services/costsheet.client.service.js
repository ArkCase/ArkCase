'use strict';

angular.module('admin').factory('Admin.CostsheetConfigurationService', [ '$http', function($http) {

    var _getProperties = function() {
        return $http({
            method: 'GET',
            url: 'api/latest/service/costsheet/properties'
        });
    };

    var _saveProperties = function(costsheetProperties) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/costsheet/properties',
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