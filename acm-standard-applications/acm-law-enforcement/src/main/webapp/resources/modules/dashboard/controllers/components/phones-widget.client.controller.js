'use strict';

angular.module('dashboard.phones', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('phones', {
                title: 'dashboard.widgets.phones.title',
                description: 'dashboard.widgets.phones.description',
                controller: 'Dashboard.PhonesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/phones-widget.client.view.html',
                commonName: 'phones'
            });
    })
    .controller('Dashboard.PhonesController', ['$scope', '$stateParams', '$translate',
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
                    var phones = _.filter($scope.objectInfo.contactMethods, {type: 'phone'});
                    gridHelper.setWidgetsGridData(phones);
                };

                var onConfigRetrieved = function (componentConfig) {
                    var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                        return widget.id === "phones";
                    });
                    gridHelper.setColumnDefs(widgetInfo);
                };

        }
    ]);

