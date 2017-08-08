'use strict';

angular.module('people').controller('People.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication', 'Organization.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ConfigService', 'PersonAssociation.Service', 'Object.LookupService', 'PermissionsService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication, OrganizationInfoService
        , HelperUiGridService, HelperObjectBrowserService, ConfigService, PersonAssociationService, ObjectLookupService, PermissionsService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );
        $scope.tableData = [];

        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            $scope.commonConfig = moduleConfig;
            return moduleConfig;
        });

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "organizations"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        ObjectLookupService.getPersonOrganizationRelationTypes().then(
            function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                return organizationTypes;
            });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            PermissionsService.getActionPermission('editPerson', $scope.objectInfo, {objectType: ObjectService.ObjectTypes.PERSON}).then(function (result) {
                if (result) {
                    gridHelper.addButton(config, "edit");
                    gridHelper.addButton(config, "delete", null, null, "isDefault");
                }
            });
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        $scope.hasSelected = false;

        $scope.editRow = function (rowEntity) {
            var params = {
                showSetPrimary: true,
                types: $scope.organizationTypes,
                organizationId: rowEntity.organization.organizationId,
                organizationValue: rowEntity.organization.organizationValue,
                type: rowEntity.personToOrganizationAssociationType,
                isDefault: rowEntity === $scope.objectInfo.defaultOrganization
            };

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                controller: 'Common.AddOrganizationModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.organization) {
                    savePersonAssociation(rowEntity, data);
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        data['organization'] = organization;
                        savePersonAssociation(rowEntity, data);
                    });
                }
            });
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = objectInfo.organizationAssociations;
        };

        $scope.addOrganization = function () {
            var params = {
                showSetPrimary: true,
                isDefault: false,
                types: $scope.organizationTypes
            };

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                controller: 'Common.AddOrganizationModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.organization) {
                    savePersonAssociation({}, data);
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        data['organization'] = organization;
                        savePersonAssociation({}, data);
                    });
                }
            });
        };

        function savePersonAssociation(association, data) {
            association.person = {id: $scope.objectInfo.id};
            association.organization = data.organization;
            association.personToOrganizationAssociationType = data.type;
            association.organizationToPersonAssociationType = data.inverseType;

            if (data.isDefault) {
                //find and change previously default organization
                var defaultAssociation = _.find($scope.objectInfo.organizationAssociations, function (object) {
                    return object.defaultOrganization;
                });
                if (defaultAssociation) {
                    defaultAssociation.defaultOrganization = false;
                }
            }
            association.defaultOrganization = data.isDefault;

            //if is new created, add it to the organization associations list
            if (!association.id) {
                if (!$scope.objectInfo.organizationAssociations) {
                    $scope.objectInfo.organizationAssociations = [];
                }
                $scope.objectInfo.organizationAssociations.push(association);
            }
            saveObjectInfoAndRefresh();
        }

        $scope.deleteRow = function (rowEntity) {
            _.remove($scope.objectInfo.organizationAssociations, function (item) {
                return item === rowEntity;
            });
            saveObjectInfoAndRefresh();
        };

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
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
                return data === $scope.objectInfo.defaultOrganization;
            }
            return false;
        }
    }
])
;