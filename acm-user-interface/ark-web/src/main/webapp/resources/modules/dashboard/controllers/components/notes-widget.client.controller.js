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
    .controller('Dashboard.NotesController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.NoteService',
        function ($scope, $translate, $stateParams, Util, CaseInfoService, ComplaintInfoService, Authentication, DashboardService
        , ObjectService, ObjectNoteService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

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
                        else if ($stateParams.type == 'task') {
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