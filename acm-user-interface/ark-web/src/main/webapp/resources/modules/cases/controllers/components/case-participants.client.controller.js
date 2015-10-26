'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q', 'UtilService', 'ValidationService', 'CasesService', 'LookupService',
    function ($scope, $stateParams, $q, Util, Validator, CasesService, LookupService) {
        $scope.$emit('req-component-config', 'participants');


        var promiseTypes = Util.serviceCall({
            service: LookupService.getParticipantTypes
            , onSuccess: function (data) {
                $scope.participantTypes = [{type: "*", name: "*"}];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.participantTypes.push({type: k, name: v});
                });
                return $scope.participantTypes;
            }
        });
        var promiseUsers = Util.serviceCall({
            service: LookupService.getUsersBasic
            , onSuccess: function (data) {
                $scope.participantUsers = [];
                var arr = Util.goodMapValue(data, "response.docs", []);
                for (var i = 0; i < arr.length; i++) {
                    var user = {};
                    user.id = arr[i].object_id_s;
                    user.name = arr[i].name;
                    $scope.participantUsers.push(user);
                }
                return $scope.participantUsers;
            }
        });
        var promiseGroups = Util.serviceCall({
            service: LookupService.getGroups
            , onSuccess: function (data) {
                $scope.participantGroups = [];
                var arr = Util.goodMapValue(data, "response.docs", []);
                for (var i = 0; i < arr.length; i++) {
                    var group = {};
                    group.id = arr[i].object_id_s;
                    group.name = arr[i].name;
                    $scope.participantGroups.push(group);
                }
                return $scope.participantGroups;
            }
        });


        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'participants' && !$scope.config) {
                Util.AcmGrid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
                Util.AcmGrid.setColumnDefs($scope, config);
                Util.AcmGrid.setBasicOptions($scope, config);
                Util.AcmGrid.addGridApiHandler($scope, function (gridApi) {
                    $scope.gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                        if (newValue == oldValue) {
                            return;
                        }

                        //
                        // Fix participant names selection
                        //
                        if (Util.Constant.LOOKUP_PARTICIPANT_TYPES === colDef.lookup) {
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
                });


                $q.all([promiseTypes, promiseUsers, promiseGroups]).then(function (data) {
                    $scope.gridOptions.enableRowSelection = false;    //need to turn off for inline edit
                    //$scope.gridOptions.enableCellEdit = true;
                    //$scope.gridOptions.enableCellEditOnFocus = true;
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if (Util.Constant.LOOKUP_PARTICIPANT_TYPES == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.participantTypes;
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Util.Constant.LOOKUP_PARTICIPANT_NAMES == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = "acm$_participantNames";
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: row.entity.acm$_participantNames:'id':'name'";
                        }
                    }
                });

            }
        });


        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $q.all([promiseTypes, promiseUsers, promiseGroups]).then(function () {
                    $scope.caseInfo = data;
                    $scope.gridOptions.data = $scope.caseInfo.participants;
                    Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.caseInfo.participants.length);
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
            Util.AcmGrid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.data.length);
        };
        $scope.updateRow = function (rowEntity) {
            var caseInfo = Util.omitNg($scope.caseInfo);
            CasesService.save({}, caseInfo
                , function (caseSaved) {
                    if (Validator.validateCaseFile(caseSaved)) {
                        //if participant is newly added, fill incomplete values with the latest
                        if (Util.isEmpty(rowEntity.id)) {
                            var participants = Util.goodMapValue(caseSaved, "participants", []);
                            var participantAdded = _.find(participants, {
                                participantType: rowEntity.participantType,
                                participantLdapId: rowEntity.participantLdapId
                            });
                            if (participantAdded) {
                                rowEntity = _.merge(rowEntity, participantAdded);
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
            Util.AcmGrid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
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


