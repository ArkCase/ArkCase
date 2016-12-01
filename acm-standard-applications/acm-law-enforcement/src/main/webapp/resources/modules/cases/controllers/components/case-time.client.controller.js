'use strict';

angular.module('cases').controller('Cases.TimeController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.TimeService', 'Case.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ObjectService, ConfigService, ObjectTimeService, CaseInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "time"
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

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.addButton(config, "edit");

            for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                if ("name" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].cellTemplate = "<a data-ui-sref=\"time-tracking.main({id: row.entity.id})\">{{row.entity.acm$_formName}}</a>";
                } else if ("tally" == $scope.config.columnDefs[i].name) {
                    $scope.gridOptions.columnDefs[i].field = "acm$_hours";
                }
            }
        };

        //if (Util.goodPositive(componentHelper.currentObjectId, false)) {
        //    ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.CASE_FILE, componentHelper.currentObjectId).then(
        //        function (timesheets) {
        //            componentHelper.promiseConfig.then(function (config) {
        //                for (var i = 0; i < timesheets.length; i++) {
        //                    timesheets[i].acm$_formName = $translate.instant("cases.comp.time.formNamePrefix") + " " + Util.goodValue(timesheets[i].startDate) + " - " + Util.goodValue(timesheets[i].endDate);
        //                    timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
        //                        return total + Util.goodValue(n.value, 0);
        //                    }, 0);
        //                }
        //
        //                $scope.gridOptions = $scope.gridOptions || {};
        //                $scope.gridOptions.data = timesheets;
        //                $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
        //                return config;
        //            });
        //            return timesheets;
        //        }
        //    );
        //}
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            var currentObjectId = Util.goodMapValue(objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
            	$scope.newTimesheetParamsFromObject = {
        			_id: objectInfo.id,
        			_type: ObjectService.ObjectTypes.CASE_FILE,
                    _number: objectInfo.caseNumber
                }
                ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.CASE_FILE, currentObjectId).then(
                    function (timesheets) {
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = timesheets[i].title;
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                        return timesheets;
                    }
                );
            }
        };
        $scope.editRow = function(rowEntity){
        	var frevvoDateFormat = $translate.instant("common.frevvo.defaultDateFormat");
            var startDate = moment(rowEntity.startDate).format(frevvoDateFormat);
        	
        	$state.go('frevvo.edit-timesheet',{period: startDate});
        }
    }
]);