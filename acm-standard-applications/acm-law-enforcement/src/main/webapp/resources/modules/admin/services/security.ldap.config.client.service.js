'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.LdapConfigService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/security.ldap.config.client.service.js modules/admin/services/security.ldap.config.client.service.js}
 *
 * The Admin.LdapConfigService provides LDAP Config REST calls functionality
 */
angular.module('admin').service('Admin.LdapConfigService', ['$http'
    , function ($http) {

        return ({
            retrieveDirectories: retrieveDirectories,
            createDirectory: createDirectory,
            deleteDirectory: deleteDirectory,
            updateDirectory: updateDirectory
        });
        /**
         * @ngdoc method
         * @name retrieveDirectories
         * @methodOf admin.service:Admin.LdapConfigService
         *
         * @description
         * Performs retrieving all directories in ldap configuration
         *
         * @returns {HttpPromise} Future info about ldap directories
         */
        function retrieveDirectories() {
            return $http({
                method: "GET",
                url: "api/latest/plugin/admin/ldapconfiguration/directories"
            });
        }

        /**
         * @ngdoc method
         * @name createDirectory
         * @methodOf admin.service:Admin.LdapConfigService
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
                url: "api/latest/plugin/admin/ldapconfiguration/directories",
                data: angular.toJson(dir),
                headers: {
                    "Content-Type": "application/json"
                }
            });
        }

        /**
         * @ngdoc method
         * @name deleteDirectory
         * @methodOf admin.service:Admin.LdapConfigService
         *
         * @description
         * Delete directory in ldap configuration
         *
         * @param {object} dirId dir id to be deleted
         *
         * @returns {HttpPromise} Future info about http delete
         */
        function deleteDirectory(dirId) {
            var url = 'api/latest/plugin/admin/ldapconfiguration/directories/' + dirId;
            return $http({
                method: "DELETE",
                url: url
            });
        }

        /**
         * @ngdoc method
         * @name updateDirectory
         * @methodOf admin.service:Admin.LdapConfigService
         *
         * @description
         * Updates directory in ldap configuration
         *
         * @param {object} dirId dir data row to be updated
         *
         * @returns {HttpPromise} Future info about http post
         */
        function updateDirectory(dir) {
            var url = 'api/latest/plugin/admin/ldapconfiguration/directories/' + dir["ldapConfig.id"];
            return $http({
                method: "PUT",
                url: url,
                data: angular.toJson(dir),
                headers: {
                    "Content-Type": "application/json"
                }
            });
        }
    }]);
