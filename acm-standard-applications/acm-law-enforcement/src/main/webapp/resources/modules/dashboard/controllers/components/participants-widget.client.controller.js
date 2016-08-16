'use strict';

angular.module('dashboard.participants', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('participants', {
                    title: 'Participants',
                    description: 'Displays Participants',
                    controller: 'Dashboard.ParticipantsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/participants-widget.client.view.html',
                    commonName: 'participants'
                }
            );
    })
    .controller('Dashboard.ParticipantsController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ConfigService'
        , 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ConfigService, HelperObjectBrowserService, HelperUiGridService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: CaseInfoService.getCaseInfo}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (module && Util.goodPositive(currentObjectId, false)) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(currentObjectId);
                var gridHelper = new HelperUiGridService.Grid({scope: $scope});
                var promiseUsers = gridHelper.getUsers();

                $q.all([promiseConfig, promiseInfo, promiseUsers]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "participants";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = info.participants;
                        $scope.gridOptions.totalItems = $scope.gridOptions.data.length;

                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);