'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ObjectService', 'Helper.UiGridService', 'Object.NoteService', 'Authentication'
    , function ($scope, $stateParams, $q, Util, ObjectService, HelperUiGridService, ObjectNoteService, Authentication) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        $scope.$emit('req-component-config', 'notes');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("notes" == componentId) {
                gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.setInPlaceEditing(config, $scope.updateRow);
                gridHelper.setUserNameFilter(promiseUsers);

                $scope.retrieveGridData();
            }
        });


        $scope.retrieveGridData = function () {
            if (Util.goodPositive($stateParams.id)) {
                var promiseQueryNotes = ObjectNoteService.queryNotes(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id);
                $q.all([promiseQueryNotes, promiseUsers]).then(function (data) {
                    var notes = data[0];
                    $scope.gridOptions.data = notes;
                    $scope.gridOptions.totalItems = notes.length;
                    gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };

        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            var newRow = {};
            newRow.parentId = $stateParams.id;
            newRow.parentType = ObjectService.ObjectTypes.COMPLAINT;
            newRow.created = Util.getCurrentDay();
            newRow.creator = $scope.userId;
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
        };
        $scope.updateRow = function (rowEntity) {
            var note = Util.omitNg(rowEntity);
            ObjectNoteService.saveNote(note).then(
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