'use strict';

angular.module('dashboard.faxes', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('faxes', {
                title: 'dashboard.widgets.faxes.title',
                description: 'dashboard.widgets.faxes.description',
                controller: 'Dashboard.FaxesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/faxes-widget.client.view.html',
                commonName: 'faxes'
            });
    })
    .controller('Dashboard.FaxesController', ['$scope', '$stateParams', 'Organization.InfoService', 'Helper.ObjectBrowserService'
        , function ($scope, $stateParams, OrganizationInfoService, HelperObjectBrowserService) {

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
                var faxes = _.filter($scope.objectInfo.contactMethods, {type: 'fax'});
                $scope.gridOptions.data = faxes;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "faxes";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };

        }
    ]);