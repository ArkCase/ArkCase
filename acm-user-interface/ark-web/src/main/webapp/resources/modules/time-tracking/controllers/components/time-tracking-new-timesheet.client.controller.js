'use strict';

angular.module('time-tracking').controller('TimeTracking.NewTimesheetController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService', 'Frevvo.FormService',
    function($scope, $stateParams, $sce, $log, $q, TicketService, LookupService, FrevvoFormService) {
        $scope.$emit('req-component-config', 'newtimesheet');

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openCreateCaseFrevvoForm = openCreateCaseFrevvoForm;

        function openCreateCaseFrevvoForm() {
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, "timesheet", $scope.acmTicket);
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

                // Opens the new timesheet Frevvo form for the user
                openCreateCaseFrevvoForm();
            });

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'newtimesheet') {
                $scope.config = config;
            }
        }
    }
]);