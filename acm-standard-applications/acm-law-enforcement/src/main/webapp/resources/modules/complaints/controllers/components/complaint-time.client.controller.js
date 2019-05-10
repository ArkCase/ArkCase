'use strict';

angular.module('complaints').controller(
        'Complaints.TimeController',
        [ '$scope', '$stateParams', '$state', '$modal', '$translate', 'UtilService', 'ObjectService', 'ConfigService', 'Object.TimeService', 'Complaint.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'FormsType.Service', 'TimeTracking.InfoService',
                function($scope, $stateParams, $state, $modal, $translate, Util, ObjectService, ConfigService, ObjectTimeService, ComplaintInfoService, HelperUiGridService, HelperObjectBrowserService, FormsTypeService, TimeTrackingInfoService) {

                    var componentHelper = new HelperObjectBrowserService.Component({
                        moduleId: "complaints",
                        componentId: "time",
                        scope: $scope,
                        stateParams: $stateParams,
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    FormsTypeService.isAngularFormType().then(function(isAngularFormType) {
                        $scope.isAngularFormType = isAngularFormType;
                    });

                    FormsTypeService.isFrevvoFormType().then(function(isFrevvoFormType) {
                        $scope.isFrevvoFormType = isFrevvoFormType;
                    });

                    var onConfigRetrieved = function(config) {
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

                    var onObjectInfoRetrieved = function(objectInfo) {
                        if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                            $scope.newTimesheetParamsFromObject = {
                                _id: objectInfo.complaintId,
                                _type: ObjectService.ObjectTypes.COMPLAINT,
                                _number: objectInfo.complaintNumber,
                                _title: objectInfo.complaintTitle
                            }
                            ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.COMPLAINT, componentHelper.currentObjectId).then(function(timesheets) {
                                componentHelper.promiseConfig.then(function(config) {
                                    for (var i = 0; i < timesheets.length; i++) {
                                        timesheets[i].acm$_formName = timesheets[i].title;
                                        timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function(total, n) {
                                            return total + Util.goodValue(n.value, 0);
                                        }, 0);

                                        timesheets[i].totalCost = calculateTotalCost(timesheets[i]);
                                    }

                                    $scope.gridOptions = $scope.gridOptions || {};
                                    $scope.gridOptions.data = timesheets;
                                    $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                                    return config;
                                });
                                return timesheets;
                            });
                        }
                    };

                    function calculateTotalCost(timesheet) {
                        var totalCost = 0.0;
                        for (var j = 0; j < timesheet.times.length; j++) {
                            if ($scope.newTimesheetParamsFromObject._type == timesheet.times[j].type && $scope.newTimesheetParamsFromObject._number == timesheet.times[j].code) {
                                totalCost += parseFloat(timesheet.times[j].totalCost);
                            }
                        }
                        return totalCost;
                    }

                    $scope.newTimesheet = function() {
                        var params = {
                            isEdit: false,
                            timeType: $scope.newTimesheetParamsFromObject._type,
                            timeNumber: $scope.newTimesheetParamsFromObject._number,
                            timeId: $scope.newTimesheetParamsFromObject._id,
                            typesName: $scope.newTimesheetParamsFromObject._title
                        };
                        showModal(params);
                    };

                    function showModal(params) {
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/time-tracking/views/components/time-tracking-new-timesheet-modal.client.view.html',
                            controller: 'TimeTracking.NewTimesheetController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                modalParams: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            var addedTimesheet = data;
                            addedTimesheet.acm$_formName = addedTimesheet.title;
                            addedTimesheet.acm$_hours = _.reduce(Util.goodArray(addedTimesheet.times), function(total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                            addedTimesheet.totalCost = calculateTotalCost(addedTimesheet);

                            if (params.isEdit) {
                                _.forEach($scope.gridOptions.data, function(timesheet, i) {
                                    if (Util.compare(timesheet.id, addedTimesheet.id)) {
                                        $scope.gridOptions.data.splice(i, 1, addedTimesheet);
                                        return false;
                                    }
                                });
                            } else {
                                $scope.gridOptions.data.push(addedTimesheet);
                            }
                        });
                    }

                    $scope.editRow = function(rowEntity) {
                        if ($scope.isFrevvoFormType) {
                            var frevvoDateFormat = $translate.instant("common.frevvo.defaultDateFormat");
                            var startDate = moment(rowEntity.startDate).format(frevvoDateFormat);

                            $state.go('frevvo.edit-timesheet', {
                                period: startDate
                            });
                        } else {
                            TimeTrackingInfoService.getTimesheetInfo(rowEntity.id).then(function(timesheetInfo) {
                                var timesheetInfo = timesheetInfo;
                                $scope.editParams = {
                                    isEdit: true,
                                    timesheet: timesheetInfo
                                };
                                showModal($scope.editParams);
                            });
                        }
                    };
                } ]);
