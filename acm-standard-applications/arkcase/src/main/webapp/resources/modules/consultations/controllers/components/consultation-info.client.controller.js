'use strict';

angular.module('consultations').controller(
    'Consultations.InfoController',
    [
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        '$modal',
        'UtilService',
        'Util.DateService',
        'ConfigService',
        'Object.LookupService',
        'Consultation.LookupService',
        'Consultation.InfoService',
        'Object.ModelService',
        'MessageService',
        'ObjectService',
        'Object.ParticipantService',
        'SearchService',
        'Search.QueryBuilderService',
        'Helper.ObjectBrowserService',
        'Helper.UiGridService',
        'Dialog.BootboxService',
        '$filter',
        'SuggestedObjectsService',
        function ($scope, $state, $stateParams, $translate, $modal, Util, UtilDateService, ConfigService, ObjectLookupService, ConsultationLookupService, ConsultationInfoService, ObjectModelService, MessageService, ObjectService, ObjectParticipantService, SearchService, SearchQueryBuilder,
                  HelperObjectBrowserService, HelperUiGridService, DialogService, $filter, SuggestedObjectsService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "info",
                retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
                validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            var promiseUsers = gridHelper.getUsers();

            var defaultDateTimeUTCFormat = $translate.instant("common.defaultDateTimeUTCFormat");
            var defaultDateTimePickerFormat = $translate.instant("common.defaultDateTimePickerFormat");

            ConfigService.getComponentConfig("consultations", "participants").then(function(componentConfig) {
                $scope.config = componentConfig;
            });
            ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                    id: "userOrGroupSearch"
                });
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

            ObjectLookupService.getLookupByLookupName("consultationTypes").then(function (consultationTypes) {
                $scope.consultationTypes = consultationTypes;
                return consultationTypes;
            });

            ObjectLookupService.getLookupByLookupName("changeConsultationStatuses").then(function(consultationStatuses) {
                $scope.consultationStatuses = consultationStatuses;
                return consultationStatuses;
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
                                if($scope.objectInfo.participants[i].participantType =='assignee' || $scope.objectInfo.participants[i].participantType =='owning group'){
                                    $scope.objectInfo.participants[i].replaceChildrenParticipant = true;
                                }
                            }
                            if (selectedGroup) {
                                $scope.owningGroup = selectedGroup.object_id_s;
                                $scope.updateOwningGroup();
                                $scope.saveConsultation();

                            } else {
                                $scope.saveConsultation();
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
                                $scope.saveConsultation();
                            } else {
                                $scope.saveConsultation();
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

            var onObjectInfoRetrieved = function(data) {
                // unbind watcher when user switch between tasks. When we call $watch() method,
                // angularJS returns an unbind function that will kill the $watch() listener when its called.
                dueDateWatch();
                $scope.objectInfo.objType = 'Consultation';
                $scope.dateInfo = $scope.dateInfo || {};
                if(!Util.isEmpty($scope.objectInfo.dueDate)){
                    $scope.dateInfo.dueDate = moment.utc($scope.objectInfo.dueDate).local().format(defaultDateTimeUTCFormat);
                    $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                    $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                }
                else {
                    $scope.dateInfo.dueDate = null;
                    $scope.dueDate.dueDateInfoUIPicker = moment(new Date).format(defaultDateTimePickerFormat);
                    $scope.dueDate.dueDateInfo = moment.utc(new Date()).local();
                }
                $scope.dueDateBeforeChange = $scope.dateInfo.dueDate;
                $scope.owningGroup = ObjectModelService.getGroup(data);
                $scope.assignee = ObjectModelService.getAssignee(data);

                $scope.minDate = moment.utc(new Date(data.receivedDate)).local();

                ConsultationLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(function(approvers) {
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

                SuggestedObjectsService.getSuggestedObjects($scope.objectInfo.title, "CONSULTATION", $scope.objectInfo.id).then(function (value) {
                    $scope.hasSuggestedConsultations = value.data.length > 0 ? true : false;
                    $scope.numberOfSuggestedConsultations = value.data.length;
                });
            };

            // Updates the ArkCase database when the user changes a consultation attribute
            // in a consultation top bar menu item and clicks the save check button
            $scope.saveConsultation = function() {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (ConsultationInfoService.validateConsultationInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = ConsultationInfoService.saveConsultationInfo(objectInfo);
                    promiseSaveInfo.then(function(consultationInfo) {
                        $scope.$emit("report-object-updated", consultationInfo);
                        return consultationInfo;
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
            $scope.updateDueDate = function(data, oldDate) {
                if (!Util.isEmpty(data)) {
                    if (UtilDateService.compareDatesForUpdate(data, $scope.objectInfo.dueDate)) {
                        var correctedDueDate = new Date(data);
                        var startDate = new Date($scope.objectInfo.receivedDate);
                        if(correctedDueDate < startDate){
                            $scope.dateInfo.dueDate = $scope.dueDateBeforeChange;
                            DialogService.alert($translate.instant("consultations.comp.info.alertMessage ") + $filter("date")(startDate, $translate.instant('common.defaultDateTimeUIFormat')));
                        }else {
                            $scope.objectInfo.dueDate = moment.utc(correctedDueDate).format();
                            $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                            $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                            $scope.dateInfo.dueDate = $scope.dueDate.dueDateInfoUIPicker;
                            // unbind due date watcher before consultation save so that when user switch to different consultation
                            // watcher won't be fired before landing on that different consultation
                            dueDateWatch();
                            $scope.saveConsultation();
                        }
                    }
                }else {
                    if (!oldDate) {
                        $scope.objectInfo.dueDate = $scope.dueDateBeforeChange;
                        $scope.dueDate.dueDateInfo = moment.utc($scope.objectInfo.dueDate).local();
                        $scope.dueDate.dueDateInfoUIPicker = moment($scope.objectInfo.dueDate).format(defaultDateTimePickerFormat);
                        $scope.dateInfo.dueDate = $scope.dueDate.dueDateInfoUIPicker;
                        // unbind due date watcher before consultation save so that when user switch to different consultation
                        // watcher won't be fired before landing on that different consultation
                        dueDateWatch();
                        $scope.saveConsultation();
                    }
                }
            };

            $scope.suggestedConsultations = function () {
                $state.go('consultations.suggestedConsultations',{
                    id: $scope.objectInfo.id
                });
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
