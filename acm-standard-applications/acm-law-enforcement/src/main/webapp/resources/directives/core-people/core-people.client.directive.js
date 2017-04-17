'use strict';

/**
 * @ngdoc directive
 * @name global.directive:corePeople
 * @restrict E
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/core-people/core-people.client.directive.js directives/core-people/core-people.client.directive.js}
 *
 * The "Core-People" directive add people grid functionality
 *
 * @param {Object} peopleInit object containing data for directive to work
 * @param {string} peopleInit.moduleId string for the id of the module
 * @param {string} peopleInit.componentId string for the id of the component
 * @param {function} peopleInit.retrieveObjectInfo function to retrieve objectInfo
 * @param {function} peopleInit.saveObjectInfo function to save objectInfo
 * @param {string} peopleInit.objectType string for the type of the object
 * @param {string} peopleInit.objectInfoId string for the name of the property representing the id of the object
 * @param {string} peopleInit.peopleTitle string for the title of people directive, can be optional
 *
 * @example
 <example>
 <file name="index.html">
 <core-people people-init="peopleInit"/>
 </file>
 <file name="app.js">
 angular.module('cases').controller('Cases.PeopleController', ['$scope', 'ObjectService', 'Case.InfoService'
 , function ($scope, ObjectService, CaseInfoService) {

        $scope.peopleInit = {
            moduleId: 'cases',
            componentId: 'people',
            retrieveObjectInfo: CaseInfoService.getCaseInfo,
            validateObjectInfo: CaseInfoService.validateCaseInfo,
            saveObjectInfo: CaseInfoService.saveCaseInfo,
            objectType: ObjectService.ObjectTypes.CASE_FILE,
            objectInfoId: 'id'
        }
    }
 ]);
 </file>
 </example>
 */
