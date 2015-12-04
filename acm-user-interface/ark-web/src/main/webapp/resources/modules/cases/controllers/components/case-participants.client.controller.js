'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q'
    , 'StoreService', 'UtilService', 'Helper.UiGridService', 'Helper.ConfigService'
    , 'Case.InfoService', 'LookupService', 'Object.LookupService'
    , function ($scope, $stateParams, $q, Store, Util, HelperUiGridService, HelperConfigService
        , CaseInfoService, LookupService, ObjectLookupService) {

        var deferParticipantData = new Store.Variable("deferCaseParticipantData");    // used to hold grid data before grid config is ready

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseTypes = ObjectLookupService.getParticipantTypes().then(
            function (participantTypes) {
                $scope.participantTypes = participantTypes;
                return participantTypes;
            }
        );
        var promiseUsers = LookupService.getUsersBasic().then(
            function (participantUsers) {
                $scope.participantUsers = participantUsers;
                return participantUsers;
            }
        );
        var promiseGroups = ObjectLookupService.getGroups().then(
            function (participantGroups) {
                $scope.participantGroups = participantGroups;
                return participantGroups;
            }
        );

        var promiseConfig = HelperConfigService.requestComponentConfig($scope, "participants", function (config) {
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.addGridApiHandler(function (gridApi) {
                $scope.gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                    if (newValue == oldValue) {
                        return;
                    }

                    //
                    // Fix participant names selection
                    //
                    if (HelperUiGridService.Lookups.PARTICIPANT_TYPES === colDef.lookup) {
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
                    if (HelperUiGridService.Lookups.PARTICIPANT_TYPES == $scope.config.columnDefs[i].lookup) {
                        $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                        $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.participantTypes;
                        $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                    } else if (HelperUiGridService.Lookups.PARTICIPANT_NAMES == $scope.config.columnDefs[i].lookup) {
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
                gridHelper.hidePagingControlsIfAllDataShown(participants.length);
            });
        };
        $scope.$on('case-updated', function (e, data) {
            if (!CaseInfoService.validateCaseInfo(data)) {
                return;
            }

            if (data.id == $stateParams.id) {
                updateGridData(data);
            } else {                      // condition when data comes before state is routed and config is not set
                deferParticipantData.set(data);
            }
        });


        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
            gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.data.length);
        };
        $scope.updateRow = function (rowEntity) {
            var caseInfo = Util.omitNg($scope.caseInfo);
            CaseInfoService.saveCaseInfo(caseInfo).then(
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
        };
        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
            }

        };
    }
])

;


