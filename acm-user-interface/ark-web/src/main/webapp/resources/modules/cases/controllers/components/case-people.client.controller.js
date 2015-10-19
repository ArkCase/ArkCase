'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', '$stateParams', '$q', '$translate', 'UtilService', 'ValidationService', 'CasesService', 'LookupService',
    function ($scope, $stateParams, $q, $translate, Util, Validator, CasesService, LookupService) {
        $scope.$emit('req-component-config', 'people');


        var promiseUsers = Util.AcmGrid.getUsers($scope);

        var promisePersonTypes = Util.serviceCall({
            service: LookupService.getPersonTypes
            , onSuccess: function (data) {
                $scope.personTypes = [];
                _.forEach(data, function (v, k) {
                    $scope.personTypes.push({type: v, name: v});
                });
                return $scope.personTypes;
            }
        });
        var promiseContactMethodTypes = Util.serviceCall({
            service: LookupService.getContactMethodTypes
            , onSuccess: function (data) {
                $scope.contactMethodTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.contactMethodTypes.push({type: k, name: v});
                });
                return $scope.contactMethodTypes;
            }
        });
        var promiseOrganizationTypes = Util.serviceCall({
            service: LookupService.getOrganizationTypes
            , onSuccess: function (data) {
                $scope.organizationTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.organizationTypes.push({type: k, name: v});
                });
                return $scope.organizationTypes;
            }
        });
        var promiseAddressTypes = Util.serviceCall({
            service: LookupService.getAddressTypes
            , onSuccess: function (data) {
                $scope.addressTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.addressTypes.push({type: k, name: v});
                });
                return $scope.addressTypes;
            }
        });
        var promiseAliasTypes = Util.serviceCall({
            service: LookupService.getAliasTypes
            , onSuccess: function (data) {
                $scope.aliasTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.aliasTypes.push({type: k, name: v});
                });
                return $scope.aliasTypes;
            }
        });
        var promiseSecurityTagTypes = Util.serviceCall({
            service: LookupService.getSecurityTagTypes
            , onSuccess: function (data) {
                $scope.securityTagTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.securityTagTypes.push({type: k, name: v});
                });
                return $scope.securityTagTypes;
            }
        });


        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'people' && Util.goodMapValue(config, "columnDefs[0]", false)) {
                configGridMain(config);
                configGridContactMethod(config);
                configGridOrganization(config);
                configGridAddress(config);
                configGridAlias(config);
                configGridSecurityTag(config);
            }
        });

        var configGridMain = function (config) {
            gridAddEntityButtons(config.columnDefs);
            Util.AcmGrid.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            Util.AcmGrid.setColumnDefs($scope, config);
            Util.AcmGrid.setBasicOptions($scope, config);
            Util.AcmGrid.setInPlaceEditing($scope, config, $scope.updateRow,
                function (rowEntity) {
                    return (!Util.isEmpty(rowEntity.personType)
                        && Util.goodMapValue(rowEntity, "person.givenName", false)
                        && Util.goodMapValue(rowEntity, "person.familyName", false)
                    );
                }
            );
            Util.AcmGrid.addGridApiHandler($scope, function (gridApi) {
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
                    if (Util.Constant.LOOKUP_PERSON_TYPES == $scope.config.columnDefs[i].lookup) {
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
                Util.AcmGrid.addDeleteButton(config.contactMethods.columnDefs, "grid.appScope.deleteRowContactMethods(row.entity)");
                Util.AcmGrid.setColumnDefs($scope.contactMethods, config.contactMethods);
                Util.AcmGrid.setBasicOptions($scope.contactMethods, config.contactMethods);
                $scope.contactMethods.gridOptions.appScopeProvider = $scope;

                $q.all([promiseContactMethodTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.contactMethods.columnDefs.length; i++) {
                        if (Util.Constant.LOOKUP_CONTACT_METHODS_TYPES == $scope.config.contactMethods.columnDefs[i].lookup) {
                            $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.contactMethodTypes;
                            $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Util.Constant.LOOKUP_USER_FULL_NAMES == $scope.config.contactMethods.columnDefs[i].lookup) {
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
                Util.AcmGrid.addDeleteButton(config.organizations.columnDefs, "grid.appScope.deleteRowOrganizations(row.entity)");
                Util.AcmGrid.setColumnDefs($scope.organizations, config.organizations);
                Util.AcmGrid.setBasicOptions($scope.organizations, config.organizations);
                $scope.organizations.gridOptions.appScopeProvider = $scope;

                $q.all([promiseOrganizationTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.organizations.columnDefs.length; i++) {
                        if (Util.Constant.LOOKUP_ORGANIZATION_TYPES == $scope.config.organizations.columnDefs[i].lookup) {
                            $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.organizationTypes;
                            $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Util.Constant.LOOKUP_USER_FULL_NAMES == $scope.config.organizations.columnDefs[i].lookup) {
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
                Util.AcmGrid.addDeleteButton(config.addresses.columnDefs, "grid.appScope.deleteRowAddresses(row.entity)");
                Util.AcmGrid.setColumnDefs($scope.addresses, config.addresses);
                Util.AcmGrid.setBasicOptions($scope.addresses, config.addresses);
                $scope.addresses.gridOptions.appScopeProvider = $scope;

                $q.all([promiseAddressTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.addresses.columnDefs.length; i++) {
                        if (Util.Constant.LOOKUP_ADDRESS_TYPES == $scope.config.addresses.columnDefs[i].lookup) {
                            $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.addressTypes;
                            $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Util.Constant.LOOKUP_USER_FULL_NAMES == $scope.config.addresses.columnDefs[i].lookup) {
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
                Util.AcmGrid.addDeleteButton(config.aliases.columnDefs, "grid.appScope.deleteRowAliases(row.entity)");
                Util.AcmGrid.setColumnDefs($scope.aliases, config.aliases);
                Util.AcmGrid.setBasicOptions($scope.aliases, config.aliases);
                $scope.aliases.gridOptions.appScopeProvider = $scope;

                $q.all([promiseAliasTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.aliases.columnDefs.length; i++) {
                        if (Util.Constant.LOOKUP_ALIAS_TYPES == $scope.config.aliases.columnDefs[i].lookup) {
                            $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.aliasTypes;
                            $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Util.Constant.LOOKUP_USER_FULL_NAMES == $scope.config.aliases.columnDefs[i].lookup) {
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
                Util.AcmGrid.addDeleteButton(config.securityTags.columnDefs, "grid.appScope.deleteRowSecurityTags(row.entity)");
                Util.AcmGrid.setColumnDefs($scope.securityTags, config.securityTags);
                Util.AcmGrid.setBasicOptions($scope.securityTags, config.securityTags);
                $scope.securityTags.gridOptions.appScopeProvider = $scope;

                $q.all([promiseSecurityTagTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.securityTags.columnDefs.length; i++) {
                        if (Util.Constant.LOOKUP_SECURITY_TAG_TYPES == $scope.config.securityTags.columnDefs[i].lookup) {
                            $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.securityTagTypes;
                            $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (Util.Constant.LOOKUP_USER_FULL_NAMES == $scope.config.securityTags.columnDefs[i].lookup) {
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
        }

        $scope.expand = function (subGrid, row) {
            if ($scope.currentSubGrid == subGrid) {
                $scope.gridApi.expandable.toggleRowExpansion(row.entity);

            } else {
                $scope.currentSubGrid = subGrid;
                if (!row.isExpanded) {
                    $scope.gridApi.expandable.toggleRowExpansion(row.entity);
                }
            }
        }

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $q.all([promiseUsers, promisePersonTypes, promiseContactMethodTypes, promiseOrganizationTypes, promiseAddressTypes, promiseAliasTypes, promiseSecurityTagTypes]).then(function () {
                    $scope.caseInfo = data;
                    $scope.gridOptions.data = $scope.caseInfo.personAssociations;

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
                        }
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
                        }
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
                        }
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
                        }
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
                        }
                        personAssociation.acm$_securityTags.gridOptions.data = personAssociation.person.securityTags;
                    }
                }); //end $q
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
                pa.parentType = Util.Constant.OBJTYPE_CASE_FILE;
                pa.person.className = Util.goodValue($scope.config.className); //"com.armedia.acm.plugins.person.model.Person";
                pa.person.givenName = givenName;
                pa.person.familyName = familyName;
                CasesService.addPersonAssociation({}, pa
                    , function (successData) {
                        if (Validator.validatePersonAssociation(successData)) {
                            var personAssociationAdded = successData;
                            rowEntity = _.merge(rowEntity, personAssociationAdded);
                        }
                    }
                    , function (errorData) {
                    }
                );

                //
                // update person association
                //
            } else {
                var caseInfo = Util.omitNg($scope.caseInfo);
                CasesService.save({}, caseInfo
                    , function (successData) {
                        if (Validator.validateCaseFile(successData)) {
                            var caseSaved = successData;
                            console.log("updated People table");
                        }
                    }
                    , function (errorData) {
                    }
                );
            }
        }
        $scope.deleteRow = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                CasesService.deletePersonAssociation({personAssociationId: id}
                    , function (personAssociationDeleted) {
                        if (Validator.validateDeletedPersonAssociation(personAssociationDeleted)) {
                            console.log("deleted People row");
                        }
                    }
                    , function (errorData) {
                    }
                );
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
        }
        $scope.updateRowContactMethods = function (personAssociation, rowEntity) {
            var caseInfo = Util.omitNg($scope.caseInfo);
            CasesService.save({}, caseInfo
                , function (caseSaved) {
                    if (Validator.validateCaseFile(caseSaved)) {
                        console.log("updated sub table");
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
                    }
                }
                , function (errorData) {
                }
            );
        };
        $scope.deleteRowContactMethods = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope.contactMethods, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CasesService.save({}, caseInfo
                    , function (caseSaved) {
                        if (Validator.validateCaseFile(caseSaved)) {
                            console.log("deleted sub table");
                            var z = 1;
                        }
                        var z = 1;
                    }
                    , function (errorData) {
                        var z = 2;
                    }
                );
            }
        }
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
        }
        $scope.updateRowOrganizations = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowOrganizations = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope.organizations, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CasesService.save({}, caseInfo
                    , function (caseSaved) {
                        if (Validator.validateCaseFile(caseSaved)) {
                            console.log("deleted sub table");
                            var z = 1;
                        }
                        var z = 1;
                    }
                    , function (errorData) {
                        var z = 2;
                    }
                );
            }
        }
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
        }
        $scope.updateRowAddresses = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowAddresses = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope.addresses, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CasesService.save({}, caseInfo
                    , function (caseSaved) {
                        if (Validator.validateCaseFile(caseSaved)) {
                            console.log("deleted sub table");
                            var z = 1;
                        }
                        var z = 1;
                    }
                    , function (errorData) {
                        var z = 2;
                    }
                );
            }
        }
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
        }
        $scope.updateRowAliases = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowAliases = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope.aliases, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var caseInfo = Util.omitNg($scope.caseInfo);
                CasesService.save({}, caseInfo
                    , function (caseSaved) {
                        if (Validator.validateCaseFile(caseSaved)) {
                            console.log("deleted sub table");
                            var z = 1;
                        }
                        var z = 1;
                    }
                    , function (errorData) {
                        var z = 2;
                    }
                );
            }
        }
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
        }
        $scope.updateRowSecurityTags = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowSecurityTags = function (rowEntity) {
            Util.AcmGrid.deleteRow($scope.securityTags, rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                //
                // save data to server
                //
            }
        }


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
