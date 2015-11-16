'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', '$q', 'StoreService', 'UtilService', 'ValidationService', 'ConstantService', 'HelperService', 'Object.NoteService', 'CallAuthentication',
    function ($scope, $stateParams, $q, Store, Util, Validator, Constant, Helper, ObjectNoteService, CallAuthentication) {
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
                var promiseQueryNotes = ObjectNoteService.queryNotes(Constant.ObjectTypes.CASE_FILE, $scope.currentId);

                //var cacheCaseNotes = new Store.CacheFifo(Helper.CacheNames.CASE_NOTES);
                //var cacheKey = $scope.currentId;
                //var notes = cacheCaseNotes.get(cacheKey);
                //var promiseQueryNotes = Util.serviceCall({
                //    service: CasesService.queryNotes
                //    , param: {
                //        parentType: Helper.ObjectTypes.CASE_FILE,
                //        parentId: $scope.currentId
                //    }
                //    , result: notes
                //    , onSuccess: function (data) {
                //        if (Validator.validateNotes(data)) {
                //            notes = data;
                //            cacheCaseNotes.put(cacheKey, notes);
                //            return notes;
                //        }
                //    }
                //});

                $q.all([promiseQueryNotes, promiseUsers]).then(function (data) {
                    var notes = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
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
            newRow.parentType = Helper.ObjectTypes.CASE_FILE;
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

            //Util.serviceCall({
            //    service: CasesService.saveNote
            //    , data: note
            //    , onSuccess: function (data) {
            //        if (Validator.validateNote(data)) {
            //            return data;
            //        }
            //    }
            //}).then(
            //    function (noteAdded) {
            //        if (Util.isEmpty(rowEntity.id)) {
            //            var noteAdded = data;
            //            rowEntity.id = noteAdded.id;
            //        }
            //    }
            //);
        };
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                ObjectNoteService.deleteNote(id);

                //Util.serviceCall({
                //    service: CasesService.deleteNote
                //    , param: {noteId: id}
                //    , data: {}
                //    , onSuccess: function (data) {
                //        if (Validator.validateDeletedNote(data)) {
                //            return data;
                //        }
                //    }
                //});
            }

        };

    }
]);