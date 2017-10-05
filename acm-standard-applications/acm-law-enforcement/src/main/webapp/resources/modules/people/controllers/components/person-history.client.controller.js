'use strict';

angular.module('people').controller('People.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Person.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Acm.StoreService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectAuditService, PersonInfoService, HelperUiGridService
        , HelperObjectBrowserService, Store) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "history"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
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
            angular.extend($scope.gridOptions, {
                expandableRowTemplate: 'modules/common/views/object-history-expandable-template.client.view.html',
                expandableRowHeight: 140,
                expandableRowScope: {
                    subGridVariable: 'subGridScopeVariable'
                },
                onRegisterApi: function( gridApi ) {
                    $scope.gridApi = gridApi;
                    $scope.gridApi.core.handleWindowResize();
                }
            });
            retrieveGridData();
        };

        function retrieveGridData() {
            gridHelper.retrieveAuditData(ObjectService.ObjectTypes.PERSON, $stateParams.id);
        }

    }
]);