/**
 * Created by nebojsha on 11/10/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.services:Admin.ModuleConfigService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/security.module_config.service.js modules/admin/services/security.module_config.service.js}
 *
 * The Admin.ModuleConfigService provides Modules config REST calls functionality
 */
angular.module('admin').service('Admin.ModulesConfigService', function ($http) {
    return ({
        getAppModules: getAppModules,
        getRolesForModulePrivilege: getRolesForModulePrivilege,
        addRolesToModule: addRolesToModule,
        removeRolesFromModule: removeRolesFromModule
    });

    /**
     * @ngdoc method
     * @name getAppModules
     * @methodOf admin.services:Admin.ModuleConfigService
     *
     * @description
     * Performs retrieving all application modules
     *
     * @returns {HttpPromise} Future info about application modules
     */
    function getAppModules() {
        return $http({
            method: 'GET',
            url: 'proxy/arkcase/api/latest/plugin/admin/moduleconfiguration/modules'
        });
    };


    /**
     * @ngdoc method
     * @name getRolesForModulePrivilege
     * @methodOf admin.services:Admin.ModuleConfigService
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
            url: 'proxy/arkcase/api/latest/plugin/admin/rolesprivileges/privileges/' + modulePrivilege + '/roles'
        });
    };

    /**
     * @ngdoc method
     * @name addRolesToModule
     * @methodOf admin.services:Admin.ModuleConfigService
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
        var url = 'proxy/arkcase/api/latest/plugin/admin/rolesprivileges/roles/' + roles.join() + '/privileges/' + modulePrivilege;
        return $http({
            method: 'PUT',
            url: url,
            data: angular.toJson({}),
            headers: {
                'Content-Type': 'application/json'
            }
        });
    };

    /**
     * @ngdoc method
     * @name removeRolesFromModule
     * @methodOf admin.services:Admin.ModuleConfigService
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
        var url = 'proxy/arkcase/api/latest/plugin/admin/rolesprivileges/roles/' + roles.join() + '/privileges/' + modulePrivilege;
        return $http({
            method: 'DELETE',
            url: url,
        });
    };

});
