'use strict';

angular.module('complaints').controller('Complaints.NotesController', ['$scope', '$stateParams', '$q', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'ComplaintsService', 'Authentication',
    function ($scope, $stateParams, $q, Store, Util, Validator, Helper, LookupService, ComplaintsService, Authentication) {
        var z = 1;
        return;
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

        Helper.getUserInfo().then(function (data) {
            $scope.userId = Util.goodValue(data.userId, null);
        });

        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                var cacheComplaintNotes = new Store.CacheFifo(Helper.CacheNames.CASE_NOTES);
                var cacheKey = $scope.currentId;
                var notes = cacheComplaintNotes.get(cacheKey);
                var promiseQueryNotes = Util.serviceCall({
                    service: ComplaintsService.queryNotes
                    , param: {
                        parentType: Helper.ObjectTypes.COMPLAINT,
                        parentId: $scope.currentId
                    }
                    , result: notes
                    , onSuccess: function (data) {
                        if (Validator.validateNotes(data)) {
                            notes = data;
                            cacheComplaintNotes.put(cacheKey, notes);
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
            newRow.parentType = Helper.ObjectTypes.COMPLAINT;
            newRow.created = Util.getCurrentDay();
            newRow.creator = $scope.userId;
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
        };
        $scope.updateRow = function (rowEntity) {
            var note = Util.omitNg(rowEntity);
            Util.serviceCall({
                service: ComplaintsService.saveNote
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
        }
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                Util.serviceCall({
                    service: ComplaintsService.deleteNote
                    , param: {noteId: id}
                    , data: {}
                    , onSuccess: function (data) {
                        if (Validator.validateDeletedNote(data)) {
                            return data;
                        }
                    }
                });
            }

        };

    }
]);