'use strict';

angular.module('cases').controller('Cases.EditCaseController', ['$scope', '$stateParams', '$sce', '$q'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService'
    , function ($scope, $stateParams, $sce, $q, Util, TicketService, LookupService, FrevvoFormService) {

        $scope.mode = "edit";
        $scope.acmTicket = '';
        $scope.acmFormsProperties = {};
        $scope.frevvoFormUrl = '';

        // Methods
        $scope.openExistingCaseFrevvoForm = openExistingCaseFrevvoForm;

        //This method generates the create new case Frevvo form url and loads the form
        function openExistingCaseFrevvoForm() {
            var caseFile = {
                id: $stateParams['id'],
                caseNumber: $stateParams['caseNumber'],
                containerId: $stateParams['containerId'],
                folderId: $stateParams['folderId']
            };
            var caseType = Util.goodValue($scope.acmFormsProperties["active.case.form"], "case");
            var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, caseType, $scope.acmTicket);
            var caseFileArgs = "caseId:'" + caseFile.id + "',caseNumber:'" + caseFile.caseNumber + "',mode:'" + $scope.mode + "',containerId:'" + caseFile.containerId + "',folderId:'" + caseFile.folderId + "',acm_ticket:";
            formUrl = formUrl.replace('acm_ticket:', caseFileArgs);
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

                // Opens the existing Case File Frevvo form for the user
                openExistingCaseFrevvoForm();

            });
    }
]);
