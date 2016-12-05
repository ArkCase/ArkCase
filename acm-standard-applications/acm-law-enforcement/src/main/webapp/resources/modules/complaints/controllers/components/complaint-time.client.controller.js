'use strict';

angular.module('complaints').controller('Complaints.TimeController', ['$scope', '$stateParams', '$state', '$translate'
    , 'UtilService', 'ObjectService', 'ConfigService', 'Object.TimeService', 'Complaint.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ObjectService, ConfigService, ObjectTimeService, ComplaintInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "complaints"
            , componentId: "time"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
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


        if (Util.goodPositive(componentHelper.currentObjectId, false)) {
        	$scope.newTimesheetParamsFromObject = {
        		_id: $scope.objectInfo.complaintId,
                _type: ObjectService.ObjectTypes.COMPLAINT,
                _number: $scope.objectInfo.complaintNumber
            }
            ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.COMPLAINT, componentHelper.currentObjectId).then(
                function (timesheets) {
                    componentHelper.promiseConfig.then(function (config) {
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = timesheets[i].title;
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                        return config;
                    });
                    return timesheets;
                }
            );
        }
        $scope.editRow = function(rowEntity){
        	var frevvoDateFormat = $translate.instant("common.frevvo.defaultDateFormat");
            var startDate = moment(rowEntity.startDate).format(frevvoDateFormat);

        	$state.go('frevvo.edit-timesheet',{period: startDate});
        }
    }
]);
