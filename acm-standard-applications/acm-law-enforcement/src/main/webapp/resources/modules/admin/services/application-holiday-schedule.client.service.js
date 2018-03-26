'use strict';

angular.module('admin').factory('Admin.HolidayScheduleService', [ '$http', function($http) {

    return ({
        getHolidaySchedule : getHolidaySchedule,
        saveHolidaySchedule : saveHolidaySchedule
    });

    function getHolidaySchedule() {
        return $http({
            method : 'GET',
            url : 'api/latest/service/holidaySchedule/config'
        });
    }

    function saveHolidaySchedule(holidayScheduleConfig) {
        return $http({
            method : 'POST',
            url : 'api/latest/service/holidaySchedule/config',
            data : holidayScheduleConfig,
            headers : {
                "Content-Type" : "application/json"
            }
        });
    }
} ]);