'use strict';

angular.module('complaints').controller('Complaints.OrganizationsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Complaint.InfoService', 'Authentication', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Organization.InfoService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, ComplaintInfoService, Authentication, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService, OrganizationInfoService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        //TODO: check for organization types
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
            , moduleId: "complaints"
            , componentId: "organizations"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
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
                , organizationType: ""
                , parentId: $scope.objectInfo.complaintId
                , parentType: $scope.objectInfo.complaintType
                , parentTitle: $scope.objectInfo.complaintNumber
                , organizationDescription: ""
                , notes: ""
                , organization: null
                , className: "com.armedia.acm.plugins.organization.model.OrganizationAssociation"
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
                    association.organizationType = data.type;
                    $scope.objectInfo.organizationAssociations.push(association);
                    saveObjectInfoAndRefresh();
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        var association = new newOrganizationAssociation();
                        association.organization = organization;
                        association.organizationType = data.type;
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
            if (ComplaintInfoService.validateComplaintInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = ComplaintInfoService.saveComplaintInfo(objectInfo);
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