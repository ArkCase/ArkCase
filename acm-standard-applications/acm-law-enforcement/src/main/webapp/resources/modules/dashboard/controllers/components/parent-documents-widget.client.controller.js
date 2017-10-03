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
    .controller('Dashboard.ParentDocumentsController', ['$scope', '$q', 'config', '$stateParams', '$translate',
        'Helper.ObjectBrowserService', 'EcmService', 'ObjectService', 'Task.InfoService', 'UtilService',
        function ($scope, $q, config, $stateParams, $translate,
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

                params.objType = module.objectType;
                params.objId = $stateParams.id;

                // to pass the access control check based on object type "TASK"
                if (params.objType === "ADHOC") {
                    params.objType = "TASK";
                }

                Ecm._getFolderDocumentCounts(params).then(function(data){
                    var result = Util.omitNg(data);
                    var count = result[Object.keys(result)[0]];

                    vm.count = count;
                });
            }
        }
    ]);