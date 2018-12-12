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
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/security.functional-access-control.client.service.js modules/admin/services/security.functional-access-control.client.service.js}
 *
 * The Admin.FunctionalAccessControlService provides Functional Access Control REST calls functionality
 */
angular.module('admin').service('Admin.FunctionalAccessControlService', [ '$http', 'base64', function($http, base64) {
    return ({
        getAppRoles: getAppRoles,
        getAppRolesPaged: getAppRolesPaged,
        getAppRolesByName: getAppRolesByName,
        getUserGroups: getUserGroups,
        getAppUserToGroups: getAppUserToGroups,
        saveAppRolesToGroups: saveAppRolesToGroups,
        addGroupsToApplicationRole: addGroupsToApplicationRole,
        deleteGroupsFromApplicationRole: deleteGroupsFromApplicationRole,
        getGroupsForRolePaged: getGroupsForRolePaged,
        getGroupsForRoleByName: getGroupsForRoleByName
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
            url: 'api/latest/functionalaccess/roles'
        });
    }

    /**
     * @ngdoc method
     * @name getAppRolesPaged
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs retrieving all application roles paged
     *
     * @returns {HttpPromise} Future info about application roles
     */
    function getAppRolesPaged(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/functionalaccess/appRoles',
            cache: false,
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getAppRolesByName
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs retrieving application roles by name
     *
     * @returns {HttpPromise} Future info about application roles
     */
    function getAppRolesByName(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/functionalaccess/appRoles',
            cache: false,
            params: {
                fn: (data.filterWord ? data.filterWord : ""),
                n: (data.n ? data.n : 50)
            }
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
            method: 'GET',
            url: 'api/latest/users/groups/get',
            cache: false
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
            method: 'GET',
            url: 'api/latest/functionalaccess/rolestogroups',
            cache: false
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
            method: 'POST',
            url: 'api/latest/functionalaccess/rolestogroups',
            data: appRolesToGroups,
            cache: false,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name addGroupsToApplicationRole
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs saving groups to application role
     *
     * @param {object} roleName - application role name
     *                 groups - groups which will be added to the application role with name roleName
     */
    function addGroupsToApplicationRole(roleName, groups) {
        return $http({
            method: 'PUT',
            url: 'api/latest/functionalaccess/' + base64.encode(roleName) + '/groups',
            data: groups,
            cache: false,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name deleteGroupsFromApplicationRole
     * @methodOf admin.service:Admin.FunctionalAccessControlService
     *
     * @description
     * Performs removing groups from application role
     *
     * @param {object} roleName - application role name
     *                 groups - groups which will be added to the application role with name roleName
     */
    function deleteGroupsFromApplicationRole(roleName, groups) {
        return $http({
            method: 'DELETE',
            url: 'api/latest/functionalaccess/' + base64.encode(roleName) + '/groups',
            data: groups,
            cache: false,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsForRolePaged
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
    function getGroupsForRolePaged(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/functionalaccess/' + base64.encode(data.roleName.key) + '/groups',
            cache: false,
            params: {
                start: (data.start ? data.start : 0),
                n: (data.n ? data.n : 50),
                authorized: data.isAuthorized
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsForRoleByName
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
    function getGroupsForRoleByName(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/functionalaccess/' + base64.encode(data.roleName.key) + '/groups',
            cache: false,
            params: {
                start: (data.start ? data.start : 0),
                n: (data.n ? data.n : 50),
                fq: (data.filterWord ? data.filterWord : ""),
                authorized: data.isAuthorized
            }
        });
    }
} ]);
