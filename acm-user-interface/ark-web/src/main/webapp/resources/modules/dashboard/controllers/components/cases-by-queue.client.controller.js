'use strict';

angular.module('dashboard.cases-by-queue')
    .controller('Dashboard.CasesByQueueController', ['$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $state, $translate, DashboardService) {

            var vm = this;

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'casesByQueue');

            function applyConfig(e, componentId, config) {
                if (componentId == 'casesByQueue') {
                    // Load Cases info and render chart
                    DashboardService.queryCasesByQueue(function (cases) {

                        var data = [];
                        var labels = [];

                        angular.forEach(cases, function (value, key) {
                            if (key.length > 0 && key[0] != '$') {
                                data.push(value);
                                labels.push(key);
                            }
                        });

                        vm.showChart = data.length > 0 ? true : false;
                        vm.data = data;
                        vm.labels = labels;
                        vm.series = [config.title];
                    });
                }
            }
        }]);