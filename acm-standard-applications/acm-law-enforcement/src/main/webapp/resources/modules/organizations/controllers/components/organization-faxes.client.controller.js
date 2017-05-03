'use strict';

angular.module('organizations').controller('Organizations.FaxesController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', '$modal', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectLookupService) {


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
            , componentId: "faxes"
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
            var faxes = _.filter($scope.objectInfo.contactMethods, {type: 'fax'});
            $scope.gridOptions.data = faxes;
        };

        $scope.addNew = function () {
            var fax = {};
            fax.created = Util.dateToIsoString(new Date());
            fax.creator = $scope.userId;

            //put contactMethod to scope, we will need it when we return from popup
            $scope.fax = fax;
            var item = {
                id: '',
                parentId: $scope.objectInfo.id,
                type: 'fax',
                subType: '',
                value: '',
                description: ''
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.fax = rowEntity;
            var item = {
                id: rowEntity.id,
                type: rowEntity.type,
                subType: rowEntity.subType,
                value: rowEntity.value,
                description: rowEntity.description
            };
            showModal(item, true);

        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.contactMethods = _.remove($scope.objectInfo.contactMethods, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function showModal(fax, isEdit) {
            var params = {};
            params.fax = fax || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(fax);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/organizations/views/components/organization-faxes-modal.client.view.html',
                controller: 'Organizations.FaxesModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                var fax;
                if (!data.isEdit)
                    fax = $scope.fax;
                else {
                    fax = _.find($scope.objectInfo.contactMethods, {id: data.fax.id});
                }
                fax.type = 'fax';
                fax.subType = data.fax.subType;
                fax.value = data.fax.value;
                fax.description = data.fax.description;

                if (!data.isEdit) {
                    $scope.objectInfo.contactMethods.push(fax);
                }

                var faxes = _.filter($scope.objectInfo.contactMethods, {type: 'fax'});
                if (data.isDefault || faxes.length == 1) {
                    $scope.objectInfo.defaultFax = fax;
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
            var id = 0;
            if ($scope.objectInfo.defaultFax) {
                id = $scope.objectInfo.defaultFax.id
            }
            return data.id == id;
        };
    }
]);