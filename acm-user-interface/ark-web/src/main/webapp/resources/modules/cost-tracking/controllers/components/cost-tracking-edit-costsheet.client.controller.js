'use strict';

/**
 * @ngdoc controller
 * @name cost-tracking.controller:CostTracking.EditCostsheetController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/controllers/components/cost-tracking-edit-costsheet.client.controller.js modules/cost-tracking/controllers/components/cost-tracking-edit-costsheet.client.controller.js}
 *
 * The EditCostsheet Controller
 */
angular.module('cost-tracking').controller('CostTracking.EditCostsheetController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService', 'Frevvo.FormService',
    function($scope, $stateParams, $sce, $log, $q, TicketService, LookupService, FrevvoFormService) {
        $scope.$emit('req-component-config', 'editcostsheet');
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'editcostsheet') {
                $scope.config = config;
            }
        }

        $scope.id = $stateParams.id;
        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openExistingCaseFrevvoForm = openExistingCaseFrevvoForm;


        /**
         * @ngdoc method
         * @name openExistingCaseFrevvoForm
         * @methodOf cost-tracking.controller:CostTracking.EditCostsheetController
         *
         * @description
         * This method generates the create new case Frevvo form url and loads the form
         * into an iframe as a trusted resource.  It can only be called after the
         * acm-forms.properties config and the acmTicket have been obtained.
         */
        function openExistingCaseFrevvoForm() {
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, "costsheet", $scope.acmTicket);
            var costsheetArgs = "id:'" +  $scope.id  + "',acm_ticket:";
            formUrl = formUrl.replace('acm_ticket:', costsheetArgs);
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

                // Opens the new costsheet Frevvo form for the user
                openExistingCaseFrevvoForm();
            });

        $scope.config = null;

    }
]);