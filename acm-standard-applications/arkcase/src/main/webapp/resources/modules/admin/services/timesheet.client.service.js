'use strict';

angular.module('admin').factory('Admin.TimesheetConfigurationService', [ '$http', function($http) {

    var _getConfig = function() {
        return $http({
            method: 'GET',
            url: 'api/latest/service/timesheet/config'
        });
    };

    var _saveConfig = function(timesheetConfig) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/timesheet/config',
            data: timesheetConfig,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    var _getProperties = function() {
        return $http({
            method: 'GET',
            url: 'api/latest/service/timesheet/properties'
        });
    };

    var _saveProperties = function(timesheetProperties) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/timesheet/properties',
            data: timesheetProperties,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    return {
        getConfig: _getConfig,
        saveConfig: _saveConfig,
        getProperties: _getProperties,
        saveProperties: _saveProperties
    };

} ]);