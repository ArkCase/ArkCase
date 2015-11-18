/**
 * Created by nebojsha on 11/15/2015.
 */

'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.LdapConfigService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/admin/services/security.ldap.config.service.js modules/admin/services/security.ldap.config.service.js}
 *
 * The Admin.LdapConfigService provides LDAP Config REST calls functionality
 */
angular.module('admin').service('Admin.LdapConfigService', function ($http) {
    return ({
        retrieveDirectories: retrieveDirectories,
        createDirectory: createDirectory,
        deleteDirectory: deleteDirectory,
        updateDirectory: updateDirectory
    });
    /**
     * @ngdoc method
     * @name retrieveDirectories
     * @methodOf admin.services:Admin.LdapConfigService
     *
     * @description
     * Performs retrieving all directories in ldap configuration
     *
     * @returns {HttpPromise} Future info about ldap directories
     */
    function retrieveDirectories() {
        return $http({
            method: "GET",
            url: "proxy/arkcase/api/latest/plugin/admin/ldapconfiguration/directories"
        });
    };
    /**
     * @ngdoc method
     * @name createDirectory
     * @methodOf admin.services:Admin.LdapConfigService
     *
     * @description
     * Create new directory in ldap configuration
     *
     *
     * @param {object} dir dir row data send to the server
     *
     * @returns {HttpPromise} Future info about http post
     */
    function createDirectory(dir) {
        return $http({
            method: "POST",
            url: "proxy/arkcase/api/latest/plugin/admin/ldapconfiguration/directories",
            data: angular.toJson(dir),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };
    /**
     * @ngdoc method
     * @name deleteDirectory
     * @methodOf admin.services:Admin.LdapConfigService
     *
     * @description
     * Delete directory in ldap configuration
     *
     * @param {object} dirId dir id to be deleted
     *
     * @returns {HttpPromise} Future info about http delete
     */
    function deleteDirectory(dirId) {
        var url = 'proxy/arkcase/api/latest/plugin/admin/ldapconfiguration/directories/' + dirId;
        return $http({
            method: "DELETE",
            url: url
        });
    };

    /**
     * @ngdoc method
     * @name updateDirectory
     * @methodOf admin.services:Admin.LdapConfigService
     *
     * @description
     * Updates directory in ldap configuration
     *
     * @param {object} dirId dir data row to be updated
     *
     * @returns {HttpPromise} Future info about http post
     */
    function updateDirectory(dir) {
        var url = 'proxy/arkcase/api/latest/plugin/admin/ldapconfiguration/directories/' + dir["ldapConfig.id"];
        return $http({
            method: "PUT",
            url: url,
            data: angular.toJson(dir),
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

});
