'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', '$stateParams', '$q', '$translate', 'UtilService', 'CasesService', 'LookupService',
	function($scope, $stateParams, $q, $translate, Util, CasesService, LookupService) {
		$scope.$emit('req-component-config', 'people');

		$scope.config = null;
		$scope.$on('component-config', applyConfig);
		function applyConfig(e, componentId, config) {
			if (componentId == 'people' && Util.goodMapValue([config, "columnDefs", "[0]"], false)) {

                //
                // People grid
                //
                $scope.config = config;
                gridAddEntityButtons(config.columnDefs);
                gridAddDeleteButtons(config.columnDefs, "grid.appScope.deleteRow(row)");
                $scope.gridOptions = {
					enableColumnResizing: true,
					enableRowSelection: true,
					enableRowHeaderSelection: false,
					multiSelect: false,
					noUnselect : false,

					paginationPageSizes: config.paginationPageSizes,
					paginationPageSize: config.paginationPageSize,
					enableFiltering: config.enableFiltering,
					columnDefs: config.columnDefs,

                    expandableRowTemplate: 'modules/cases/views/components/case-people.sub.view.html',
                    expandableRowHeight: 305,
                    expandableRowScope: {                            //from sample. what is it for?
                        subGridVariable: 'subGridScopeVariable'
                    },


					onRegisterApi: function(gridApi) {
						$scope.gridApi = gridApi;

                        gridApi.core.on.rowsRendered($scope, function() {
                            $scope.gridApi.grid.columns[0].hideColumn();
                        });


						gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
							//
							//Insert code here to save data to service
							//
                            var a0 = 0;
                            console.log("People afterCellEdit, newValue=" + newValue);

						});
					}
				};


				var promiseTypes = Util.servicePromise({
					service: LookupService.getPersonTypes
					,callback: function(data){
						$scope.personTypes = [];
						_.forEach(data, function(v, k) {
							$scope.personTypes.push({type: v, name: v});
						});
						return $scope.personTypes;
					}
				});
                promiseTypes.then(function(data) {
					$scope.gridOptions.enableRowSelection = false;
					for (var i = 0; i < $scope.config.columnDefs.length; i++) {
						if ("personTypes" == $scope.config.columnDefs[i].lookup) {
							$scope.gridOptions.columnDefs[i].enableCellEdit = true;
							$scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
							$scope.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
							$scope.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
							$scope.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.personTypes;
                            $scope.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'type':'name'";
						}
					}
				});

                var promiseUsers = Util.servicePromise({
                    service: LookupService.getUsers
                    ,callback: function(data){
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


                //
                //ContactMethods grid
                //
                if (Util.goodMapValue([config, "contactMethods", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.contactMethods.columnDefs, "grid.appScope.deleteRowContactMethods(row)");
                    $scope.contactMethods = {};
                    $scope.contactMethods.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect : false,

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


                    var promiseTypes = Util.servicePromise({
                        service: LookupService.getContactMethodTypes
                        ,callback: function(data){
                            $scope.contactMethodTypes = [];
                            Util.forEachTypical(data, function(v, k) {
                                $scope.contactMethodTypes.push({type: k, name: v});
                            });
                            return $scope.contactMethodTypes;
                        }
                    });

                    $q.all([promiseTypes, promiseUsers]).then(function(data) {
                        for (var i = 0; i < $scope.config.contactMethods.columnDefs.length; i++) {
                            if ("contactMethodTypes" == $scope.config.contactMethods.columnDefs[i].lookup) {
                                var a1 = $scope.contactMethods;
                                var a2 = $scope.contactMethods.gridOptions;

                                $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.contactMethodTypes;
                                $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.contactMethods.columnDefs[i].lookup) {
                                $scope.contactMethods.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.contactMethods.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.contactMethods.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.contactMethods.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'id':'name'";
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
                    gridAddDeleteButtons(config.organizations.columnDefs, "grid.appScope.deleteRowOrganizations(row)");
                    $scope.organizations = {};
                    $scope.organizations.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect : false,

                        paginationPageSizes: config.organizations.paginationPageSizes,
                        paginationPageSize: config.organizations.paginationPageSize,
                        enableFiltering: config.organizations.enableFiltering,
                        columnDefs: config.organizations.columnDefs,
                        appScopeProvider: $scope,
                    };


                    var promiseTypes = Util.servicePromise({
                        service: LookupService.getOrganizationTypes
                        ,callback: function(data){
                            $scope.organizationTypes = [];
                            Util.forEachTypical(data, function(v, k) {
                                $scope.organizationTypes.push({type: k, name: v});
                            });
                            return $scope.organizationTypes;
                        }
                    });

                    $q.all([promiseTypes, promiseUsers]).then(function(data) {
                        for (var i = 0; i < $scope.config.organizations.columnDefs.length; i++) {
                            if ("organizationTypes" == $scope.config.organizations.columnDefs[i].lookup) {
                                $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.organizationTypes;
                                $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.organizations.columnDefs[i].lookup) {
                                $scope.organizations.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.organizations.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.organizations.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.organizations.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }


                //
                //Addresses grid
                //
                if (Util.goodMapValue([config, "addresses", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.addresses.columnDefs, "grid.appScope.deleteRowAddresses(row)");
                    $scope.addresses = {};
                    $scope.addresses.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect : false,

                        paginationPageSizes: config.addresses.paginationPageSizes,
                        paginationPageSize: config.addresses.paginationPageSize,
                        enableFiltering: config.addresses.enableFiltering,
                        columnDefs: config.addresses.columnDefs,
                        appScopeProvider: $scope,
                    };


                    var promiseTypes = Util.servicePromise({
                        service: LookupService.getAddressTypes
                        ,callback: function(data){
                            $scope.addressTypes = [];
                            Util.forEachTypical(data, function(v, k) {
                                $scope.addressTypes.push({type: k, name: v});
                            });
                            return $scope.addressTypes;
                        }
                    });

                    $q.all([promiseTypes, promiseUsers]).then(function(data) {
                        for (var i = 0; i < $scope.config.addresses.columnDefs.length; i++) {
                            if ("addressTypes" == $scope.config.addresses.columnDefs[i].lookup) {
                                $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.addressTypes;
                                $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.addresses.columnDefs[i].lookup) {
                                $scope.addresses.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.addresses.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.addresses.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.addresses.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }


                //
                //Aliases grid
                //
                if (Util.goodMapValue([config, "aliases", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.aliases.columnDefs, "grid.appScope.deleteRowAliases(row)");
                    $scope.aliases = {};
                    $scope.aliases.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect : false,

                        paginationPageSizes: config.aliases.paginationPageSizes,
                        paginationPageSize: config.aliases.paginationPageSize,
                        enableFiltering: config.aliases.enableFiltering,
                        columnDefs: config.aliases.columnDefs,
                        appScopeProvider: $scope, //$scope.subGridScope,
                    };


                    var promiseTypes = Util.servicePromise({
                        service: LookupService.getAliasTypes
                        ,callback: function(data){
                            $scope.aliasTypes = [];
                            Util.forEachTypical(data, function(v, k) {
                                $scope.aliasTypes.push({type: k, name: v});
                            });
                            return $scope.aliasTypes;
                        }
                    });

                    $q.all([promiseTypes, promiseUsers]).then(function(data) {
                        for (var i = 0; i < $scope.config.aliases.columnDefs.length; i++) {
                            if ("aliasTypes" == $scope.config.aliases.columnDefs[i].lookup) {
                                $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.aliasTypes;
                                $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.aliases.columnDefs[i].lookup) {
                                $scope.aliases.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.aliases.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.aliases.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.aliases.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }


                //
                //SecurityTags grid
                //
                if (Util.goodMapValue([config, "securityTags", "columnDefs", "[0]"], false)) {
                    gridAddDeleteButtons(config.securityTags.columnDefs, "grid.appScope.deleteRowSecurityTags(row)");
                    $scope.securityTags = {};
                    $scope.securityTags.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        multiSelect: false,
                        noUnselect : false,

                        paginationPageSizes: config.securityTags.paginationPageSizes,
                        paginationPageSize: config.securityTags.paginationPageSize,
                        enableFiltering: config.securityTags.enableFiltering,
                        columnDefs: config.securityTags.columnDefs,
                        appScopeProvider: $scope, //$scope.subGridScope,
                    };


                    var promiseTypes = Util.servicePromise({
                        service: LookupService.getSecurityTagTypes
                        ,callback: function(data){
                            $scope.securityTagTypes = [];
                            Util.forEachTypical(data, function(v, k) {
                                $scope.securityTagTypes.push({type: k, name: v});
                            });
                            return $scope.securityTagTypes;
                        }
                    });

                    $q.all([promiseTypes, promiseUsers]).then(function(data) {
                        for (var i = 0; i < $scope.config.securityTags.columnDefs.length; i++) {
                            if ("securityTagTypes" == $scope.config.securityTags.columnDefs[i].lookup) {
                                $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = true;
                                $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownIdLabel = "type";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.securityTagTypes;
                                $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'type':'name'";


                            } else if ("userFullNames" == $scope.config.securityTags.columnDefs[i].lookup) {
                                $scope.securityTags.gridOptions.columnDefs[i].enableCellEdit = false;
                                $scope.securityTags.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownValueLabel = "name";
                                $scope.securityTags.gridOptions.columnDefs[i].editDropdownOptionsArray = $scope.userFullNames;
                                $scope.securityTags.gridOptions.columnDefs[i].cellFilter = "mapIdValue: col.colDef.editDropdownOptionsArray:'id':'name'";
                            }
                        }
                    });
                }

			}
		}

        var gridAddEntityButtons = function(columnDefs) {
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
        var gridAddDeleteButtons = function(columnDefs, onClickDelete) {
            var columnDef = {name: "act"
                ,cellEditableCondition: false
                //,enableFiltering: false
                //,enableHiding: false
                //,enableSorting: false                                 onClickDelete
                //,enableColumnResizing: false
                ,width: 40
                ,headerCellTemplate: "<span></span>"
                ,cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='" + onClickDelete + "'></i></span>"
            };
            columnDefs.push(columnDef);
        }

        $scope.expand = function(subGrid, row) {
            if ($scope.currentSubGrid == subGrid) {
                $scope.gridApi.expandable.toggleRowExpansion(row.entity);

            } else {
                $scope.currentSubGrid = subGrid;
                if (!row.isExpanded) {
                    $scope.gridApi.expandable.toggleRowExpansion(row.entity);
                }
            }
        }

        var onContactMethodChanged = function(personAssociation, rowEntity, colDef, newValue, oldValue){
            //
            //Insert code here to save data to service
            //
            console.log("onContactMethodChanged, newValue=" + newValue);
        };
        var onOrganizationChanged = function(personAssociation, rowEntity, colDef, newValue, oldValue){
            console.log("onOrganizationChanged, newValue=" + newValue);
        };
        var onAddressChanged = function(personAssociation, rowEntity, colDef, newValue, oldValue){
            console.log("onAddressChanged, newValue=" + newValue);
        };
        var onAliasChanged = function(personAssociation, rowEntity, colDef, newValue, oldValue){
            console.log("onAliasChanged, newValue=" + newValue);
        };
        var onSecurityTagChanged = function(personAssociation, rowEntity, colDef, newValue, oldValue){
            console.log("onSecurityTagChanged, newValue=" + newValue);
        };

		$scope.$on('case-retrieved', function(e, data){
            var personAssociations = Util.goodMapValue([data, "personAssociations"], []);
            $scope.gridOptions.data = personAssociations;

            for (var i = 0; i < personAssociations.length; i++) {
                var personAssociation = personAssociations[i];

                personAssociation.contactMethodsOptions = Util.goodValue($scope.contactMethods.gridOptions, {columnDefs: [], data: []});
                personAssociation.contactMethodsOptions.onRegisterApi = function(gridApi) {
                    personAssociation.contactMethodsOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onContactMethodChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.contactMethodsOptions.data = personAssociation.person.contactMethods;


                personAssociation.organizationsOptions = Util.goodValue($scope.organizations.gridOptions, {columnDefs: [], data: []});
                personAssociation.organizationsOptions.onRegisterApi = function(gridApi) {
                    personAssociation.organizationsOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onOrganizationChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.organizationsOptions.data = personAssociation.person.organizations;



                personAssociation.addressesOptions = Util.goodValue($scope.addresses.gridOptions, {columnDefs: [], data: []});
                personAssociation.addressesOptions.onRegisterApi = function(gridApi) {
                    personAssociation.addressesOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onAddressChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.addressesOptions.data = personAssociation.person.addresses;



                personAssociation.aliasesOptions = Util.goodValue($scope.aliases.gridOptions, {columnDefs: [], data: []});
                personAssociation.aliasesOptions.onRegisterApi = function(gridApi) {
                    personAssociation.aliasesOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onAliasChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.aliasesOptions.data = personAssociation.person.personAliases;



                personAssociation.securityTagsOptions = Util.goodValue($scope.securityTags.gridOptions, {columnDefs: [], data: []});
                personAssociation.securityTagsOptions.onRegisterApi = function(gridApi) {
                    personAssociation.securityTagsOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onSecurityTagChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.securityTagsOptions.data = personAssociation.person.securityTags;
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
                //personAssociation.contactMethodsOptions = _.cloneDeep(Util.goodValue($scope.contactMethods.gridOptions, {columnDefs: [], data: []}));
                personAssociation.contactMethodsOptions = Util.goodValue($scope.contactMethods.gridOptions, {columnDefs: [], data: []});
                personAssociation.contactMethodsOptions.onRegisterApi = function(gridApi) {
                    personAssociation.contactMethodsOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onContactMethodChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.contactMethodsOptions.data = [{type: "Home phone", value: "703-555-5555", created: "09/03/05", creator: "Ant"}
                    ,{type: "Email", value: "abc@some.com", created: "08/03/05", creator: "ann-acm"}
                    ,{type: "Email", value: "abc2@some.com", created: "08/02/05", creator: "ann-acm"}
                    ,{type: "Email", value: "abc3@some.com", created: "08/02/05", creator: "ann-acm"}
                ];

                //var organizations = personAssociation.person.contactMethods;
                //,id      : Acm.goodValue(organizations[i].organizationId, 0)
                //,type    : Acm.goodValue(organizations[i].organizationType)
                //,value   : Acm.goodValue(organizations[i].organizationValue)
                personAssociation.organizationsOptions = Util.goodValue($scope.organizations.gridOptions, {columnDefs: [], data: []});
                personAssociation.organizationsOptions.onRegisterApi = function(gridApi) {
                    personAssociation.organizationsOptions.gridApi = gridApi;
                    gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
                        onContactMethodChanged(personAssociation, rowEntity, colDef, newValue, oldValue);
                    });
                }
                personAssociation.organizationsOptions.data = [{type: "Government", value: "IRS"+i, created: "09/03/05", creator: "Ant"}
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
        $scope.addNew = function() {
            var lastPage = $scope.gridApi.pagination.getTotalPages();
            $scope.gridApi.pagination.seek(lastPage);
            $scope.gridOptions.data.push({});
        };
        $scope.deleteRow = function(row) {
            var idx = _.findIndex($scope.gridOptions.data, function(obj) {
                return (obj == row.entity);
            });
            if (0 <= idx) {
                $scope.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([row, "entity", "id"], 0);
            if (0 < id) {    //not deleting a new row
                //
                // save data to server
                //
            }

        };
        $scope.addNewContactMethods = function(rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function(obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "contactMethodsOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].contactMethodsOptions.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].contactMethodsOptions.data.push({});
            }

            var z = 1;
        }
        $scope.deleteRowContactMethods = function(row) {
            var idx = _.findIndex($scope.contactMethods.gridOptions.data, function(obj) {
                return (obj == row.entity);
            });
            if (0 <= idx) {
                $scope.contactMethods.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([row, "entity", "id"], 0);
            if (0 < id) {    //not deleting a new row
                //
                // save data to server
                //
            }
        }
        $scope.addNewOrganizations = function(rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function(obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "organizationsOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].organizationsOptions.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].organizationsOptions.data.push({});
            }
        }
        $scope.deleteRowOrganizations = function(row) {
            var idx = _.findIndex($scope.organizations.gridOptions.data, function(obj) {
                return (obj == row.entity);
            });
            if (0 <= idx) {
                $scope.organizations.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([row, "entity", "id"], 0);
            if (0 < id) {    //not deleting a new row
                //
                // save data to server
                //
            }
        }
        $scope.addNewAddresses = function(rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function(obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "addressesOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].addressesOptions.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].addressesOptions.data.push({});
            }
        }
        $scope.deleteRowAddresses = function(row) {
            var idx = _.findIndex($scope.addresses.gridOptions.data, function(obj) {
                return (obj == row.entity);
            });
            if (0 <= idx) {
                $scope.addresses.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([row, "entity", "id"], 0);
            if (0 < id) {    //not deleting a new row
                //
                // save data to server
                //
            }
        }
        $scope.addNewAliases = function(rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function(obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "aliasesOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].aliasesOptions.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].aliasesOptions.data.push({});
            }
        }
        $scope.deleteRowAliases = function(row) {
            var idx = _.findIndex($scope.aliases.gridOptions.data, function(obj) {
                return (obj == row.entity);
            });
            if (0 <= idx) {
                $scope.aliases.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([row, "entity", "id"], 0);
            if (0 < id) {    //not deleting a new row
                //
                // save data to server
                //
            }
        }
        $scope.addNewSecurityTags = function(rowParent) {
            var idx = _.findIndex($scope.gridOptions.data, function(obj) {
                return (obj == rowParent.entity);
            });
            if (Util.goodMapValue([$scope.gridOptions.data, ("[" + idx + "]"), "securityTagsOptions", "gridApi"], false)) {
                var gridApi = $scope.gridOptions.data[idx].securityTagsOptions.gridApi;
                var lastPage = gridApi.pagination.getTotalPages();
                gridApi.pagination.seek(lastPage);
                $scope.gridOptions.data[idx].securityTagsOptions.data.push({});
            }
        }
        $scope.deleteRowSecurityTags = function(row) {
            var idx = _.findIndex($scope.securityTags.gridOptions.data, function(obj) {
                return (obj == row.entity);
            });
            if (0 <= idx) {
                $scope.securityTags.gridOptions.data.splice(idx, 1);
            }

            var id = Util.goodMapValue([row, "entity", "id"], 0);
            if (0 < id) {    //not deleting a new row
                //
                // save data to server
                //
            }
        }
	}
])

;
