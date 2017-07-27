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
        'Organization.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
            function ($scope, $stateParams, $translate,
                      OrganizationInfoService, HelperObjectBrowserService, HelperUiGridService) {

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

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

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
                gridHelper.setWidgetsGridData(dbas);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "dbas";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

        }
    ]);