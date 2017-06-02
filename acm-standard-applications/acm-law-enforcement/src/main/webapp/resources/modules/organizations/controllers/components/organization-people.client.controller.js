'use strict';

angular.module('organizations').controller('Organizations.PeopleController', ['$scope', '$q', '$stateParams'
    , '$translate', '$modal', 'UtilService', 'ObjectService', 'Organization.InfoService'
    , 'Authentication', 'Person.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'OrganizationAssociation.Service', 'ConfigService', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService
        , Authentication, PersonInfoService, HelperUiGridService, HelperObjectBrowserService, OrganizationAssociationService, ConfigService, ObjectLookupService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            $scope.commonConfig = moduleConfig;
            return moduleConfig;
        });

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "people"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete", null, null, "isDefault");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = objectInfo.personAssociations;
        };

        ObjectLookupService.getOrganizationPersonRelationTypes().then(
            function (types) {
                $scope.personAssociationTypes = types;
                return types;
            });

        $scope.editRow = function (rowEntity) {
            var params = {
                showSetPrimary: true,
                types: $scope.personAssociationTypes,
                personId: rowEntity.person.id,
                person: rowEntity.person,
                personName: rowEntity.person.givenName + ' ' + rowEntity.person.familyName,
                type: rowEntity.organizationToPersonAssociationType,
                isDefault: $scope.isDefault(rowEntity)
            };

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                controller: 'Common.AddPersonModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.person) {
                    if (!data.person.id) {
                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function (savedPerson) {
                            data['person'] = savedPerson;
                            savePersonAssociation(rowEntity, data);
                        });
                    } else {
                        savePersonAssociation(rowEntity, data);
                    }
                } else {
                    PersonInfoService.getPersonInfo(data.organizationId).then(function (person) {
                        data['person'] = person;
                        savePersonAssociation(rowEntity, data);
                    });
                }
            });
        };

        $scope.addPerson = function () {
            var params = {
                showSetPrimary: true,
                isDefault: false,
                types: $scope.personAssociationTypes
            };

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                controller: 'Common.AddPersonModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.person) {
                    if (!data.person.id) {
                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function (response) {
                            data['person'] = response.data;
                            savePersonAssociation({}, data);
                        });
                    } else {
                        savePersonAssociation({}, data);
                    }
                } else {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                        data['person'] = person;
                        savePersonAssociation({}, data);
                    });
                }
            });
        };

        function savePersonAssociation(association, data) {
            association['organization'] = $scope.objectInfo;
            association['person'] = data.person;
            association['organizationToPersonAssociationType'] = data.type;
            association['personToOrganizationAssociationType'] = data.inverseType;

            if (data.isDefault) {
                $scope.objectInfo.primaryContact = association;
            }

            //if is new created, add it to the organization associations list
            if (!association.id) {
                if (!$scope.objectInfo.personAssociations) {
                    $scope.objectInfo.personAssociations = [];
                }
                $scope.objectInfo.personAssociations.push(association);
            }
            saveObjectInfoAndRefresh();
        }

        $scope.deleteRow = function (rowEntity) {
            _.remove($scope.objectInfo.personAssociations, function (item) {
                return item === rowEntity;
            });
            saveObjectInfoAndRefresh();
        };

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                promiseSaveInfo.then(
                    function (objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        return objectInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                );
            }
            return promiseSaveInfo;
        }

        $scope.isDefault = function (data) {
            if ($scope.objectInfo) {
                return data === $scope.objectInfo.primaryContact;
            }
            return false;
        }
    }
]);