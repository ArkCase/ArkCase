'use strict';

angular.module('admin').controller('Admin.EditPlainFormController', ['$scope', '$stateParams', '$sce', '$q', 'TicketService', 'LookupService', 'FrevvoFormService',
    function($scope, $stateParams, $sce, $q, TicketService, LookupService, FrevvoFormService) {

        $scope.key = $stateParams.key;
        $scope.target = $stateParams.target;
        $scope.mode = "edit";
        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openExistingPlainFrevvoForm = openExistingPlainFrevvoForm;

        function openExistingPlainFrevvoForm() {
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, "plain_configuration", $scope.acmTicket);
            var plainFormArgs = "formKey:'" +  $scope.key  + "',formTarget:'" + $scope.target + "',mode:'" + $scope.mode + "',acm_ticket:";
            formUrl = formUrl.replace('acm_ticket:', plainFormArgs);
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
                openExistingPlainFrevvoForm();
            });


    }
]);