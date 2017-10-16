'use strict';

angular.module('complaints').controller('Complaints.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Complaint.InfoService', 'Authentication', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Organization.InfoService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, ComplaintInfoService, Authentication, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService, OrganizationInfoService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        //TODO: check for organization types
        ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT).then(
            function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                return organizationTypes;
            });

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "organizations"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
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

        $scope.getPrimaryContact = function(organizationAssiciation)
        {
            var primaryContact = organizationAssiciation.organization.primaryContact;
            if (!!primaryContact) {
                var getPrimaryConactGivenName = Util.goodValue(primaryContact.person.givenName);
                var getPrimaryConactFamilyName = Util.goodValue(primaryContact.person.familyName);
                return (getPrimaryConactGivenName.trim() + ' ' + getPrimaryConactFamilyName.trim()).trim();
            }
            return '';
        };

        $scope.getDefaultAddress = function (defaultAddress) {
            if(!!defaultAddress){
                var getDefaultAddressState = Util.goodValue(defaultAddress.state);
                var getDefaultAddressCity = Util.goodValue(defaultAddress.city);
                return (getDefaultAddressState.trim() + ' ' + getDefaultAddressCity.trim()).trim();
            }
            return '';
        };

        var newOrganizationAssociation = function () {
            return {
                id: null,
                associationType: "",
                parentId: $scope.objectInfo.complaintId,
                parentType: ObjectService.ObjectTypes.COMPLAINT,
                parentTitle: $scope.objectInfo.complaintNumber,
                organization: null,
                className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
            };
        };

        $scope.addOrganization = function () {
            pickOrganization(null);
        };

        function pickOrganization(association) {
            var params = {};
            params.types = $scope.organizationTypes;

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
        };

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
            if (ComplaintInfoService.validateComplaintInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = ComplaintInfoService.saveComplaintInfo(objectInfo);
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