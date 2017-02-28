'use strict';

angular.module('complaints').controller('Complaints.InfoController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService', 'Complaint.LookupService', 'Complaint.InfoService'
    , 'Object.ModelService', 'Helper.ObjectBrowserService', 'MessageService', 'ObjectService', 'Helper.UiGridService', '$modal'
    , 'Object.ParticipantService', '$q', '$filter', 'SearchService', 'Search.QueryBuilderService'
    , function ($scope, $stateParams, $translate, $timeout
        , Util, UtilDateService, ConfigService, ObjectLookupService, ComplaintLookupService, ComplaintInfoService
        , ObjectModelService, HelperObjectBrowserService, MessageService, ObjectService, HelperUiGridService, $modal, ObjectParticipantService, $q, $filter
        , SearchService, SearchQueryBuilder) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "info"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        var promiseConfig = ConfigService.getModuleConfig("complaints");

        $q.all([promiseConfig]).then(function (data) {
            var foundComponent = data[0].components.filter(function(component) { return component.title === 'Participants'; });
            $scope.config = foundComponent[0];
        });

        ObjectLookupService.getPriorities().then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );

        ObjectLookupService.getGroups().then(
            function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({value: group.name, text: group.name});
                });
                $scope.owningGroups = options;
                return groups;
            }
        );

        ComplaintLookupService.getComplaintTypes().then(
            function (complaintTypes) {
                var options = [];
                _.forEach(complaintTypes, function (item) {
                    options.push({value: item, text: item});
                });
                $scope.complaintTypes = options;
                return complaintTypes;
            }
        );

        $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;
        $scope.picker = {opened: false};
        $scope.onPickerClick = function () {
        	$scope.picker.opened = true;
        };

        $scope.openAssigneePickerModal = function () {
            var participant = {
                        id: '',
                        participantLdapId: '',
                        config: $scope.config
                    };
            showModal(participant, false);
        };

        var showModal = function (participant, isEdit) {
            var modalScope = $scope.$new();
            modalScope.participant = participant || {};

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: "modules/complaints/views/components/complaint-assignee-picker-modal.client.view.html",
                controller: "Complaints.AssigneePickerController",
                size: 'md',
                backdrop: 'static',
                resolve: {
                    owningGroup: function () {
                        return $scope.owningGroup;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                $scope.participant = {};
                if (data.participant.participantLdapId != '' && data.participant.participantLdapId != null) {
                    $scope.participant.participantLdapId = data.participant.participantLdapId;
                    $scope.assignee = data.participant.participantLdapId;
                    $scope.updateAssignee();
                }
            }, function(error) {    
            });
        };

        $scope.openGroupPickerModal = function () {
            var participant = {
                        id: '',
                        participantLdapId: '',
                        config: $scope.config
                    };
            showGroupModal(participant, false);
        };

        var showGroupModal = function (participant, isEdit) {
            var modalScope = $scope.$new();
            modalScope.participant = participant || {};

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: "modules/complaints/views/components/complaint-group-picker-modal.client.view.html",
                controller: "Complaints.GroupPickerController",
                size: 'md',
                backdrop: 'static',
                resolve: {
                    owningGroup: function () {
                        return $scope.owningGroup;
                    }
                }
            });

            modalInstance.result.then(function (chosenGroup) {
                $scope.participant = {};
                 
                if (chosenGroup.participant.participantLdapId != '' && chosenGroup.participant.participantLdapId != null) {
                    $scope.participant.participantLdapId = chosenGroup.participant.participantLdapId;
                    $scope.participant.object_type_s = chosenGroup.participant.object_type_s;

                    var currentAssignee = $scope.assignee;
                    var chosenOwningGroup = chosenGroup.participant.participantLdapId;
                    $scope.assigneeOptions = [];
                    $scope.iscurrentAssigneeInOwningGroup = false;
                    var size = 20;
                    var start = 0;
                    var searchQuery = '*';
                    var filter = 'fq=fq="object_type_s": USER' + '&fq="groups_id_ss": ' + chosenOwningGroup;
                    
                    var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuery(searchQuery, filter, size, start);
                    if (query) {
                        SearchService.queryFilteredSearch({
                            query: query
                        },
                        function (data) {
                            var returnedUsers = data.response.docs;
                            // Going through th collection of returnedUsers to see if there is a match with the current assignee
                            // if there is a match that means the current assignee is within that owning group hence no 
                            // changes to the current assignee is needed
                            _.each(returnedUsers, function (returnedUser) {
                                if (currentAssignee === returnedUser.object_id_s) {
                                    $scope.iscurrentAssigneeInOwningGroup = true;
                                }
                            });

                            if ($scope.participant.participantLdapId && $scope.iscurrentAssigneeInOwningGroup) {
                                $scope.owningGroup = chosenGroup.participant.selectedAssigneeName;
                                $scope.updateOwningGroup();
                            } else {
                                $scope.owningGroup = chosenGroup.participant.selectedAssigneeName;
                                $scope.assignee = '';

                                var assigneeParticipantType = 'assignee';
                                // Iterating through the array to find the participant with the ParticipantType eqaul assignee
                                // then setiing the participantLdapId to empty string
                                _.each($scope.objectInfo.participants, function(participant) {
                                    if(participant.participantType == assigneeParticipantType){
                                        participant.participantLdapId = '';
                                    }
                                });

                                $scope.updateOwningGroup();
                                $scope.updateAssignee(); 
                            }    
                        });
                    }
                }
            }, function(error) {    
            });
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.dateInfo = $scope.dateInfo || {};
            $scope.dateInfo.dueDate = UtilDateService.isoToDate($scope.objectInfo.dueDate);
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
            $scope.owningGroup = ObjectModelService.getGroup(objectInfo);

            //if (previousId != objectId) {
            ComplaintLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(
                function (approvers) {
                    var options = [];
                    _.each(approvers, function (approver) {
                        options.push({id: approver.userId, name: approver.fullName});
                    });
                    $scope.assignees = options;
                    return approvers;
                }
            );
        };

        /**
         * Persists the updated complaint metadata to the ArkComplaint data
         */
        function saveComplaint() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (ComplaintInfoService.validateComplaintInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = ComplaintInfoService.saveComplaintInfo(objectInfo);
                promiseSaveInfo.then(
                    function (complaintInfo) {
                        $scope.$emit("report-object-updated", complaintInfo);
                        return complaintInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                );
            }
            return promiseSaveInfo;
        }

        $scope.saveComplaint = function () {
            saveComplaint();
        };
        $scope.updateOwningGroup = function () {
            ObjectModelService.setGroup($scope.objectInfo, $scope.owningGroup);
            saveComplaint();
        };
        $scope.updateAssignee = function () {
            ObjectModelService.setAssignee($scope.objectInfo, $scope.assignee);
            saveComplaint();
        };
        $scope.updateDueDate = function (dueDate) {
            $scope.objectInfo.dueDate = UtilDateService.dateToIso($scope.dateInfo.dueDate);
            saveComplaint();
        };

    }
]);