'use strict';

angular.module('dashboard').factory('Dashboard.DashboardService', ['$resource',
    function ($resource) {
        return $resource('', {}, {
            getConfig: {
                method: 'GET',
                url: 'api/v1/plugin/dashboard/get',
                params: {
                    moduleName: "@moduleName"
                },
                data: ''
            },

            queryCasesByQueue: {
                method: 'GET',
                url: 'api/v1/plugin/casefile/number/by/queue',
                isArray: false,
                data: ''
            },

            queryCasesByStatus: {
                method: 'GET',
                url: 'api/v1/plugin/casebystatus/:period',
                isArray: true,
                data: ''
            },

            queryNewComplaints: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:COMPLAINT+' +
                'AND+create_date_tdt\\:[NOW-1MONTH TO NOW]',
                isArray: false,
                data: ''
            },

            queryNewCases: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:CASE_FILE+' +
                'AND+NOT+status_lcs\\:DELETED+AND+create_date_tdt\\:[NOW-1MONTH TO NOW]',
                isArray: false
            },

            queryMyTasks: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=assignee_id_lcs\\::userId+' +
                'AND+object_type_s\\:TASK+' +
                'AND+status_lcs\\:ACTIVE&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },

            queryMyComplaints: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=assignee_id_lcs\\::userId+' +
                'AND+object_type_s\\:COMPLAINT+' +
                'AND+NOT+status_lcs\\:CLOSED&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },

            queryMyCases: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=assignee_id_lcs\\::userId+' +
                'AND+object_type_s\\:CASE_FILE+' +
                'AND+NOT+status_lcs\\:DRAFT&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },

            queryTeamWorkload: {
                method: 'GET',
                url: 'api/v1/plugin/task/getListByDueDate/:due',
                isArray: true,
                data: ''
            },
            
            getWidgetsPerRoles: {
                method: 'GET',
                url: 'api/latest/plugin/dashboard/widgets/get',
                isArray: true,
            },

            saveConfig: {
                method: 'POST',
                url: 'api/v1/plugin/dashboard/set'
            }
        })
    }
]);