'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , 'Dashboard.DashboardService', 'Admin.CalendarConfigurationService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, CaseInfoService, ObjectService, HelperObjectBrowserService, DashboardService, CalendarConfigurationService) {

        $scope.isNodeDisabled = function(node){
            return HelperObjectBrowserService.getDisabled('cases', $translate.instant(node));
        }
        CalendarConfigurationService.getCurrentCalendarConfiguration().then(function (calendarAdminConfigRes) {
            if(calendarAdminConfigRes.data.configurationsByType['CASE_FILE'].integrationEnabled){
                HelperObjectBrowserService.setDisabled('cases', 'Calendar', false);
            }else{
                HelperObjectBrowserService.setDisabled('cases', 'Calendar', true);
            }
        });

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "cases"
            , resetObjectInfo: CaseInfoService.resetCaseInfo
            , getObjectInfo: CaseInfoService.getCaseInfo
            , updateObjectInfo: CaseInfoService.updateCaseInfo
            , getObjectTypeFromInfo: function (objectInfo) {
                return ObjectService.ObjectTypes.CASE_FILE;
            }
        });

    }
]);