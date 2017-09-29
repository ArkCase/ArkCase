'use strict';

angular.module('dashboard.new-complaints').controller('Dashboard.NewComplaintsController', ['$scope', '$translate'
    , 'Authentication', 'Dashboard.DashboardService', 'ConfigService', 'params'
    , function ($scope, $translate
        , Authentication, DashboardService, ConfigService, params
    ) {
        var vm = this;
        vm.config = null;

        if(params.description !== undefined) {
            $scope.$parent.model.description = " - " + params.description;
        }

        ConfigService.getComponentConfig("dashboard", "newComplaints").then(function (config) {
            DashboardService.queryNewComplaints(function (solrData) {

                var data = {};
                var chartData = [];
                var labels = [];
                angular.forEach(solrData.response.docs, function (complaintDoc) {
                    var day = complaintDoc.create_date_tdt;
                    day = day.substring(0, day.indexOf("T"));
                    data[day] ? data[day]++ : data[day] = 1;
                });

                angular.forEach(data, function (index, value) {
                    labels.push(value);
                    chartData.push(index);
                });

                vm.showChart = labels.length > 0 && chartData.length > 0 ? true : false;

                vm.labels = labels;
                vm.data = [chartData];
                vm.series = [config.title];
            });
        });
    }
]);
