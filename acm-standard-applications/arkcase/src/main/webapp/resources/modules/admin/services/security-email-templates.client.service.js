'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.EmailSTemplatesService
 *
 * @description
 * Contains REST calls for Admin Email Templates Configuration
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/admin/services/security-email-templates.client.service.js modules/admin/services/security-email-templates.client.service.js}
 *
 * Contains REST calls for Admin Email Templates Configuration
 */
angular.module('admin').factory('Admin.EmailTemplatesService', [ '$resource', 'UtilService', 'Upload', '$http', function($resource, Util, Upload, $http) {
    var Service = $resource('api/latest/plugin', {}, {});

    /**
     * @ngdoc method
     * @name getEmailTemplate
     * @methodOf services:Admin.EmailTemplatesService
     *
     * @description
     * Gets email template with name templateName
     *
     * @returns {Object} Promise
     */
    Service.getEmailTemplate = function(templateName) {
        return $http({
            url: 'api/latest/service/email/configure/template/' + templateName,
            method: 'GET'
        });
    };

    /**
     * @ngdoc method
     * @name saveEmailReceiverConfiguration
     * @methodOf services:Admin.EmailTemplatesService
     *
     * @description
     * Performs saving of the email receiver configuration.
     *
     * @param {Object} emailConf - the configuration that should be saved
     *
     * @returns {Object} http promise
     */
    Service.saveEmailReceiverConfiguration = function(emailConf) {
        return $http({
            url: 'api/latest/plugin/admin/email/receiver/configuration',
            method: 'PUT',
            data: emailConf
        });
    };

    Service.getEmailReceiverConfiguration = function() {
        return $http({
            url: 'api/latest/plugin/admin/email/receiver/configuration',
            method: 'GET',
        });
    };

    return Service;
} ]);
