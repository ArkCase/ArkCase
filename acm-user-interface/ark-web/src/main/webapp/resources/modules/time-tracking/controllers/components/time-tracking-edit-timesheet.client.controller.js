'use strict';

angular.module('time-tracking').controller('TimeTracking.EditTimesheetController', ['$scope', '$stateParams', '$sce', '$q', 'TicketService', 'LookupService', 'Frevvo.FormService',
    function($scope, $stateParams, $sce, $q, TicketService, LookupService, FrevvoFormService) {
        $scope.$emit('req-component-config', 'edittimesheet');
        $scope.$on('component-config', applyConfig);

        function applyConfig(e, componentId, config) {
            if (componentId == 'edittimesheet') {
                $scope.config.edittimesheet = config;
            }
        }

        $scope.period = $stateParams.period;

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openExistingCaseFrevvoForm = openExistingCaseFrevvoForm;

        function openExistingCaseFrevvoForm() {

            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, "timesheet", $scope.acmTicket);
            var timesheetArgs = "period:'" +  $scope.period  + "',acm_ticket:";
            formUrl = formUrl.replace('acm_ticket:', timesheetArgs);
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

                // Opens the existing timesheet Frevvo form for the user
                openExistingCaseFrevvoForm();
            });


    }
]);