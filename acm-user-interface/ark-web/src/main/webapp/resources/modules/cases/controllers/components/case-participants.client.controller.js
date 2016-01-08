'use strict';

angular.module('cases').controller('Cases.ParticipantsController', ['$scope', '$stateParams', '$q'
    , 'StoreService', 'UtilService', 'ConfigService', 'Case.InfoService', 'LookupService', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q
        , Store, Util, ConfigService, CaseInfoService, LookupService, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService) {

        //var deferParticipantData = new Store.Variable("deferCaseParticipantData");    // used to hold grid data before grid config is ready

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


        var promiseConfig = ConfigService.getComponentConfig("cases", "participants").then(function (config) {
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
                        //$scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        //$scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        //$scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        //$scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = "acm$_participantNames";
                        //$scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: row.entity.acm$_participantNames:'id':'name'";
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<div class='ui-grid-cell-contents' ng-click='grid.appScope.pickUser(row.entity)'>{{row.entity[col.field] | mapKeyValue: row.entity.acm$_participantNames:'id':'name'}}</div>";
                    }
                }
            });

            return config;
        });

        $scope.pickUser = function (rowEntity) {
            alert("pickUser");
            var params = {};

            var modalInstance = $modal.open({
                templateUrl: "directives/doc-tree/doc-tree.email.dialog.html"
                , controller: 'directives.DocTreeEmailDialogController'
                , resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (recipients) {
                if (!Util.isArrayEmpty(recipients)) {
                    var emailAddresses = _.pluck(recipients, "email");

                    if (Email.allowMailFilesAsAttachments) {
                        var emailData = Email._makeEmailDataForEmailWithAttachments(emailAddresses, nodes);
                        EcmEmailService.sendEmailWithAttachments(emailData);
                    }
                    else {
                        //var emailData = Email._makeEmailDataForEmailWithLinks(emailAddresses, nodes, title);
                        var emailData = Email._makeEmailDataForEmailWithLinks(emailAddresses, nodes);
                        EcmEmailService.sendEmail(emailData);
                    }
                }
            });
        };
        $scope.merge = function (caseInfo) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/case-merge.client.view.html',
                controller: 'Cases.MergeController',
                size: 'lg',
                resolve: {
                    $clientInfoScope: function () {
                        return $scope.caseFileSearchConfig;
                    },
                    $filter: function () {
                        return $scope.caseFileSearchConfig.caseInfoFilter;
                    }
                }
            });
            modalInstance.result.then(function (selectedCase) {
                if (selectedCase) {
                    if (selectedCase.parentId != null) {
                        //Already Merged
                    }
                    else {
                        MergeSplitService.mergeCaseFile(caseInfo.id, selectedCase.object_id_s).then(
                            function (data) {
                                ObjectService.gotoUrl(ObjectService.ObjectTypes.CASE_FILE, data.id);
                            });
                    }
                }
            }, function () {
                // Cancel button was clicked
            });
        };

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
                //gridHelper.hidePagingControlsIfAllDataShown(participants.length);
            });
        };
        //$scope.$on('object-updated', function (e, data) {
        //    if (!CaseInfoService.validateCaseInfo(data)) {
        //        return;
        //    }
        //
        //    if (data.id == $stateParams.id) {
        //        updateGridData(data);
        //    } else {                      // condition when data comes before state is routed and config is not set
        //        deferParticipantData.set(data);
        //    }
        //});
        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CaseInfoService.getCaseInfo(currentObjectId).then(function (caseInfo) {
                updateGridData(caseInfo);
                return caseInfo;
            });
        }


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


angular.module('directives').controller('Cases.ParticipantPickerController', ['$scope', '$modalInstance'
        , 'UtilService', 'params', 'ConfigService'
        , function ($scope, $modalInstance, Util, params, ConfigService) {
            $scope.modalInstance = $modalInstance;

            ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.config = Util.goodMapValue(moduleConfig, "docTree.emailDialog");
                //$scope.filter = $scope.config.userFacetFilter;
                return moduleConfig;
            });

            $scope.header = "User";
            $scope.filter = '"Object Type": USER';

            $scope.recipients = [];
            $scope.onItemsSelected = function (selectedItems, lastSelectedItems, isSelected) {
                var recipientTokens = Util.goodValue($scope.recipientsStr).split(";");
                _.each(lastSelectedItems, function (selectedItem) {
                    var found = _.find($scope.recipients, function (recipient) {
                        return Util.compare(selectedItem.name, recipient.name) || Util.compare(selectedItem.email_lcs, recipient.email)
                    });
                    if (isSelected && !found) {
                        $scope.recipients.push({
                            name: Util.goodValue(selectedItem.name)
                            , email: Util.goodValue(selectedItem.email_lcs)
                        });

                    } else if (!isSelected && found) {
                        _.remove($scope.recipients, found);
                    }
                });

                $scope.recipientsStr = _.pluck($scope.recipients, "name").join(";");
            };
            $scope.onChangeRecipients = function () {
                var recipientsNew = [];
                var recipientTokens = Util.goodValue($scope.recipientsStr).split(";");
                _.each(recipientTokens, function (token) {
                    token = token.trim();
                    if (!Util.isEmpty(token)) {
                        var found = _.find($scope.recipients, function (recipient) {
                            return (token == recipient.name || token == recipient.email);
                        });
                        if (found) {
                            recipientsNew.push(found);
                        } else {
                            var recipientUserTyped = {name: token, email: token};
                            recipientsNew.push(recipientUserTyped);
                        }
                    }
                });
                $scope.recipients = recipientsNew;
            };
            $scope.onClickCancel = function () {
                $modalInstance.close(false);
            };
            $scope.onClickOk = function () {
                //var a = $scope.searchControl.getSelectedItems();
                $modalInstance.close($scope.recipients);
            };
            $scope.disableOk = function () {
                return Util.isEmpty($scope.recipientsStr);
            };

        }
    ]
);

