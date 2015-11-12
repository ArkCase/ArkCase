/**
 * Created by nebojsha on 10/30/2015.
 */

'use strict';
angular.module('admin').service('Admin.DashboardConfigService', function ($http) {
    return ({
        getRolesByWidgets: getRolesByWidgets,
        authorizeRolesForWidget: authorizeRolesForWidget
    });
    /**
     * @ngdoc method
     * @name getRolesByWidgets
     * @methodOf admin.services:Admin.DashboardConfigService
     *
     * @description
     * Performs retrieving all widgets with defined roles authorization for dashboard
     *
     * @returns {HttpPromise} Future info about widgets
     */
    function getRolesByWidgets() {
        return $http({
            method: "GET",
            url: "proxy/arkcase/api/latest/plugin/dashboard/widgets/rolesByWidget/all"
        });
    };

    /**
     * @ngdoc method
     * @name getRolesByWidgets
     * @methodOf admin.services:Admin.DashboardConfigService
     *
     * @description
     * Performs saving widget with changed roles authorizations
     * @param {String} query Query to send to the server
     */
    function authorizeRolesForWidget(widget) {
        $http({
            method: "POST",
            url: "proxy/arkcase/api/latest/plugin/dashboard/widgets/set",
            data: angular.toJson(widget),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
});
