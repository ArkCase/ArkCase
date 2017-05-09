'use strict';

angular.module('organizations').controller('Organizations.RelatedController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectLookupService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ObjectLookupService.getOrganizationRelationTypes().then(
            function (relationshipTypes) {
                $scope.relationshipTypes = relationshipTypes;
                return relationshipTypes;
            });

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "related"
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
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.associationsToObjects;
        };

        var newOrganizationAssociation = function () {
            return {
                id: null
                , associationType: ""
                , parentId: $scope.objectInfo.organizationId
                , parentType: $scope.objectInfo.objectType
                , parentTitle: $scope.objectInfo.organizationValue
                , description: ""
                , organization: null
                , className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
            };
        };

        $scope.addOrganization = function () {

            var params = {};
            params.types = $scope.relationshipTypes;
            params.showDescription = true;

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
                    var association = new newOrganizationAssociation();
                    association.organization = data.organization;
                    association.associationType = data.type;
                    association.description = data.description;
                    $scope.objectInfo.associationsToObjects.push(association);
                    saveObjectInfoAndRefresh();
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        var association = new newOrganizationAssociation();
                        association.person = person;
                        association.associationType = data.type;
                        association.description = data.description;
                        $scope.objectInfo.associationsToObjects.push(association);
                        saveObjectInfoAndRefresh();
                    })
                }
            });
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.associationsToObjects = _.remove($scope.objectInfo.associationsToObjects, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
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