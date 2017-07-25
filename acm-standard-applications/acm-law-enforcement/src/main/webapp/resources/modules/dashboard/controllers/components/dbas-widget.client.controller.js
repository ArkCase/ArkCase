'use strict';

angular.module('dashboard.dbas', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('dbas', {
                title: 'dashboard.widgets.dbas.title',
                description: 'dashboard.widgets.dbas.description',
                controller: 'Dashboard.DbasController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/dbas-widget.client.view.html',
                commonName: 'dbas'
            });
    })
    .controller('Dashboard.DbasController', ['$scope', '$stateParams', '$translate',
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
                $scope.objectInfo = objectInfo;
                var dbas = _.filter($scope.objectInfo.organizationDBAs, {type: 'DBA'});
                if(!Util.isArrayEmpty(dbas)) {
                    $scope.gridOptions.data = dbas;
                    $scope.gridOptions.noData = false;
                }
                else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.dbas.noDataMessage');
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "dbas";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };

        }
    ]);