/**
 * Created by nebojsha on 11/10/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.FunctionalAccessControlService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/security.functional_access_control.service.js modules/admin/services/security.functional_access_control.service.js}
 *
 * The Admin.FunctionalAccessControlService provides Functional Access Control REST calls functionality
 */
angular.module('admin').service('Admin.FunctionalAccessControlService', function ($http) {
    return ({
        getAppRoles: getAppRoles,
        getUserGroups: getUserGroups,
        getAppUserToGroups: getAppUserToGroups,
        saveAppRolesToGroups: saveAppRolesToGroups,
    });

    /**
     * @ngdoc method
     * @name getAppRoles
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs retrieving all application roles
     *
     * @returns {HttpPromise} Future info about application roles
     */
    function getAppRoles() {
        return $http({
            method: 'GET',
            url: 'proxy/arkcase/api/latest/functionalaccess/roles'
        });
    };

    /**
     * @ngdoc method
     * @name getUserGroups
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs retrieving all user groups
     *
     * @returns {HttpPromise} Future info about user groups
     */
    function getUserGroups() {
        return $http({
            method: 'GET',
            url: 'proxy/arkcase/api/latest/users/groups/get'
        });
    };

    /**
     * @ngdoc method
     * @name getAppUserToGroups
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs retrieving all app roles with user groups mapped
     *
     * @returns {HttpPromise} Future info about app roles with user groups
     */
    function getAppUserToGroups() {
        return $http({
            method: 'GET',
            url: 'proxy/arkcase/api/latest/functionalaccess/rolestogroups'
        });
    };

    /**
     * @ngdoc method
     * @name saveAppRolesToGroups
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs saving application roles to groups
     *
     * @param {object} appRolesToGroups AppRolesUserGroups map to send to the server
     */
    function saveAppRolesToGroups(appRolesToGroups) {
        return $http({
            method: 'POST',
            url: 'proxy/arkcase/api/latest/functionalaccess/rolestogroups',
            data: angular.toJson(appRolesToGroups),
            headers: {
                'Content-Type': 'application/json'
            }
        });
    };
});
