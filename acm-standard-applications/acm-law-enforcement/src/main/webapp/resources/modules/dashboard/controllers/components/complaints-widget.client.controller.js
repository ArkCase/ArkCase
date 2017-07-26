'use strict';

angular.module('dashboard.complaints', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('complaints', {
                title: 'dashboard.widgets.complaints.title',
                description: 'dashboard.widgets.complaints.description',
                controller: 'Dashboard.ComplaintsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/complaints-widget.client.view.html',
                commonName: 'complaints'
            });
    })
    .controller('Dashboard.ComplaintsController', ['$scope', '$stateParams', '$translate',
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
                //FIX this when relations between complaints and organizations works
                //gridHelper.setWidgetsGridData(objectInfo.complaints);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "complaints";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

        }
    ]);