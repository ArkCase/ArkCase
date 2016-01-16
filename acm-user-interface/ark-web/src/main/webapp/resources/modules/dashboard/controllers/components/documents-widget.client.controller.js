'use strict';

angular.module('dashboard.documents', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('documents', {
                title: 'Documents',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.DocumentsController',
                controllerAs: 'documents',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/documents-widget.client.view.html'
            });
    })
    .controller('Dashboard.DocumentsController', ['$scope', 'config', '$stateParams', '$translate', 'Dashboard.DashboardService'
        , 'Helper.ObjectBrowserService', 'EcmService', 'Case.InfoService', 'ObjectService', 'Complaint.InfoService'
        , 'Task.InfoService', 'UtilService',
        function ($scope, config, $stateParams, $translate, DashboardService, HelperObjectBrowserService, Ecm, CaseInfoService
            , ObjectService, ComplaintInfoService, TaskInfoService, Util) {

            var vm = this;

            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: CaseInfoService.getCaseInfo, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo, objectType: ObjectService.ObjectTypes.COMPLAINT}
                , {name: "TASK", configName: "tasks", getInfo: TaskInfoService.getTaskInfo, objectType: ObjectService.ObjectTypes.TASK}
                , {name: "ADHOC", configName: "tasks", getInfo: TaskInfoService.getTaskInfo, objectType: ObjectService.ObjectTypes.ADHOC_TASK}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {


                var chartData = [];
                var labels = [];
                var params;
                module.getInfo(currentObjectId)
                    .then(function (data) {
                        params.objId = data.id;
                        params.objType = module.objectType;
                        params.start = 0;
                        params.start = 16;
                        return params;
                    })
                    .then(function (params) {
                        Ecm.getFolderDocumentCounts(params,
                            function(data) {
                                $scope.folderData = data;
                            },
                            function(error){

                            }
                        );
                    });

                /**
                 * $scope.folderData has structure:
                 *[
                 * {name: $nameOfFolder, count: $numberOfDocuments},
                 * {name: $nameOfFolder, count: $numberOfDocuments}
                 *]
                 * "base" folder corresponds to "/" folder in doc-tree
                 * use $scope.folderData to populate chart data
                 **/

                vm.showChart = chartData.length > 0;
                vm.data = [chartData];
                vm.labels = labels;
            }
        }
    ]);