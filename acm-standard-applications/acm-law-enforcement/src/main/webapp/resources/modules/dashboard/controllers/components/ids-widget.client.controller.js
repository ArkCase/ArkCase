'use strict';

angular.module('dashboard.ids', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('ids', {
                title: 'dashboard.widgets.ids.title',
                description: 'dashboard.widgets.ids.description',
                controller: 'Dashboard.IdsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/ids-widget.client.view.html',
                commonName: 'ids'
            });
    })
    .controller('Dashboard.IdsController', ['$scope', '$stateParams', '$translate',
        'Organization.InfoService', 'Helper.ObjectBrowserService', 'UtilService',
            function ($scope, $stateParams, $translate,
                  OrganizationInfoService, HelperObjectBrowserService, Util) {

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
                if(!Util.isEmpty(objectInfo.identifications)) {
                    $scope.gridOptions.data = objectInfo.identifications;
                    $scope.gridOptions.noData = false;
                }
                else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.ids.noDataMessage');
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "ids";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };

        }
    ]);