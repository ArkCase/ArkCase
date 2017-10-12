'use strict';

angular.module('organizations').controller('Organizations.EmailsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'PermissionsService', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, PermissionsService, ObjectLookupService) {


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
            , componentId: "emails"
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
            var emails = _.filter($scope.objectInfo.contactMethods, {type: 'email'});
            $scope.gridOptions.data = emails;
        };

        ObjectLookupService.getContactMethodTypes().then(
            function (contactMethodTypes) {
                var found = _.find(contactMethodTypes, {key: 'email'});
                if(!Util.isArray(found)){
                    $scope.emailTypes = found.subLookup;
                }
                return contactMethodTypes;
            });

        $scope.getLookupValue = function(value, key){
            return ObjectLookupService.getLookupValue(value, key);
        };

        $scope.addNew = function () {
            var email = {};
            email.created = Util.dateToIsoString(new Date());
            email.creator = $scope.userId;
            email.className = "com.armedia.acm.plugins.addressable.model.ContactMethod";

            //put contactMethod to scope, we will need it when we return from popup
            $scope.email = email;
            var item = {
                id: '',
                parentId: $scope.objectInfo.id,
                type: 'email',
                subType: '',
                value: '',
                description: ''
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.email = rowEntity;
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
            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.contactMethods = _.remove($scope.objectInfo.contactMethods, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function showModal(email, isEdit) {
            var params = {};
            params.email = email || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(email);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/organizations/views/components/organization-emails-modal.client.view.html',
                controller: 'Organizations.EmailsModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                var email;
                if (!data.isEdit)
                    email = $scope.email;
                else {
                    email = _.find($scope.objectInfo.contactMethods, {id: data.email.id});
                }
                email.type = 'email';
                email.subType = data.email.subLookup;
                email.value = data.email.value;
                email.description = data.email.description;

                if (!data.isEdit) {
                    $scope.objectInfo.contactMethods.push(email);
                }

                var emails = _.filter($scope.objectInfo.contactMethods, {type: 'email'});
                if (data.isDefault || emails.length == 1) {
                    $scope.objectInfo.defaultEmail = email;
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
            if ($scope.objectInfo.defaultEmail) {
                id = $scope.objectInfo.defaultEmail.id
            }
            return data.id == id;
        };
    }
]);