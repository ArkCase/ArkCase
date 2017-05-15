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
angular.module('admin').service('Admin.ReportsConfigService', function ($http) {
    return ({
        getReports: getReports,
        getUserGroups: getUserGroups,
        getReportsUserGroups: getReportsUserGroups,
        saveReportsUserGroups: saveReportsUserGroups,
        saveReports: saveReports
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
            url: "api/latest/plugin/report/get/pentaho"
        });
    };

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
            method: "GET",
            url: "api/latest/users/groups/get"
        });
    };

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
            method: "GET",
            url: "api/latest/plugin/report/reporttogroupsmap"
        });
    };

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
            method: "POST",
            url: "api/latest/plugin/report/reporttogroupsmap",
            data: reportsUserGroups,
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

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
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});
