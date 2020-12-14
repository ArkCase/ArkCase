'use strict';

angular.module('tasks').controller('Tasks.NotesController',
        [ '$scope', '$stateParams', '$translate', 'ConfigService', 'ObjectService', 'Helper.ObjectBrowserService', 'PermissionsService', 'Task.InfoService', function($scope, $stateParams, $translate, ConfigService, ObjectService, HelperObjectBrowserService, PermissionsService, TaskInfoService) {

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "tasks",
                componentId: "notes",
                retrieveObjectInfo: TaskInfoService.getTaskInfo,
                validateObjectInfo: TaskInfoService.validateTaskInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                },
                onTranslateChangeSuccess: function(data) {
                    onTranslateChangeSuccess(data);
                }
            });

            var onConfigRetrieved = function(config) {

                $scope.config = config;

            };

            $scope.notesInit = {
                noteTitle: $translate.instant("tasks.comp.notes.title"),
                objectType: ObjectService.ObjectTypes.TASK,
                currentObjectId: $stateParams.id,
                parentTitle: "",
                isReadOnly: false
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                if ($scope.notesInit) {
                    $scope.notesInit.parentTitle = $scope.objectInfo.parentObjectName;
                }
                PermissionsService.getActionPermission('editNote', objectInfo, {
                    objectType: ObjectService.ObjectTypes.TASK
                }).then(function(result) {
                    $scope.notesInit.isReadOnly = !result;
                });
            };

            var onTranslateChangeSuccess = function(data) {
                if ($scope.notesInit) {
                    $scope.notesInit.noteTitle = $translate.instant("tasks.comp.notes.title");
                }
            };
        } ]);