angular.module('directives').directive('corePeople', ['$stateParams', '$q', '$translate', '$modal', 'UtilService'
    , 'Object.PersonService', 'Object.LookupService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', "Authentication"
    , function ($stateParams, $q, $translate, $modal, Util
        , ObjectPersonService, ObjectLookupService, HelperUiGridService, HelperObjectBrowserService, Authentication) {
        return {
            restrict: 'E',
            scope: {
                peopleInit: '='
            },
            link: function (scope, element, attrs) {

                Authentication.queryUserInfo().then(
                    function (userInfo) {
                        scope.userId = userInfo.userId;
                        return userInfo;
                    }
                );

                new HelperObjectBrowserService.Component({
                    scope: scope
                    , stateParams: $stateParams
                    , moduleId: scope.peopleInit.moduleId
                    , componentId: scope.peopleInit.componentId
                    , retrieveObjectInfo: scope.peopleInit.retrieveObjectInfo
                    , validateObjectInfo: scope.peopleInit.validateObjectInfo
                    , onConfigRetrieved: function (componentConfig) {
                        return onConfigRetrieved(componentConfig);
                    }
                    , onObjectInfoRetrieved: function (objectInfo) {
                        onObjectInfoRetrieved(objectInfo);
                    }
                });
                var gridHelper = new HelperUiGridService.Grid({scope: scope});
                var promiseUsers = gridHelper.getUsers();

                var promisePersonTypes = ObjectLookupService.getPersonTypes().then(
                    function (personTypes) {
                        var options = [];
                        _.forEach(personTypes, function (v, k) {
                            options.push({type: v, name: v});
                        });
                        scope.personTypes = options;
                        return personTypes;
                    });
                var promiseContactMethodTypes = ObjectLookupService.getContactMethodTypes().then(
                    function (contactMethodTypes) {
                        scope.contactMethodTypes = contactMethodTypes;
                        return contactMethodTypes;
                    });
                var promiseOrganizationTypes = ObjectLookupService.getOrganizationTypes().then(
                    function (organizationTypes) {
                        scope.organizationTypes = organizationTypes;
                        return organizationTypes;
                    });
                var promiseAddressTypes = ObjectLookupService.getAddressTypes().then(
                    function (addressTypes) {
                        scope.addressTypes = addressTypes;
                        return addressTypes;
                    });
                var promiseAliasTypes = ObjectLookupService.getAliasTypes().then(
                    function (aliasTypes) {
                        scope.aliasTypes = aliasTypes;
                        return aliasTypes;
                    });

                var onConfigRetrieved = function (config) {
                    if (!scope.peopleInit.peopleTitle)
                        scope.peopleInit.peopleTitle = $translate.instant("common.directive.corePeople.title");
                    scope.config = config;
                    configGridMain(config);
                };

                scope.isInitiator = function (data) {
                    return data.personType == "Initiator";
                };

                var configGridMain = function (config) {
                    gridAddEntityButtons(config.columnDefs);
                    //gridHelper.addEditButton(config.columnDefs, "grid.appScope.editRow(row.entity)");
                    //gridHelper.addDeleteButton(config.columnDefs, "grid.appScope.deleteRow(row.entity)");


                    gridHelper.addButton(config, "edit");
                    gridHelper.addButton(config, "delete", null, null, "isInitiator");

                    gridHelper.setColumnDefs(config);
                    gridHelper.setBasicOptions(config);
                    gridHelper.disableGridScrolling(config);

                    gridHelper.addGridApiHandler(function (gridApi) {
                        gridApi.core.on.rowsRendered(scope, function () {
                            scope.gridApi.grid.columns[0].hideColumn();
                        });
                    });

                    //scope.gridOptions is defined by above setBasicOptions()
                    scope.gridOptions.expandableRowTemplate = "directives/core-people/core-people-sub.client.view.html";
                    scope.gridOptions.expandableRowHeight = 305;
                    scope.gridOptions.expandableRowScope = {       //from sample. what is it for?
                        subGridVariable: 'subGridScopeVariable'
                    };
                };

                var gridAddEntityButtons = function (columnDefs) {
                    if ("entity" == Util.goodMapValue(columnDefs, "[0].name")) {
                        var columnDef = columnDefs[0];
                        columnDef.width = 92;
                        columnDef.headerCellTemplate = "<span></span>";
                        columnDef.cellTemplate = "<a ng-click='grid.appScope.expand(\"contactMethods\", row)' title='" + $translate.instant("common.directive.corePeople.contactMethods.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-phone'></i></a>"
                            + "<a ng-click='grid.appScope.expand(\"organizations\", row)' title='" + $translate.instant("common.directive.corePeople.organizations.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-cubes'></i></a>"
                            + "<a ng-click='grid.appScope.expand(\"addresses\", row)' title='" + $translate.instant("common.directive.corePeople.addresses.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-map-marker'></i></a>"
                            + "<a ng-click='grid.appScope.expand(\"aliases\", row)' title='" + $translate.instant("common.directive.corePeople.aliases.title") + "' class='inline animated btn btn-default btn-xs'><i class='fa fa-users'></i></a>"
                        ;
                    }
                };

                scope.expand = function (subGrid, row) {
                    if (row.entity.acm$_currentSubGrid == subGrid) {
                        scope.gridApi.expandable.toggleRowExpansion(row.entity);

                    } else {
                        row.entity.acm$_currentSubGrid = subGrid;
                        if (!row.isExpanded) {
                            scope.gridApi.expandable.toggleRowExpansion(row.entity);
                        }
                    }
                };

                scope.objectInfoLoaded = false;
                var onObjectInfoRetrieved = function (objectInfo) {
                    scope.objectInfo = objectInfo;
                    
                    if (scope.objectInfo.caseNumber) {
                        scope.parentTitle = scope.objectInfo.caseNumber;
                    } else if (scope.objectInfo.complaintNumber) {
                        scope.parentTitle = scope.objectInfo.complaintNumber;
                    } 

                    if (!scope.objectInfoLoaded) {
                        scope.objectInfoLoaded = true;
                        $q.all([promiseUsers, promisePersonTypes, promiseContactMethodTypes, promiseOrganizationTypes
                            , promiseAddressTypes, promiseAliasTypes]).then(function () {
                            scope.retrieveGridData();
                        }); //end $q
                    }
                };

                scope.retrieveGridData = function () {
                    var data = scope.objectInfo.personAssociations;

                    // to avoid circular references some objects are serialized only as numbers (to point to original
                    // reference id) not full objects
                    data = _.filter(data, function (persAssoc) {
                        return (typeof persAssoc === 'object');
                    });
                    scope.gridOptions = scope.gridOptions || {};
                    scope.gridOptions.data = data;

                    for (var i = 0; i < scope.objectInfo.personAssociations.length; i++) {
                        var pa = scope.objectInfo.personAssociations[i];

                        if (typeof pa === 'object') {
                            pa.acm$_contactMethods = {
                                gridOptions: getGridOptions(configGridContactMethod)
                            };
                            pa.acm$_contactMethods.gridOptions.data = pa.person.contactMethods;
                            _.each(pa.acm$_contactMethods.gridOptions.data, function (item) {
                                item.acm$_paId = pa.id;
                            });
                            gridHelper.setUserNameFilterToConfig(promiseUsers, pa.acm$_contactMethods.gridOptions);

                            pa.acm$_organizations = {
                                gridOptions: getGridOptions(configGridOrganization)
                            };
                            pa.acm$_organizations.gridOptions.data = pa.person.organizations;
                            _.each(pa.acm$_organizations.gridOptions.data, function (item) {
                                item.acm$_paId = pa.id;
                            });
                            gridHelper.setUserNameFilterToConfig(promiseUsers, pa.acm$_organizations.gridOptions);

                            pa.acm$_addresses = {
                                gridOptions: getGridOptions(configGridAddress)
                            };
                            pa.acm$_addresses.gridOptions.data = pa.person.addresses;
                            _.each(pa.acm$_addresses.gridOptions.data, function (item) {
                                item.acm$_paId = pa.id;
                            });
                            gridHelper.setUserNameFilterToConfig(promiseUsers, pa.acm$_addresses.gridOptions);

                            pa.acm$_aliases = {
                                gridOptions: getGridOptions(configGridAlias)
                            };
                            pa.acm$_aliases.gridOptions.data = pa.person.personAliases;
                            _.each(pa.acm$_aliases.gridOptions.data, function (item) {
                                item.acm$_paId = pa.id;
                            });
                            gridHelper.setUserNameFilterToConfig(promiseUsers, pa.acm$_aliases.gridOptions);


                        }
                    }
                };

                //People
                scope.addNew = function () {
                    //
                    // add new person association
                    //
                    var personAssociation = newPersonAssociation();
                    //ObjectInfo in different modules has different name for Id property
                    //we are setting it from controller where directive is used
                    personAssociation.parentId = scope.objectInfo[scope.peopleInit.objectInfoId];
                    personAssociation.parentType = scope.peopleInit.objectType;
                    personAssociation.parentTitle = scope.parentTitle;
                    personAssociation.person.className = Util.goodValue(scope.config.className); //"com.armedia.acm.plugins.person.model.Person";
                    personAssociation.person.givenName = '';
                    personAssociation.person.familyName = '';
                    personAssociation.personType = '';
                    personAssociation.className = Util.goodValue(scope.config.personAssociationClassName); //"com.armedia.acm.plugins.person.model.PersonAssociation"

                    //put personAssociation to scope, we will need it when we return from popup
                    scope.personAssociation = personAssociation;
                    var person = {
                        personType: personAssociation.personType,
                        givenName: personAssociation.person.givenName,
                        familyName: personAssociation.person.familyName,
                        personTypes: scope.personTypes,
                        parentTitle: personAssociation.parentTitle
                    };
                    showModalPeople(person, false);
                };
                scope.editRow = function (rowEntity) {
                    scope.personAssociation = rowEntity;
                    var person = {
                        personType: rowEntity.personType,
                        givenName: angular.copy(rowEntity.person.givenName),
                        familyName: rowEntity.person.familyName,
                        personTypes: scope.personTypes
                    };
                    showModalPeople(person, true);
                };
                scope.deleteRow = function (rowEntity) {
                    gridHelper.deleteRow(rowEntity);
                    var id = Util.goodMapValue(rowEntity, "id", 0);
                    if (0 < id) {    //do not need to save for deleting a new row
                        ObjectPersonService.deletePersonAssociation(id).then(
                            function (personAssociationDeleted) {
                                refresh();
                                return personAssociationDeleted;
                            }
                            , function (error) {
                                return error;
                            }
                        );
                    }

                };

                //Contact Methods
                scope.addNewContactMethods = function (rowParent) {
                    var contactMethod = {};
                    contactMethod.created = Util.dateToIsoString(new Date());
                    contactMethod.creator = scope.userId;

                    //put contactMethod to scope, we will need it when we return from popup
                    scope.contactMethod = contactMethod;
                    var item = {
                        id: '',
                        parentId: rowParent.entity.id,
                        contactMethodType: '',
                        value: '',
                        contactMethodTypes: scope.contactMethodTypes
                    };
                    showModalContactMethods(item, false);

                };
                scope.editRowContactMethods = function (rowEntity) {
                    scope.contactMethod = rowEntity;
                    var item = {
                        id: rowEntity.id,
                        parentId: rowEntity.acm$_paId,
                        contactMethodType: rowEntity.type,
                        value: rowEntity.value,
                        contactMethodTypes: scope.contactMethodTypes
                    };
                    showModalContactMethods(item, true);

                };
                scope.deleteRowContactMethods = function (rowEntity) {
                    var id = Util.goodMapValue(rowEntity, "id", 0);
                    if (0 < id) {    //do not need to save for deleting a new row
                        var idxPa = _.findIndex(scope.objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, rowEntity.acm$_paId);
                        });
                        var personAssociation = scope.objectInfo.personAssociations[idxPa];
                        var idxContact = _.findIndex(personAssociation.person.contactMethods, function (contact) {
                            return Util.compare(contact.id, rowEntity.id);
                        });
                        personAssociation.person.contactMethods.splice(idxContact, 1);
                        saveObjectInfoAndRefresh(personAssociation);
                    }
                };

                //Organizations
                scope.addNewOrganizations = function (rowParent) {
                    var organization = {};
                    organization.created = Util.dateToIsoString(new Date());
                    organization.creator = scope.userId;
                    organization.className = Util.goodValue(scope.config.organizations.className);

                    //put organization to scope, we will need it when we return from popup
                    scope.organization = organization;
                    var item = {
                        id: '',
                        parentId: rowParent.entity.id,
                        organizationType: '',
                        organizationValue: '',
                        organizationTypes: scope.organizationTypes
                    };
                    showModalOrganizations(item, false);

                };
                scope.editRowOrganizations = function (rowEntity) {
                    scope.organization = rowEntity;
                    var item = {
                        organizationId: rowEntity.organizationId,
                        parentId: rowEntity.acm$_paId,
                        organizationType: rowEntity.organizationType,
                        organizationValue: rowEntity.organizationValue,
                        organizationTypes: scope.organizationTypes
                    };
                    showModalOrganizations(item, true);

                };
                scope.deleteRowOrganizations = function (rowEntity) {
                    var id = Util.goodMapValue(rowEntity, "organizationId", 0);
                    if (0 < id) {    //do not need to save for deleting a new row
                        var objectInfo = scope.objectInfo;

                        var idxPa = _.findIndex(objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, rowEntity.acm$_paId);
                        });
                        var personAssociation = objectInfo.personAssociations[idxPa];
                        var idxOrganization = _.findIndex(personAssociation.person.organizations, function (organization) {
                            return Util.compare(organization.organizationId, rowEntity.organizationId);
                        });
                        personAssociation.person.organizations.splice(idxOrganization, 1);
                        saveObjectInfoAndRefresh(personAssociation);
                    }
                };

                //Addresses
                scope.addNewAddresses = function (rowParent) {

                    var address = {};
                    address.created = Util.dateToIsoString(new Date());
                    address.creator = scope.userId;
                    scope.address = address;
                    var item = {
                        id: '',
                        parentId: rowParent.entity.id,
                        addressType: '',
                        addressTypes: scope.addressTypes,
                        streetAddress: '',
                        city: '',
                        state: '',
                        zip: '',
                        country: ''
                    };
                    showModalAddresses(item, false);
                };
                scope.editRowAddresses = function (rowEntity) {
                    scope.address = rowEntity;
                    var item = {
                        id: rowEntity.id,
                        parentId: rowEntity.acm$_paId,
                        addressType: rowEntity.type,
                        addressTypes: scope.addressTypes,
                        streetAddress: rowEntity.streetAddress,
                        city: rowEntity.city,
                        state: rowEntity.state,
                        zip: rowEntity.zip,
                        country: rowEntity.country

                    };
                    showModalAddresses(item, true);
                };
                scope.deleteRowAddresses = function (rowEntity) {
                    var id = Util.goodMapValue(rowEntity, "id", 0);
                    if (0 < id) {    //do not need to save for deleting a new row
                        var objectInfo = scope.objectInfo;

                        var idxPa = _.findIndex(objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, rowEntity.acm$_paId);
                        });
                        var personAssociation = objectInfo.personAssociations[idxPa];
                        var idxAddresses = _.findIndex(personAssociation.person.addresses, function (address) {
                            return Util.compare(address.id, rowEntity.id);
                        });
                        personAssociation.person.addresses.splice(idxAddresses, 1);

                        saveObjectInfoAndRefresh(personAssociation);

                    }
                };

                //Aliases
                scope.addNewAliases = function (rowParent) {
                    var alias = {};
                    alias.created = Util.dateToIsoString(new Date());
                    alias.creator = scope.userId;

                    //put alias to scope, we will need it when we return from popup
                    scope.alias = alias;
                    var item = {
                        id: '',
                        parentId: rowParent.entity.id,
                        aliasType: '',
                        aliasValue: '',
                        aliasTypes: scope.aliasTypes
                    };
                    showModalAliases(item, false);
                };
                scope.editRowAliases = function (rowEntity) {
                    scope.alias = rowEntity;
                    var item = {
                        id: rowEntity.id,
                        parentId: rowEntity.acm$_paId,
                        aliasType: rowEntity.aliasType,
                        aliasValue: rowEntity.aliasValue,
                        aliasTypes: scope.aliasTypes
                    };
                    showModalAliases(item, true);
                };
                scope.deleteRowAliases = function (rowEntity) {
                    var id = Util.goodMapValue(rowEntity, "id", 0);
                    if (0 < id) {    //do not need to save for deleting a new row
                        var objectInfo = scope.objectInfo;

                        var idxPa = _.findIndex(objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, rowEntity.acm$_paId);
                        });
                        var personAssociation = objectInfo.personAssociations[idxPa];
                        var idxAlias = _.findIndex(personAssociation.person.personAliases, function (alias) {
                            return Util.compare(alias.id, rowEntity.id);
                        });
                        personAssociation.person.personAliases.splice(idxAlias, 1);
                        saveObjectInfoAndRefresh(personAssociation);
                    }
                };

                //modals
                var showModalPeople = function (person, isEdit) {
                    var modalScope = scope.$new();
                    modalScope.person = person || {};
                    modalScope.isEdit = isEdit || false;
                    modalScope.isInitiator = person.personType == "Initiator";
                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'directives/core-people/core-people-modal.client.view.html',
                        controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                            $scope.onClickOk = function () {
                                $modalInstance.close({
                                    person: $scope.person,
                                    isEdit: $scope.isEdit
                                });
                            };
                            $scope.onClickCancel = function () {
                                $modalInstance.dismiss('cancel');
                            }
                        }],
                        size: 'sm'
                    });
                    modalInstance.result.then(function (data) {
                        scope.personAssociation.person.givenName = data.person.givenName;
                        scope.personAssociation.person.familyName = data.person.familyName;
                        scope.personAssociation.personType = data.person.personType;
                        scope.personAssociation.parentTitle = data.person.parentTitle;
                        scope.objectInfo.modifier = scope.userId;
                        if (data.isEdit) {
                            var index = _.indexOf(_.pluck(scope.objectInfo.personAssociations, 'id'), scope.personAssociation.id);
                            scope.objectInfo.personAssociations[index] = scope.personAssociation;
                            saveObjectInfoAndRefresh(scope.personAssociation);
                        }
                        else {
                            ObjectPersonService.addPersonAssociation(scope.personAssociation).then(
                                function (personAssociation) {
                                    refresh();
                                    scope.objectInfo.personAssociations.push(personAssociation);
                                    scope.retrieveGridData();
                                }
                            );
                        }
                    });
                };
                var showModalContactMethods = function (contact, isEdit) {
                    var modalScope = scope.$new();
                    modalScope.contact = contact || {};
                    modalScope.isEdit = isEdit || false;

                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'directives/core-people/core-people-contact-methods-modal.client.view.html',
                        controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                            $scope.onClickOk = function () {
                                $modalInstance.close({
                                    contact: $scope.contact,
                                    isEdit: $scope.isEdit
                                });
                            };
                            $scope.onClickCancel = function () {
                                $modalInstance.dismiss('cancel');
                            }
                        }],
                        size: 'sm'
                    });
                    modalInstance.result.then(function (data) {
                        var contactMethod;
                        var personAssociation = _.find(scope.objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, data.contact.parentId);
                        });
                        if (!data.isEdit)
                            contactMethod = scope.contactMethod;
                        else {
                            contactMethod = _.find(personAssociation.person.contactMethods, {id: data.contact.id});
                        }
                        contactMethod.type = data.contact.contactMethodType;
                        contactMethod.value = data.contact.value;
                        if (!data.isEdit) {
                            personAssociation.person.contactMethods.push(contactMethod);
                        }
                        saveObjectInfoAndRefresh(personAssociation);
                    });
                };
                var showModalOrganizations = function (organization, isEdit) {
                    var modalScope = scope.$new();
                    modalScope.config = scope.config;
                    modalScope.organization = organization || {};
                    modalScope.isEdit = isEdit || false;

                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'directives/core-people/core-people-organizations-modal.client.view.html',
                        controller: "Directives.CorePeopleOrganizationsModalController",
                        size: 'sm'
                    });

                    modalInstance.result.then(function (data) {
                            var organization;
                            var isDuplicate = false;
                            var personAssociation = _.find(scope.objectInfo.personAssociations, function (pa) {
                                return Util.compare(pa.id, data.organization.parentId);
                            });
                            if (!data.isEdit) {
                                var duplicateOrganization;
                                duplicateOrganization = _.find(personAssociation.person.organizations, function (organization) {
                                    return Util.compare(organization.organizationId, data.organization.organizationId);
                                });

                                if (duplicateOrganization == undefined) {
                                    organization = scope.organization;
                                    organization.organizationId = data.organization.organizationId;
                                } else {
                                    isDuplicate = true;
                                }
                            }
                            else {
                                organization = _.find(personAssociation.person.organizations, {organizationId: data.organization.organizationId});
                            }

                            if (!isDuplicate) {
                                organization.organizationType = data.organization.organizationType;
                                organization.organizationValue = data.organization.organizationValue;

                                if (!data.isEdit) {
                                    personAssociation.person.organizations.push(organization);
                                }
                                saveObjectInfoAndRefresh(personAssociation);
                            }
                        }
                    );
                };
                var showModalAddresses = function (address, isEdit) {
                    var modalScope = scope.$new();
                    modalScope.address = address || {};
                    modalScope.isEdit = isEdit || false;

                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'directives/core-people/core-people-addresses-modal.client.view.html',
                        controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                            $scope.onClickOk = function () {
                                $modalInstance.close({
                                    address: $scope.address,
                                    isEdit: $scope.isEdit
                                });
                            };
                            $scope.onClickCancel = function () {
                                $modalInstance.dismiss('cancel');
                            }
                        }],
                        size: 'sm'
                    });

                    modalInstance.result.then(function (data) {
                        var address;
                        var personAssociation = _.find(scope.objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, data.address.parentId);
                        });
                        if (!data.isEdit)
                            address = scope.address;
                        else {
                            address = _.find(personAssociation.person.addresses, {id: data.address.id});
                        }
                        address.type = data.address.addressType;
                        address.streetAddress = data.address.streetAddress;
                        address.city = data.address.city;
                        address.state = data.address.state;
                        address.zip = data.address.zip;
                        address.country = data.address.country;
                        if (!data.isEdit) {
                            personAssociation.person.addresses.push(address);
                        }
                        saveObjectInfoAndRefresh(personAssociation);
                    });
                };
                var showModalAliases = function (alias, isEdit) {
                    var modalScope = scope.$new();
                    modalScope.alias = alias || {};
                    modalScope.isEdit = isEdit || false;

                    var modalInstance = $modal.open({
                        scope: modalScope,
                        animation: true,
                        templateUrl: 'directives/core-people/core-people-aliases-modal.client.view.html',
                        controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                            $scope.onClickOk = function () {
                                $modalInstance.close({
                                    alias: $scope.alias,
                                    isEdit: $scope.isEdit
                                });
                            };
                            $scope.onClickCancel = function () {
                                $modalInstance.dismiss('cancel');
                            }
                        }],
                        size: 'sm'
                    });

                    modalInstance.result.then(function (data) {
                        var alias;
                        var personAssociation = _.find(scope.objectInfo.personAssociations, function (pa) {
                            return Util.compare(pa.id, data.alias.parentId);
                        });
                        if (!data.isEdit)
                            alias = scope.alias;
                        else {
                            alias = _.find(personAssociation.person.personAliases, {id: data.alias.id});
                        }
                        alias.aliasType = data.alias.aliasType;
                        alias.aliasValue = data.alias.aliasValue;
                        if (!data.isEdit) {
                            personAssociation.person.personAliases.push(alias);
                        }
                        saveObjectInfoAndRefresh(personAssociation);
                    });
                };

                //config for subgrids
                var configGridContactMethod = function (config, gridContactMethodHelper) {
                    if (Util.goodMapValue(config, "contactMethods.columnDefs[0]", false)) {
                        gridContactMethodHelper.addEditButton(config.contactMethods.columnDefs, "grid.appScope.editRowContactMethods(row.entity)");
                        gridContactMethodHelper.addDeleteButton(config.contactMethods.columnDefs, "grid.appScope.deleteRowContactMethods(row.entity)");
                        gridContactMethodHelper.setColumnDefs(config.contactMethods);
                        gridContactMethodHelper.setBasicOptions(config.contactMethods);
                        gridContactMethodHelper.disableGridScrolling(config.contactMethods);

                        //todo:
                        //filter for create date??
                    }
                };
                var configGridOrganization = function (config, gridOrganizationHelper) {
                    if (Util.goodMapValue(config, "organizations.columnDefs[0]", false)) {
                        gridOrganizationHelper.addEditButton(config.organizations.columnDefs, "grid.appScope.editRowOrganizations(row.entity)");
                        gridOrganizationHelper.addDeleteButton(config.organizations.columnDefs, "grid.appScope.deleteRowOrganizations(row.entity)");
                        gridOrganizationHelper.setColumnDefs(config.organizations);
                        gridOrganizationHelper.setBasicOptions(config.organizations);
                        gridOrganizationHelper.disableGridScrolling(config.organizations);
                    }
                };
                var configGridAddress = function (config, gridAddressHelper) {
                    if (Util.goodMapValue(config, "addresses.columnDefs[0]", false)) {
                        gridAddressHelper.addEditButton(config.addresses.columnDefs, "grid.appScope.editRowAddresses(row.entity)");
                        gridAddressHelper.addDeleteButton(config.addresses.columnDefs, "grid.appScope.deleteRowAddresses(row.entity)");
                        gridAddressHelper.setColumnDefs(config.addresses);
                        gridAddressHelper.setBasicOptions(config.addresses);
                        gridAddressHelper.disableGridScrolling(config.addresses);
                    }
                };
                var configGridAlias = function (config, gridAliasHelper) {
                    if (Util.goodMapValue(config, "aliases.columnDefs[0]", false)) {
                        gridAliasHelper.addEditButton(config.aliases.columnDefs, "grid.appScope.editRowAliases(row.entity)");
                        gridAliasHelper.addDeleteButton(config.aliases.columnDefs, "grid.appScope.deleteRowAliases(row.entity)");
                        gridAliasHelper.setColumnDefs(config.aliases);
                        gridAliasHelper.setBasicOptions(config.aliases);
                        gridAliasHelper.disableGridScrolling(config.aliases);
                    }
                };

                var newPersonAssociation = function () {
                    return {
                        id: null
                        , personType: ""
                        , parentId: null
                        , parentType: ""
                        , parentTitle: ""
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
                };

                var saveObjectInfoAndRefresh = function (pa) {
                    var personAssociation = Util.omitNg(pa);
                    ObjectPersonService.addPersonAssociation(personAssociation).then(
                        function (personAssociation) {
                            refresh();
                            scope.personAssociation = personAssociation;
                            var idxPa = _.findIndex(scope.objectInfo.personAssociations, function (pa) {
                                return Util.compare(pa.id, personAssociation.id);
                            });
                            scope.objectInfo.personAssociations[idxPa] = personAssociation;
                            scope.retrieveGridData();
                        }
                    );
                };

                var getGridOptions = function getGridOptions(configGrid) {
                    var sc = {gridOptions: {appScopeProvider: scope}};
                    var gridHelper = new HelperUiGridService.Grid({scope: sc});
                    configGrid(scope.config, gridHelper);
                    return gridHelper.getGridOptions();
                };

                var refresh = function () {
                    scope.$emit('report-object-refreshed', $stateParams.id);
                };

            },
            templateUrl: 'directives/core-people/core-people.client.view.html'
        };
    }
]);
