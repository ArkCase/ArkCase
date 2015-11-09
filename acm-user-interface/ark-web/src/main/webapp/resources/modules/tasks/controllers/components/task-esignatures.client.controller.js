'use strict';

angular.module('tasks').controller('Tasks.ESignaturesController', ['$scope', '$stateParams', '$q', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'TasksService', 'Authentication',
    function ($scope, $stateParams, $q, Store, Util, Validator, Helper, LookupService, TasksService, Authentication) {
        return;
        $scope.$emit('req-component-config', 'esignatures');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("esignatures" == componentId) {
                Helper.Grid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setInPlaceEditing($scope, config, $scope.updateRow);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });

        var promiseUsers = Helper.Grid.getUsers($scope);

        Helper.getUserInfo().then(function (data) {
            $scope.userId = Util.goodValue(data.userId, null);
        });

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                var cacheTaskNotes = new Store.CacheFifo(Helper.CacheNames.CASE_NOTES);
                var cacheKey = $scope.currentId;
                var notes = cacheTaskNotes.get(cacheKey);
                var promiseQueryNotes = Util.serviceCall({
                    service: TasksService.queryNotes
                    , param: {
                        parentType: Helper.ObjectTypes.CASE_FILE,
                        parentId: $scope.currentId
                    }
                    , result: notes
                    , onSuccess: function (data) {
                        if (Validator.validateNotes(data)) {
                            notes = data;
                            cacheTaskNotes.put(cacheKey, notes);
                            return notes;
                        }
                    }
                });
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
            newRow.parentType = Helper.ObjectTypes.CASE_FILE;
            newRow.created = Util.getCurrentDay();
            newRow.creator = $scope.userId;
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
        };
        $scope.updateRow = function (rowEntity) {
            var note = Util.omitNg(rowEntity);
            Util.serviceCall({
                service: TasksService.saveNote
                , data: note
                , onSuccess: function (data) {
                    if (Validator.validateNote(data)) {
                        return data;
                    }
                }
            }).then(
                function (noteAdded) {
                    if (Util.isEmpty(rowEntity.id)) {
                        var noteAdded = data;
                        rowEntity.id = noteAdded.id;
                    }
                }
            );
            //TasksService.saveNote({}, note
            //    , function (successData) {
            //        if (Validator.validateNote(successData)) {
            //            if (Util.isEmpty(rowEntity.id)) {
            //                var noteAdded = successData;
            //                rowEntity.id = noteAdded.id;
            //            }
            //        }
            //    }
            //    , function (errorData) {
            //    }
            //);
        }
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                Util.serviceCall({
                    service: TasksService.deleteNote
                    , param: {noteId: id}
                    , data: {}
                    , onSuccess: function (data) {
                        if (Validator.validateDeletedNote(data)) {
                            return data;
                        }
                    }
                });
                //TasksService.deleteNote({noteId: id}
                //    , function (successData) {
                //        if (Validator.validateDeletedNote(successData)) {
                //        }
                //    }
                //    , function (errorData) {
                //    }
                //);
            }

        };

    }
]);