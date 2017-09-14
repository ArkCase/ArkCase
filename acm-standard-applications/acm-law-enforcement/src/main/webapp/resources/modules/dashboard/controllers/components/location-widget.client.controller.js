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
        'Complaint.InfoService', 'Person.InfoService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'UtilService',
            function ($scope, $stateParams, $translate,
                      ComplaintInfoService, PersonInfoService, OrganizationInfoService, HelperObjectBrowserService, HelperUiGridService, Util) {

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
            //var promiseUsers = gridHelper.getUsers();

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
                /*if (!Util.isEmpty(objectInfo.location)) {
                    $scope.gridOptions.data = [objectInfo];
                    var fullAddress = createFullAddress(objectInfo.location);
                    $scope.gridOptions.data[0].location.fullAddress = fullAddress ? fullAddress : "Error creating full address";
                }
                else if(!Util.isArrayEmpty(objectInfo.addresses)){
                    $scope.gridOptions.data = $scope.objectInfo.addresses;
                    $scope.gridOptions.noData = false;
                }
                else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.locations.noDataMessage');
                }
                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;*/
                $scope.objectInfo = objectInfo;
                gridHelper.setWidgetsGridData(objectInfo.location);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "locations";
                });
                //gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                gridHelper.setColumnDefs(widgetInfo);
            };

            /*var createFullAddress = function (location) {
                if (location) {
                    var street = location.streetAddress;
                    //street += location.streetAddress2 ? " " + location.streetAddress2 : "";
                    street = _.filter([street, location.streetAddress2]).join(" ");
                    var city = location.city;
                    var state = location.state;
                    var zip = location.zip;
                    var country = location.country ? location.country : "USA";
                    return _.filter([street, city, state, zip, country]).join(", ");
                }
                return "";
            };*/
        }
    ]);