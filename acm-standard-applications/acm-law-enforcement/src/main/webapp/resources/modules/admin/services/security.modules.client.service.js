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
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/security.modules.client.service.js modules/admin/services/security.modules.client.service.js}
 *
 * The Admin.ModulesService provides Modules REST calls functionality
 */
angular.module('admin').service('Admin.ModulesService', function ($http, UtilService) {
    return ({
        getAppModules: getAppModules,
        getRolesForModulePrivilege: getRolesForModulePrivilege,
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
            url: UtilService.noCacheUrl('api/latest/plugin/admin/moduleconfiguration/modules')
        });
    };


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
            url: UtilService.noCacheUrl('api/latest/plugin/admin/rolesprivileges/privileges/' + modulePrivilege + '/roles')
        });
    };

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
            data: angular.toJson({}),
            headers: {
                'Content-Type': 'application/json'
            }
        });
    };

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
            url: url
        });
    };

});
