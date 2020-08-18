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
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/reports.config.client.service.js modules/admin/services/reports.config.client.service.js}
 *
 * The Admin.ReportsConfigService provides Reports Config REST calls functionality
 */
angular.module('admin').service('Admin.ReportsConfigService', function ($http) {
    return ({
        getReports: getReports,
        getReportsByMatchingName: getReportsByMatchingName,
        getReportsPaged: getReportsPaged,
        getAppRoles: getAppRoles,
        getRolesForReport: getRolesForReport,
        getRolesForReportByName: getRolesForReportByName,
        getReportsRoles: getReportsRoles,
        getReportsRolesPaged: getReportsRolesPaged,
        getReportsRolesByName: getReportsRolesByName,
        saveReportsRoles: saveReportsRoles,
        addRolesToReport: addRolesToReport,
        removeRolesFromReport: removeRolesFromReport,
        saveReports: saveReports,
        syncReports: syncReports,
        exportReports: exportReports,
        exportReportToNIEMXml: exportReportToNIEMXml
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
            method: "GET",
            url: "api/latest/plugin/report/get/pentaho",
            cache: false
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
            method: "GET",
            url: "api/latest/plugin/report/pentaho",
            cache: false,
            params: {
                fn: (data.filterWord ? data.filterWord : ""),
                dir: (data.dir ? data.dir : "ASC"),
                start: (data.start ? data.start : 0),
                n: (data.n ? data.n : 50)
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
            method: "GET",
            url: "api/latest/plugin/report/pentaho",
            cache: false,
            params: {
                dir: (data.dir ? data.dir : "ASC"),
                start: (data.start ? data.start : 0),
                n: (data.n ? data.n : 50)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRoles
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all application roles
     *
     * @returns {HttpPromise} Future info about roles
     */
    function getAppRoles() {
        return $http({
            method: 'GET',
            url: 'api/latest/functionalaccess/roles'
        });
    }

    /**
     * @ngdoc method
     * @name getRolesForReport
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all roles for report
     *      String: data.isAuthorized = define which roles to be retrieved(authorized/notAuthorized)
     *      String: data.dir = Sort direction
     *      Integer: data.n = End position
     *      Integer: data.start = Start position
     *
     * @returns {HttpPromise} Future info about roles
     */
    function getRolesForReport(data) {
        return $http({
            method: "GET",
            url: "api/latest/plugin/report/" + data.report.key + "/roles",
            cache: false,
            params: {
                authorized: data.isAuthorized,
                dir: (data.dir ? data.dir : ""),
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                fn: (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRolesForReportByName
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving filtered application modules by name:
     *      String: data.dir = Sort direction
     *      Integer: data.start = Start position
     *      Integer: data.n = End position
     *      String: data.fq = Filter word
     *
     * @returns {HttpPromise} Future info about roles
     */
    function getRolesForReportByName(data) {
        return $http({
            method: "GET",
            url: "api/latest/plugin/report/" + data.report.key + "/roles",
            cache: false,
            params: {
                authorized: data.isAuthorized,
                dir: (data.dir ? data.dir : ""),
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                fn: (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name getReportsRoles
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports with application roles mapped
     *
     * @returns {HttpPromise} Future info about reports with roles
     */
    function getReportsRoles() {
        return $http({
            method: "GET",
            cache: false,
            url: "api/latest/plugin/report/reporttorolesmap"
        });
    }

    /**
     * @ngdoc method
     * @name getReportsRolesPaged
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports(only names) paged
     *
     * @returns {HttpPromise} Future info about reports with roles
     */
    function getReportsRolesPaged(data) {
        return $http({
            method: "GET",
            cache: false,
            url: "api/latest/plugin/report/reportstoroles",
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getReportsRolesByName
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Performs retrieving all reports(only names) by name
     *
     * @returns {HttpPromise} Future info about reports with roles
     */
    function getReportsRolesByName(data) {
        return $http({
            method: "GET",
            cache: false,
            url: "api/latest/plugin/report/reportstoroles",
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                fq: (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name saveReportsRoles
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     * Performs saving reports with changed roles authorizations
     *
     * @param {object} reportsRoles ReportsRoles map to send to the server
     */
    function saveReportsRoles(reportsRoles) {
        return $http({
            method: "POST",
            url: "api/latest/plugin/report/reporttorolesmap",
            data: reportsRoles,
            cache: false,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name addRolesToReport
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs saving roles to a privilege
     *
     * @param {object} roleName - privilege name
     *        {list}  roles - roles which will be added to the privilege with name privilegeName
     *
     * @returns void
     */
    function addRolesToReport(privilegeName, roles) {
        return $http({
            method: 'PUT',
            url: 'api/latest/plugin/report/' + privilegeName + '/roles',
            data: roles,
            cache: false,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name removeRolesFromReport
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs removing roles from a privilege
     *
     * @param {object} privilegeName - privilege name
     *        {list} roles - roles which will be added to the privilege with name privilegeName
     *
     * @returns void
     */
    function removeRolesFromReport(privilegeName, roles) {
        return $http({
            method: 'DELETE',
            url: 'api/latest/plugin/report/' + privilegeName + '/roles',
            data: roles,
            cache: false,
            headers: {
                'Content-Type': 'application/json'
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
            method: "POST",
            url: "api/latest/plugin/report/save",
            data: reports,
            cache: false,
            headers: {
                "Content-Type": "application/json"
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
            method: "PUT",
            url: "api/latest/plugin/report/sync",
            cache: false,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name exportReports
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Export data for all (DOJ) reports in a single document.
     *
     */
    function exportReports(formatType) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/report/exportYearlyReport',
            params: {
                "exportFormatType": formatType
            },
            responseType: 'arraybuffer'
        });
    }

    /**
     * @ngdoc method
     * @name exportReportToNIEMXml
     * @methodOf admin.service:Admin.ReportsConfigService
     *
     * @description
     * Export data for single (DOJ) report in a NIEM XML document.
     *
     */
    function exportReportToNIEMXml(report) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/report/exportReportToNIEMXml',
            params: {
                "report": report
            },
            responseType: 'arraybuffer'
        });
    }
});
