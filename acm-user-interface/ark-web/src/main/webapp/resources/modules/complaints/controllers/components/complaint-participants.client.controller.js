'use strict';

angular.module('complaints').controller('Complaints.ParticipantsController', ['$scope', '$stateParams', '$q'
    , 'StoreService', 'UtilService', 'HelperService', 'Complaint.InfoService', 'LookupService', 'Object.LookupService'
    , function ($scope, $stateParams, $q, Store, Util, Helper, ComplaintInfoService, LookupService, ObjectLookupService) {

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


                var deferParticipantData = new Store.Variable("deferComplaintParticipantData");    // used to hold grid data before grid config is ready
                var complaintInfo = deferParticipantData.get();
                if (complaintInfo) {
                    updateGridData(complaintInfo);
                    deferParticipantData.set(null);
                }
            });
        });


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

        var promiseGroups = ObjectLookupService.getOwningGroups().then(
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
                $scope.gridOptions.data = participants;
                $scope.complaintInfo = data;
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, participants.length);
            });
        };
        $scope.$on('complaint-updated', function (e, data) {
            if (data.id == $stateParams.id) {
                updateGridData(data);
            } else {                      // condition when data comes before state is routed and config is not set
                var deferParticipantData = new Store.Variable("deferComplaintParticipantData");
                deferParticipantData.set(data);
            }
        });


        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
            Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.data.length);
        };
        $scope.updateRow = function (rowEntity) {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                function (complaintSaved) {
                    //if participant is newly added, fill incomplete values with the latest
                    if (Util.isEmpty(rowEntity.id)) {
                        var participants = Util.goodMapValue(complaintSaved, "participants", []);
                        var participantAdded = _.find(participants, {
                            participantType: rowEntity.participantType,
                            participantLdapId: rowEntity.participantLdapId
                        });
                        if (participantAdded) {
                            rowEntity = _.merge(rowEntity, participantAdded);
                        }
                    }
                    return complaintSaved;
                }
            );
        };
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
            }

        };
    }
])

;


