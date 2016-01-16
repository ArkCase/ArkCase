'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Authentication', 'ObjectService', 'Object.NoteService'
    , 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Helper.NoteService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, Authentication, ObjectService, ObjectNoteService
        , HelperObjectBrowserService, HelperUiGridService, HelperNoteService) {

        var noteHelper = new HelperNoteService.Note();
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ConfigService.getComponentConfig("complaints", "notes").then(function (config) {
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setInPlaceEditing(config, $scope.updateRow);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });


        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        $scope.retrieveGridData = function () {
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryNotes = ObjectNoteService.queryNotes(ObjectService.ObjectTypes.COMPLAINT, currentObjectId);
                $q.all([promiseQueryNotes, promiseUsers]).then(function (data) {
                    var notes = data[0];
                    $scope.gridOptions.data = notes;
                    $scope.gridOptions.totalItems = notes.length;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };

        $scope.addNew = function () {
            gridHelper.gotoLastPage();
            var newRow = noteHelper.createNote($stateParams.id, ObjectService.ObjectTypes.COMPLAINT, $scope.userId);
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
        };
        $scope.updateRow = function (rowEntity) {
            var note = Util.omitNg(rowEntity);
            noteHelper.saveNote(note, rowEntity);
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