'use strict';

angular.module('services').factory('FrevvoFormService', [
    function () {
        return {
            /**
              * @ngdoc method
              * @name buildFrevvoUrl
              * @methodOf FrevvoFormService
              *
              * @param {JSON Object} acmFormsProperties properties from the acm-forms.properties configuration file
              * @param (String) type of the form to load (case_file, change_case_status, etc.)
              * @param (String) authentication token for ArkCase for the currently logged in user
              *
              * @description
              * This method takes the configuration from acm-forms.properties and generates the
              * full Frevvo form url for the specified form type
              */
            buildFrevvoUrl: function(acmFormsProperties, formType, acmTicket) {

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
                urlTemplate = urlTemplate.replace('{frevvo_locale}', acmFormsProperties['frevvo.locale']);
                urlTemplate = urlTemplate.replace('{acm_ticket}', acmTicket);
                urlTemplate = urlTemplate.replace('{frevvo_service_baseUrl}', acmFormsProperties['frevvo.service.baseUrl']);
                urlTemplate = urlTemplate.replace('{frevvo_browser_redirect_baseUrl}', acmFormsProperties['frevvo.browser.redirect.baseUrl']);

                // Assembles the full url including the server host/port and the configured Frevvo form path
                return protocol + "://" + host + ":" + port + urlTemplate;
            }
        }
    }
]);