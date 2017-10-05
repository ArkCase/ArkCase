'use strict';

angular.module('dashboard.new-cases')
    .controller('Dashboard.NewCasesController', ['$scope', '$translate', 'Authentication', 'Dashboard.DashboardService', 'ConfigService', 'params', 'UtilService',
    function ($scope, $translate, Authentication, DashboardService, ConfigService, params, Util) {

        var vm = this;

        vm.config = null;

        if(!Util.isEmpty( params.description)) {
            $scope.$parent.model.description = " - " + params.description;
        }
        else {
            $scope.$parent.model.description = "";
        }

        ConfigService.getComponentConfig("dashboard", "newCases").then(function (config) {
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
        });
    }
]);

