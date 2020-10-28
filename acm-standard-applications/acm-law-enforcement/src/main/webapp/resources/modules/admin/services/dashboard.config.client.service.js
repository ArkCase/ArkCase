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
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/dashboard.config.client.service.js modules/admin/services/dashboard.config.client.service.js}
 *
 * The Admin.DashboardConfigService provides Dashboard Config REST calls functionality
 */
angular.module('admin').service('Admin.DashboardConfigService', function($http) {
    return ({
        getRolesByWidgets: getRolesByWidgets,
        authorizeRolesForWidget: authorizeRolesForWidget,
        getRolesGroups: getRolesGroups,
        addRoleGroupToWidget: addRoleGroupToWidget,
        removeRoleGroupToWidget: removeRoleGroupToWidget,
        getRolesGroupsByName: getRolesGroupsByName
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
    }

    /**
     * @ngdoc method
     * @name authorizeRolesForWidget
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
            data: widget,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRolesGroups
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     *      Retrieves roles and groups for a widget
     *
     * @param {object} data that holds:
     *      {authorized} data.authorized what type of roles/groups will be returned (authorized/notAuthorized)
     *      {string} widgetName - widget name
     *      {n} data.n end position
     *      {start} data.start start position
     */
    function getRolesGroups(data) {
        return $http({
            method: "GET",
            url: "api/latest/plugin/dashboard/widgets/" + data.role.key + "/roles",
            cache: false,
            params: {
                authorized: data.isAuthorized,
                n: (data.n ? data.n : 50)
            }
        });
    }

    /**
     * @ngdoc method
     * @name addRoleGroupToWidget
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     *      Performs saving widget with changed roles/groups authorizations(notAuthorization to authorization)
     *
     * @param {authorized} isAuthorized what type of roles/groups will be returned (authorized/notAuthorized)
     *      {string} widgetName - widget name
     *      {list} groups to be removed from group
     */
    function addRoleGroupToWidget(widgetName, rolesGroups, isAuthorized) {
        return $http({
            method: "PUT",
            url: "api/latest/plugin/dashboard/widgets/roleGroupToWidget",
            cache: false,
            data: rolesGroups,
            params: {
                widgetName: widgetName,
                authorized: isAuthorized
            }
        });
    }

    /**
     * @ngdoc method
     * @name removeRoleGroupToWidget
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     *      Performs saving widget with changed roles/groups authorizations(authorization to notAuthorization)
     *
     * @param {isAuthorized} isAuthorized what type of roles/groups will be returned (authorized/notAuthorized)
     *      {string} widgetName - widget name
     *      {list} groups to be removed from group
     */
    function removeRoleGroupToWidget(widgetName, rolesGroups, isAuthorized) {
        return $http({
            method: "PUT",
            url: "api/latest/plugin/dashboard/widgets/roleGroupToWidget",
            cache: false,
            data: rolesGroups,
            params: {
                widgetName: widgetName,
                authorized: isAuthorized
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRolesGroupsByName
     * @methodOf admin.service:Admin.DashboardConfigService
     *
     * @description
     *      Retrieves roles and groups for a widget
     *
     * @param {object} data that holds:
     *      {authorized} data.authorized what type of roles/groups will be returned (authorized/notAuthorized)
     *      {string} widgetName - widget name
     *      {n} data.n end position
     *      {start} data.start start position
     */
    function getRolesGroupsByName(data) {
        return $http({
            method: "GET",
            url: "api/latest/plugin/dashboard/widgets/" + data.widget.key + "/roles",
            cache: false,
            params: {
                authorized: data.isAuthorized,
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                fn: (data.filterWord ? data.filterWord : "")
            }
        });
    }
});
