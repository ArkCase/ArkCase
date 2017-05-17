'use strict';

angular.module('organizations').controller('Organization.DBAsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Organization.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Authentication', '$modal'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, OrganizationInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService, Authentication, $modal) {

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
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete", null, null, "isDefault");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            if (objectInfo.organizationDBAs) {
                $scope.gridOptions.data = objectInfo.organizationDBAs;
                $scope.gridOptions.noData = false;
            } else {
                $scope.gridOptions.data = [];
                $scope.gridOptions.noData = true;
            }
        };

        //Aliases
        $scope.addNew = function () {

            var dba = {};
            dba.created = Util.dateToIsoString(new Date());
            dba.creator = $scope.userId;
            dba.organization = $scope.objectInfo;
            $scope.dba = dba;
            var item = {
                id: '',

                type: '',
                value: '',
                description: ''
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.dba = rowEntity;
            var item = {
                id: rowEntity.id,
                type: rowEntity.type,
                value: rowEntity.value,
                description: rowEntity.description
            };
            showModal(item, true);
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.organizationDBAs = _.remove($scope.objectInfo.organizationDBAs, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function showModal(dba, isEdit) {
            var params = {};
            params.dba = dba || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(dba);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: "modules/organizations/views/components/organization-dbas-modal.client.view.html",
                controller: 'Organizations.DBAsModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                var dba;
                if (!data.isEdit)
                    dba = $scope.dba;
                else {
                    dba = _.find($scope.objectInfo.organizationDBAs, {id: data.dba.id});
                }
                dba.type = data.dba.type;
                dba.value = data.dba.value;
                dba.description = data.dba.description;

                if (!data.isEdit) {
                    $scope.objectInfo.organizationDBAs.push(dba);
                }

                if (data.isDefault || $scope.objectInfo.organizationDBAs.length == 1) {
                    $scope.objectInfo.defaultDBA = dba;
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
            return data === $scope.objectInfo.defaultDBA;
        };
    }
]);