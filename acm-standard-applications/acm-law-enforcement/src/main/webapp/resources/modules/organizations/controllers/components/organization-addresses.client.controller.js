'use strict';

angular.module('organizations').controller('Organizations.AddressesController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'PermissionsService', 'Object.LookupService', 'Object.ModelService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, PermissionsService, ObjectLookupService, ObjectModelService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "addresses"
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
            PermissionsService.getActionPermission('editOrganization', $scope.objectInfo, {objectType: ObjectService.ObjectTypes.ORGANIZATION}).then(function (result) {
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

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.addresses;
        };

        ObjectLookupService.getAddressTypes().then(
            function (addressTypes) {
                $scope.addressTypes = addressTypes;
                return addressTypes;
            });

        ObjectLookupService.getCountries().then(function (countries) {
            $scope.countries = countries;
        });

        //Addresses
        $scope.addNew = function () {

            var address = {};
            address.created = Util.dateToIsoString(new Date());
            address.creator = $scope.userId;
            address.className = "com.armedia.acm.plugins.addressable.model.PostalAddress";
            $scope.address = address;
            var item = {
                id: '',
                parentId: $scope.objectInfo.id,
                addressType: '',
                streetAddress: '',
                streetAddress2: '',
                city: '',
                state: '',
                zip: '',
                country: ''
            };
            showModal(item, false);
        };
        $scope.editRow = function (rowEntity) {
            $scope.address = rowEntity;
            var item = {
                id: rowEntity.id,
                parentId: $scope.objectInfo.id,
                addressType: rowEntity.type,
                streetAddress: rowEntity.streetAddress,
                streetAddress2: rowEntity.streetAddress2,
                city: rowEntity.city,
                state: rowEntity.state,
                zip: rowEntity.zip,
                country: rowEntity.country

            };
            showModal(item, true);
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.addresses = _.remove($scope.objectInfo.addresses, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };


        $scope.setPrimary = function () {
            console.log('set primary');
        };

        function showModal(address, isEdit) {
            var params = {};
            params.address = address || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(address);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/organizations/views/components/organization-addresses-modal.client.view.html',
                controller: 'Organizations.AddressesModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                var address;
                if (!data.isEdit)
                    address = $scope.address;
                else {
                    address = _.find($scope.objectInfo.addresses, {id: data.address.id});
                }
                address.type = data.address.addressType;
                address.streetAddress = data.address.streetAddress;
                address.streetAddress2 = data.address.streetAddress2;
                address.city = data.address.city;
                address.state = data.address.state;
                address.zip = data.address.zip;
                address.country = data.address.country;
                if (!data.isEdit) {
                    $scope.objectInfo.addresses.push(address);
                }

                if (data.isDefault || $scope.objectInfo.addresses.length == 1) {
                    $scope.objectInfo.defaultAddress = address;
                }
                saveObjectInfoAndRefresh();
            });
        }

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
            return ObjectModelService.isObjectReferenceSame($scope.objectInfo, data, "defaultAddress");
        }
    }
]);