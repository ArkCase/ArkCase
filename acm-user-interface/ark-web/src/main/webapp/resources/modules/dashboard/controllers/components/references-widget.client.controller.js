'use strict';

angular.module('dashboard.references', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('references', {
                    title: 'References',
                    description: 'Displays references',
                    controller: 'Dashboard.ReferencesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/references-widget.client.view.html',
                    commonName: 'references'
                }
            );
    })
    .controller('Dashboard.ReferencesController', ['$scope', '$translate', '$stateParams', '$q','UtilService'
        , 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ConfigService'
        , 'Helper.ObjectBrowserService', 'ObjectService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ConfigService, HelperObjectBrowserService, ObjectService) {

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

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "references";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        /**
                         * Complaints and CaseFiles return their references in a different way.
                         */
                        var references = [];
                        if(module.name == ObjectService.ObjectTypes.COMPLAINT){
                            _.forEach(info.childObjects, function (childObject) {
                                if (ComplaintInfoService.validateReferenceRecord(childObject)) {
                                    references.push(childObject);
                                }
                            });
                            $scope.gridOptions.data = references ? references : [];
                            $scope.gridOptions.totalItems = references ? references.length : 0;
                        } else {
                            references = info.references;
                            $scope.gridOptions.data = references ? references : [];
                            $scope.gridOptions.totalItems = references ? references.length : 0;
                        }
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);