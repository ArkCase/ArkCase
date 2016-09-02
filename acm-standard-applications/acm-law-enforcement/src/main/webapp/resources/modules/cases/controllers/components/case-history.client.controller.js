'use strict';

angular.module('cases').controller('Cases.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.AuditService', 'Case.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Acm.StoreService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, ObjectAuditService, CaseInfoService, HelperUiGridService
        , HelperObjectBrowserService, Store) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "history"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
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
            retrieveGridData();
        };

        function retrieveGridData () {
          gridHelper.retrieveAuditData(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id);
        }

    }
]);