'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q', '$translate', '$modal'
    , 'StoreService', 'UtilService', 'ConfigService', 'Case.InfoService', 'LookupService', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q, $translate, $modal
        , Store, Util, ConfigService, CaseInfoService, LookupService, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "participants"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onObjectInfoRetrieved: function (caseInfo) {
                onObjectInfoRetrieved(caseInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

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


        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
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
                gridHelper.setLookupDropDown(HelperUiGridService.Lookups.PARTICIPANT_TYPES, "type", "name", $scope.participantTypes);



                //$scope.gridOptions.enableRowSelection = false;    //need to turn off for inline edit
                ////$scope.gridOptions.enableCellEdit = true;
                ////$scope.gridOptions.enableCellEditOnFocus = true;
                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    //    if (HelperUiGridService.Lookups.PARTICIPANT_TYPES == $scope.config.columnDefs[i].lookup) {
                    //        $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                    //        $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                    //        $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                    //        $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                    //        $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.participantTypes;
                    //        $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";
                    //
                    //
                    //    } else
                    if (HelperUiGridService.Lookups.PARTICIPANT_NAMES == $scope.config.columnDefs[i].lookup) {
                        //$scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        //$scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        //$scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        //$scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = "acm$_participantNames";
                        //$scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: row.entity.acm$_participantNames:'id':'name'";
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<div class='ui-grid-cell-contents' ng-click='grid.appScope.pickParticipant(row.entity)'>{{row.entity[col.field] | mapKeyValue: row.entity.acm$_participantNames:'id':'name'}}</div>";
                    }
                }
            });
        };

        $scope.pickParticipant = function (rowEntity) {
            var params = {};
            if (rowEntity.acm$_participantNames == $scope.participantUsers) {
                params.header = $translate.instant("cases.comp.participants.dialogUserPicker.header");
                params.filter = '"Object Type": USER';
                params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

            } else if (rowEntity.acm$_participantNames == $scope.participantGroups) {
                params.header = $translate.instant("cases.comp.participants.dialogGroupPicker.header");
                params.filter = '"Object Type": GROUP';
                params.config = Util.goodMapValue($scope.config, "dialogGroupPicker");

            } else { //if ("*" == Util.goodValue(rowEntity.participantType)) {
                return;
            }

            var modalInstance = $modal.open({
                templateUrl: "modules/cases/views/components/case-participant-picker.dialog.html"
                , controller: 'Cases.ParticipantPickerController'
                , animation: true
                , size: 'lg'
                , resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (selected) {
                if (!Util.isEmpty(selected)) {
                    rowEntity.participantLdapId = selected.object_id_s;
                    $scope.updateRow(rowEntity);
                }
            });
        };

        var onObjectInfoRetrieved = function (caseInfo) {
            $scope.caseInfo = caseInfo;
            $q.all([promiseTypes, promiseUsers, promiseGroups, $scope.promiseConfig]).then(function () {
                var participants = caseInfo.participants;
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
                //gridHelper.hidePagingControlsIfAllDataShown(participants.length);
            });
        };


        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
            //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.data.length);
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
]);


angular.module('directives').controller('Cases.ParticipantPickerController', ['$scope', '$modalInstance', 'params'
        , function ($scope, $modalInstance, params) {
            $scope.modalInstance = $modalInstance;
            $scope.header = params.header;
            $scope.filter = params.filter;
            $scope.config = params.config;
        }
    ]
);

