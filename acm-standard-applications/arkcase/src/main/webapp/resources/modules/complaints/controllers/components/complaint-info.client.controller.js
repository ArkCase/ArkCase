'use strict';

angular.module('complaints').controller(
        'Complaints.InfoController',
        [
                '$scope',
                '$stateParams',
                '$translate',
                '$modal',
                'UtilService',
                'Util.DateService',
                'ConfigService',
                'Object.LookupService',
                'Complaint.LookupService',
                'Complaint.InfoService',
                'Object.ModelService',
                'Helper.ObjectBrowserService',
                'MessageService',
                'ObjectService',
                'Helper.UiGridService',
                'Object.ParticipantService',
                'SearchService',
                'Search.QueryBuilderService',
                'Dialog.BootboxService',
                '$filter',
                function($scope, $stateParams, $translate, $modal, Util, UtilDateService, ConfigService, ObjectLookupService, ComplaintLookupService, ComplaintInfoService, ObjectModelService, HelperObjectBrowserService, MessageService, ObjectService, HelperUiGridService, ObjectParticipantService,
                        SearchService, SearchQueryBuilder, DialogService, $filter) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "info",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });
                    var promiseUsers = gridHelper.getUsers();

                    var defaultDateTimeUTCFormat = $translate.instant("common.defaultDateTimeUTCFormat");
                    var defaultDateTimePickerFormat = $translate.instant("common.defaultDateTimePickerFormat");

                    ConfigService.getComponentConfig("complaints", "participants").then(function(componentConfig) {
                        $scope.config = componentConfig;
                    });

                    ObjectLookupService.getGroups().then(function(groups) {
                        var options = [];
                        _.each(groups, function(group) {
                            options.push({
                                value: group.name,
                                text: group.name
                            });
                        });
                        $scope.owningGroups = options;
                        return groups;
                    });

                    ObjectLookupService.getLookupByLookupName("priorities").then(function(priorities) {
                        $scope.priorities = priorities;
                        return priorities;
                    });

                    ObjectLookupService.getLookupByLookupName("complaintTypes").then(function(complaintTypes) {
                        $scope.complaintTypes = complaintTypes;
                        return complaintTypes;
                    });

                    ObjectLookupService.getLookupByLookupName("complaintStatuses").then(function(complaintStatuses) {
                        $scope.complaintStatuses = complaintStatuses;
                        return complaintStatuses;
                    });

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                            id: "userOrGroupSearch"
                        });
                    });

                    $scope.userOrGroupSearch = function() {
                        var assigneUserName = _.find($scope.userFullNames, function(user) {
                            return user.id === $scope.assignee
                        });
                        var params = {
                            owningGroup: $scope.owningGroup,
                            assignee: assigneUserName
                        };
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/common/views/user-group-picker-modal.client.view.html',
                            controller: 'Common.UserGroupPickerController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                $filter: function() {
                                    return $scope.userOrGroupSearchConfig.userOrGroupSearchFilters.userOrGroupFacetFilter;
                                },
                                $extraFilter: function() {
                                    return $scope.userOrGroupSearchConfig.userOrGroupSearchFilters.userOrGroupFacetExtraFilter;
                                },
                                $config: function() {
                                    return $scope.userOrGroupSearchConfig;
                                },
                                $params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(selection) {

                            if (selection) {
                                var selectedObjectType = selection.masterSelectedItem.object_type_s;
                                if (selectedObjectType === 'USER') { // Selected user
                                    var selectedUser = selection.masterSelectedItem;
                                    var selectedGroup = selection.detailSelectedItems;

                                    $scope.assignee = selectedUser.object_id_s;
                                    $scope.updateAssignee();

                                    //set for AFDP-6831 to inheritance in the Folder/file participants
                                    var len = $scope.objectInfo.participants.length;
                                    for (var i = 0; i < len; i++) {
                                        if($scope.objectInfo.participants[i].participantType =='assignee'|| $scope.objectInfo.participants[i].participantType =='owning group'){
                                            $scope.objectInfo.participants[i].replaceChildrenParticipant = true;
                                        }
                                    }

                                    if (selectedGroup) {
                                        $scope.owningGroup = selectedGroup.object_id_s;
                                        $scope.updateOwningGroup();
                                        $scope.saveComplaint()

                                    } else {
                                        $scope.saveComplaint();
                                    }

                                    return;
                                } else if (selectedObjectType === 'GROUP') { // Selected group
                                    var selectedUser = selection.detailSelectedItems;
                                    var selectedGroup = selection.masterSelectedItem;

                                    $scope.owningGroup = selectedGroup.object_id_s;
                                    $scope.updateOwningGroup();

                                    //set for AFDP-6831 to inheritance in the Folder/file participants
                                    var len = $scope.objectInfo.participants.length;
                                    for (var i = 0; i < len; i++) {
                                        if($scope.objectInfo.participants[i].participantType =='owning group'|| $scope.objectInfo.participants[i].participantType =='assignee') {
                                            $scope.objectInfo.participants[i].replaceChildrenParticipant = true;
                                        }
                                    }

                                    if (selectedUser) {
                                        $scope.assignee = selectedUser.object_id_s;
                                        $scope.updateAssignee();
                                        $scope.saveComplaint();
                                    } else {
                                        $scope.saveComplaint();
                                    }

                                    return;
                                }
                            }

                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });

                    };

                    $scope.dueDate = {
                        dueDateInfo: null
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        // unbind watcher when user switch between tasks. When we call $watch() method,
                        // angularJS returns an unbind function that will kill the $watch() listener when its called.
                        dueDateWatch();
                        $scope.objectInfo = objectInfo;
                        $scope.dateInfo = $scope.dateInfo || {};
                        if(!Util.isEmpty($scope.objectInfo.dueDate)){
                            $scope.dateInfo.dueDate = moment.utc($scope.objectInfo.dueDate).local().format(defaultDateTimeUTCFormat);
                            $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                            $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                        }
                        else {
                            $scope.dateInfo.dueDate = null;
                            $scope.dueDate.dueDateInfoUIPicker = moment(new Date).format(defaultDateTimePickerFormat)
                            $scope.dueDate.dueDateInfo = moment.utc(new Date()).local();
                        }
                        $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;

                        $scope.minDate = moment.utc(new Date(objectInfo.created)).local();

                        $scope.assignee = ObjectModelService.getAssignee(objectInfo);
                        $scope.owningGroup = ObjectModelService.getGroup(objectInfo);

                        ComplaintLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(function(approvers) {
                            var options = [];
                            _.each(approvers, function(approver) {
                                options.push({
                                    id: approver.userId,
                                    name: approver.fullName
                                });
                            });
                            $scope.assignees = options;
                            return approvers;
                        });

                        var complaintTypeObj = _.filter($scope.complaintTypes, function(complaintType) {
                            if (objectInfo.complaintType == complaintType.key) {
                                return complaintType;
                            }
                        });

                        if (objectInfo.complaintType) {
                            objectInfo.complaintType = $translate.instant(complaintTypeObj[0].value);
                        }
                    };

                    $scope.saveComplaint = function() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (ComplaintInfoService.validateComplaintInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = ComplaintInfoService.saveComplaintInfo(objectInfo);
                            promiseSaveInfo.then(function(complaintInfo) {
                                $scope.$emit("report-object-updated", complaintInfo);
                                return complaintInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    };
                    $scope.updateOwningGroup = function() {
                        ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
                    };
                    $scope.updateAssignee = function() {
                        ObjectModelService.setAssignee($scope.objectInfo, $scope.assignee);
                    };
                    $scope.updateDueDate = function(data, oldValue) {
                        if (!Util.isEmpty(data)) {
                            if (UtilDateService.compareDatesForUpdate(data, $scope.objectInfo.dueDate)) {
                                var correctedDueDate = new Date(data);
                                var startDate = new Date($scope.objectInfo.created);
                                if(correctedDueDate < startDate){
                                    $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                                    DialogService.alert($translate.instant("complaints.comp.info.alertMessage")+ $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                                }else {
                                    $scope.objectInfo.dueDate = moment.utc(correctedDueDate).format();
                                    $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                                    $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                                    $scope.dateInfo.dueDate = $scope.dueDate.dueDateInfoUIPicker;
                                    // unbind due date watcher before complaint save so that when user switch to different complaint
                                    // watcher won't be fired before landing on that different complaint
                                    dueDateWatch();
                                    $scope.saveComplaint();
                                }
                            }
                        }else {
                            if (!oldValue) {
                                $scope.objectInfo.dueDate = $scope.dueDateBeforeChange;
                                $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                                $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                                $scope.dateInfo.dueDate = $scope.dueDate.dueDateInfoUIPicker;
                                // unbind due date watcher before complaint save so that when user switch to different complaint
                                // watcher won't be fired before landing on that different complaint
                                dueDateWatch();
                                $scope.saveComplaint();
                            }
                        }
                    };

                    // store function reference returned by $watch statement in variable
                    var dueDateWatch = $scope.$watch('dueDate.dueDateInfo', dueDateChangeFn, true);

                    // update due date and save task
                    var dueDateChangeFn = function (newValue, oldValue) {
                        if (newValue && !moment(newValue).isSame(moment(oldValue)) && $scope.dueDate.isOpen) {
                            $scope.updateDueDate(newValue);
                        }
                    }

                    // register watcher when user open date picker
                    $scope.registerWatcher = function () {
                        dueDateWatch = $scope.$watch('dueDate.dueDateInfo', dueDateChangeFn, true);
                    }
                } ]);
