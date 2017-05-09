'use strict';

angular.module('people').controller('People.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication', 'Organization.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ConfigService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication, OrganizationInfoService
        , HelperUiGridService, HelperObjectBrowserService, ConfigService) {


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

        $scope.hasSelected = false;

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.organizations;
            $scope.gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                if (row && row.isSelected) {
                    $scope.hasSelected = true;
                    $scope.selectedItem = row.entity;
                } else {
                    $scope.hasSelected = false;
                }
            });
        };

        $scope.addNew = function () {
            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/new-organization-modal.client.view.html',
                controller: 'Common.NewOrganizationModalController',
                size: 'lg'
            });

            modalInstance.result.then(function (data) {
                if (!$scope.objectInfo.organizations) {
                    $scope.objectInfo.organizations = [];
                }
                $scope.objectInfo.organizations.push(data.organization);
                saveObjectInfoAndRefresh();
            });
        };

        $scope.setPrimary = function () {
            if ($scope.selectedItem) {
                $scope.objectInfo.defaultOrganization = $scope.selectedItem;
            }

            saveObjectInfoAndRefresh();
        };

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "organizationId", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.organizations = _.remove($scope.objectInfo.organizations, function (item) {
                    return item.organizationId != id;
                });
                saveObjectInfoAndRefresh();
            }
        };

        $scope.addExisting = function () {
            var params = {};
            params.header = $translate.instant("common.dialogOrganizationPicker.header");
            params.filter = '"Object Type": ORGANIZATION';
            params.config = Util.goodMapValue($scope.commonConfig, "dialogOrganizationPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/common/views/object-picker-modal.client.view.html",
                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                    $scope.modalInstance = $modalInstance;
                    $scope.header = params.header;
                    $scope.filter = params.filter;
                    $scope.config = params.config;
                }],
                animation: true,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (selected) {
                if (!Util.isEmpty(selected)) {
                    OrganizationInfoService.getOrganizationInfo(selected.object_id_s).then(function (organization) {
                        if (!$scope.objectInfo.organizations) {
                            $scope.objectInfo.organizations = [];
                        }
                        $scope.objectInfo.organizations.push(organization);
                        //save person info
                        saveObjectInfoAndRefresh();
                    });
                }
            });
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
            var id = 0;
            if ($scope.objectInfo.defaultOrganization) {
                id = $scope.objectInfo.defaultOrganization.organizationId
            }
            if (data) {
                return data.organizationId == id;
            } else if ($scope.selectedItem) {
                return $scope.selectedItem.organizationId == id;
            }
            return false;
        }
    }
]);