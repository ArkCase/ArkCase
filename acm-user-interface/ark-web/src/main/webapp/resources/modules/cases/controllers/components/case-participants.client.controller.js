'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'CasesService', 'LookupService',
    function ($scope, $stateParams, $q, Store, Util, Validator, Helper, CasesService, LookupService) {
        var deferParticipantData = new Store.Variable("deferCaseParticipantData");    // used to hold grid data before grid config is ready

        var promiseConfig = Helper.requestComponentConfig($scope, "participants", function (config) {
            Helper.Grid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            Helper.Grid.setColumnDefs($scope, config);
            Helper.Grid.setBasicOptions($scope, config);
            Helper.Grid.addGridApiHandler($scope, function (gridApi) {
                $scope.gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                    if (newValue == oldValue) {
                        return;
                    }

                    //
                    // Fix participant names selection
                    //
                    if (Helper.Lookups.PARTICIPANT_TYPES === colDef.lookup) {
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
                    if (Helper.Lookups.PARTICIPANT_TYPES == $scope.config.columnDefs[i].lookup) {
                        $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                        $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.participantTypes;
                        $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                    } else if (Helper.Lookups.PARTICIPANT_NAMES == $scope.config.columnDefs[i].lookup) {
                        $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        $scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = "acm$_participantNames";
                        $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: row.entity.acm$_participantNames:'id':'name'";
                    }
                }


                var caseInfo = deferParticipantData.get();
                if (caseInfo) {
                    updateGridData(caseInfo);
                    deferParticipantData.set(null);
                }
            });
        });


        var cacheParticipantTypes = new Store.SessionData(Helper.SessionCacheNames.PARTICIPANT_TYPES);
        var participantTypes = cacheParticipantTypes.get();
        var promiseTypes = Util.serviceCall({
            service: LookupService.getParticipantTypes
            , result: participantTypes
            , onSuccess: function (data) {
                if (Validator.validateParticipantTypes(data)) {
                    participantTypes = [{type: "*", name: "*"}];
                    Util.forEachStripNg(data, function (v, k) {
                        participantTypes.push({type: k, name: v});
                    });
                }
                cacheParticipantTypes.set(participantTypes);
                return participantTypes;
            }
        }).then(
            function (participantTypes) {
                $scope.participantTypes = participantTypes;
                return participantTypes;
            }
        );


        var cacheParticipantUsers = new Store.SessionData(Helper.SessionCacheNames.PARTICIPANT_USERS);
        var participantUsers = cacheParticipantUsers.get();
        var promiseUsers = Util.serviceCall({
            service: LookupService.getUsersBasic
            , result: participantUsers
            , onSuccess: function (data) {
                if (Validator.validateSolrData(data)) {
                    participantUsers = [];
                    _.each(data.response.docs, function (doc) {
                        var user = {};
                        user.id = Util.goodValue(doc.object_id_s, 0);
                        user.name = Util.goodValue(doc.name);
                        participantUsers.push(user);

                    });
                    cacheParticipantUsers.set(participantUsers);
                    return participantUsers;
                }
            }
        }).then(
            function (participantUsers) {
                $scope.participantUsers = participantUsers;
                return participantUsers;
            }
        );

        var cacheParticipantGroups = new Store.SessionData(Helper.SessionCacheNames.PARTICIPANT_GROUPS);
        var participantGroups = cacheParticipantGroups.get();
        var promiseGroups = Util.serviceCall({
            service: LookupService.getGroups
            , result: participantGroups
            , onSuccess: function (data) {
                if (Validator.validateSolrData(data)) {
                    participantGroups = [];
                    _.each(data.response.docs, function (doc) {
                        var group = {};
                        group.id = Util.goodValue(doc.object_id_s, 0);
                        group.name = Util.goodValue(doc.name);
                        participantGroups.push(group);
                    });
                    cacheParticipantGroups.set(participantGroups);
                    return participantGroups;
                }
            }
        }).then(
            function (participantGroups) {
                $scope.participantGroups = participantGroups;
                return participantGroups;
            }
        );

        var updateGridData = function (data) {
            $q.all([promiseTypes, promiseUsers, promiseGroups, promiseConfig]).then(function () {
                var participants = data.participants;
                _.each(participants, function (participant) {
                    if ("*" === participant.participantType) {
                        participant.acm$_participantNames = [
                            {id: "*", name: "*"}
                        ];
                    } else if ("owning group" === participant.participantType) {
                        participant.acm$_participantNames = $scope.participantGroups;
                    } else {
                        participant.acm$_participantNames = $scope.participantUsers;
                    }
                });
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = participants;
                $scope.caseInfo = data;
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, participants.length);
            });
        };
        $scope.$on('case-updated', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                if (data.id == $stateParams.id) {
                    updateGridData(data);
                } else {                      // condition when data comes before state is routed and config is not set
                    deferParticipantData.set(data);
                }
            }
        });


        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.data.length);
        };
        $scope.updateRow = function (rowEntity) {
            var caseInfo = Util.omitNg($scope.caseInfo);
            Util.serviceCall({
                service: CasesService.save
                , data: caseInfo
                , onSuccess: function (data) {
                    if (Validator.validateCaseFile(data)) {
                        return data;
                    }
                }
            }).then(
                function (caseSaved) {
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
                    return caseSaved;
                }
            );
            //CasesService.save({}, caseInfo
            //    , function (caseSaved) {
            //        if (Validator.validateCaseFile(caseSaved)) {
            //            //if participant is newly added, fill incomplete values with the latest
            //            if (Util.isEmpty(rowEntity.id)) {
            //                var participants = Util.goodMapValue(caseSaved, "participants", []);
            //                var participantAdded = _.find(participants, {
            //                    participantType: rowEntity.participantType,
            //                    participantLdapId: rowEntity.participantLdapId
            //                });
            //                if (participantAdded) {
            //                    rowEntity = _.merge(rowEntity, participantAdded);
            //                }
            //            }
            //        }
            //    }
            //    , function (errorData) {
            //        var z = 2;
            //    }
            //);
        };
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                Util.serviceCall({
                    service: CasesService.save
                    , data: caseInfo
                    , onSuccess: function (data) {
                        if (Validator.validateCaseFile(data)) {
                            return data;
                        }
                    }
                });
                //CasesService.save({}, caseInfo
                //    , function (caseSaved) {
                //        if (Validator.validateCaseFile(caseSaved)) {
                //            var z = 1;
                //        }
                //        var z = 1;
                //    }
                //    , function (errorData) {
                //        var z = 2;
                //    }
                //);
            }

        };
    }
])

;


