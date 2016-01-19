'use strict';

angular.module('frevvo').controller('FrevvoController', ['$scope', '$stateParams', '$sce', '$q'
    , 'UtilService', 'ConfigService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'ServCommService'
    , function ($scope, $stateParams, $sce, $q
        , Util, ConfigService, TicketService, LookupService, FrevvoFormService, ServCommService) {

        var promiseConfig = ConfigService.getModuleConfig("frevvo");
        var promiseTicket = TicketService.getArkCaseTicket();
        var acmFormsInfo = LookupService.getConfig("acm-forms");

        $q.all([promiseConfig, promiseTicket, acmFormsInfo]).then(function (data) {
            $scope.config = data[0];
            $scope.acmTicket = data[1].data;
            $scope.acmFormsProperties = data[2];
            var found = _.find(Util.goodArray($scope.config.forms), {name: Util.goodValue($stateParams.name)});
            if (found && (found.formKey || found.formDefault)) {
                var formType = Util.goodValue($scope.acmFormsProperties[found.formKey], found.formDefault);
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, formType, $scope.acmTicket, $stateParams.arg);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);

                var z = 1;
                //$scope.$emit("rootScope:servcomm-request", {request: "new-case", data: "new case data"});
                //ServCommService.request("frevvo", "new-case", "data");
            }
        });
    }
]);

