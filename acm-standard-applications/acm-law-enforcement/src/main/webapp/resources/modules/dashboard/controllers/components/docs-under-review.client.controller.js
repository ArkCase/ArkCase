'use strict';

angular.module('dashboard.docsUnderReview', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('docsUnderReview', {
        title: 'preference.overviewWidgets.docsUnderReview.title',
        description: 'dashboard.widgets.docsUnderReview.description',
        controller: 'Dashboard.DocsUnderReviewController',
        controllerAs: 'docsUnderReview',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/docs-under-review-widget.client.view.html',
        commonName: 'docsUnderReview'
    });
}).controller(
    'Dashboard.DocsUnderReviewController',
    [ '$scope', 'config', '$stateParams', '$translate', 'Dashboard.DashboardService', 'Helper.ObjectBrowserService', 'EcmService', 'Case.InfoService', 'ObjectService', 'Complaint.InfoService', 'Task.InfoService', 'UtilService',
        function($scope, config, $stateParams, $translate, DashboardService, HelperObjectBrowserService, Ecm, CaseInfoService, ObjectService, ComplaintInfoService, TaskInfoService, Util) {

            var vm = this;

            var modules = [ {
                name: "TASK",
                configName: "tasks",
                getInfo: TaskInfoService.getTaskInfo,
                validateInfo: TaskInfoService.validateTaskInfo
            }, {
                name: "ADHOC",
                configName: "tasks",
                getInfo: TaskInfoService.getTaskInfo,
                validateInfo: TaskInfoService.validateTaskInfo
            } ];

            var module = _.find(modules, function(module) {
                return module.name == $stateParams.type;
            });

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                var params = {};

                params.objId = $stateParams.id;
                params.objType = module.name;

                // to pass the access control check based on object type "TASK"
                if (params.objType === "ADHOC") {
                    params.objType = "TASK";
                }

                Ecm.getFolderDocumentCounts(params, function(data) {
                    var chartData = [];
                    var labels = [];
                    var folderData = Util.omitNg(data);
                    _.forEach(folderData, function(value, label) {
                        if (label == "base") {
                            label = "Root (/)";
                        }
                        chartData.push(value);
                        labels.push(label);
                    });
                    vm.showChart = chartData.length > 0;
                    vm.data = [ chartData ];
                    vm.labels = labels;
                    vm.series = [ $translate.instant("dashboard.widgets.docsUnderReview.title") ];
                }, function(error) {

                });
            }
        } ]);