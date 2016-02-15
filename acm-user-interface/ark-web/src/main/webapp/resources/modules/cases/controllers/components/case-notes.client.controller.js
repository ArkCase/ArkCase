'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.NoteService', 'Authentication', 'Case.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Helper.NoteService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectNoteService, Authentication, CaseInfoService
        , HelperUiGridService, HelperObjectBrowserService, HelperNoteService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "notes"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

        var noteHelper = new HelperNoteService.Note();
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        var onConfigRetrieved = function (config) {
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setInPlaceEditing(config, $scope.updateRow);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        };

        $scope.retrieveGridData = function () {
            if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                var promiseQueryNotes = ObjectNoteService.queryNotes(ObjectService.ObjectTypes.CASE_FILE, componentHelper.currentObjectId);
                $q.all([promiseQueryNotes, promiseUsers, componentHelper.promiseConfig]).then(function (data) {
                    var notes = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = notes;
                    $scope.gridOptions.totalItems = notes.length;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };

        $scope.addNew = function () {
            gridHelper.gotoLastPage();
            var newRow = noteHelper.createNote($stateParams.id, ObjectService.ObjectTypes.CASE_FILE, $scope.userId);
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
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