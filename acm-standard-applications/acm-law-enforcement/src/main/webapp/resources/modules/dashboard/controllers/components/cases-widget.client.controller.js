'use strict';

angular.module('dashboard.cases', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('cases', {
                title: 'dashboard.widgets.cases.title',
                description: 'dashboard.widgets.cases.description',
                controller: 'Dashboard.CasesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-widget.client.view.html',
                commonName: 'cases'
            });
    })
    .controller('Dashboard.CasesController', ['$scope', '$stateParams', '$translate',
        'Organization.InfoService', 'Helper.ObjectBrowserService',
        function ($scope, $stateParams, $translate,
                  OrganizationInfoService, HelperObjectBrowserService) {

            var modules = [
                {
                    name: "ORGANIZATION",
                    configName: "organizations",
                    getInfo: OrganizationInfoService.getOrganizationInfo,
                    validateInfo: OrganizationInfoService.validateOrganizationInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                if(objectInfo.cases.length != 0) {
                    $scope.gridOptions.data = objectInfo.cases;
                    $scope.gridOptions.noData = false;
                }
                else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.cases.noDataMessage');
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "cases";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };

        }
    ]);