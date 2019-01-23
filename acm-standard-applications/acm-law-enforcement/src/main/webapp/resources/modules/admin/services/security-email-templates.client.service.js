'use strict';
/**
 * @ngdoc service
 * @name admin.service:Admin.EmailSTemplatesService
 *
 * @description
 * Contains REST calls for Admin Email Templates Configuration
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/security-email-templates.client.service.js modules/admin/services/security-email-templates.client.service.js}
 *
 * Contains REST calls for Admin Email Templates Configuration
 */
angular.module('admin').factory('Admin.EmailTemplatesService', [ '$resource', 'UtilService', 'Upload', '$http', function($resource, Util, Upload, $http) {
    var Service = $resource('api/latest/plugin', {}, {

        /**
         * @ngdoc method
         * @name list
         * @methodOf services:Admin.EmailTemplatesService
         *
         * @description
         * Get email templates
         *
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        list: {
            method: 'GET',
            url: 'api/latest/service/email/configure/template',
            cache: false,
            isArray: true
        },

        /**
         * @ngdoc method
         * @name delete
         * @methodOf services:Admin.EmailTemplatesService
         *
         * @description
         * Delete template data
         *
         * @param {Number} params.templateName  Template Name
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        _delete: {
            method: 'DELETE',
            url: 'api/latest/service/email/configure/template/:templateName',
            cache: false
        }

    });

    /**
     * @ngdoc method
     * @name listEmailTemplates
     * @methodOf services:Admin.EmailTemplatesService
     *
     * @description
     * Query email templates
     *
     * @returns {Object} Promise
     */
    Service.listEmailTemplates = function() {
        return Util.serviceCall({
            service: Service.list,
            onSuccess: function(data) {
                return data;
            }
        });
    };

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
     * @name deleteEmailTemplate
     * @methodOf services:Admin.EmailTemplatesService
     *
     * @description
     * Query person pictures
     *
     * @param {string} templateName  Template Name
     *
     * @returns {Object} Promise
     */
    Service.deleteEmailTemplate = function(templateName) {
        return Util.serviceCall({
            service: Service._delete,
            param: {
                templateName: templateName
            },
            onSuccess: function(data) {
                return data;
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateEmailTemplate
     * @methodOf services:Admin.EmailTemplatesService
     *
     * @description
     * Validate template data
     *
     * @param {Object} template  Template data
     *
     * @returns {Object} Promise
     */
    Service.validateEmailTemplate = function(template) {
        return $http({
            url: 'api/latest/service/email/configure/template/validate',
            method: 'PUT',
            data: template
        });
    };

    /**
     * @ngdoc method
     * @name saveEmailTemplate
     * @methodOf services:Admin.EmailSTemplatesService
     *
     * @description
     * Save template data
     *
     * @param {Object} template  Template data
     * @param {Object} file  File data
     *
     * @returns {Object} Promise
     */
    Service.saveEmailTemplate = function(template, file) {
        return Upload.upload({
            url: 'api/latest/service/email/configure/template',
            method: 'PUT',
            fields: {
                data: template
            },
            sendFieldsAs: 'json-blob',
            file: file
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
