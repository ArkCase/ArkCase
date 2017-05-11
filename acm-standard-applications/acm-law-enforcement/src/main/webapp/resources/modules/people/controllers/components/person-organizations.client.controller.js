'use strict';

angular.module('people').controller('People.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication', 'Organization.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ConfigService', 'PersonAssociation.Service'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication, OrganizationInfoService
        , HelperUiGridService, HelperObjectBrowserService, ConfigService, PersonAssociationService) {


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

            PersonAssociationService.getPersonAssociations(objectInfo.id, 'ORGANIZATION', null, 'true').then(function (response) {
                $scope.gridOptions.data = response.response.docs;
                $scope.gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                    if (row && row.isSelected) {
                        $scope.hasSelected = true;
                        $scope.selectedItem = row.entity;
                    } else {
                        $scope.hasSelected = false;
                    }
                });
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
                OrganizationInfoService.saveOrganizationInfo(data.organization).then(function (savedOrganization) {
                    var personAssociation = {
                        person: {id: $scope.objectInfo.id},
                        parentId: savedOrganization.organizationId,
                        parentType: savedOrganization.objectType,
                        parentTitle: savedOrganization.organizationValue,
                        personType: "Employee"
                    };
                    if (!$scope.objectInfo.associationsFromObjects) {
                        $scope.objectInfo.associationsFromObjects = [];
                    }
                    $scope.objectInfo.associationsFromObjects.push(personAssociation);
                    saveObjectInfoAndRefresh();
                });
            });
        };

        $scope.setPrimary = function () {
            //TODO
        };

        $scope.deleteRow = function (rowEntity) {
            //TODO remove old code below, and make API call for deleting
            // var id = Util.goodMapValue(rowEntity, "organizationId", 0);
            // if (0 < id) {    //do not need to call service when deleting a new row with id==0
            //     $scope.objectInfo.organizations = _.remove($scope.objectInfo.organizations, function (item) {
            //         return item.organizationId != id;
            //     });
            //     saveObjectInfoAndRefresh();
            // }
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
                    var personAssociation = {
                        person: {id: $scope.objectInfo.id},
                        parentId: selected.object_id_s,
                        parentType: selected.object_type_s,
                        parentTitle: selected.value_parseable,
                        personType: "Employee"
                    };
                    if (!$scope.objectInfo.associationsFromObjects) {
                        $scope.objectInfo.associationsFromObjects = [];
                    }
                    $scope.objectInfo.associationsFromObjects.push(personAssociation);
                    saveObjectInfoAndRefresh();
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
                id = $scope.objectInfo.defaultOrganization.parentId
            }
            if (data) {
                return data.parent_object_id == id;
            } else if ($scope.selectedItem) {
                return $scope.selectedItem.parent_object_id == id;
            }
            return false;
        }
    }
]);