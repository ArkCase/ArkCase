'use strict';

angular.module('dashboard.parentDocs', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('parentDocs', {
                title: 'dashboard.widgets.parentDocs.title',
                description: 'dashboard.widgets.parentDocs.description',
                controller: 'Dashboard.ParentDocumentsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/parent-documents-widget.client.view.html',
                commonName: 'parentDocs'
            });
    })
    .controller('Dashboard.ParentDocumentsController', ['$scope', 'config', '$stateParams', '$translate',
        'Helper.ObjectBrowserService', 'EcmService', 'ObjectService', 'Task.InfoService', 'UtilService',
        function ($scope, config, $stateParams, $translate,
                  HelperObjectBrowserService, Ecm, ObjectService, TaskInfoService, Util) {

            var vm = this;

            var modules = [
                {
                    name: "TASK",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    objectType: ObjectService.ObjectTypes.TASK
                }
                ,
                {
                    name: "ADHOC",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    objectType: ObjectService.ObjectTypes.ADHOC_TASK
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                var params = {};

                params.objId = $stateParams.id;
                params.objType = module.objectType;

                // to pass the access control check based on object type "TASK"
                if (params.objType === "ADHOC") {
                    params.objType = "TASK";
                }

                Ecm.getFolderDocumentCounts(params,
                    function (data) {
                        var chartData = [];
                        var labels = [];
                        var folderData = Util.omitNg(data);
                        _.forEach(folderData, function (value, label) {
                            if(label == "base") {
                                label = "Root (/)";
                            }
                            chartData.push(value);
                            labels.push(label);
                        });
                        vm.showChart = chartData.length > 0;
                        vm.data = [chartData];
                        vm.labels = labels;
                        vm.series = ["Number of Documents"];
                    },
                    function (error) {

                    }
                );
            }
        }
    ]);