/**
 * Created by nebojsha on 11/11/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.SelectPrivilegesService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/security.select-privileges.client.service.js modules/admin/services/security.select-privileges.client.service.js}
 *
 * The Admin.SelectPrivilegesService provides Create Role/Select privileges REST calls functionality
 */
angular.module('admin').service('Admin.SelectPrivilegesService', [ '$http', 'base64', function($http, base64) {
    return ({
        getAppRoles: getAppRoles,
        getAllPrivileges: getAllPrivileges,
        addRolePrivileges: addRolePrivileges,
        addPrivilegeToApplicationRole: addPrivilegeToApplicationRole,
        removePrivilegeFromApplicationRole: removePrivilegeFromApplicationRole,
        getRolePrivileges: getRolePrivileges,
        getRolePrivilegesByName: getRolePrivilegesByName,
        upsertRole: upsertRole
    });

    /**
     * @ngdoc method
     * @name getAppRoles
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs retrieving all application roles
     *
     * @returns {HttpPromise} Future info about application roles
     */
    function getAppRoles() {
        return $http({
            method: 'GET',
            cache: false,
            url: 'api/latest/plugin/admin/rolesprivileges/roles'
        });
    }

    /**
     * @ngdoc method
     * @name getAllPrivileges
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs retrieving all privileges
     *
     * @returns {HttpPromise} Future info about all privileges
     */
    function getAllPrivileges() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/rolesprivileges/privileges'
        });
    }

    /**
     * @ngdoc method
     * @name addRolePrivileges
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs adding privileges to role
     *
     * @param {string} roleName role name which privileges to be added
     * @param {array} privileges to be added
     *
     * @returns {HttpPromise} Future info add role privileges
     */
    function addRolePrivileges(roleName, privileges) {
        var url = 'api/latest/plugin/admin/rolesprivileges/roles/' + roleName + '/privileges';
        var data = {};
        data['privileges'] = privileges;
        return $http({
            method: 'PUT',
            url: url,
            data: data,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name addPrivilegeToApplicationRole
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs adding privileges to application role
     *
     * @param {string} roleName role name which privileges to be added
     * @param {array} privileges to be added
     *
     * @returns {HttpPromise} Future info add role privileges
     */
    function addPrivilegeToApplicationRole(roleName, privileges) {
        var url = 'api/latest/plugin/admin/rolesprivileges/' + base64.encode(roleName) + '/privileges';
        return $http({
            method: 'PUT',
            url: url,
            cache: false,
            data: privileges,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name removePrivilegeFromApplicationRole
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs removing privileges from application role
     *
     * @param {string} roleName role name which privileges to be added
     * @param {array} privileges to be added
     *
     * @returns {HttpPromise} Future info add role privileges
     */
    function removePrivilegeFromApplicationRole(roleName, privileges) {
        var url = 'api/latest/plugin/admin/rolesprivileges/' + base64.encode(roleName) + '/privileges';
        return $http({
            method: 'DELETE',
            url: url,
            cache: false,
            data: privileges,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRolePrivileges
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs retrieving all app privileges for provided role
     *
     * @param {string} roleName role name which privileges will be returned
     *
     * @returns {HttpPromise} Future info about role privileges
     */
    function getRolePrivileges(roleName) {
        var url = 'api/latest/plugin/admin/rolesprivileges/roles/' + roleName + '/privileges';
        return $http({
            method: 'GET',
            url: url
        });
    }

    /**
     * @ngdoc method
     * @name getNRolePrivileges
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs retrieving N app privileges for provided role
     *
     * @param {object} data that holds:
     *      {authorized} data.authorized what type will be returned privileges(authorized/notAuthorized)
     *      {string} roleName role name which privileges will be returned
     *      {n} data.n end position
     *      {start} data.start start position
     *
     * @returns {HttpPromise} Future info about role privileges
     */
    function getRolePrivilegesByName(data) {
        var url = 'api/latest/plugin/admin/' + base64.encode(data.role.name) + '/privileges';
        return $http({
            method: 'GET',
            url: url,
            cache: false,
            params: {
                authorized: data.isAuthorized,
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                fn: (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name upsertRole
     * @methodOf admin.service:Admin.SelectPrivilegesService
     *
     * @description
     * Performs upserting role. If old name is not used than inserting is performed, else we update role name into new name.
     *
     * @param {string} roleName role name to be upserted
     * @param {string} oldRoleName role name which will be renamed. If inserting this param is not inserted.
     */
    function upsertRole(roleName, oldRoleName) {
        var url = 'api/latest/plugin/admin/rolesprivileges/roles';
        if (oldRoleName)
            url = url + '/' + oldRoleName;
        var data = {};
        data['roleName'] = roleName;
        return $http({
            method: oldRoleName ? 'PUT' : 'POST',
            url: url,
            data: data,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
} ]);
