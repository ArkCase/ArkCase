'use strict';

angular.module('dashboard.notes', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('notes', {
                    title: 'Notes',
                    description: 'Displays notes',
                    controller: 'Dashboard.NotesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/people-widget.client.view.html',
                    commonName: 'notes'
                }
            );
    })
    .controller('Dashboard.NotesController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService'
        , 'Object.NoteService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ObjectService, ObjectNoteService, ConfigService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.COMPLAINT}
                , {name: "TASK", configName: "tasks", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.TASK}
                , {name: "ADHOC", configName: "tasks", getInfo: ObjectNoteService.queryNotes, objectType: ObjectService.ObjectTypes.TASK}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(module.objectType, $stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "notes";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        var notes = info[0];
                        $scope.gridOptions.data = notes;
                        $scope.gridOptions.totalItems = notes ? notes.length : 0;
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);