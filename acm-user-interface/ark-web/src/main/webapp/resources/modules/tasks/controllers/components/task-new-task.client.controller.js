'use strict';


angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$stateParams', '$sce', '$q'
    , 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService'
    , function ($scope, $stateParams, $sce, $q, Util, TicketService, LookupService, FrevvoFormService) {

        var ticketInfo = TicketService.getArkCaseTicket();
        var acmFormsInfo = LookupService.getConfig({name: 'acm-forms'});

        $q.all([ticketInfo, acmFormsInfo.$promise])
            .then(function (data) {
                $scope.acmTicket = data[0].data;
                $scope.acmFormsProperties = data[1];
                var taskType = Util.goodValue($scope.acmFormsProperties["active.task.form"], "task");
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, taskType, $scope.acmTicket);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);
            });

    }
]);