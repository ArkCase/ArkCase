'use strict';

angular.module('dashboard.locations', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('locations', {
                    title: 'Locations',
                    description: 'Displays locations',
                    controller: 'Dashboard.LocationsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/location-widget.client.view.html',
                    commonName: 'locations'
                }
            );
    })
    .controller('Dashboard.LocationsController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Complaint.InfoService', 'Authentication', 'Dashboard.DashboardService', 'ConfigService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $translate, $stateParams, $q, Util, ComplaintInfoService, Authentication, DashboardService, ConfigService
        , HelperObjectBrowserService, HelperUiGridService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (module && Util.goodPositive(currentObjectId, false)) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(currentObjectId);
                var gridHelper = new HelperUiGridService.Grid({scope: $scope});
                var promiseUsers = gridHelper.getUsers();

                $q.all([promiseConfig, promiseInfo, promiseUsers]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "locations";
                        });
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = [info];
                        if($scope.gridOptions.data[0].location) {
                            var fullAddress = createFullAddress(info.location);
                            $scope.gridOptions.data[0].location.fullAddress = fullAddress ? fullAddress : "Error creating full address";
                            $scope.gridOptions.totalItems = 1;
                        } else {
                            //No location data to show
                            $scope.gridOptions.totalItems = 0;
                        }
                    },
                    function (err) {

                    }
                );
            }

            var createFullAddress = function (location) {
                if (location) {
                    var street = location.streetAddress;
                    street += location.streetAddress2 ? " " + location.streetAddress2 : "";
                    var city = location.city;
                    var state = location.state;
                    var zip = location.zip;
                    var country = location.country ? location.country : "USA";
                    return street + ", " + city + ", " + state + " " + zip + " " + country;
                }
                return undefined;
            };
        }
    ]);