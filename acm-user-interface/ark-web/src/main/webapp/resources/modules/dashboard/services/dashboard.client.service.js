'use strict';

angular.module('dashboard').factory('Dashboard.DashboardService', ['$resource',
    function ($resource) {
        return $resource('', {}, {
            getConfig: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/dashboard/get',
                data: ''
            },

            queryCasesByStatus: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/casebystatus/:period',
                isArray: true,
                data: ''
            },

            queryMyTasks: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/task/forUser/:userId',
                isArray: true,
                data: ''
            },

            queryTeamWorkload: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/task/list/:due',
                isArray: true,
                data: ''
            },

            saveConfig: {
                method: 'POST',
                url: 'proxy/arkcase/api/v1/plugin/dashboard/set'
            }
        })
    }
]);