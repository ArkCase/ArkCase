'use strict';

angular.module('dashboard.new-cases').controller('Dashboard.NewCasesController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService',
    function ($scope, $translate, Authentication, DashboardService) {

        var vm = this;

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'newCases');

        vm.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'newCases') {

                DashboardService.queryNewCases(function (solrData) {

                    var data = {};
                    var chartData = [];
                    var labels = [];
                    angular.forEach(solrData.response.docs, function (caseDoc) {
                        var day = caseDoc.create_date_tdt;
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

