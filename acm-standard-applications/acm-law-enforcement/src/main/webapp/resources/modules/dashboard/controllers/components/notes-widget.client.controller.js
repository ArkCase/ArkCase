'use strict';

angular.module('dashboard.notes', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('notes', {
                title: 'dashboard.widgets.notes.title',
                description: 'dashboard.widgets.notes.description',
                controller: 'Dashboard.NotesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/notes-widget.client.view.html',
                commonName: 'notes'
            });
    })
    .controller('Dashboard.NotesController', ['$scope', '$translate', '$stateParams', '$q',
        'UtilService', 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.NoteService', 'ConfigService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
            function ($scope, $translate, $stateParams, $q,
                  Util, CaseInfoService, ComplaintInfoService, Authentication, DashboardService, ObjectService, ObjectNoteService, ConfigService, HelperObjectBrowserService, HelperUiGridService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.COMPLAINT}
                , {name: "TASK", configName: "tasks", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.TASK}
                , {name: "ADHOC", configName: "tasks", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.TASK}
                , {name: "DOC_REPO", configName: "document-repository", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.DOC_REPO}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (module && Util.goodPositive(currentObjectId, false)) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(module.objectType, currentObjectId);
                var promiseUsers = gridHelper.getUsers();

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "notes";
                        });
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        var notes = info;
                        gridHelper.setWidgetsGridData(notes);
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);