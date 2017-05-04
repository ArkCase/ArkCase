'use strict';

angular.module('cases').controller('Cases.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Case.InfoService', 'Authentication', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Organization.InfoService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, CaseInfoService, Authentication, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService, OrganizationInfoService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        //TODO: change personTypes with some new organizationTypes
        ObjectLookupService.getPersonTypes().then(
            function (organizationTypes) {
                var options = [];
                _.forEach(organizationTypes, function (v, k) {
                    options.push({type: v, name: v});
                });
                $scope.organizationTypes = options;
                return organizationTypes;
            });

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "organizations"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
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
            $scope.gridOptions.data = $scope.objectInfo.organizationAssociations;
        };

        var newOrganizationAssociation = function () {
            return {
                id: null
                , associationType: ""
                , parentId: $scope.objectInfo.id
                , parentType: $scope.objectInfo.caseType
                , parentTitle: $scope.objectInfo.caseNumber
                , organization: null
                , className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
            };
        };

        $scope.addOrganization = function () {

            var params = {};
            params.types = $scope.organizationTypes;

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
                    $scope.objectInfo.organizationAssociations.push(association);
                    saveObjectInfoAndRefresh();
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        var association = new newOrganizationAssociation();
                        association.organization = organization;
                        association.associationType = data.type;
                        $scope.objectInfo.organizationAssociations.push(association);
                        saveObjectInfoAndRefresh();
                    })
                }
            });
        };

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.organizationAssociations = _.remove($scope.objectInfo.organizationAssociations, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = CaseInfoService.saveCaseInfo(objectInfo);
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