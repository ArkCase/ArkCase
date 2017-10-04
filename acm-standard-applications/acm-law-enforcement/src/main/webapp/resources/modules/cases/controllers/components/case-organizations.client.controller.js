'use strict';

angular.module('cases').controller('Cases.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Case.InfoService', 'Authentication', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Organization.InfoService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, CaseInfoService, Authentication, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService, OrganizationInfoService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        //TODO: change personTypes with some new organizationTypes
        ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE).then(
            function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                return organizationTypes;
            });

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "organizations"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
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
            gridHelper.addButton(config, "edit", null, null, "isEditDisabled");
            gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.organizationAssociations;
        };

        var newOrganizationAssociation = function () {
            return {
                id: null
                , associationType: ""
                , parentId: $scope.objectInfo.id
                , parentType: ObjectService.ObjectTypes.CASE_FILE
                , parentTitle: $scope.objectInfo.caseNumber
                , organization: null
                , className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
            };
        };

        $scope.addOrganization = function () {
            pickOrganization(null);
        };

        function pickOrganization(association) {
            $scope.isFirstOrganization = $scope.gridOptions.data.length == 0 ? true : false;
            var params = {
                types: $scope.organizationTypes,
                showSetPrimary: true,
                isDefault: false,
                isFirstOrganization: $scope.isFirstOrganization
            };

            if (association) {
                angular.extend(params, {
                    organizationId: association.organization.organizationId,
                    organizationValue: association.organization.organizationValue,
                    type: association.associationType,
                    description: association.description
                });
            } else {
                association = new newOrganizationAssociation();
            }

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
                if (data.isNew) {
                    updateOrganizationAssociationData(association, data.organization, data);
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        updateOrganizationAssociationData(association, organization, data);
                    })
                }
            });
        }

        function updateOrganizationAssociationData(association, organization, data) {
            association.organization = organization;
            association.associationType = data.type;
            if (!association.id) {
                $scope.objectInfo.organizationAssociations.push(association);
            }
            saveObjectInfoAndRefresh();
        }

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "id", 0);
            _.remove($scope.objectInfo.organizationAssociations, function (item) {
                return item === rowEntity;
            });
            if (rowEntity.id) {
                saveObjectInfoAndRefresh();
            }
        };

        $scope.editRow = function (rowEntity) {
            pickOrganization(rowEntity);
        };

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = CaseInfoService.saveCaseInfo(objectInfo);
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

        $scope.isEditDisabled = function (rowEntity) {
            //add conditions if edit button shouldn't be visible
            return false;
        };

        $scope.isDeleteDisabled = function (rowEntity) {
            //add conditions if delete button shouldn't be visible
            return false;
        };
    }
]);