'use strict';

angular.module('dashboard').factory('Dashboard.DashboardService', ['$resource',
    function ($resource) {
        return $resource('', {}, {
            getConfig: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/dashboard/get',
                data: ''
            },

            queryCasesByQueue: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/casefile/number/by/queue',
                isArray: false,
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

            queryMyCases: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/plugin/search/advancedSearch?q=assignee_id_lcs\\::userId+AND+object_type_s\\:CASE_FILE&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
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