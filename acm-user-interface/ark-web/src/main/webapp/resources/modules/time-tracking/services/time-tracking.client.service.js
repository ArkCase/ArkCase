'use-strict'

angular.module('services').factory('TimeTrackingService', ['$resource',
    function ($resource){
        return $resource('proxy/arkcase/api/v1/service/timesheet', {}, {
            listObjects: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/timesheet/user/:userId?start=:start&n=:n&sort=:sort',
                cache: false,
                isArray: false
            },
            get: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/timesheet/:id',
                cache: false,
                isArray: false
            },
            save: {
                method: 'POST',
                url: 'proxy/arkcase/api/v1/service/timesheet',
                cache: false
            }
        });
    }
]);
