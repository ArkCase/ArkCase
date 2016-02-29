'use strict';

angular.module('document-details').controller('Document.NotesController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Helper.UiGridService', 'Object.NoteService', 'Authentication'
    , function ($scope, $stateParams, $q, Util, ConfigService, ObjectService, HelperUiGridService, ObjectNoteService, Authentication) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        var promiseConfig = ConfigService.getComponentConfig("document-details", "notes").then(function (config) {
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setInPlaceEditing(config, $scope.updateRow);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });

        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryNotes = ObjectNoteService.queryNotes(ObjectService.ObjectTypes.FILE, $stateParams.id);
                $q.all([promiseQueryNotes, promiseUsers, promiseConfig]).then(function (data) {
                    var notes = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = notes;
                    $scope.gridOptions.totalItems = notes.length;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };

        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            var newRow = {};
            newRow.parentId = $stateParams.id;
            newRow.parentType = ObjectService.ObjectTypes.FILE;
            newRow.created = Util.getCurrentDay();
            newRow.creator = $scope.userId;
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
        };
        $scope.updateRow = function (rowEntity) {
            var note = Util.omitNg(rowEntity);
            var docNote = {
                note: note.note,
                parentId: note.parentId,
                parentType: note.parentType
            }
            ObjectNoteService.saveNote(docNote).then(
                function (noteAdded) {
                    if (Util.isEmpty(rowEntity.id)) {
                        rowEntity.id = noteAdded.id;
                    }
                }
            );
        };
        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                ObjectNoteService.deleteNote(id);
            }

        };

    }
]);