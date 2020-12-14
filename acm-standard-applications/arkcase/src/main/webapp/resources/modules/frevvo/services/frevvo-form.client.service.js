'use strict';

/**
 * @ngdoc service
 * @name service:Frevvo.FormService
 *
 * @description
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/frevvo/services/frevvo-form.client.service.js modules/frevvo/services/frevvo-form.client.service.js}
 *
 * This service contains functionality for Frevvo form management.
 */
angular.module('frevvo').factory('Frevvo.FormService', [ 'UtilService', 'Config.LocaleService', function(Util, LocaleService) {
    return {
        /**
         * @ngdoc method
         * @name buildFrevvoUrl
         * @methodOf service:Frevvo.FormService
         *
         * @param {JSON} acmFormsProperties properties from the acm-forms.properties configuration file
         * @param {String} type of the form to load (case_file, change_case_status, etc.)
         * @param {String} authentication token for ArkCase for the currently logged in user
         * @param {JSON} optional caseFile metadata for an existing case file to be modified
         *
         * @description
         * This method takes the configuration from acm-forms.properties and generates the
         * full Frevvo form url for the specified form type.  If a caseFile is specified,
         * then that case file data will be loaded into the form to edit.
         */
        buildFrevvoUrl: function(acmFormsProperties, formType, acmTicket, arg) {

            // Loads Frevvo server basic configuration
            var protocol = acmFormsProperties['frevvo.protocol'];
            var host = acmFormsProperties['frevvo.host'];
            var port = acmFormsProperties['frevvo.port'];

            // Loads the Frevvo url template and adds the values of its parameters
            var urlTemplate = acmFormsProperties['frevvo.uri'];
            urlTemplate = urlTemplate.replace('{tenant}', acmFormsProperties['frevvo.tenant']);
            urlTemplate = urlTemplate.replace('{user}', acmFormsProperties['frevvo.designer.user']);
            urlTemplate = urlTemplate.replace('{application}', acmFormsProperties[formType + '.application.id']);
            urlTemplate = urlTemplate.replace('{type}', acmFormsProperties[formType + '.type']);
            urlTemplate = urlTemplate.replace('{mode}', acmFormsProperties[formType + '.mode']);
            urlTemplate = urlTemplate.replace('{frevvo_timezone}', acmFormsProperties['frevvo.timezone']);
            urlTemplate = urlTemplate.replace('{frevvo_locale}', LocaleService.getLocaleData().code);
            urlTemplate = urlTemplate.replace('{acm_ticket}', acmTicket);
            urlTemplate = urlTemplate.replace('{frevvo_service_baseUrl}', acmFormsProperties['frevvo.service.baseUrl']);
            urlTemplate = urlTemplate.replace('{frevvo_service_external_baseUrl}', acmFormsProperties['frevvo.service.external.baseUrl']);
            urlTemplate = urlTemplate.replace('{frevvo_browser_redirect_baseUrl}', acmFormsProperties['frevvo.browser.redirect.baseUrl']);
            if (!Util.isEmpty(arg)) {
                var replacement = "";
                _.each(arg, function(v, k) {
                    replacement += k + ":'" + v + "',";
                });
                if (!Util.isEmpty(replacement)) {
                    replacement += "acm_ticket:";
                    urlTemplate = urlTemplate.replace('acm_ticket:', replacement);
                }
            }

            //// Frevvo will load an existing case file if we are changing the status/editing a case file
            //if (caseFile) {
            //    var caseFileArgs = "caseId:'" + caseFile.id + "',actionNumber:'" + caseFile.caseNumber + "',status:'" + caseFile.status + "',acm_ticket:";
            //    urlTemplate = urlTemplate.replace('acm_ticket:', caseFileArgs);
            //}

            // Assembles the full url including the server host/port and the configured Frevvo form path
            return protocol + "://" + host + ":" + port + urlTemplate;
        }
    }
} ]);