/**
 * Created by dragan.simonovski on 2/17/2016.
 */
'use strict';

/**
 * @ngdoc directive
 * @name global.directive:coreNotes
 * @restrict E
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/core-notes/core-notes.client.directive.js directives/core-notes/core-notes.client.directive.js}
 *
 * The "Core-Notes" directive add notes grid functionality
 *
 * @param {Object} config object containing configuration items for current component
 * @param {Object} objectInfo object containing object type and current object id
 *
 * @example
 <example>
 <file name="index.html">
 <core-notes config="config" object-info="objectInfo"/>
 </file>
 <file name="app.js">
 angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
 , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("cases", "notes").then(function (config) {
            $scope.objectInfo = {
                objectType: ObjectService.ObjectTypes.CASE_FILE,
                currentObjectId: $stateParams.id
            };
            $scope.config = config;
            return config;
        });
    }
 ]);
 </file>
 </example>
 */
angular.module('directives').directive('coreNotes', ['$q', '$modal', '$translate', 'Authentication',
    'Helper.UiGridService', 'Helper.NoteService', 'Object.NoteService', 'UtilService',
    function ($q, $modal, $translate, Authentication, HelperUiGridService, HelperNoteService,
              ObjectNoteService, Util) {
        return {
            restrict: 'E',
            scope: {
                objectInfo: '=',
                config: '='
            },
            link: function (scope, element, attrs) {

                Authentication.queryUserInfo().then(
                    function (userInfo) {
                        scope.userId = userInfo.userId;
                        return userInfo;
                    }
                );
                var noteHelper = new HelperNoteService.Note();
                var gridHelper = new HelperUiGridService.Grid({scope: scope});
                var promiseUsers = gridHelper.getUsers();

                scope.$watchCollection('config', function (config, oldValue) {
                    if (config) {
                        gridHelper.addEditButton(config.columnDefs, "grid.appScope.editRow(row.entity)");
                        gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setUserNameFilter(promiseUsers);
                        scope.retrieveGridData();
                    }
                });

                scope.retrieveGridData = function () {
                    if (Util.goodPositive(scope.objectInfo.currentObjectId, false)) {
                        var promiseQueryNotes = ObjectNoteService.queryNotes(scope.objectInfo.objectType, scope.objectInfo.currentObjectId);
                        $q.all([promiseQueryNotes, promiseUsers]).then(function (data) {
                            var notes = data[0];
                            scope.gridOptions.data = notes;
                            scope.gridOptions.totalItems = notes.length;
                        });
                    }
                };

                scope.addNew = function () {
                    var note = noteHelper.createNote(scope.objectInfo.currentObjectId, scope.objectInfo.objectType, scope.userId);
                    showModal(note, false);
                };
                scope.editRow = function (rowEntity) {
                    var note = angular.copy(rowEntity);
                    showModal(note, true);
                };
                scope.deleteRow = function (rowEntity) {
                    gridHelper.deleteRow(rowEntity);

                    var id = Util.goodMapValue(rowEntity, "id", 0);
                    if (0 < id) {    //do not need to call service when deleting a new row with id==0
                        ObjectNoteService.deleteNote(id);
                    }
                };
                function showModal(note, isEdit) {

                    var modalScope = scope.$new();
                    modalScope.note = note || {};
                    modalScope.isEdit = isEdit || false;

                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'directives/core-notes/core-notes.modal.client.view.html',
                        controller: function ($scope, $modalInstance) {
                            $scope.onClickOk = function () {
                                $modalInstance.close({note: $scope.note, isEdit: $scope.isEdit});
                            };
                            $scope.onClickCancel = function () {
                                $modalInstance.dismiss('cancel');
                            }
                        },
                        size: 'lg',
                        backdrop: 'static'
                    });

                    modalInstance.result.then(function (data) {
                            // The date string needs to be reformatted so that it can be accepted by the backend
                            // It is expected to be in ISO format
                            data.note.created = Util.dateToIsoString(new Date(note.created));
                            ObjectNoteService.saveNote(data.note).then(function () {
                                scope.retrieveGridData();
                            }, function () {
                            });
                        }
                    );
                }
            },
            templateUrl: 'directives/core-notes/core-notes.client.view.html'
        };
    }
]);
