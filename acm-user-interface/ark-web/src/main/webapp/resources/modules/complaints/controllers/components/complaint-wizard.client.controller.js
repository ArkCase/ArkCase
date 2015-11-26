'use strict';

angular.module('complaints').controller('Complaints.WizardController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService', 'FrevvoFormService',
    function ($scope, $stateParams, $sce, $log, $q, TicketService, LookupService, FrevvoFormService) {
        $scope.$emit('req-component-config', 'wizard');

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openCreateComplaintFrevvoForm = openCreateComplaintFrevvoForm;

        /**
         * This method generates the create new complaint Frevvo form url and loads the form
         * into an iframe as a trusted resource.  It can only be called after the
         * acm-forms.properties config and the acmTicket have been obtained.
         */
        function openCreateComplaintFrevvoForm() {
            var complaintType = $scope.acmFormsProperties['active.complaint.form'];
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, complaintType, $scope.acmTicket);
            $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);
        }

        // Obtains authentication token for ArkCase
        var ticketInfo = TicketService.getArkCaseTicket();

        // Retrieves the properties from the acm-forms.properties file (including Frevvo configuration)
        var acmFormsInfo = LookupService.getConfig({name: 'acm-forms'});

        $q.all([ticketInfo, acmFormsInfo.$promise])
            .then(function (data) {
                $scope.acmTicket = data[0].data;
                $scope.acmFormsProperties = data[1];

                // Opens the new complaint Frevvo form for the user
                openCreateComplaintFrevvoForm();
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