/**
 * Created by nebojsha on 10/30/2015.
 */

'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.DashboardConfigService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/dashboard.config.client.service.js modules/admin/services/dashboard.config.client.service.js}
 *
 * The Admin.DashboardConfigService provides Dashboard Config REST calls functionality
 */
angular.module('admin').service('Admin.DashboardConfigService', function ($http) {
    return ({
        getRolesByWidgets: getRolesByWidgets,
        authorizeRolesForWidget: authorizeRolesForWidget
    });
    /**
     * @ngdoc method
     * @name getRolesByWidgets
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     * Performs retrieving all widgets with defined roles authorization for dashboard
     *
     * @returns {HttpPromise} Future info about widgets
     */
    function getRolesByWidgets() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/dashboard/widgets/rolesByWidget/all"
        });
    };

    /**
     * @ngdoc method
     * @name getRolesByWidgets
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     * Performs saving widget with changed roles authorizations
     * @param {String} query Query to send to the server
     */
    function authorizeRolesForWidget(widget) {
        return $http({
            method: "POST",
            url: "api/latest/plugin/dashboard/widgets/set",
            data: angular.toJson(widget),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});
