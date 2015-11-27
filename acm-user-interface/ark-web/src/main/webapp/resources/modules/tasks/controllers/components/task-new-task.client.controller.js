'use strict';


angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$stateParams', '$sce', '$log', '$q'
    , 'UtilService', 'TicketService', 'Authentication', 'LookupService', 'Frevvo.FormService'
    , function ($scope, $stateParams, $sce, $log, $q, Util, TicketService, Authentication, LookupService, FrevvoFormService) {

        $scope.$emit('req-component-config', 'newTask');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'newTask') {
                $scope.config = config;
            }
        });

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