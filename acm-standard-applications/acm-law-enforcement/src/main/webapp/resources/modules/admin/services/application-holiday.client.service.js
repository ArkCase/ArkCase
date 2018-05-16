'use strict';

angular.module('admin').factory('Admin.HolidayService', [ '$http', function($http) {

    return ({
        getHolidays: getHolidays,
        saveHolidays: saveHolidays
    });

    function getHolidays() {
        return $http({
            method: 'GET',
            url: 'api/latest/service/holidayConfig'
        });
    }

    function saveHolidays(holidayConfig) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/holidayConfig',
            data: holidayConfig,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }
} ]);