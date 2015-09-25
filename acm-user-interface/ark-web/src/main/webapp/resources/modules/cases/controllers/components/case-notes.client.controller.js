'use strict';

angular.module('cases').controller('Cases.NotesController', ['$scope', '$stateParams', '$q', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'notes');

        $scope.currentId = $stateParams.id;
        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];


        var promiseUsers = Util.servicePromise({
            service: LookupService.getUsers
            , callback: function (data) {
                $scope.userFullNames = [];
                var arr = Util.goodArray(data);
                for (var i = 0; i < arr.length; i++) {
                    var obj = Util.goodJsonObj(arr[i]);
                    if (obj) {
                        var user = {};
                        user.id = Util.goodValue(obj.object_id_s);
                        user.name = Util.goodValue(obj.name);
                        $scope.userFullNames.push(user);
                    }
                }
                return $scope.userFullNames;
            }
        });


        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'notes') {
                var columnDef = {
                    name: "act",
                    cellEditableCondition: false,
                    width: 40,
                    headerCellTemplate: "<span></span>",
                    cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='grid.appScope.deleteRow(row.entity)'></i></span>"
                };
                config.columnDefs.push(columnDef);

                $scope.config = config;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: false,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    useExternalPagination: true,
                    useExternalSorting: true,

                    //comment out filtering until service side supports it
                    ////enableFiltering: config.enableFiltering,
                    //enableFiltering: true,
                    //useExternalFiltering: true,

                    columnDefs: config.columnDefs,
                    onRegisterApi: function (gridApi) {
                        $scope.gridApi = gridApi;
                        $scope.gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
                            if (0 >= sortColumns.length) {
                                $scope.sort.by = null;
                                $scope.sort.dir = null;
                            } else {
                                $scope.sort.by = sortColumns[0].field;
                                $scope.sort.dir = sortColumns[0].sort.direction;
                            }
                            $scope.updatePageData();
                        });
                        $scope.gridApi.core.on.filterChanged($scope, function () {
                            var grid = this.grid;
                            $scope.filters = [];
                            for (var i = 0; i < grid.columns.length; i++) {
                                if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                    var filter = {};
                                    filter.by = grid.columns[i].field;
                                    filter.with = grid.columns[i].filters[0].term;
                                    $scope.filters.push(filter);
                                }
                            }
                            $scope.updatePageData();
                        });
                        $scope.gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                            $scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                            $scope.pageSize = pageSize;
                            $scope.updatePageData();
                        });

                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }
                            $scope.updateRow(rowEntity);
                        });
                    }
                };


                $q.all([promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("userFullNames" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";
                        }
                    }
                });

                $scope.pageSize = config.paginationPageSize;
                $scope.updatePageData();
            }
        }

        $scope.updatePageData = function () {
            var sort = "";
            if ($scope.sort) {
                if (!_.isEmpty($scope.sort.by) && !_.isEmpty($scope.sort.dir)) {
                    sort = $scope.sort.by + "%20" + $scope.sort.dir;
                }
            }
            //implement filtering here when service side supports it
            //var filter = "";
            ////$scope.filters = [{by: "eventDate", with: "term"}];

            CasesService.queryNotes({
                parentType: "CASE_FILE",
                parentId: $scope.currentId,
                startWith: $scope.start,
                count: $scope.pageSize,
                sort: sort
            }, function (data) {

                //data = [
                //    {
                //        "id": 102,
                //        "note": "test",
                //        "type": "GENERAL",
                //        "creator": "ann-acm",
                //        "created": "2015-08-28T13:17:52.036-0400",
                //        "parentId": 105,
                //        "parentType": "CASE_FILE",
                //        "modified": null,
                //        "modifier": null
                //    }
                //];

                if (Validator.validateNotes(data)) {
                    $q.all([promiseUsers]).then(function () {
                        var notes = data;
                        $scope.gridOptions.data = notes;
                        $scope.gridOptions.totalItems = notes.length;
                    }); //end $q
                }
            })
        };

        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            var newRow = {};
            newRow.parentId = $scope.currentId;
            newRow.parentType = "CASE_FILE";
            newRow.created = "2015-09-28T13:17:52.036-0400"; //Acm.getCurrentDay();
            newRow.creator = "ann-acm"; //App.getUserName();
            $scope.gridOptions.data.push(newRow);
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
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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