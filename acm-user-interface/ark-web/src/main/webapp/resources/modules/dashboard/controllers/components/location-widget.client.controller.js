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
        , 'Complaint.InfoService', 'Authentication', 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, ComplaintInfoService, Authentication, DashboardService, ConfigService) {

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

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo($stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "locations";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = [info];

                        var fullAddress = createFullAddress(info.location);
                        $scope.gridOptions.data[0].location.fullAddress = fullAddress ? fullAddress : "Error creating full address";
                        $scope.gridOptions.totalItems = 1;
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