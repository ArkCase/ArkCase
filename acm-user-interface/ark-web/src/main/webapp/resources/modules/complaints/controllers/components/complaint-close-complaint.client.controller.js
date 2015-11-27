'use strict';

angular.module('complaints').controller('Complaints.CloseComplaintController', ['$scope', '$stateParams', '$sce', '$log', '$q'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService'
    , function ($scope, $stateParams, $sce, $log, $q, Util, TicketService, LookupService, FrevvoFormService) {

        var ticketInfo = TicketService.getArkCaseTicket();
        var acmFormsInfo = LookupService.getConfig({name: 'acm-forms'});

        $q.all([ticketInfo, acmFormsInfo.$promise])
            .then(function (data) {
                $scope.acmTicket = data[0].data;
                $scope.acmFormsProperties = data[1];
                var complaintType = Util.goodValue($scope.acmFormsProperties["close.complaint.form"], "close_complaint");
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, complaintType, $scope.acmTicket);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);
            });
    }
]);