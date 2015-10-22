'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', '$q', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'notes');

        var promiseUsers = Util.AcmGrid.getUsers($scope);

        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'notes') {
                Util.AcmGrid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                Util.AcmGrid.setColumnDefs($scope, config);
                Util.AcmGrid.setBasicOptions($scope, config);
                Util.AcmGrid.setInPlaceEditing($scope, config, $scope.updateRow);
                Util.AcmGrid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });


        $scope.currentId = $stateParams.id;
        $scope.retrieveGridData = function () {
            if ($scope.currentId) {
                CasesService.queryNotes({
                    parentType: Util.Constant.OBJTYPE_CASE_FILE,
                    parentId: $scope.currentId
                }, function (data) {
                    if (Validator.validateNotes(data)) {
                        promiseUsers.then(function () {
                            var notes = data;
                            $scope.gridOptions.data = notes;
                            $scope.gridOptions.totalItems = notes.length;
                            Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                        });
                    }
                });
            }
        };

        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            var newRow = {};
            newRow.parentId = $scope.currentId;
            newRow.parentType = Util.Constant.OBJTYPE_CASE_FILE;
            newRow.created = "2015-09-28T13:17:52.036-0400"; //Acm.getCurrentDay();
            newRow.creator = "ann-acm"; //App.getUserName();
            $scope.gridOptions.data.push(newRow);
            $scope.gridOptions.totalItems++;
            Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
        };
        $scope.updateRow = function (rowEntity) {
            var note = Util.omitNg(rowEntity);
            CasesService.saveNote({}, note
                , function (successData) {
                    if (Validator.validateNote(successData)) {
                        if (Util.isEmpty(rowEntity.id)) {
                            var noteAdded = successData;
                            rowEntity.id = noteAdded.id;
                        }
                    }
                }
                , function (errorData) {
                }
            );
        }
        $scope.deleteRow = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                CasesService.deleteNote({noteId: id}
                    , function (successData) {
                        if (Validator.validateDeletedNote(successData)) {
                        }
                    }
                    , function (errorData) {
                    }
                );
            }

        };

    }
]);