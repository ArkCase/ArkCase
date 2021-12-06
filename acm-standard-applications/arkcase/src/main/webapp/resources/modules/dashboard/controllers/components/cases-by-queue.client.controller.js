'use strict';

angular.module('dashboard.cases-by-queue').controller('Dashboard.CasesByQueueController',
        [ '$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService', 'ConfigService', 'params', 'UtilService', 'Menus', function($scope, config, $state, $translate, DashboardService, ConfigService, params, Util, Menus) {

            var vm = this;

            vm.chartClick = chartClick;
            var config = null;

            if (!Util.isEmpty(params.description)) {
                $scope.$parent.model.description = " - " + params.description;
            } else {
                $scope.$parent.model.description = "";
            }

            ConfigService.getComponentConfig("dashboard", "casesByQueue").then(function(cfg) {
                config = cfg;
                // Load Cases info and render chart
                DashboardService.queryCasesByQueue(function(cases) {

                    var data = [];
                    var labels = [];

                    angular.forEach(cases, function(value, key) {
                        if (key.length > 0 && key[0] != '$') {
                            if (key != "Delete" && key != "Release") {
                                data.push(value);
                                labels.push(key);
                            }
                        }
                    });

                    vm.showChart = data.length > 0 ? true : false;
                    vm.data = [ data ];
                    vm.labels = labels;
                    vm.series = [ cfg.title ];
                });
            });

            /**
             * Redirect application to another state, if redirectSettings contains information
             * @param bars
             */
            function chartClick(bars) {
                if (config.redirectSettings && bars.length > 0) {
                    var label = bars[0].label;
                    // in case the queues have / in their names we need to replace them 
                    label = label.replaceAll("/", "-SLASH-");
                    var redirectObject = config.redirectSettings[label];
                    var dataModules = Menus.getMenu('leftnav');
                    angular.forEach(dataModules.items, function(item, i){
                        if(redirectObject && item.link == "queues"){
                            $state.go(redirectObject.state, redirectObject.params)
                        }
                    });
                }
            }
        } ]);