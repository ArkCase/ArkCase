'use strict';

angular.module('time-tracking').controller('TimeTracking.EditTimesheetController', ['$scope', '$stateParams', '$sce', '$log', '$q', 'TicketService', 'LookupService', 'FrevvoFormService', 'StoreService',
    function($scope, $stateParams, $sce, $log, $q, TicketService, LookupService, FrevvoFormService, Store) {
        $scope.$emit('req-component-config', 'edittimesheet');
        $scope.$on('send-data-for-frevvo', function(e, data){
            var editTimesheetStore = new Store.Variable("EditTimesheetStore");
            editTimesheetStore.set(data);
            /*$scope.getData = data;
            console.log($scope.getData);
            $scope.getData.startDate = moment($scope.getData.startDate).format($scope.config.frevvoDateFormat);*/
        });

        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openCreateCaseFrevvoForm = openCreateCaseFrevvoForm;

        function openCreateCaseFrevvoForm() {
            var editTimesheetStore = new Store.Variable("EditTimesheetStore");
            var data = editTimesheetStore.get();
            if (data) {
                editTimesheetStore.set(null);
                $scope.getStartDate = data.startDate;
                $scope.getStartDate = moment($scope.getStartDate).format($scope.config.frevvoDateFormat);
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, "timesheet", $scope.acmTicket);
                var timesheetArgs = "period:'" +  $scope.getStartDate  + "',acm_ticket:";
                formUrl = formUrl.replace('acm_ticket:', timesheetArgs);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);
            }
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
            if (componentId == 'edittimesheet') {
                $scope.config = config;
            }
        }
    }
]);