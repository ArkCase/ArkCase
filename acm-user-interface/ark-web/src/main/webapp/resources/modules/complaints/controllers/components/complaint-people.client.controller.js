'use strict';

angular.module('complaints').controller('Complaints.PeopleController', ['$scope', '$stateParams', '$q', '$translate'
    , 'StoreService', 'UtilService', 'ObjectService', 'Helper.UiGridService', 'Helper.ConfigService'
    , 'Complaint.InfoService', 'Object.PersonService', 'LookupService', 'Object.LookupService'
    , function ($scope, $stateParams, $q, $translate, Store, Util, ObjectService, HelperUiGridService, HelperConfigService
        , ComplaintInfoService, ObjectPersonService, LookupService, ObjectLookupService) {

        $scope.contactMethods = {gridOptions: {appScopeProvider: $scope}};
        $scope.organizations = {gridOptions: {appScopeProvider: $scope}};
        $scope.addresses = {gridOptions: {appScopeProvider: $scope}};
        $scope.aliases = {gridOptions: {appScopeProvider: $scope}};
        $scope.securityTags = {gridOptions: {appScopeProvider: $scope}};
        var gridContactMethodHelper = new HelperUiGridService.Grid({scope: $scope.contactMethods});
        var gridOrganizationHelper = new HelperUiGridService.Grid({scope: $scope.organizations});
        var gridAddressHelper = new HelperUiGridService.Grid({scope: $scope.addresses});
        var gridAliasHelper = new HelperUiGridService.Grid({scope: $scope.aliases});
        var gridSecurityTagHelper = new HelperUiGridService.Grid({scope: $scope.securityTags});
        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers($scope);

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

        var promiseConfig = HelperConfigService.requestComponentConfig($scope, "people", function (config) {
            configGridMain(config);
            configGridContactMethod(config);
            configGridOrganization(config);
            configGridAddress(config);
            configGridAlias(config);
            configGridSecurityTag(config);

            $q.all([promisePersonTypes, promiseUsers, promiseContactMethodTypes, promiseAddressTypes, promiseAliasTypes, promiseSecurityTagTypes]).then(function (data) {
                var deferPeopleData = new Store.Variable("deferComplaintPeopleData");    // used to hold grid data before grid config is ready
                var complaintInfo = deferPeopleData.get();
                if (complaintInfo) {
                    updateGridData(complaintInfo);
                    deferPeopleData.set(null);
                }
            });
        });


        var configGridMain = function (config) {
            gridAddEntityButtons(config.columnDefs);
            gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.setInPlaceEditing(config, $scope.updateRow,
                function (rowEntity) {
                    return (!Util.isEmpty(rowEntity.personType)
                        && Util.goodMapValue(rowEntity, "person.givenName", false)
                        && Util.goodMapValue(rowEntity, "person.familyName", false)
                    );
                }
            );
            gridHelper.addGridApiHandler(function (gridApi) {
                gridApi.core.on.rowsRendered($scope, function () {
                    $scope.gridApi.grid.columns[0].hideColumn();
                });
            });

            //$scope.gridOptions is defined by above setBasicOptions()
            $scope.gridOptions.expandableRowTemplate = "modules/complaints/views/components/complaint-people.sub.view.html";
            $scope.gridOptions.expandableRowHeight = 305;
            $scope.gridOptions.expandableRowScope = {       //from sample. what is it for?
                subGridVariable: 'subGridScopeVariable'
            };

            promisePersonTypes.then(function (data) {
                $scope.gridOptions.enableRowSelection = false;
                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if (HelperUiGridService.Lookups.PERSON_TYPES == $scope.config.columnDefs[i].lookup) {
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
                gridContactMethodHelper.addDeleteButton(config.contactMethods.columnDefs, "grid.appScope.deleteRowContactMethods(row.entity)");
                gridContactMethodHelper.setColumnDefs(config.contactMethods);
                gridContactMethodHelper.setBasicOptions(config.contactMethods);
                $q.all([promiseContactMethodTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.contactMethods.columnDefs.length; i++) {
                        if (HelperUiGridService.Lookups.CONTACT_METHODS_TYPES == $scope.config.contactMethods.columnDefs[i].lookup) {
                            $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.contactMethodTypes;
                            $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (HelperUiGridService.Lookups.USER_FULL_NAMES == $scope.config.contactMethods.columnDefs[i].lookup) {
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
                gridOrganizationHelper.addDeleteButton(config.organizations.columnDefs, "grid.appScope.deleteRowOrganizations(row.entity)");
                gridOrganizationHelper.setColumnDefs(config.organizations);
                gridOrganizationHelper.setBasicOptions(config.organizations);
                $q.all([promiseOrganizationTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.organizations.columnDefs.length; i++) {
                        if (HelperUiGridService.Lookups.ORGANIZATION_TYPES == $scope.config.organizations.columnDefs[i].lookup) {
                            $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.organizationTypes;
                            $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (HelperUiGridService.Lookups.USER_FULL_NAMES == $scope.config.organizations.columnDefs[i].lookup) {
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
                gridAddressHelper.addDeleteButton(config.addresses.columnDefs, "grid.appScope.deleteRowAddresses(row.entity)");
                gridAddressHelper.setColumnDefs(config.addresses);
                gridAddressHelper.setBasicOptions(config.addresses);
                $q.all([promiseAddressTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.addresses.columnDefs.length; i++) {
                        if (HelperUiGridService.Lookups.ADDRESS_TYPES == $scope.config.addresses.columnDefs[i].lookup) {
                            $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.addressTypes;
                            $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (HelperUiGridService.Lookups.USER_FULL_NAMES == $scope.config.addresses.columnDefs[i].lookup) {
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
                gridAliasHelper.addDeleteButton(config.aliases.columnDefs, "grid.appScope.deleteRowAliases(row.entity)");
                gridAliasHelper.setColumnDefs(config.aliases);
                gridAliasHelper.setBasicOptions(config.aliases);
                $q.all([promiseAliasTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.aliases.columnDefs.length; i++) {
                        if (HelperUiGridService.Lookups.ALIAS_TYPES == $scope.config.aliases.columnDefs[i].lookup) {
                            $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.aliasTypes;
                            $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (HelperUiGridService.Lookups.USER_FULL_NAMES == $scope.config.aliases.columnDefs[i].lookup) {
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
                gridSecurityTagHelper.addDeleteButton(config.securityTags.columnDefs, "grid.appScope.deleteRowSecurityTags(row.entity)");
                gridSecurityTagHelper.setColumnDefs(config.securityTags);
                gridSecurityTagHelper.setBasicOptions(config.securityTags);
                $q.all([promiseSecurityTagTypes, promiseUsers]).then(function (data) {
                    for (var i = 0; i < $scope.config.securityTags.columnDefs.length; i++) {
                        if (HelperUiGridService.Lookups.SECURITY_TAG_TYPES == $scope.config.securityTags.columnDefs[i].lookup) {
                            $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.securityTagTypes;
                            $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                        } else if (HelperUiGridService.Lookups.USER_FULL_NAMES == $scope.config.securityTags.columnDefs[i].lookup) {
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
                columnDef.cellTemplate = "<a ng-click='grid.appScope.expand(\"contactMethods\", row)' title='" + $translate.instant("complaints.comp.people.contactMethods.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-phone'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"organizations\", row)' title='" + $translate.instant("complaints.comp.people.organizations.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-cubes'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"addresses\", row)' title='" + $translate.instant("complaints.comp.people.addresses.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-map-marker'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"aliases\", row)' title='" + $translate.instant("complaints.comp.people.aliases.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-users'></i></a>"
                    + "<a ng-click='grid.appScope.expand(\"securityTags\", row)' title='" + $translate.instant("complaints.comp.people.securityTags.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-shield'></i></a>"
                ;
            }
        };

        $scope.expand = function (subGrid, row) {
            if (row.entity.currentSubGrid == subGrid) {
                $scope.gridApi.expandable.toggleRowExpansion(row.entity);

            } else {
                row.entity.currentSubGrid = subGrid;
                if (!row.isExpanded) {
                    $scope.gridApi.expandable.toggleRowExpansion(row.entity);
                }
            }
        };

        var updateGridData = function (data) {
            $q.all([promiseUsers, promisePersonTypes, promiseContactMethodTypes, promiseOrganizationTypes, promiseAddressTypes, promiseAliasTypes, promiseSecurityTagTypes, promiseConfig]).then(function () {
                $scope.complaintInfo = data;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.complaintInfo.personAssociations;
                gridHelper.hidePagingControlsIfAllDataShown($scope.complaintInfo.personAssociations.length);

                for (var i = 0; i < $scope.complaintInfo.personAssociations.length; i++) {
                    var personAssociation = $scope.complaintInfo.personAssociations[i];

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
                    _.each(personAssociation.acm$_contactMethods.gridOptions.data, function (item) {
                        item.acm$_paId = personAssociation.id;
                    });
                    gridContactMethodHelper.hidePagingControlsIfAllDataShown(personAssociation.acm$_contactMethods.gridOptions.data.length);


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
                    _.each(personAssociation.acm$_organizations.gridOptions.data, function (item) {
                        item.acm$_paId = personAssociation.id;
                    });
                    gridOrganizationHelper.hidePagingControlsIfAllDataShown(personAssociation.acm$_organizations.gridOptions.data.length);


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
                    _.each(personAssociation.acm$_addresses.gridOptions.data, function (item) {
                        item.acm$_paId = personAssociation.id;
                    });
                    gridAddressHelper.hidePagingControlsIfAllDataShown(personAssociation.acm$_addresses.gridOptions.data.length);


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
                    _.each(personAssociation.acm$_aliases.gridOptions.data, function (item) {
                        item.acm$_paId = personAssociation.id;
                    });
                    gridAliasHelper.hidePagingControlsIfAllDataShown(personAssociation.acm$_aliases.gridOptions.data.length);


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
                    _.each(personAssociation.acm$_securityTags.gridOptions.data, function (item) {
                        item.acm$_paId = personAssociation.id;
                    });
                    gridSecurityTagHelper.hidePagingControlsIfAllDataShown(personAssociation.acm$_securityTags.gridOptions.data.length);
                }
            }); //end $q
        };
        $scope.$on('complaint-updated', function (e, data) {
            if (!ComplaintInfoService.validateComplaintInfo(data)) {
                return;
            }

            if (data.complaintId == $stateParams.id) {
                updateGridData(data);
            } else {                      // condition when data comes before state is routed and config is not set
                var deferPeopleData = new Store.Variable("deferComplaintPeopleData");
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
                pa.parentId = $scope.complaintInfo.complaintId;
                pa.parentType = ObjectService.ObjectTypes.CASE_FILE;
                pa.person.className = Util.goodValue($scope.config.className); //"com.armedia.acm.plugins.person.model.Person";
                pa.person.givenName = givenName;
                pa.person.familyName = familyName;
                ObjectPersonService.addPersonAssociation(pa).then(
                    function (personAssociationAdded) {
                        rowEntity = _.merge(rowEntity, personAssociationAdded);
                        return personAssociationAdded;
                    }
                );

                //
                // update person association
                //
            } else {
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                    function (complaintSaved) {
                        return complaintSaved;
                    }
                    , function (error) {
                        return error;
                    }
                );
            }
        };
        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

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
            }

        };
        $scope.addNewContactMethods = function (rowParent) {
            var entityId = Util.goodMapValue(rowParent.entity, "id", 0);
            var idxPa = _.findIndex($scope.gridOptions.data, function (pa) {
                return Util.compare(pa.id, entityId);
            });

            if (0 <= idxPa) {
                if (Util.goodMapValue($scope.gridOptions.data, "[" + idxPa + "].acm$_contactMethods.gridApi", false)) {
                    var gridApi = $scope.gridOptions.data[idx].acm$_contactMethods.gridApi;
                    var lastPage = gridApi.pagination.getTotalPages();
                    gridApi.pagination.seek(lastPage);
                    $scope.gridOptions.data[idx].acm$_contactMethods.gridOptions.data.push({});
                }
            }
        };
        $scope.updateRowContactMethods = function (personAssociation, rowEntity) {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                function (complaintSaved) {
                    if (Util.isEmpty(rowEntity.id)) {
                        var personAssociationsSaved = Util.goodMapValue(complaintSaved, "personAssociations", []);
                        var personAssociationSaved = _.find(personAssociationsSaved, {id: personAssociation.id});
                        if (personAssociationSaved) {
                            var contactMethodSaved = _.find(personAssociationSaved.person.contactMethods, {id: rowEntity.id});
                            if (contactMethodSaved) {
                                rowEntity = _.merge(rowEntity, contactMethodSaved);
                            }
                        }
                    }
                    return complaintSaved;
                }
            );
        };
        $scope.deleteRowContactMethods = function (rowEntity) {
            var idxPa = _.findIndex($scope.gridOptions.data, function (pa) {
                return (pa.id == rowEntity.acm$_paId);
            });
            if (0 <= idxPa) {
                var entityId = Util.goodMapValue(rowEntity, "id", 0);
                var gridData = $scope.gridOptions.data[idxPa].acm$_contactMethods.gridOptions.data;
                var idx = _.findIndex(gridData, function (obj) {
                    return Util.compare(obj.id, entityId);
                });
                if (0 <= idx) {
                    gridData.splice(idx, 1);
                }

                if (0 < entityId) {    //do not need to save for deleting a new row
                    var complaintInfo = Util.omitNg($scope.complaintInfo);
                    ComplaintInfoService.saveComplaintInfo(complaintInfo);
                }
            }
        };
        //$scope.deleteRowContactMethods = function (rowEntity) {
        //    gridContactMethodHelper.deleteRow(rowEntity);
        //
        //    var id = Util.goodMapValue(rowEntity, "id", 0);
        //    if (0 < id) {    //do not need to save for deleting a new row
        //        var complaintInfo = Util.omitNg($scope.complaintInfo);
        //        ComplaintInfoService.saveComplaintInfo(complaintInfo);
        //    }
        //};
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
            gridOrganizationHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
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
            gridAddressHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
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
            gridAliasHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
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
            gridSecurityTagHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to save for deleting a new row
                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
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
