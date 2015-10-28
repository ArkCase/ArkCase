'use strict';

angular.module('cases').controller('Cases.StatusController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService', 'FrevvoFormService',
    function($scope, $stateParams, $sce, $log, $q, TicketService, LookupService, FrevvoFormService) {
        $scope.$emit('req-component-config', 'status');

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openChangeCaseStatusFrevvoForm = openChangeCaseStatusFrevvoForm;

        /**
          * @ngdoc method
          * @name openChangeCaseStatusFrevvoForm
          * @methodOf Cases.StatusController
          *
          * @description
          * This method generates the change case status Frevvo form url and loads the form
          * into an iframe as a trusted resource.  It can only be called after the
          * acm-forms.properties config and the acmTicket have been obtained.
          */
        function openChangeCaseStatusFrevvoForm() {
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, 'change_case_status', $scope.acmTicket);
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

                // Opens the change case status Frevvo form for the user
                openChangeCaseStatusFrevvoForm();
            });

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'status') {
                $scope.config = config;
            }
        }
    }
]);