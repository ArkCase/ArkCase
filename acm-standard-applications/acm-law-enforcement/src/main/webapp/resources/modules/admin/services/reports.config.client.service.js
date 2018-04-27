/**
 * Created by nebojsha on 10/30/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.ReportsConfigService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/reports.config.client.service.js modules/admin/services/reports.config.client.service.js}
 *
 * The Admin.ReportsConfigService provides Reports Config REST calls functionality
 */
angular.module('admin').service('Admin.ReportsConfigService', function($http) {
    return ({
        getReports : getReports,
        getReportsByMatchingName : getReportsByMatchingName,
        getReportsPaged : getReportsPaged,
        getUserGroups : getUserGroups,
        getGroupsForReport : getGroupsForReport,
        getGroupsForReportByName : getGroupsForReportByName,
        getReportsUserGroups : getReportsUserGroups,
        getReportsGroupsPaged : getReportsGroupsPaged,
        getReportsGroupsByName : getReportsGroupsByName,
        saveReportsUserGroups : saveReportsUserGroups,
        addGroupsToReport : addGroupsToReport,
        removeGroupsFromReport : removeGroupsFromReport,
        saveReports : saveReports,
        syncReports : syncReports
    });

    /**
     * @ngdoc method
     * @name getReports
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports
     *
     * @returns {HttpPromise} Future info about reports
     */
    function getReports() {
        return $http({
            method : "GET",
            url : "api/latest/plugin/report/get/pentaho",
            cache : false
        });
    }

    /**
     * @ngdoc method
     * @name getReportsByMatchingName
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving reports by matching name
     *      String: data.fn = Filter name
     *      String: data.dir = Sort direction
     *      Integer: data.start = Start position
     *      Integer: data.n = End position
     *
     * @returns {HttpPromise} Future info about reports
     */
    function getReportsByMatchingName(data) {
        return $http({
            method : "GET",
            url : "api/latest/plugin/report/pentaho",
            cache : false,
            params : {
                fn : (data.filterWord ? data.filterWord : ""),
                dir : (data.dir ? data.dir : "ASC"),
                start : (data.start ? data.start : 0),
                n : (data.n ? data.n : 18)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getReportsPaged
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving reports paged
     *      String: data.dir = Sort direction
     *      Integer: data.start = Start position
     *      Integer: data.n = End position
     *
     * @returns {HttpPromise} Future info about reports
     */
    function getReportsPaged(data) {
        return $http({
            method : "GET",
            url : "api/latest/plugin/report/pentaho",
            cache : false,
            params : {
                dir : (data.dir ? data.dir : "ASC"),
                start : (data.start ? data.start : 0),
                n : (data.n ? data.n : 18)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getUserGroups
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all user groups
     *
     * @returns {HttpPromise} Future info about user groups
     */
    function getUserGroups() {
        return $http({
            method : "GET",
            url : "api/latest/users/groups/get",
            cache : false
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsForReport
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all user groups
     *      String: data.isAuthorized = define which groups to be retrieved(authorized/notAuthorized)
     *      String: data.dir = Sort direction
     *      Integer: data.n = End position
     *      Integer: data.start = Start position
     *
     * @returns {HttpPromise} Future info about user groups
     */
    function getGroupsForReport(data) {
        return $http({
            method : "GET",
            url : "api/latest/plugin/report/" + data.report.key + "/groups",
            cache : false,
            params : {
                authorized : data.isAuthorized,
                dir : (data.dir ? data.dir : ""),
                n : (data.n ? data.n : 18),
                start : (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsForReportByName
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving filtered application modules by name:
     *      String: data.dir = Sort direction
     *      Integer: data.start = Start position
     *      Integer: data.n = End position
     *      String: data.fq = Filter word
     *
     * @returns {HttpPromise} Future info about user groups
     */
    function getGroupsForReportByName(data) {
        return $http({
            method : "GET",
            url : "api/latest/plugin/report/" + data.report.key + "/groups",
            cache : false,
            params : {
                authorized : data.isAuthorized,
                dir : (data.dir ? data.dir : ""),
                n : (data.n ? data.n : 18),
                start : (data.start ? data.start : 0),
                fq : (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name getReportsUserGroups
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports with user groups mapped
     *
     * @returns {HttpPromise} Future info about reports with user groups
     */
    function getReportsUserGroups() {
        return $http({
            method : "GET",
            cache : false,
            url : "api/latest/plugin/report/reporttogroupsmap"
        });
    }

    /**
     * @ngdoc method
     * @name getReportsGroupsPaged
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports(only names) paged
     *
     * @returns {HttpPromise} Future info about reports with user groups
     */
    function getReportsGroupsPaged(data) {
        return $http({
            method : "GET",
            cache : false,
            url : "api/latest/plugin/report/reportstogroups",
            params : {
                n : (data.n ? data.n : 12),
                start : (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getReportsGroupsByName
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports(only names) by name
     *
     * @returns {HttpPromise} Future info about reports with user groups
     */
    function getReportsGroupsByName(data) {
        return $http({
            method : "GET",
            cache : false,
            url : "api/latest/plugin/report/reportstogroups",
            params : {
                n : (data.n ? data.n : 12),
                start : (data.start ? data.start : 0),
                fq : (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name saveReportsUserGroups
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     * Performs saving reports with changed roles authorizations
     *
     * @param {object} reportsUserGroups ReportsUserGroups map to send to the server
     */
    function saveReportsUserGroups(reportsUserGroups) {
        return $http({
            method : "POST",
            url : "api/latest/plugin/report/reporttogroupsmap",
            data : reportsUserGroups,
            cache : false,
            headers : {
                "Content-Type" : "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name saveReports
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     * Performs saving reports.
     *
     * @param {object} reports reports objects send to the server
     */
    function saveReports(reports) {
        return $http({
            method : "POST",
            url : "api/latest/plugin/report/save",
            data : reports,
            cache : false,
            headers : {
                "Content-Type" : "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name addGroupsToReport
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs saving groups to a privilege
     *
     * @param {object} roleName - privilege name
     *        {list}  groups - groups which will be added to the privilege with name privilegeName
     *
     * @returns void
     */
    function addGroupsToReport(privilegeName, groups) {
        return $http({
            method : 'PUT',
            url : 'api/latest/plugin/report/' + privilegeName + '/groups/adhoc',
            data : groups,
            cache : false,
            headers : {
                'Content-Type' : 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name removeGroupsFromReport
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs removing groups from a privilege
     *
     * @param {object} privilegeName - privilege name
     *        {list} groups - groups which will be added to the privilege with name privilegeName
     *
     * @returns void
     */
    function removeGroupsFromReport(privilegeName, groups) {
        return $http({
            method : 'DELETE',
            url : 'api/latest/plugin/report/' + privilegeName + '/groups/adhoc',
            data : groups,
            cache : false,
            headers : {
                'Content-Type' : 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name syncReports
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs sync reports.
     *
     */
    function syncReports() {
        return $http({
            method : "PUT",
            url : "api/latest/plugin/report/sync",
            cache : false,
            headers : {
                "Content-Type" : "application/json"
            }
        });
    }
});
