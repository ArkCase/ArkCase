'use strict';

angular.module('dashboard.location', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('location', {
                    title: 'Location Widget',
                    description: 'Displays location',
                    controller: 'Dashboard.LocationController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/location-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.LocationController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Complaint.InfoService', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, ComplaintInfoService, Authentication, DashboardService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[12].columnDefs; //widget[12] = locations

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == 'complaint') {
                            ComplaintInfoService.getComplaintInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = [data];
                                    $scope.gridOptions.data[0].location.fullAddress = createFullAddress(data.location);
                                    $scope.gridOptions.totalItems = 1;
                                }
                                , function (error) {
                                    $scope.complaintInfo = null;
                                    $scope.progressMsg = $translate.instant("complaint.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }

            var createFullAddress = function (location) {
                var addr = null;
                if (location) {
                    var street = location.streetAddress;
                    if (location.streetAddress2) {
                        street += " " + location.streetAddress2
                    }
                    var city = location.city;
                    var state = location.state;
                    var zip = location.zip;
                    var country = "USA";
                    if (location.country) {
                        country = location.country;
                    }
                    return street + ", " + city + ", " + state + " " + zip + " " + country;
                }
            };
        }
    ]);