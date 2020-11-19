/**
 * @ngdoc service
 * @name admin.service:Admin.ReportsScheduleService
 *
 * @description
 *
 *
 * Admin.ReportsScheduleService provides the functions for creating scheduled reports in Pentaho.
 */

angular.module('admin').factory('Admin.ScheduleReportService', [ '$resource', '$state', 'UtilService', function($resource, $state, Util) {
    var Service = $resource('api/latest/plugin', {}, {

        scheduleNewReport: {
            method: 'POST',
            url: 'api/latest/plugin/report/schedule',
            cache: false
        }

        ,
        _getSchedules: {
            method: 'GET',
            url: 'api/latest/plugin/report/schedule',
            cache: false
        }

        ,
        _deleteSchedule: {
            method: 'DELETE',
            url: 'api/latest/plugin/report/schedule/byId',
            cache: false
        }
    });

    Service.scheduleReport = function(reportData) {
        return Util.serviceCall({
            service: Service.scheduleNewReport,
            data: reportData,
            onSuccess: function(data) {
                if (true) {
                    // replace with something to validate that scheduled report created
                    return data;
                }
            },
            onError: function(errorData) {
                return errorData;
            }
        })
    };

    Service.getSchedules = function() {
        return Util.serviceCall({
            service: Service._getSchedules,
            onSuccess: function(data) {
                return data;
            },
            onError: function(errorData) {
                return errorData;
            }
        });
    };

    Service.deleteSchedule = function(scheduleId) {
        return Util.serviceCall({
            service: Service._deleteSchedule,
            param: {
                id: scheduleId
            },
            onSuccess: function(data) {
                return data;
            },
            onError: function(errorData) {
                return errorData;
            }
        });
    };

    return Service;
} ]);