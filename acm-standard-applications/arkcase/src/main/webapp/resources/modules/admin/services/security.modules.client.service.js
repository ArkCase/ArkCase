/**
 * Created by nebojsha on 11/10/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.ModulesService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/admin/services/security.modules.client.service.js modules/admin/services/security.modules.client.service.js}
 *
 * The Admin.ModulesService provides Modules REST calls functionality
 */
angular.module('admin').service('Admin.ModulesService', [ "$http", "UtilService", function($http, Util) {
    return ({
        getAppModules: getAppModules,
        getAppModulesPaged: getAppModulesPaged,
        getAppModulesByName: getAppModulesByName,
        getRolesForModulePrivilege: getRolesForModulePrivilege,
        getRolesForModulePaged: getRolesForModulePaged,
        getRolesForModuleByName: getRolesForModuleByName,
        addRolesToModule: addRolesToModule,
        removeRolesFromModule: removeRolesFromModule
    });

    /**
     * @ngdoc method
     * @name getAppModules
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs retrieving all application modules
     *
     * @returns {HttpPromise} Future info about application modules
     */
    function getAppModules() {
        return $http({
            method: 'GET',
            cache: false,
            url: 'api/latest/plugin/admin/moduleconfiguration/modules'
        });
    }

    /**
     * @ngdoc method
     * @name getAppModulesPaged
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs retrieving N application modules with info:
     *      String: name
     *      String: id
     *      String: privilege
     *
     * @returns {Array} List of application modules objects
     *
     */
    function getAppModulesPaged(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/moduleconfiguration/modules/paged',
            cache: false,
            params: {
                dir: (data.dir ? data.dir : "name_lcs ASC"),
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getAppModulesByName
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs retrieving filtered application modules by name with info:
     *      String: name
     *      String: id
     *      String: privilege
     *
     * @returns {Array} List of application modules objects
     *
     */
    function getAppModulesByName(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/admin/moduleconfiguration/modules',
            cache: false,
            params: {
                fn: (data.filterWord ? data.filterWord : ""),
                n: (data.n ? data.n : 50)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRolesForModulePrivilege
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs retrieving roles for provided module privilege
     *
     * @param {string} modulePrivilege privilege for which roles will be retrieved
     *
     * @returns {HttpPromise} Future info roles for module privilege
     */
    function getRolesForModulePrivilege(modulePrivilege) {
        return $http({
            method: 'GET',
            cache: false,
            url: 'api/latest/plugin/admin/rolesprivileges/privileges/' + modulePrivilege + '/roles'
        });
    }

    /**
     * @ngdoc method
     * @name getRolesForModulePaged
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs retrieving roles for provided module paged
     *
     * @param {string} modulePrivilege privilege for which roles will be retrieved
     *
     * @returns {HttpPromise} Future info roles for module privilege
     */
    function getRolesForModulePaged(data) {
        return $http({
            method: 'GET',
            cache: false,
            url: 'api/latest/plugin/admin/rolesprivileges/' + data.module.key + '/roles',
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                authorized: data.isAuthorized
            }
        });
    }

    /**
     * @ngdoc method
     * @name getRolesForModuleByName
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs retrieving roles for provided module paged
     *
     * @param {string} modulePrivilege privilege for which roles will be retrieved
     *
     * @returns {HttpPromise} Future info roles for module privilege
     */
    function getRolesForModuleByName(data) {
        return $http({
            method: 'GET',
            cache: false,
            url: 'api/latest/plugin/admin/rolesprivileges/' + data.module.key + '/roles',
            params: {
                n: (data.n ? data.n : 50),
                authorized: data.isAuthorized,
                fn: (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name addRolesToModule
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs adding roles to module
     *
     * @param {string} modulePrivilege module privilege for which roles will be added
     *
     * @param {array} roles array of roles which will be added to the module
     *
     */
    function addRolesToModule(modulePrivilege, roles) {
        var url = 'api/latest/plugin/admin/rolesprivileges/roles/' + roles.join() + '/privileges/' + modulePrivilege;
        return $http({
            method: 'PUT',
            url: url,
            cache: false,
            data: {},
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    /**
     * @ngdoc method
     * @name removeRolesFromModule
     * @methodOf admin.service:Admin.ModulesService
     *
     * @description
     * Performs removing roles to module
     *
     * @param {string} modulePrivilege module privilege for which roles will be removed
     *
     * @param {array} roles array of roles which should be removed
     *
     */
    function removeRolesFromModule(modulePrivilege, roles) {
        var url = 'api/latest/plugin/admin/rolesprivileges/roles/' + roles.join() + '/privileges/' + modulePrivilege;
        return $http({
            method: 'DELETE',
            cache: false,
            url: url
        });
    }
} ]);
