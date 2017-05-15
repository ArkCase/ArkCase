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
angular.module('admin').service('Admin.SelectPrivilegesService', function ($http) {
    return ({
        getAppRoles: getAppRoles,
        getAllPrivileges: getAllPrivileges,
        addRolePrivileges: addRolePrivileges,
        getRolePrivileges: getRolePrivileges,
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
            url: 'api/latest/plugin/admin/rolesprivileges/roles'
        });
    };

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
    };

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
    };

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
    };

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
    };
});
