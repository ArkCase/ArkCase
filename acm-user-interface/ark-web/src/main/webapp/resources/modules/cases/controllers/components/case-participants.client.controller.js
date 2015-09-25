'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q', 'UtilService', 'ValidationService', 'CasesService', 'LookupService',
    function ($scope, $stateParams, $q, Util, Validator, CasesService, LookupService) {
        $scope.$emit('req-component-config', 'participants');


        var promiseTypes = Util.servicePromise({
            service: LookupService.getParticipantTypes
            , callback: function (data) {
                $scope.participantTypes = [{type: "*", name: "*"}];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.participantTypes.push({type: k, name: v});
                });
                return $scope.participantTypes;
            }
        });
        var promiseUsers = Util.servicePromise({
            service: LookupService.getUsersBasic
            , callback: function (data) {
                $scope.participantUsers = [];
                var arr = Util.goodMapValue([data, "response", "docs"], []);
                for (var i = 0; i < arr.length; i++) {
                    var user = {};
                    user.id = arr[i].object_id_s;
                    user.name = arr[i].name;
                    $scope.participantUsers.push(user);
                }
                return $scope.participantUsers;
            }
        });
        var promiseGroups = Util.servicePromise({
            service: LookupService.getGroups
            , callback: function (data) {
                $scope.participantGroups = [];
                var arr = Util.goodMapValue([data, "response", "docs"], []);
                for (var i = 0; i < arr.length; i++) {
                    var group = {};
                    group.id = arr[i].object_id_s;
                    group.name = arr[i].name;
                    $scope.participantGroups.push(group);
                }
                return $scope.participantGroups;
            }
        });


        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'participants' && !$scope.config) {

                var columnDef = {
                    name: "act",
                    cellEditableCondition: false,
                    //,enableFiltering: false
                    //,enableHiding: false
                    //,enableSorting: false
                    //,enableColumnResizing: false
                    width: 40,
                    headerCellTemplate: "<span></span>",
                    cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='grid.appScope.deleteRow(row.entity)'></i></span>"
                };
                config.columnDefs.push(columnDef);

                $scope.config = config;
                $scope.gridOptions = {
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    noUnselect: false,

                    paginationPageSizes: config.paginationPageSizes,
                    paginationPageSize: config.paginationPageSize,
                    enableFiltering: config.enableFiltering,
                    columnDefs: config.columnDefs,
                    onRegisterApi: function (gridApi) {
                        $scope.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }

                            //
                            // Fix participant names selection
                            //
                            if ("participantTypes" === colDef.lookup) {
                                if ("*" === newValue) {
                                    rowEntity.acm$_participantNames = [
                                        {id: "*", name: "*"}
                                    ];
                                } else if ("owning group" === newValue) {
                                    rowEntity.acm$_participantNames = $scope.participantGroups;
                                } else {
                                    rowEntity.acm$_participantNames = $scope.participantUsers;
                                }

                                $scope.$apply();
                            }

                            //
                            // Save changes
                            //
                            if (!Util.isEmpty(rowEntity.participantType) && !Util.isEmpty(rowEntity.participantLdapId)) {
                                $scope.updateRow(rowEntity);
                            }
                        });
                    }
                };


                $q.all([promiseTypes, promiseUsers, promiseGroups]).then(function (data) {
                    $scope.gridOptions.enableRowSelection = false;    //need to turn off for inline edit
                    //$scope.gridOptions.enableCellEdit = true;
                    //$scope.gridOptions.enableCellEditOnFocus = true;
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("participantTypes" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.participantTypes;
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if ("participantNames" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = "acm$_participantNames";
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: row.entity.acm$_participantNames:'id':'name'";
                        }
                    }
                });

            }
        }


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $q.all([promiseTypes, promiseUsers, promiseGroups]).then(function () {
                    $scope.caseInfo = data;
                    $scope.gridOptions.data = $scope.caseInfo.participants;
                    _.each($scope.gridOptions.data, function (item) {
                        if ("*" === item.participantType) {
                            item.acm$_participantNames = [
                                {id: "*", name: "*"}
                            ];
                        } else if ("owning group" === item.participantType) {
                            item.acm$_participantNames = $scope.participantGroups;
                        } else {
                            item.acm$_participantNames = $scope.participantUsers;
                        }
                    });
                });
            }
        });

        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
        };
        $scope.updateRow = function (rowEntity) {
            var caseInfo = Util.omitNg($scope.caseInfo);
            CasesService.save({}, caseInfo
                , function (caseSaved) {
                    if (Validator.validateCaseFile(caseSaved)) {
                        //if participant is newly added, fill incomplete values with the latest
                        if (Util.isEmpty(rowEntity.id)) {
                            var participants = Util.goodMapValue([caseSaved, "participants"], []);
                            var participantAdded = _.where(participants, {
                                participantType: rowEntity.participantType,
                                participantLdapId: rowEntity.participantLdapId
                            });
                            if (0 < participantAdded.length) {
                                rowEntity = _.merge(rowEntity, participantAdded[0]);
                            }
                        }
                    }
                }
                , function (errorData) {
                    var z = 2;
                }
            );
        };
        $scope.deleteRow = function (rowEntity) {
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CasesService.save({}, caseInfo
                    , function (caseSaved) {
                        if (Validator.validateCaseFile(caseSaved)) {
                            var z = 1;
                        }
                        var z = 1;
                    }
                    , function (errorData) {
                        var z = 2;
                    }
                );
            }

        };
    }
])

;


