'use strict';

angular.module('organizations').controller('Organizations.PeopleController', ['$scope', '$q', '$stateParams'
    , '$translate', '$modal', 'UtilService', 'ObjectService', 'Organization.InfoService'
    , 'Authentication', 'Person.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'OrganizationAssociation.Service', 'ConfigService'
    , function ($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService
        , Authentication, PersonInfoService, HelperUiGridService, HelperObjectBrowserService, OrganizationAssociationService, ConfigService) {


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
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            OrganizationAssociationService.getOrganizationAssociations(objectInfo.organizationId, 'PERSON', null, 'true').then(function (response) {
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
                templateUrl: 'modules/common/views/new-person-modal.client.view.html',
                controller: 'Common.NewPersonModalController',
                size: 'lg'
            });

            modalInstance.result.then(function (data) {
                PersonInfoService.savePersonInfo(data.person).then(function (savedPerson) {
                    var organizationAssociation = {
                        organization: {organizationId: $scope.objectInfo.organizationId},
                        parentId: savedPerson.id,
                        parentType: savedPerson.objectType,
                        parentTitle: savedPerson.givenName + ' ' + savedPerson.familyName,
                        associationType: "Employee"
                    };
                    if (!$scope.objectInfo.associationsFromObjects) {
                        $scope.objectInfo.associationsFromObjects = [];
                    }
                    $scope.objectInfo.associationsFromObjects.push(organizationAssociation);
                    saveObjectInfoAndRefresh();
                });
            });
        };

        $scope.addExisting = function () {
            var params = {};
            params.header = $translate.instant("common.dialogPersonPicker.header");
            params.filter = '"Object Type": PERSON';
            params.config = Util.goodMapValue($scope.commonConfig, "dialogPersonPicker");

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
                    var organizationAssociation = {
                        organization: {organizationId: $scope.objectInfo.organizationId},
                        parentId: selected.object_id_s,
                        parentType: selected.object_type_s,
                        parentTitle: selected.full_name_lcs,
                        associationType: "Employee"
                    };
                    if (!$scope.objectInfo.associationsFromObjects) {
                        $scope.objectInfo.associationsFromObjects = [];
                    }
                    $scope.objectInfo.associationsFromObjects.push(organizationAssociation);
                    saveObjectInfoAndRefresh();
                }
            });
        };

        $scope.deleteRow = function (rowEntity) {
            //TODO remove old code below, and make API call for deleting
            // var id = Util.goodMapValue(rowEntity, "personId", 0);
            // if (0 < id) {    //do not need to call service when deleting a new row with id==0
            //     $scope.objectInfo.people = _.remove($scope.objectInfo.people, function (item) {
            //         return item.personId != personId;
            //     });
            //     saveObjectInfoAndRefresh()
            // }
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
    }
]);