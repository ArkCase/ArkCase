'use strict';

angular.module('dashboard.notes', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('notes', {
                    title: 'Notes Widget',
                    description: 'Displays notes',
                    controller: 'Dashboard.NotesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/people-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.NotesController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService'
        , 'Object.NoteService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication, DashboardService
            , ObjectService, ObjectNoteService, ConfigService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectNoteService.queryNotes, ObjectType: ObjectService.ObjectTypes.CASE_FILE}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(module.ObjectType, $stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "notes";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;
                        $scope.gridOptions.data = info[0];
                        $scope.gridOptions.totalItems = 1;
                    },
                    function (err) {

                    }
                );
            }

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[4].columnDefs; //widget[4] = notes

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            ObjectNoteService.queryNotes(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id)
                                .then(function (data) {
                                    var notes = data[0];
                                    $scope.gridOptions.data = notes;
                                    $scope.gridOptions.totalItems = notes.length;
                                });
                        }
                        else if ($stateParams.type == 'complaint') {
                            ObjectNoteService.queryNotes(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id)
                                .then(function (data) {
                                    var notes = data[0];
                                    $scope.gridOptions.data = notes;
                                    $scope.gridOptions.totalItems = notes.length;
                                });
                        }
                        else if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                            ObjectNoteService.queryNotes(ObjectService.ObjectTypes.TASK, $stateParams.id)
                                .then(function (data) {
                                    var notes = data[0];
                                    $scope.gridOptions.data = notes;
                                    $scope.gridOptions.totalItems = notes.length;
                                });
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }
        }
    ]);