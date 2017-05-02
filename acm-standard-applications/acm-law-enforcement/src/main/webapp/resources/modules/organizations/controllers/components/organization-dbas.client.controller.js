'use strict';

angular.module('organizations').controller('Organization.DBAsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Organization.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Authentication', 'Organization.PicturesService', '$modal'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, OrganizationInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService, Authentication, OrganizationPicturesService, $modal) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "dbas"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        $scope.organizationInfo = null;

        var currentUser = '';

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            $scope.config = config;
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete", null, null, "isDefault");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            if (objectInfo.dbas) {
                $scope.gridOptions.data = objectInfo.dbas;
                $scope.gridOptions.noData = false;
            } else {
                $scope.gridOptions.data = [];
                $scope.gridOptions.noData = true;
            }
        };

        //Aliases
        $scope.addNew = function () {

            var alias = {};
            alias.created = Util.dateToIsoString(new Date());
            alias.creator = $scope.userId;
            $scope.alias = alias;
            var item = {
                id: '',
                parentId: $scope.objectInfo.id,
                aliasType: '',
                aliasValue: '',
                description: ''
            };
            showModal(item, false);
        };
        $scope.editRow = function (rowEntity) {
            $scope.alias = rowEntity;
            var item = {
                id: rowEntity.id,
                parentId: $scope.objectInfo.id,
                aliasType: rowEntity.aliasType,
                aliasValue: rowEntity.aliasValue,
                description: rowEntity.description
            };
            showModal(item, true);
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.organizationAliases = _.remove($scope.objectInfo.organizationAliases, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function showModal(alias, isEdit) {
            var params = {};
            params.alias = alias || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(alias);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: "modules/organizations/views/components/organization-aliases-modal.client.view.html",
                controller: 'Organization.AliasesModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                var alias;
                if (!data.isEdit)
                    alias = $scope.alias;
                else {
                    alias = _.find($scope.objectInfo.organizationAliases, {id: data.alias.id});
                }
                alias.aliasType = data.alias.aliasType;
                alias.aliasValue = data.alias.aliasValue;
                alias.description = data.alias.description;
                if (!data.isEdit) {
                    $scope.objectInfo.organizationAliases.push(alias);
                }

                if (data.isDefault || $scope.objectInfo.organizationAliases.length == 1) {
                    $scope.objectInfo.defaultAlias = alias;
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
            if ($scope.objectInfo.defaultAlias) {
                id = $scope.objectInfo.defaultAlias.id
            }
            return data.id == id;
        };
    }
]);