'use strict';

angular.module('dashboard.locations', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('locations', {
                title: 'dashboard.widgets.locations.title',
                description: 'dashboard.widgets.locations.description',
                controller: 'Dashboard.LocationsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/location-widget.client.view.html',
                commonName: 'locations'
            });
    })
    .controller('Dashboard.LocationsController', ['$scope', '$stateParams', '$translate',
        'Complaint.InfoService', 'Person.InfoService', 'Organization.InfoService', 'ObjectService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'UtilService',
        function ($scope, $stateParams, $translate,
                  ComplaintInfoService, PersonInfoService, OrganizationInfoService, ObjectService, HelperObjectBrowserService, HelperUiGridService, Util) {

            var modules = [
                {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                },
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
            var promiseUsers = gridHelper.getUsers();

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
                if((objectInfo.objectType != ObjectService.ObjectTypes.PERSON && objectInfo.objectType != ObjectService.ObjectTypes.ORGANIZATION) && objectInfo.container.containerObjectType == ObjectService.ObjectTypes.COMPLAINT) {
                    var location = Util.goodMapValue($scope.objectInfo, "location", null);
                    if(location != null){
                        var locationArrayTransformer = [location];
                    }
                    gridHelper.setWidgetsGridData(locationArrayTransformer);
                }
                else {
                    gridHelper.setWidgetsGridData(objectInfo.addresses);
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "addresses";
                });
                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                gridHelper.setColumnDefs(widgetInfo);
            };

        }
    ]);