'use strict';

angular.module('dashboard.urls', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('urls', {
                title: 'dashboard.widgets.urls.title',
                description: 'dashboard.widgets.urls.description',
                controller: 'Dashboard.UrlsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/urls-widget.client.view.html',
                commonName: 'urls'
            });
    })
    .controller('Dashboard.UrlsController', ['$scope', '$stateParams', '$translate',
        'Person.InfoService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'UtilService', 'Object.LookupService',
            function ($scope, $stateParams, $translate,
                  PersonInfoService, OrganizationInfoService, HelperObjectBrowserService, HelperUiGridService, Util, ObjectLookupService) {

                var modules = [
                    {
                        name: "PERSON",
                        configName: "people",
                        getInfo: PersonInfoService.getPersonInfo,
                        validateInfo: PersonInfoService.validatePersonInfo
                    },
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
                    var urls = _.filter($scope.objectInfo.contactMethods, {type: 'url'});
                    gridHelper.setWidgetsGridData(urls);
                };

                var onConfigRetrieved = function (componentConfig) {
                    var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                        return widget.id === "urls";
                    });
                    gridHelper.setColumnDefs(widgetInfo);
                };

                ObjectLookupService.getSubContactMethodType('email').then(
                    function (contactMethodTypes) {
                        $scope.urlTypes = contactMethodTypes;
                        return contactMethodTypes;
                    });
        }
    ]);