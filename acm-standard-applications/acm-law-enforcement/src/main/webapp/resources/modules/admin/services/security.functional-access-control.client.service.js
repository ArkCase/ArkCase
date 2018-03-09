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
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/security.functional-access-control.client.service.js modules/admin/services/security.functional-access-control.client.service.js}
 *
 * The Admin.FunctionalAccessControlService provides Functional Access Control REST calls functionality
 */
angular.module('admin').service('Admin.FunctionalAccessControlService', function($http) {
    return ({
        getAppRoles : getAppRoles,
        getUserGroups : getUserGroups,
        getAppUserToGroups : getAppUserToGroups,
        saveAppRolesToGroups : saveAppRolesToGroups,
        getGroupsForRole : getGroupsForRole
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
            method : 'GET',
            url : 'api/latest/functionalaccess/roles'
        });
    }

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
            method : 'GET',
            url : 'api/latest/users/groups/get'
        });
    }

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
            method : 'GET',
            url : 'api/latest/functionalaccess/rolestogroups'
        });
    }

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
            method : 'POST',
            url : 'api/latest/functionalaccess/rolestogroups',
            data : appRolesToGroups,
            headers : {
                'Content-Type' : 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsForRole
     * @methodOf services.service:Admin.FunctionalAccessControlService
     *
     * @description
     * List of N groups for a specified Role:
     *      Start position: start
     *      End position: n
     *      Is the group part of the Role: authorized/unauthorized
     *
     * @returns List of all authorized/unauthorized groups
     */
    function getGroupsForRole(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/functionalaccess/' + data.roleName.key + '/groups/',
            params : {
                start : (data.start ? data.start : 0),
                n : (data.n ? data.n : 50),
                authorized : data.isAuthorized
            }
        });
    }
});
