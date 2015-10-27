'use strict';

angular.module('cases').controller('Cases.WizardController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService',
    function($scope, $stateParams, $sce, $log, $q, TicketService, LookupService) {
        $scope.$emit('req-component-config', 'wizard');

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.buildFrevvoUrl = buildFrevvoUrl;
        $scope.openCreateCaseFrevvoForm = openCreateCaseFrevvoForm;

        /**
         * @ngdoc method
         * @name buildFrevvoUrl
         * @methodOf Cases.WizardController
         *
         * @param {JSON Object} acmFormsProperties properties from the acm-forms.properties configuration file
         *
         * @description
         * This method takes the configuration from acm-forms.properties and generates the
         * full Frevvo form url for the New Case form
         */
        function buildFrevvoUrl(acmFormsProperties) {

            // Loads Frevvo server basic configuration
            var protocol = acmFormsProperties['frevvo.protocol.internal'];
            var host = acmFormsProperties['frevvo.host.internal'];
            var port = acmFormsProperties['frevvo.port.internal'];

            // Loads the Frevvo url template and adds the values of its parameters
            var urlTemplate = acmFormsProperties['frevvo.uri'];
            var caseType = acmFormsProperties['active.case.form'];
            urlTemplate = urlTemplate.replace('{tenant}', acmFormsProperties['frevvo.tenant']);
            urlTemplate = urlTemplate.replace('{user}', acmFormsProperties['frevvo.designer.user']);
            urlTemplate = urlTemplate.replace('{application}', acmFormsProperties[caseType + '.application.id']);
            urlTemplate = urlTemplate.replace('{type}', acmFormsProperties[caseType + '.type']);
            urlTemplate = urlTemplate.replace('{mode}', acmFormsProperties[caseType + '.mode']);
            urlTemplate = urlTemplate.replace('{frevvo_timezone}', acmFormsProperties['frevvo.timezone']);
            urlTemplate = urlTemplate.replace('{frevvo_locale}', acmFormsProperties['frevvo.locale']);
            urlTemplate = urlTemplate.replace('{acm_ticket}', $scope.acmTicket);
            urlTemplate = urlTemplate.replace('{frevvo_service_baseUrl}', acmFormsProperties['frevvo.service.baseUrl']);
            urlTemplate = urlTemplate.replace('{frevvo_browser_redirect_baseUrl}', acmFormsProperties['frevvo.browser.redirect.baseUrl']);

            // Assembles the full url including the server host/port and the configured Frevvo form path
            return protocol + "://" + host + ":" + port + urlTemplate;
        }

        /**
          * @ngdoc method
          * @name openCreateCaseFrevvoForm
          * @methodOf Cases.WizardController
          *
          * @description
          * This method generates the create new case Frevvo form url and loads the form
          * into an iframe as a trusted resource.  It can only be called after the
          * acm-forms.properties config and the acmTicket have been obtained.
          */
        function openCreateCaseFrevvoForm() {
            var formUrl = buildFrevvoUrl($scope.acmFormsProperties);
            $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);
        }

        // Obtains authentication token for ArkCase
        var ticketInfo = TicketService.getArkCaseTicket();

        // Retrieves the properties from the acm-forms.properties file (including Frevvo configuration)
        var acmFormsInfo = LookupService.getConfig({name: 'acm-forms'});

        $q.all([ticketInfo, acmFormsInfo.$promise])
            .then(function(data) {
                $scope.acmTicket = data[0].data;
                $scope.acmFormsProperties = data[1];

                // Opens the new case Frevvo form for the user
                openCreateCaseFrevvoForm();
            });

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'wizard') {
                $scope.config = config;
            }
        }
    }
]);