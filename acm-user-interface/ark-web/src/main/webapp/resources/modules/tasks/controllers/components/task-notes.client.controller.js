'use strict';

angular.module('tasks').controller('Tasks.NotesController', ['$scope', '$stateParams', '$q', 'UtilService', 'ConstantService', 'HelperService', 'Object.NoteService', 'CallAuthentication',
    function ($scope, $stateParams, $q, Util, Constant, Helper, ObjectNoteService, CallAuthentication) {
        $scope.$emit('req-component-config', 'notes');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("notes" == componentId) {
                Helper.Grid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setInPlaceEditing($scope, config, $scope.updateRow);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });

        var promiseUsers = Helper.Grid.getUsers($scope);

        CallAuthentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                var promiseQueryNotes = ObjectNoteService.queryNotes(Constant.ObjectTypes.TASK, $scope.currentId);
                $q.all([promiseQueryNotes, promiseUsers]).then(function (data) {
                    var notes = data[0];
                    $scope.gridOptions.data = notes;
                    $scope.gridOptions.totalItems = notes.length;
                    Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                });
            }
        };

        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            var newRow = {};
            newRow.parentId = $scope.currentId;
            newRow.parentType = Helper.ObjectTypes.TASK;
            newRow.created = Util.getCurrentDay();
            newRow.creator = $scope.userId;
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
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
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                ObjectNoteService.deleteNote(id);
            }
        };

    }
]);