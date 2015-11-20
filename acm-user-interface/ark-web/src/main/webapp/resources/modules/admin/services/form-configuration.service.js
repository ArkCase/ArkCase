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
angular.module('admin').service('Admin.FormConfigService', function ($http) {
    return ({
        retrievePlainForms: retrievePlainForms,
        deletePlainForm: deletePlainForm
    });

    function retrievePlainForms() {
        return $http({
            method: "GET",
            url: "proxy/arkcase/api/latest/plugin/admin/plainforms"
        });
    }

    function deletePlainForm(key, target) {
        var url = 'proxy/arkcase/api/latest/plugin/admin/plainforms/' + key + "/" + target;
        return $http({
            method: "DELETE",
            url: url
        });
    }
});
