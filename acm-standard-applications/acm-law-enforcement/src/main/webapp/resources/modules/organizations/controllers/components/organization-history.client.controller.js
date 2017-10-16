'use strict';

angular.module('organizations').controller('Organizations.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Organization.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Acm.StoreService', '$modal'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectAuditService, OrganizationInfoService, HelperUiGridService
        , HelperObjectBrowserService, Store, $modal) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "history"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }, onObjectInfoRetrieved: function (objectInfo) {
                retrieveGridData();
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);
            retrieveGridData();
        };

        function retrieveGridData() {
            gridHelper.retrieveAuditData(ObjectService.ObjectTypes.ORGANIZATION, $stateParams.id);
        }

        $scope.refresh = function () {
            retrieveGridData();
        };

        $scope.showDetails = function (objectHistoryDetails) {
            var params = {};
            params.details = objectHistoryDetails;

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/common/views/object-history-details-modal.client.view.html',
                controller: 'Common.ObjectHistoryDetailsController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {

            });
        }

    }
]);