'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', '$stateParams', '$q', '$translate', 'UtilService', 'ValidationService', 'CasesService', 'LookupService',
    function ($scope, $stateParams, $q, $translate, Util, Validator, CasesService, LookupService) {
        $scope.$emit('req-component-config', 'people');


        var promiseUsers = Util.servicePromise({
            service: LookupService.getUsers
            , callback: function (data) {
                $scope.userFullNames = [];
                var arr = Util.goodArray(data);
                for (var i = 0; i < arr.length; i++) {
                    var obj = Util.goodJsonObj(arr[i]);
                    if (obj) {
                        var user = {};
                        user.id = Util.goodValue(obj.object_id_s);
                        user.name = Util.goodValue(obj.name);
                        $scope.userFullNames.push(user);
                    }
                }
                return $scope.userFullNames;
            }
        });
        var promisePersonTypes = Util.servicePromise({
            service: LookupService.getPersonTypes
            , callback: function (data) {
                $scope.personTypes = [];
                _.forEach(data, function (v, k) {
                    $scope.personTypes.push({type: v, name: v});
                });
                return $scope.personTypes;
            }
        });
        var promiseContactMethodTypes = Util.servicePromise({
            service: LookupService.getContactMethodTypes
            , callback: function (data) {
                $scope.contactMethodTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.contactMethodTypes.push({type: k, name: v});
                });
                return $scope.contactMethodTypes;
            }
        });
        var promiseOrganizationTypes = Util.servicePromise({
            service: LookupService.getOrganizationTypes
            , callback: function (data) {
                $scope.organizationTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.organizationTypes.push({type: k, name: v});
                });
                return $scope.organizationTypes;
            }
        });
        var promiseAddressTypes = Util.servicePromise({
            service: LookupService.getAddressTypes
            , callback: function (data) {
                $scope.addressTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.addressTypes.push({type: k, name: v});
                });
                return $scope.addressTypes;
            }
        });
        var promiseAliasTypes = Util.servicePromise({
            service: LookupService.getAliasTypes
            , callback: function (data) {
                $scope.aliasTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.aliasTypes.push({type: k, name: v});
                });
                return $scope.aliasTypes;
            }
        });
        var promiseSecurityTagTypes = Util.servicePromise({
            service: LookupService.getSecurityTagTypes
            , callback: function (data) {
                $scope.securityTagTypes = [];
                Util.forEachStripNg(data, function (v, k) {
                    $scope.securityTagTypes.push({type: k, name: v});
                });
                return $scope.securityTagTypes;
            }
        });


        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'people' && Util.goodMapValue([config, "columnDefs", "[0]"], false)) {

                //
                // People grid
                //
                $scope.config = config;
                gridAddEntityButtons(config.columnDefs);
                gridAddDeleteButtons(config.columnDefs, "grid.appScope.deleteRow(row.entity)");
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

                    expandableRowTemplate: 'modules/cases/views/components/case-people.sub.view.html',
                    expandableRowHeight: 305,
                    expandableRowScope: {                            //from sample. what is it for?
                        subGridVariable: 'subGridScopeVariable'
                    },


                    onRegisterApi: function (gridApi) {
                        $scope.gridApi = gridApi;

                        gridApi.core.on.rowsRendered($scope, function () {
                            $scope.gridApi.grid.columns[0].hideColumn();
                        });


                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }

                            if (!Util.isEmpty(rowEntity.personType)
                                && Util.goodMapValue([rowEntity, "person", "givenName"], false)
                                && Util.goodMapValue([rowEntity, "person", "familyName"], false)
                            ) {
                                $scope.updateRow(rowEntity);
                            }
                        });
                    }
                };


                promisePersonTypes.then(function (data) {
                    $scope.gridOptions.enableRowSelection = false;
                    for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                        if ("personTypes" == $scope.config.columnDefs[i].lookup) {
                            $scope.gridOptions.columnDefs[i].enableCellEdit = true;
                            $scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                            $scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                            $scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                            $scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.personTypes;
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";
                        }
                    }
                });


                //
                //ContactMethods grid
                //
                if (Util.goodMapValue([config, "contactMethods", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.contactMethods.columnDefs, "grid.appScope.deleteRowContactMethods(row.entity)");
                    $scope.contactMethods = {};
                    $scope.contactMethods.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect: false,

                        paginationPageSizes: config.contactMethods.paginationPageSizes,
                        paginationPageSize: config.contactMethods.paginationPageSize,
                        enableFiltering: config.contactMethods.enableFiltering,
                        columnDefs: config.contactMethods.columnDefs,
                        appScopeProvider: $scope, //$scope.subGridScope,

                        //onRegisterApi: function(gridApi) {
                        //    $scope.contactMethods.gridApi = gridApi;
                        //    gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
                        //        //
                        //        //Insert code here to save data to service
                        //        //
                        //        var a = 1;
                        //        console.log("contactMethods afterCellEdit, newValue=" + newValue);
                        //    });
                        //}
                    };

                    $q.all([promiseContactMethodTypes, promiseUsers]).then(function (data) {
                        for (var i = 0; i < $scope.config.contactMethods.columnDefs.length; i++) {
                            if ("contactMethodTypes" == $scope.config.contactMethods.columnDefs[i].lookup) {
                                var a1 = $scope.contactMethods;
                                var a2 = $scope.contactMethods.gridOptions;

                                $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.contactMethodTypes;
                                $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.contactMethods.columnDefs[i].lookup) {
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


                //
                //Organizations grid
                //
                if (Util.goodMapValue([config, "organizations", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.organizations.columnDefs, "grid.appScope.deleteRowOrganizations(row.entity)");
                    $scope.organizations = {};
                    $scope.organizations.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect: false,

                        paginationPageSizes: config.organizations.paginationPageSizes,
                        paginationPageSize: config.organizations.paginationPageSize,
                        enableFiltering: config.organizations.enableFiltering,
                        columnDefs: config.organizations.columnDefs,
                        appScopeProvider: $scope,
                    };

                    $q.all([promiseOrganizationTypes, promiseUsers]).then(function (data) {
                        for (var i = 0; i < $scope.config.organizations.columnDefs.length; i++) {
                            if ("organizationTypes" == $scope.config.organizations.columnDefs[i].lookup) {
                                $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.organizationTypes;
                                $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.organizations.columnDefs[i].lookup) {
                                $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }


                //
                //Addresses grid
                //
                if (Util.goodMapValue([config, "addresses", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.addresses.columnDefs, "grid.appScope.deleteRowAddresses(row.entity)");
                    $scope.addresses = {};
                    $scope.addresses.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect: false,

                        paginationPageSizes: config.addresses.paginationPageSizes,
                        paginationPageSize: config.addresses.paginationPageSize,
                        enableFiltering: config.addresses.enableFiltering,
                        columnDefs: config.addresses.columnDefs,
                        appScopeProvider: $scope,
                    };

                    $q.all([promiseAddressTypes, promiseUsers]).then(function (data) {
                        for (var i = 0; i < $scope.config.addresses.columnDefs.length; i++) {
                            if ("addressTypes" == $scope.config.addresses.columnDefs[i].lookup) {
                                $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.addressTypes;
                                $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.addresses.columnDefs[i].lookup) {
                                $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }


                //
                //Aliases grid
                //
                if (Util.goodMapValue([config, "aliases", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.aliases.columnDefs, "grid.appScope.deleteRowAliases(row.entity)");
                    $scope.aliases = {};
                    $scope.aliases.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect: false,

                        paginationPageSizes: config.aliases.paginationPageSizes,
                        paginationPageSize: config.aliases.paginationPageSize,
                        enableFiltering: config.aliases.enableFiltering,
                        columnDefs: config.aliases.columnDefs,
                        appScopeProvider: $scope, //$scope.subGridScope,
                    };

                    $q.all([promiseAliasTypes, promiseUsers]).then(function (data) {
                        for (var i = 0; i < $scope.config.aliases.columnDefs.length; i++) {
                            if ("aliasTypes" == $scope.config.aliases.columnDefs[i].lookup) {
                                $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.aliasTypes;
                                $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.aliases.columnDefs[i].lookup) {
                                $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }


                //
                //SecurityTags grid
                //
                if (Util.goodMapValue([config, "securityTags", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.securityTags.columnDefs, "grid.appScope.deleteRowSecurityTags(row.entity)");
                    $scope.securityTags = {};
                    $scope.securityTags.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect: false,

                        paginationPageSizes: config.securityTags.paginationPageSizes,
                        paginationPageSize: config.securityTags.paginationPageSize,
                        enableFiltering: config.securityTags.enableFiltering,
                        columnDefs: config.securityTags.columnDefs,
                        appScopeProvider: $scope, //$scope.subGridScope,
                    };

                    $q.all([promiseSecurityTagTypes, promiseUsers]).then(function (data) {
                        for (var i = 0; i < $scope.config.securityTags.columnDefs.length; i++) {
                            if ("securityTagTypes" == $scope.config.securityTags.columnDefs[i].lookup) {
                                $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.securityTagTypes;
                                $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.securityTags.columnDefs[i].lookup) {
                                $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }

            }
        }

        var gridAddEntityButtons = function (columnDefs) {
            if ("entity" == Util.goodMapValue([columnDefs, "[0]", "name"])) {
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
        var gridAddDeleteButtons = function (columnDefs, onClickDelete) {
            var columnDef = {
                name: "act"
                , cellEditableCondition: false
                //,enableFiltering: false
                //,enableHiding: false
                //,enableSorting: false                                 onClickDelete
                //,enableColumnResizing: false
                , width: 40
                , headerCellTemplate: "<span></span>"
                , cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='" + onClickDelete + "'></i></span>"
            };
            columnDefs.push(columnDef);
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

        /* for testing purpose
         $scope.$on('xxxx_for_test_xxxx_case-retrieved', function(e, data){
         var personAssociations = Util.goodMapValue([data, "personAssociations"], []);
         //$scope.gridOptions.data = personAssociations;
         $scope.gridOptions.data = [{assocId: 101, personType: "Witness", person: {givenName: "John", familyName: "Doe"}}
         ,{assocId: 102, personType: "Witness", person: {givenName: "John2", familyName: "Doe"}}
         ,{assocId: 103, personType: "Witness", person: {givenName: "John3", familyName: "Doe"}}
         ,{assocId: 104, personType: "Witness", person: {givenName: "John4", familyName: "Doe"}}
         ,{assocId: 105, personType: "Witness", person: {givenName: "John5", familyName: "Doe"}}
         ];
         for (var i = 0; i < $scope.gridOptions.data.length; i++) {
         var personAssociation = $scope.gridOptions.data[i];
         //for (var i = 0; i < personAssociations.length; i++) {
         //    var personAssociation = personAssociations[i];

         //var organizations = personAssociation.person.contactMethods;
         //personAssociation.acm$_contactMethods.gridOptions = _.cloneDeep(Util.goodValue($scope.contactMethods.gridOptions, {columnDefs: [], data: []}));
         personAssociation.acm$_contactMethods.gridOptions = Util.goodValue($scope.contactMethods.gridOptions, {columnDefs: [], data: []});
         personAssociation.acm$_contactMethods.gridOptions.onRegisterApi = function(gridApi) {
         personAssociation.acm$_contactMethods.gridApi = gridApi;
         gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
         onContactMethodChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
         });
         }
         personAssociation.acm$_contactMethods.gridOptions.data = [{type: "Home phone", value: "703-555-5555", created: "09/03/05", creator: "Ant"}
         ,{type: "Email", value: "abc@some.com", created: "08/03/05", creator: "ann-acm"}
         ,{type: "Email", value: "abc2@some.com", created: "08/02/05", creator: "ann-acm"}
         ,{type: "Email", value: "abc3@some.com", created: "08/02/05", creator: "ann-acm"}
         ];

         //var organizations = personAssociation.person.contactMethods;
         //,id      : Acm.goodValue(organizations[i].organizationId, 0)
         //,type    : Acm.goodValue(organizations[i].organizationType)
         //,value   : Acm.goodValue(organizations[i].organizationValue)
         personAssociation.acm$_organizations.gridOptions = Util.goodValue($scope.organizations.gridOptions, {columnDefs: [], data: []});
         personAssociation.acm$_organizations.gridOptions.onRegisterApi = function(gridApi) {
         personAssociation.acm$_organizations.gridApi = gridApi;
         gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
         onContactMethodChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
         });
         }
         personAssociation.acm$_organizations.gridOptions.data = [{type: "Government", value: "IRS"+i, created: "09/03/05", creator: "Ant"}
         ,{type: "Non-profit", value: "Red Cross" + i, created: "08/03/05", creator: "Lee"}
         ];


         //var addresses = personAssociation.person.addresses;
         //,streetAddress : Acm.goodValue(addresses[i].streetAddress)
         //,city          : Acm.goodValue(addresses[i].city)
         //,state         : Acm.goodValue(addresses[i].state)
         //,zip           : Acm.goodValue(addresses[i].zip)
         //,country       : Acm.goodValue(addresses[i].country)


         //var personAliases = personAssociation.person.personAliases;
         //,id      : Acm.goodValue(personAliases[i].id, 0)
         //,type    : Acm.goodValue(personAliases[i].aliasType)
         //,value   : Acm.goodValue(personAliases[i].aliasValue)
         //,created : Acm.getDateFromDatetime(personAliases[i].created,$.t("common:date.short"))
         //,creator : App.Model.Users.getUserFullName(Acm.goodValue(personAliases[i].creator))


         //var securityTags = personAssociation.person.securityTags;
         }
         });
         */
        $scope.addNew = function () {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
        };
        $scope.updateRow = function (rowEntity) {
            var givenName = Util.goodMapValue([rowEntity, "person", "givenName"]);
            var familyName = Util.goodMapValue([rowEntity, "person", "familyName"]);

            //
            // add new person association
            //
            if (Util.isEmpty(rowEntity.id)) {
                var pa = newPersonAssociation();
                pa.parentId = $scope.caseInfo.id;
                pa.parentType = "CASE_FILE";
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
            var idx = _.findIndex($scope.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            //var personAssociationId = 0;
            if (0 <= idx) {
                //var personAssociationId = $scope.gridOptions.data[idx].id;
                $scope.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "acm$_contactMethods", "gridApi"], false)) {
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
                            var personAssociationsSaved = Util.goodMapValue([caseSaved, "personAssociations"], []);
                            var personAssociationSaved = _.where(personAssociationsSaved, {id: personAssociation.id});
                            var contactMethodSaved = _.where(personAssociationSaved.person.contactMethods, {id: rowEntity.id});
                            if (0 < contactMethodSaved.length) {
                                rowEntity = _.merge(rowEntity, contactMethodSaved[0]);
                            }
                        }
                    }
                }
                , function (errorData) {
                }
            );
        };
        $scope.deleteRowContactMethods = function (rowEntity) {
            var idx = _.findIndex($scope.contactMethods.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.contactMethods.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "acm$_organizations.gridOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_organizations.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_organizations.gridOptions.data.push({});
            }
        }
        $scope.updateRowOrganizations = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowOrganizations = function (rowEntity) {
            var idx = _.findIndex($scope.organizations.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.organizations.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "acm$_addresses.gridOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_addresses.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_addresses.gridOptions.data.push({});
            }
        }
        $scope.updateRowAddresses = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowAddresses = function (rowEntity) {
            var idx = _.findIndex($scope.addresses.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.addresses.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "acm$_aliases.gridOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_aliases.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_aliases.gridOptions.data.push({});
            }
        }
        $scope.updateRowAliases = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowAliases = function (rowEntity) {
            var idx = _.findIndex($scope.aliases.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.aliases.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "acm$_securityTags.gridOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].acm$_securityTags.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].acm$_securityTags.gridOptions.data.push({});
            }
        }
        $scope.updateRowSecurityTags = function (personAssociation, rowEntity) {
        };
        $scope.deleteRowSecurityTags = function (rowEntity) {
            var idx = _.findIndex($scope.securityTags.gridOptions.data, function (obj) {
                return (obj == rowEntity);
            });
            if (0 <= idx) {
                $scope.securityTags.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([rowEntity, "id"], 0);
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
