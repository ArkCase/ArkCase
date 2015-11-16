'use strict';

/**
 * @ngdoc controller
 * @name cases.controller:Cases.WizardController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/controllers/components/case-newcase.client.controller.js modules/cases/controllers/components/case-newcase.client.controller.js}
 *
 * The Wizard Controller
 */
angular.module('cases').controller('Cases.WizardController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService', 'FrevvoFormService',
    function($scope, $stateParams, $sce, $log, $q, TicketService, LookupService, FrevvoFormService) {
        $scope.$emit('req-component-config', 'newcase');

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openCreateCaseFrevvoForm = openCreateCaseFrevvoForm;

        /**
          * @ngdoc method
          * @name openCreateCaseFrevvoForm
          * @methodOf cases.controller:Cases.WizardController
          *
          * @description
          * This method generates the create new case Frevvo form url and loads the form
          * into an iframe as a trusted resource.  It can only be called after the
          * acm-forms.properties config and the acmTicket have been obtained.
          */
        function openCreateCaseFrevvoForm() {
            var caseType = $scope.acmFormsProperties['active.case.form'];
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, caseType, $scope.acmTicket);
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
            if (componentId == 'newcase') {
                $scope.config = config;
            }
        }
    }
]);