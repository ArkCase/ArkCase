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
                //if(objectInfo.complaints.length != 0) {
                    $scope.gridOptions.data = objectInfo.response.docs;
                    $scope.gridOptions.noData = false;
                //}
                /*else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.complaints.noDataMessage');
                }*/
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "complaints";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };

        }
    ]);