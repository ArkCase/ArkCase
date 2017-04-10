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
                templateUrl: 'modules/dashboard/views/components/documents-widget.client.view.html',
                commonName: 'documents'
            });
    })
    .controller('Dashboard.DocumentsController', ['$scope', 'config', '$stateParams', '$translate', 'Dashboard.DashboardService'
        , 'Helper.ObjectBrowserService', 'EcmService', 'Case.InfoService', 'ObjectService', 'Complaint.InfoService'
        , 'Task.InfoService', 'UtilService', 'DocumentRepository.InfoService',
        function ($scope, config, $stateParams, $translate, DashboardService, HelperObjectBrowserService, Ecm, CaseInfoService
            , ObjectService, ComplaintInfoService, TaskInfoService, Util, DocumentRepositoryInfoService) {

            var vm = this;

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    objectType: ObjectService.ObjectTypes.CASE_FILE
                }
                , {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    objectType: ObjectService.ObjectTypes.COMPLAINT
                }
                , {
                    name: "TASK",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    objectType: ObjectService.ObjectTypes.TASK
                }
                , {
                    name: "ADHOC",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    objectType: ObjectService.ObjectTypes.ADHOC_TASK
                }
                , {
                    name: "DOC_REPO",
                    configName: "document-repository",
                    getInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                    objectType: ObjectService.ObjectTypes.DOC_REPO
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