'use strict';

angular.module('directives').directive('coreNotes', ['$q', '$modal', '$translate', 'Authentication',
    'Helper.UiGridService', 'Helper.NoteService', 'Object.NoteService', 'UtilService', 'MessageService',
    function ($q, $modal, $translate, Authentication, HelperUiGridService, HelperNoteService,
              ObjectNoteService, Util, MessageService) {
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
                        gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                        gridHelper.addEditButton(config.columnDefs, "grid.appScope.editRow(row.entity)");

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
                            //gridHelper.hidePagingControlsIfAllDataShown(scope.gridOptions.totalItems);
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
                                /*                                if (data.isEdit) {
                                 //MessageService.info($translate.instant('common.directives.coreNotes.messages.update.success'));
                                 MessageService.info('note updated successfully');
                                 }
                                 else
                                 MessageService.info('note created successfully');*/
                            }, function () {
                                /*                                if (data.isEdit)
                                 MessageService.error('error in updating the note');
                                 else
                                 MessageService.error('error in creating the note');*/
                            });
                        }
                    );
                }

            },
            templateUrl: 'directives/core-notes/core-notes.client.view.html'
        };
    }
]);
