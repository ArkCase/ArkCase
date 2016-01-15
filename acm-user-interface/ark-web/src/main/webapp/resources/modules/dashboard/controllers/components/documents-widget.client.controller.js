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
        , 'Task.InfoService',
        function ($scope, config, $stateParams, $translate, DashboardService, HelperObjectBrowserService, Ecm, CaseInfoService
        , ObjectService, ComplaintInfoService, TaskInfoService) {

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
                        Ecm.retrieveFolderList(params, function (folderList) {
                            var folderData = [{name:  'base', count: 0}];
                            var folders = [];
                            if(folderList.children && folderList.children.length) {
                                _.forEach(folderList.children, function(child) {
                                    if(child.objectType == 'folder'){
                                        var folder = {};
                                        folder.name = child.name;
                                        folder.objectId = child.objectId;
                                        folders.push(folder);
                                    } else {
                                        folderData[0].count++;
                                    }
                                });
                                _.forEach(folders, function(folder) {
                                    //findDocumentCount
                                    var documentCount = findDocumentCount(folder, params);
                                    //add document count and name of folder to folderData
                                    var folderInfo = {};
                                    folderInfo.name = folder.name;
                                    folderInfo.count = documentCount;
                                    folderData.push(folderInfo);
                                });
                            }

                            $scope.folderData = folderData;
                        },
                        function(error) {

                        });
                    });

                //$scope.folderData has structure:
                // [
                //  {name: $nameOfFolder, count: $numberOfDocuments},
                //  {name: $nameOfFolder, count: $numberOfDocuments}
                //]

                vm.showChart = chartData.length > 0 ? true : false;
                vm.data = [chartData];
                vm.labels = labels;
            }

            var findDocumentCount = function (folder, params) {
                params.folderId = folder.objectId;
                var count = 0;
                Ecm.retrieveFolderList(params, function (folderList) {
                        if (folderList.children && folderList.children.length) {
                            _.forEach(folderList.children, function (child) {
                                if (child.objectType == 'folder') {
                                    var folder = {};
                                    folder.name = child.name;
                                    folder.objectId = child.objectId;
                                    folders.push(folder);
                                } else {
                                    count++;
                                }
                            });
                            _.forEach(folder, function (folder) {
                                count += findDocumentCount(folder, params);
                            });
                        }
                    },
                    function (error) {

                    });
                return count;
            }

        }
    ]);