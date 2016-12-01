'use strict';

angular.module('dashboard.new-complaints').controller('Dashboard.NewComplaintsController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService',
    function ($scope, $translate, Authentication, DashboardService) {

        var vm = this;

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'newComplaints');

        vm.config = null;
        var userInfo = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'newComplaints') {

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
                })
            }
        }
    }
]);
