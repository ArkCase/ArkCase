'use strict';

angular.module('frevvo').controller('FrevvoController', ['$rootScope', '$scope', '$stateParams', '$sce', '$q', '$modal', '$translate'
    , 'UtilService', 'ConfigService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'ServCommService'
    , function ($rootScope, $scope, $stateParams, $sce, $q, $modal, $translate
        , Util, ConfigService, TicketService, LookupService, FrevvoFormService, ServCommService) {

        var promiseConfig = ConfigService.getModuleConfig("frevvo");
        var promiseTicket = TicketService.getArkCaseTicket();
        var acmFormsInfo = LookupService.getConfig("acm-forms", ["frevvo.admin.user", "frevvo.admin.password"]);

        $q.all([promiseConfig, promiseTicket, acmFormsInfo]).then(function (data) {
            $scope.config = data[0];
            $scope.acmTicket = data[1].data;
            $scope.acmFormsProperties = data[2];
            var found = _.find(Util.goodArray($scope.config.forms), {name: Util.goodValue($stateParams.name)});
            if (found && (found.formKey || found.formDefault)) {
                var formType = Util.goodValue($scope.acmFormsProperties[found.formKey], found.formDefault);
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, formType, $scope.acmTicket, $stateParams);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);

                ServCommService.request($scope, "frevvo", $stateParams.name, found);
            }
        });

        $scope.iframeLoaded = function(){
            if ($rootScope.frevvoMessaging == null) {
                $rootScope.frevvoMessaging = {};
                $rootScope.frevvoMessaging.receiver = document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow;
                $rootScope.frevvoMessaging.send = function send(message) {
                    $rootScope.frevvoMessaging.receiver.postMessage(message, '*');
                }
                $rootScope.frevvoMessaging.receive = function receive(e) {
                    if (!Util.isEmpty(e.data.source) &&  e.data.source == "frevvo" && !Util.isEmpty(e.data.action)) {
                        // Do actions sent from Frevvo
                        if (e.data.action == "open-user-picker") {
                            pickUser(e.data);
                        }
                    }
                }

                window.addEventListener("message", $rootScope.frevvoMessaging.receive);
            } else {
                $rootScope.frevvoMessaging.receiver = document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow;
            }
        };

        function pickUser(data) {
            var params = {};

            var owningGroup = "";
            if (!Util.isEmpty(data.data) && !Util.isEmpty(data.data.owningGroup))
            {
                owningGroup = '&fq="Group": ' + data.data.owningGroup;
            }

            params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
            params.filter = '"Object Type": USER' + owningGroup;
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/frevvo/views/frevvo-participants-picker-modal.client.view.html",
                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                    $scope.modalInstance = $modalInstance;
                    $scope.header = params.header;
                    $scope.filter = params.filter;
                    $scope.config = params.config;
                }],
                animation: true,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (selected) {
                if (!Util.isEmpty(selected)) {

                    var message = {};
                    message.source = "arkcase";
                    message.data = selected;
                    message.action = "fill-user-picker-data";
                    message.elementId = data.elementId;

                    $scope.frevvoMessaging.send(message);
                }
            });
        };
    }
]);