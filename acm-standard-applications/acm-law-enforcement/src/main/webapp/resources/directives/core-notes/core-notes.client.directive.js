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
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/core-notes/core-notes.client.directive.js directives/core-notes/core-notes.client.directive.js}
 *
 * The "Core-Notes" directive add notes grid functionality
 *
 * @param {Object} config object containing configuration items for current component
 * @param {Object} notesInit object containing object type and current object id
 * @param {string} notesInit.objectType string for the type of the object
 * @param {string} notesInit.noteType string for the type of the note object, can be optional
 * @param {string} notesInit.noteTitle string for the title of notes directive, can be optional
 *
 * @example
 <example>
 <file name="index.html">
 <core-notes config="config" notes-init="notesInit"/>
 </file>
 <file name="app.js">
 angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', 'ConfigService', 'ObjectService'
 , function ($scope, $stateParams, ConfigService, ObjectService) {

        ConfigService.getComponentConfig("cases", "notes").then(function (config) {
            $scope.notesInit = {
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
                notesInit: '=',
                config: '='
            },
            link: function (scope, element, attrs) {

                Authentication.queryUserInfo().then(
                    function (userInfo) {
                        scope.userId = userInfo.userId;
                        return userInfo;
                    }
                );

                // set scope.isReadOnly for addButton in gridHelper
                scope.isReadOnly = function (objectInfo) {
                    return scope.notesInit.isReadOnly;
                };

                var noteHelper = new HelperNoteService.Note();
                var gridHelper = new HelperUiGridService.Grid({scope: scope});
                var promiseUsers = gridHelper.getUsers();

                scope.$watchCollection('config', function (config, oldValue) {
                    if (!scope.notesInit.noteTitle) {
                        scope.notesInit.noteTitle = $translate.instant("common.directive.coreNotes.title");
                    }
                    if (config) {
                    	
                            // if buttons were defined in config, try to use the getConfigurableButton method
                        if (Util.goodArray(config.buttons)) {
                            _.each(config.buttons, function (button) {
                                gridHelper.addConfigurableButton(config, button);
                            });
                            // check if the config had an entry for 'edit'. If not, add in default edit button
                            if (!hasButton(config, "edit")) {
                                gridHelper.addButton(config, "edit");
                            }
                            // check if the config had an entry for 'delete'. If not, add in default delete button
                            if (!hasButton(config, "delete")) {
                                gridHelper.addButton(config, "delete");
                            }
                        } else {
                            // no buttons were found in the config, use the default settings
                            gridHelper.addButton(config, "edit");
                            gridHelper.addButton(config, "delete");
                        }
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setExternalPaging(config, scope.retrieveGridData);
                        gridHelper.setUserNameFilter(promiseUsers);
                        scope.retrieveGridData();
                    }
                    
                });

                // check if config file has an entry for any named button
                var hasButton = function (config, name) {
                    var buttonPresent = false;
                    _.each(config.buttons, function (button) {
                        if (button.name == name) {
                            buttonPresent = true;
                            //return false to break out of the loop
                            return false;
                        }
                    });
                    return buttonPresent;
                };

                scope.retrieveGridData = function () {
                    var info = scope.notesInit;
                    var promiseQueryNotes = ObjectNoteService.queryNotesPage(
                        info.objectType
                        , info.currentObjectId
                        , info.noteType
                        , Util.goodValue(scope.start, 0)
                        , Util.goodValue(scope.pageSize, 10)
                        , Util.goodMapValue(scope.sort, "by")
                        , Util.goodMapValue(scope.sort, "dir")
                    );

                    promiseQueryNotes.then(function (data) {
                        scope.gridOptions = scope.gridOptions || {};
                        scope.gridOptions.data = data.resultPage;
                        scope.gridOptions.totalItems = data.totalCount;
                    });

                };

                scope.addNew = function () {
                    var info = scope.notesInit;
                    var note = noteHelper.createNote(info.currentObjectId, info.objectType, info.tag,
                        scope.userId, info.noteType);
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
                        size: 'md',
                        backdrop: 'static'
                    });

                    modalInstance.result.then(function (data) {
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
