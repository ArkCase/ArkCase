'use strict';

angular.module('complaints').controller('Complaints.NewComplaintController', ['$scope', '$stateParams', '$sce', '$q'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService'
    , function ($scope, $stateParams, $sce, $q, Util, TicketService, LookupService, FrevvoFormService) {

        //$scope.$emit('req-component-config', 'new');
        //$scope.$on('component-config', function (e, componentId, config) {
        //    if (componentId == 'new') {
        //        $scope.config = config;
        //    }
        //});

        //$scope.acmTicket = '';
        //$scope.acmFormsProperties = {};
        //$scope.frevvoFormUrl = '';

        var ticketInfo = TicketService.getArkCaseTicket();
        var acmFormsInfo = LookupService.getConfig({name: 'acm-forms'});

        $q.all([ticketInfo, acmFormsInfo.$promise])
            .then(function (data) {
                $scope.acmTicket = data[0].data;
                $scope.acmFormsProperties = data[1];
                var complaintType = Util.goodValue($scope.acmFormsProperties["active.complaint.form"], "complaint");
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, complaintType, $scope.acmTicket);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);
            });
    }
]);