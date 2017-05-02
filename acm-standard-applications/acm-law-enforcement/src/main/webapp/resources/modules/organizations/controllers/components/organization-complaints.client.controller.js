'use strict';

angular.module('organizations').controller('Organizations.ComplaintsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.OrganizationService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectOrganizationService) {


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
            , componentId: "complaints"
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
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
                ObjectOrganizationService.getPesonComplaints(currentObjectId).then(function (data) {
                    var complaints = data.response.docs;
                    $scope.gridOptions.data = complaints;
                    $scope.gridOptions.totalItems = data.response.numFound;
                    return data;
                });
            }
        };
    }
]);