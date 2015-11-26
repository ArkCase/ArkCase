'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', '$stateParams', '$q', '$translate'
    , 'StoreService', 'UtilService', 'ConstantService', 'HelperService', 'Case.InfoService', 'Object.PersonService'
    , 'LookupService', 'Object.LookupService'
    , function ($scope, $stateParams, $q, $translate, Store, Util, Constant, Helper, CaseInfoService
        , ObjectPersonService, LookupService, ObjectLookupService) {

        var promiseConfig = Helper.requestComponentConfig($scope, "people", function (config) {
            configGridMain(config);
            configGridContactMethod(config);
            configGridOrganization(config);
            configGridAddress(config);
            configGridAlias(config);
            configGridSecurityTag(config);

            $q.all([promisePersonTypes, promiseUsers, promiseContactMethodTypes, promiseAddressTypes, promiseAliasTypes, promiseSecurityTagTypes]).then(function (data) {
                var deferPeopleData = new Store.Variable("deferCasePeopleData");    // used to hold grid data before grid config is ready
                var caseInfo = deferPeopleData.get();
                if (caseInfo) {
                    updateGridData(caseInfo);
                    deferPeopleData.set(null);
                }
            });
        });


        var promiseUsers = Helper.Grid.getUsers($scope);

        var promisePersonTypes = ObjectLookupService.getPersonTypes().then(
            function (personTypes) {
                var options = [];
                _.forEach(personTypes, function (v, k) {
                    options.push({type: v, name: v});
                });
                $scope.personTypes = options;
                return personTypes;
            }
        );

        var promiseContactMethodTypes = ObjectLookupService.getContactMethodTypes().then(
            function (contactMethodTypes) {
                $scope.contactMethodTypes = contactMethodTypes;
                return contactMethodTypes;
            }
        );

        var promiseOrganizationTypes = ObjectLookupService.getOrganizationTypes().then(
            function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                return organizationTypes;
            }
        );

        var promiseAddressTypes = ObjectLookupService.getAddressTypes().then(
            function (addressTypes) {
                $scope.addressTypes = addressTypes;
                return addressTypes;
            }
        );

        var promiseAliasTypes = ObjectLookupService.getAliasTypes().then(
            function (aliasTypes) {
                $scope.aliasTypes = aliasTypes;
                return aliasTypes;
            }
        );

        var promiseSecurityTagTypes = ObjectLookupService.getSecurityTagTypes().then(
            function (securityTagTypes) {
                $scope.securityTagTypes = securityTagTypes;
                return securityTagTypes;
            }
        );

        var configGridMain = function (config) {
            gridAddEntityButtons(config.columnDefs);
            Helper.Grid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            Helper.Grid.setColumnDefs($scope, config);
            Helper.Grid.setBasicOptions($scope, config);
            Helper.Grid.setInPlaceEditing($scope, config, $scope.updateRow,
                function (rowEntity) {
                    return (!Util.isEmpty(rowEntity.personType)
                        && Util.goodMapValue(rowEntity, "person.givenName", false)
                        && Util.goodMapValue(rowEntity, "person.familyName", false)
                    );
                }
            );
            Helper.Grid.addGridApiHandler($scope, function (gridApi) {
                gridApi.core.on.rowsRendered($scope, function () {
                    $scope.gridApi.grid.columns[0].hideColumn();
                });
            });

            //$scope.gridOptions is defined by above setBasicOptions()
            $scope.gridOptions.expandableRowTemplate = "modules/cases/views/components/case-people.sub.view.html";
            $scope.gridOptions.expandableRowHeight = 305;
            $scope.gridOptions.expandableRowScope = {       //from sample. what is it for?
                subGridVariable: 'subGridScopeVariable'
            };

            promisePersonTypes.then(function (data) {
                $scope.gridOptions.enableRowSelection = false;
                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if (Helper.Lookups.PERSON_TYPES == $scope.config.columnDefs[i].lookup) {
                        $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                        $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                        $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                        $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                        $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.personTypes;
                        $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";
                    }
                }
            });
        };

        var configGridContactMethod = function (config) {
            if (Util.goodMapValue(config, "contactMethods.columnDefs[0]", false)) {
                $scope.contactMethods = {};
                Helper.Grid.addDeleteButton(config.contactMethods.columnDefs, "grid.appScope.deleteRowContactMethods(row.entity)");
                Helper.Grid.setColumnDefs($scope.contactMethods, config.contactMethods);
                Helper.Grid.setBasicOptions($scope.contactMethods, config.contactMethods);
                $scope.contactMethods.gridOptions.appScopeProvider = $scope;

                $q.all([promiseContactMethodTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.contactMethods.columnDefs.length; i++) {
                        if (Helper.Lookups.CONTACT_METHODS_TYPES == $scope.config.contactMethods.columnDefs[i].lookup) {
                            $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.contactMethodTypes;
                            $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Helper.Lookups.USER_FULL_NAMES == $scope.config.contactMethods.columnDefs[i].lookup) {
                            $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = false;
                            $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                            $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                        }
                    }
                });

                //todo:
                //filter for create date??
            }
        };

        var configGridOrganization = function (config) {
            if (Util.goodMapValue(config, "organizations.columnDefs[0]", false)) {
                $scope.organizations = {};
                Helper.Grid.addDeleteButton(config.organizations.columnDefs, "grid.appScope.deleteRowOrganizations(row.entity)");
                Helper.Grid.setColumnDefs($scope.organizations, config.organizations);
                Helper.Grid.setBasicOptions($scope.organizations, config.organizations);
                $scope.organizations.gridOptions.appScopeProvider = $scope;

                $q.all([promiseOrganizationTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.organizations.columnDefs.length; i++) {
                        if (Helper.Lookups.ORGANIZATION_TYPES == $scope.config.organizations.columnDefs[i].lookup) {
                            $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.organizationTypes;
                            $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Helper.Lookups.USER_FULL_NAMES == $scope.config.organizations.columnDefs[i].lookup) {
                            $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = false;
                            $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                            $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                        }
                    }
                });
            }
        };

        var configGridAddress = function (config) {
            if (Util.goodMapValue(config, "addresses.columnDefs[0]", false)) {
                $scope.addresses = {};
                Helper.Grid.addDeleteButton(config.addresses.columnDefs, "grid.appScope.deleteRowAddresses(row.entity)");
                Helper.Grid.setColumnDefs($scope.addresses, config.addresses);
                Helper.Grid.setBasicOptions($scope.addresses, config.addresses);
                $scope.addresses.gridOptions.appScopeProvider = $scope;

                $q.all([promiseAddressTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.addresses.columnDefs.length; i++) {
                        if (Helper.Lookups.ADDRESS_TYPES == $scope.config.addresses.columnDefs[i].lookup) {
                            $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.addressTypes;
                            $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Helper.Lookups.USER_FULL_NAMES == $scope.config.addresses.columnDefs[i].lookup) {
                            $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = false;
                            $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                            $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                        }
                    }
                });
            }
        };

        var configGridAlias = function (config) {
            if (Util.goodMapValue(config, "aliases.columnDefs[0]", false)) {
                $scope.aliases = {};
                Helper.Grid.addDeleteButton(config.aliases.columnDefs, "grid.appScope.deleteRowAliases(row.entity)");
                Helper.Grid.setColumnDefs($scope.aliases, config.aliases);
                Helper.Grid.setBasicOptions($scope.aliases, config.aliases);
                $scope.aliases.gridOptions.appScopeProvider = $scope;

                $q.all([promiseAliasTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.aliases.columnDefs.length; i++) {
                        if (Helper.Lookups.ALIAS_TYPES == $scope.config.aliases.columnDefs[i].lookup) {
                            $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.aliasTypes;
                            $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Helper.Lookups.USER_FULL_NAMES == $scope.config.aliases.columnDefs[i].lookup) {
                            $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = false;
                            $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                            $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                        }
                    }
                });
            }
        };

        var configGridSecurityTag = function (config) {
            if (Util.goodMapValue(config, "securityTags.columnDefs[0]", false)) {
                $scope.securityTags = {};
                Helper.Grid.addDeleteButton(config.securityTags.columnDefs, "grid.appScope.deleteRowSecurityTags(row.entity)");
                Helper.Grid.setColumnDefs($scope.securityTags, config.securityTags);
                Helper.Grid.setBasicOptions($scope.securityTags, config.securityTags);
                $scope.securityTags.gridOptions.appScopeProvider = $scope;

                $q.all([promiseSecurityTagTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.securityTags.columnDefs.length; i++) {
                        if (Helper.Lookups.SECURITY_TAG_TYPES == $scope.config.securityTags.columnDefs[i].lookup) {
                            $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.securityTagTypes;
                            $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Helper.Lookups.USER_FULL_NAMES == $scope.config.securityTags.columnDefs[i].lookup) {
                            $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = false;
                            $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                            $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                        }
                    }
                });
            }
        };

        var gridAddEntityButtons = function (columnDefs) {
            if ("entity" == Util.goodMapValue(columnDefs, "[0].name")) {
                var columnDef = columnDefs[0];
                columnDef.width = 116;
                columnDef.headerCellTemplate = "<span></span>";
                columnDef.cellTemplate = "<a ng-click='grid.appScope.expand(\"contactMethods\", row)' title='" + $translate.instant("cases.comp.people.contactMethods.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-phone'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"organizations\", row)' title='" + $translate.instant("cases.comp.people.organizations.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-cubes'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"addresses\", row)' title='" + $translate.instant("cases.comp.people.addresses.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-map-marker'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"aliases\", row)' title='" + $translate.instant("cases.comp.people.aliases.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-users'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"securityTags\", row)' title='" + $translate.instant("cases.comp.people.securityTags.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-shield'></i></a>"
                ;
            }
        };

        $scope.expand = function (subGrid, row) {
            if ($scope.currentSubGrid == subGrid) {
                $scope.gridApi.expandable.toggleRowExpansion(row.entity);

            } else {
                $scope.currentSubGrid = subGrid;
                if (!row.isExpanded) {
                    $scope.gridApi.expandable.toggleRowExpansion(row.entity);
                }
            }
        };

        var updateGridData = function (data) {
            $q.all([promiseUsers, promisePersonTypes, promiseContactMethodTypes, promiseOrganizationTypes, promiseAddressTypes, promiseAliasTypes, promiseSecurityTagTypes, promiseConfig]).then(function () {
                $scope.caseInfo = data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.caseInfo.personAssociations;
                Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.caseInfo.personAssociations.length);

                for (var i = 0; i < $scope.caseInfo.personAssociations.length; i++) {
                    var personAssociation = $scope.caseInfo.personAssociations[i];
                    personAssociation.acm$_contactMethods = {};
                    personAssociation.acm$_contactMethods.gridOptions = Util.goodValue($scope.contactMethods.gridOptions, {
                        columnDefs: [],
                        data: []
                    });
                    personAssociation.acm$_contactMethods.gridOptions.onRegisterApi = function (gridApi) {
                        personAssociation.acm$_contactMethods.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }
                            $scope.updateRowContactMethods(personAssociation, rowEntity);
                        });
                    };
                    personAssociation.acm$_contactMethods.gridOptions.data = personAssociation.person.contactMethods;


                    personAssociation.acm$_organizations = {};
                    personAssociation.acm$_organizations.gridOptions = Util.goodValue($scope.organizations.gridOptions, {
                        columnDefs: [],
                        data: []
                    });
                    personAssociation.acm$_organizations.gridOptions.onRegisterApi = function (gridApi) {
                        personAssociation.acm$_organizations.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }
                            $scope.updateRowOrganizations(personAssociation, rowEntity);
                        });
                    };
                    personAssociation.acm$_organizations.gridOptions.data = personAssociation.person.organizations;


                    personAssociation.acm$_addresses = {};
                    personAssociation.acm$_addresses.gridOptions = Util.goodValue($scope.addresses.gridOptions, {
                        columnDefs: [],
                        data: []
                    });
                    personAssociation.acm$_addresses.gridOptions.onRegisterApi = function (gridApi) {
                        personAssociation.acm$_addresses.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }
                            $scope.updateRowAddresses(personAssociation, rowEntity);
                        });
                    };
                    personAssociation.acm$_addresses.gridOptions.data = personAssociation.person.addresses;


                    personAssociation.acm$_aliases = {};
                    personAssociation.acm$_aliases.gridOptions = Util.goodValue($scope.aliases.gridOptions, {
                        columnDefs: [],
                        data: []
                    });
                    personAssociation.acm$_aliases.gridOptions.onRegisterApi = function (gridApi) {
                        personAssociation.acm$_aliases.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }
                            $scope.updateRowAliases(personAssociation, rowEntity);
                        });
                    };
                    personAssociation.acm$_aliases.gridOptions.data = personAssociation.person.personAliases;


                    personAssociation.acm$_securityTags = {};
                    personAssociation.acm$_securityTags.gridOptions = Util.goodValue($scope.securityTags.gridOptions, {
                        columnDefs: [],
                        data: []
                    });
                    personAssociation.acm$_securityTags.gridOptions.onRegisterApi = function (gridApi) {
                        personAssociation.acm$_securityTags.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }
                            $scope.updateRowSecurityTags(personAssociation, rowEntity);
                        });
                    };
                    personAssociation.acm$_securityTags.gridOptions.data = personAssociation.person.securityTags;
                }
            }); //end $q
        };
        $scope.$on('case-updated', function (e, data) {
            if (data.id == $stateParams.id) {
                updateGridData(data);
            } else {                      // condition when data comes before state is routed and config is not set
                var deferPeopleData = new Store.Variable("deferCasePeopleData");
                deferPeopleData.set(data);
            }
        });


        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
        };
        $scope.updateRow = function (rowEntity) {
            var givenName = Util.goodMapValue(rowEntity, "person.givenName");
            var familyName = Util.goodMapValue(rowEntity, "person.familyName");

            //
            // add new person association
            //
            if (Util.isEmpty(rowEntity.id)) {
                var pa = newPersonAssociation();
                pa.parentId = $scope.caseInfo.id;
                pa.parentType = Constant.ObjectTypes.CASE_FILE;
                pa.person.className = Util.goodValue($scope.config.className); //"com.armedia.acm.plugins.person.model.Person";
                pa.person.givenName = givenName;
                pa.person.familyName = familyName;
                ObjectPersonService.addPersonAssociation(pa).then(
                    function (personAssociationAdded) {
                        rowEntity = _.merge(rowEntity, personAssociationAdded);
                        return personAssociationAdded;
                    }
                );
                //Util.serviceCall({
                //    service: ObjectPersonService.addPersonAssociation
                //    , data: pa
                //    , onSuccess: function (data) {
                //        if (Validator.validatePersonAssociation(data)) {
                //            return data;
                //        }
                //    }
                //}).then(
                //    function (personAssociationAdded) {
                //        rowEntity = _.merge(rowEntity, personAssociationAdded);
                //        return personAssociationAdded;
                //    }
                //);


                //
                // update person association
                //
            } else {
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo).then(
                    function (caseSaved) {
                        return caseSaved;
                    }
                    , function (error) {
                        return error;
                    }
                );
                //Util.serviceCall({
                //    service: CasesService.save
                //    , data: caseInfo
                //    , onSuccess: function (data) {
                //        if (Validator.validateCaseFile(data)) {
                //            var caseSaved = data;
                //            return caseSaved;
                //        }
                //    }
                //}).then(
                //    function (caseSaved) {
                //        return caseSaved;
                //    }
                //    , function (error) {
                //        return error;
                //    }
                //);
            }
        };
        $scope.deleteRow = function (rowEntity) {
            Helper.Grid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                ObjectPersonService.deletePersonAssociation(id).then(
                    function (personAssociationDeleted) {
                        return personAssociationDeleted;
                    }
                    , function (error) {
                        return error;
                    }
                );
                //Util.serviceCall({
                //    service: ObjectPersonService.deletePersonAssociation
                //    , param: {personAssociationId: id}
                //    , data: {}
                //    , onSuccess: function (data) {
                //        if (Validator.validateDeletedPersonAssociation(data)) {
                //            return data;
                //        }
                //    }
                //}).then(
                //    function (personAssociationDeleted) {
                //        return personAssociationDeleted;
                //    }
                //    , function (error) {
                //        return error;
                //    }
                //);
            }

        };
        $scope.addNewContactMethods = function (rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowParent.entity);
            });

            if (Util.goodMapValue($scope.gridOptions.data, "[" + idx + "].acm$_contactMethods.gridApi", false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_contactMethods.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_contactMethods.gridOptions.data.push({});
            }
        };
        $scope.updateRowContactMethods = function (personAssociation, rowEntity) {
            var caseInfo = Util.omitNg($scope.caseInfo);
            CaseInfoService.saveCaseInfo(caseInfo).then(
                function (caseSaved) {
                    if (Util.isEmpty(rowEntity.id)) {
                        var personAssociationsSaved = Util.goodMapValue(caseSaved, "personAssociations", []);
                        var personAssociationSaved = _.find(personAssociationsSaved, {id: personAssociation.id});
                        if (personAssociationSaved) {
                            var contactMethodSaved = _.find(personAssociationSaved.person.contactMethods, {id: rowEntity.id});
                            if (contactMethodSaved) {
                                rowEntity = _.merge(rowEntity, contactMethodSaved);
                            }
                        }
                    }
                    return caseSaved;
                }
            );
            //Util.serviceCall({
            //    service: CasesService.save
            //    , data: caseInfo
            //    , onSuccess: function (data) {
            //        if (Validator.validateCaseFile(data)) {
            //            return data;
            //        }
            //    }
            //}).then(
            //    function (caseSaved) {
            //        if (Util.isEmpty(rowEntity.id)) {
            //            var personAssociationsSaved = Util.goodMapValue(caseSaved, "personAssociations", []);
            //            var personAssociationSaved = _.find(personAssociationsSaved, {id: personAssociation.id});
            //            if (personAssociationSaved) {
            //                var contactMethodSaved = _.find(personAssociationSaved.person.contactMethods, {id: rowEntity.id});
            //                if (contactMethodSaved) {
            //                    rowEntity = _.merge(rowEntity, contactMethodSaved);
            //                }
            //            }
            //        }
            //        return caseSaved;
            //    }
            //);
        };
        $scope.deleteRowContactMethods = function (rowEntity) {
            Helper.Grid.deleteRow($scope.contactMethods, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
                //Util.serviceCall({
                //    service: CasesService.save
                //    , data: caseInfo
                //    , onSuccess: function (data) {
                //        if (Validator.validateCaseFile(data)) {
                //            return data;
                //        }
                //    }
                //});
            }
        };
        $scope.addNewOrganizations = function (rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue($scope.gridOptions.data, "[" + idx + "].acm$_organizations.gridApi", false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_organizations.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_organizations.gridOptions.data.push({});
            }
        };
        $scope.updateRowOrganizations = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowOrganizations = function (rowEntity) {
            Helper.Grid.deleteRow($scope.organizations, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
                //Util.serviceCall({
                //    service: CasesService.save
                //    , data: caseInfo
                //    , onSuccess: function (data) {
                //        if (Validator.validateCaseFile(data)) {
                //            var caseSaved = data;
                //            return caseSaved;
                //        }
                //    }
                //});
            }
        };
        $scope.addNewAddresses = function (rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue($scope.gridOptions.data, "[" + idx + "].acm$_addresses.gridApi", false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_addresses.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_addresses.gridOptions.data.push({});
            }
        };
        $scope.updateRowAddresses = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowAddresses = function (rowEntity) {
            Helper.Grid.deleteRow($scope.addresses, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
                //Util.serviceCall({
                //    service: CasesService.save
                //    , data: caseInfo
                //    , onSuccess: function (caseSaved) {
                //        if (Validator.validateCaseFile(caseSaved)) {
                //            return caseSaved;
                //        }
                //    }
                //});
            }
        };
        $scope.addNewAliases = function (rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue($scope.gridOptions.data, "[" + idx + "].acm$_aliases.gridApi", false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_aliases.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_aliases.gridOptions.data.push({});
            }
        };
        $scope.updateRowAliases = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowAliases = function (rowEntity) {
            Helper.Grid.deleteRow($scope.aliases, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
                //Util.serviceCall({
                //    service: CasesService.save
                //    , data: caseInfo
                //    , onSuccess: function (caseSaved) {
                //        if (Validator.validateCaseFile(caseSaved)) {
                //            return caseSaved;
                //        }
                //    }
                //});
            }
        };
        $scope.addNewSecurityTags = function (rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowParent.entity);
            });

            if (Util.goodMapValue($scope.gridOptions.data, "[" + idx + "].acm$_securityTags..gridApi", false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_securityTags.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_securityTags.gridOptions.data.push({});
            }
        };
        $scope.updateRowSecurityTags = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowSecurityTags = function (rowEntity) {
            Helper.Grid.deleteRow($scope.securityTags, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
            }
        };


        var newPersonAssociation = function () {
            return {
                id: null
                , personType: ""
                , parentId: null
                , parentType: ""
                , personDescription: ""
                , notes: ""
                , person: {
                    id: null
                    , title: ""
                    , givenName: ""
                    , familyName: ""
                    , company: ""
                    /*,hairColor:""
                     ,eyeColor:""
                     ,heightInInches:null*/
                    , weightInPounds: null
                    /*,dateOfBirth:null
                     ,dateMarried:null*/
                    , addresses: []
                    , contactMethods: []
                    , securityTags: []
                    , personAliases: []
                    , organizations: []
                }
            };
        }
    }
])

;
