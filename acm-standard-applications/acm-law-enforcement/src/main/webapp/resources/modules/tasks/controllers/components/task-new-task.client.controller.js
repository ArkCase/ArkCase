'use strict';

angular.module('tasks').controller('Tasks.NewTaskController', ['$scope', '$state', '$sce', '$q', '$modal'
    , 'ConfigService', 'UtilService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'Task.NewTaskService'
    , 'Authentication', 'Util.DateService', 'Dialog.BootboxService', 'ObjectService', 'Object.LookupService', 'Admin.FunctionalAccessControlService'
    , 'modalParams', 'moment'
    , function ($scope, $state, $sce, $q, $modal, ConfigService, Util, TicketService, LookupService
        , FrevvoFormService, TaskNewTaskService, Authentication, UtilDateService, DialogService, ObjectService, ObjectLookupService
        , AdminFunctionalAccessControlService, modalParams, moment) {

        $scope.modalParams = modalParams;
        $scope.taskType = $scope.modalParams.taskType || 'ACM_TASK';
        $scope.config = null;
        $scope.userSearchConfig = null;
        $scope.objectSearchConfig = null;
        $scope.isAssocType = false;
        $scope.loading = false;

        $scope.groupTask = false;
        $scope.chosenGroup = '';

        $scope.options = {
            focus: true,
            dialogsInBody: true
            //,height: 120
        };

        if ($scope.taskType === 'REVIEW_DOCUMENT') {
            $scope.documentsToReview = $scope.modalParams.documentsToReview;
            $scope.documentsToReviewIds = extractDocumentIds($scope.documentsToReview);
            $scope.selectedBusinessProcessType = null;
            ObjectLookupService.getBusinessProcessTypes().then(
                function (res) {
                    $scope.businessProcessTypes = res;
                });
        }

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userInfo = userInfo;
                $scope.userFullName = userInfo.fullName;
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ObjectLookupService.getGroups().then(
            function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({value: group.name, text: group.name});
                });
                $scope.assignableGroups = options;
                return groups;
            }
        );

        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "newTask"});

            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});
            $scope.objectSearchConfig = _.find(moduleConfig.components, {id: "objectSearch"});

            $scope.userName = $scope.userFullName;
            $scope.config.data.assignee = "";
            $scope.config.data.taskStartDate = new Date();
            $scope.config.data.dueDate = new Date();
            $scope.config.data.priority = $scope.config.priority[1].id;
            $scope.config.data.percentComplete = 0;


            if (!Util.isEmpty($scope.modalParams.parentObject) && !Util.isEmpty($scope.modalParams.parentType) && !Util.isEmpty($scope.modalParams.parentId)) {
                $scope.config.data.attachedToObjectName = $scope.modalParams.parentObject;
                $scope.config.data.attachedToObjectType = $scope.modalParams.parentType;
                $scope.config.data.attachedToObjectId = $scope.modalParams.parentId;
                if (!Util.isEmpty($scope.modalParams.parentTitle)) {
                    $scope.config.data.parentObjectTitle = $scope.modalParams.parentTitle;
                }
            }

            return moduleConfig;
        });

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
        $scope.saved = false;
        $scope.minStartDate = new Date();
        $scope.minDueDate = new Date();

        $scope.isValidDate = function () {
            if (Util.isEmpty($scope.config.data.taskStartDate) || Util.isEmpty($scope.config.data.dueDate)) {
                return false;
            }

            return true;
        };

        $scope.startDateChanged = function () {
            var todayDate = new Date();
            if (Util.isEmpty($scope.config.data.taskStartDate) || moment($scope.config.data.taskStartDate).isBefore(todayDate)) {
                $scope.config.data.taskStartDate = todayDate;
            } else {
                $scope.config.data.taskStartDate = UtilDateService.convertToCurrentTime($scope.config.data.taskStartDate);
            }

            if (moment($scope.config.data.taskStartDate).isAfter($scope.config.data.dueDate)) {
                $scope.config.data.dueDate = UtilDateService.convertToCurrentTime($scope.config.data.taskStartDate);
            }

            $scope.config.data.dueDate = UtilDateService.convertToSecondTime($scope.config.data.dueDate, $scope.config.data.taskStartDate);
            $scope.minDueDate = $scope.config.data.taskStartDate;
            $scope.validDate = $scope.isValidDate();
        };

        $scope.dueDateChanged = function () {
            var todayDate = new Date();
            if (Util.isEmpty($scope.config.data.dueDate)) {
                $scope.config.data.dueDate = todayDate;
            } else {
                $scope.config.data.dueDate = UtilDateService.convertToCurrentTime($scope.config.data.dueDate);
            }

            if (moment($scope.config.data.dueDate).isBefore($scope.config.data.taskStartDate)) {
                $scope.config.data.dueDate = UtilDateService.convertToCurrentTime($scope.config.data.taskStartDate);
            }

            $scope.config.data.taskStartDate = UtilDateService.convertToSecondTime($scope.config.data.taskStartDate, $scope.config.data.dueDate);
            $scope.validDate = $scope.isValidDate();
        };

        $scope.saveNewTask = function () {
            $scope.saved = true;
            $scope.loading = true;
            if ($scope.config.data.attachedToObjectName === "") {
                $scope.config.data.attachedToObjectName = "";
                $scope.config.data.attachedToObjectId = "";
            }
            var taskData = angular.copy($scope.config.data);
            taskData.dueDate = moment.utc(UtilDateService.dateToIso($scope.config.data.dueDate));
            taskData.taskStartDate = moment.utc(UtilDateService.dateToIso($scope.config.data.taskStartDate));
            if ($scope.taskType === 'REVIEW_DOCUMENT' && $scope.documentsToReview) {
                taskData.documentsToReview = processDocumentsUnderReview();
                TaskNewTaskService.reviewDocuments(taskData, $scope.selectedBusinessProcessType).then(reviewDocumentTaskSuccessCallback, errorCallback);
            } else {
                TaskNewTaskService.saveAdHocTask(taskData).then(saveNewTaskSuccessCallback, errorCallback);
            }
        };

        function reviewDocumentTaskSuccessCallback(data) {
            $scope.saved = false;
            $scope.loading = false;
            $scope.onModalClose();
        }

        function saveNewTaskSuccessCallback(data) {
            $scope.saved = false;
            $scope.loading = false;
            if ($scope.modalParams.returnState != null && $scope.modalParams.returnState != ':returnState') {
                $state.go($scope.modalParams.returnState, {
                    type: $scope.modalParams.parentType,
                    id: $scope.modalParams.parentId
                });
            } else {
                ObjectService.showObject(ObjectService.ObjectTypes.ADHOC_TASK, data.data.taskId);
            }
            $scope.onModalClose();
        }

        function errorCallback(err) {
            $scope.saved = false;
            $scope.loading = false;
            if (!Util.isEmpty(err)) {
                var statusCode = Util.goodMapValue(err, 'status');
                var message = Util.goodMapValue(err, 'data.message');

                if (statusCode == 400) {
                    DialogService.alert(message);
                }
            }
        }

        $scope.updateBusinessProcessType = function (selectedBusinessProcessType) {
            $scope.selectedBusinessProcessType = selectedBusinessProcessType;
        };

        function extractDocumentIds(selectedNodes) {
            var fileIds = [];
            if (Util.isArray(selectedNodes)) {
                for (var i = 0; i < selectedNodes.length; i++) {
                    fileIds.push(Util.goodMapValue(selectedNodes[i], 'data.objectId'));
                }
            }
            return fileIds;
        }

        function processDocumentsUnderReview() {
            var processedDocuments = [];
            angular.forEach($scope.documentsToReviewIds, function (value) {
                var doc = _.find($scope.documentsToReview, function (d) {
                    return d.data.objectId === value;
                });
                processedDocuments.push({
                    fileId: doc.data.objectId,
                    fileName: doc.data.name
                });
            });

            return processedDocuments;
        }

        $scope.onSelectFile = function (fileId) {
            var idx = $scope.documentsToReviewIds.indexOf(fileId);

            if (idx > -1) {
                $scope.documentsToReviewIds.splice(idx, 1);
            } else {
                $scope.documentsToReviewIds.push(fileId);
            }
        };

        $scope.updateAssocParentType = function () {
            $scope.isAssocType = $scope.config.data.attachedToObjectType !== '';
        };

        $scope.inputClear = function () {
            $scope.config.data.attachedToObjectName = "";
            $scope.config.data.attachedToObjectId = "";
        };

        //groupChange function
        $scope.groupChange = function () {
            $scope.config.data.candidateGroups = [$scope.chosenGroup];
        };

        $scope.groupTaskToggle = function () {
            //Clear relevant information
            $scope.config.data.candidateGroups = [];
            $scope.chosenGroup = "";
            $scope.config.data.assignee = null;
            $scope.userName = "";
        };

        $scope.userOrGroupSearch = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
                controller: 'Tasks.UserSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.userOrGroupSearch.userOrGroupFacetFilter;
                    },
                    $extraFilter: function () {
                        return $scope.config.userOrGroupSearch.userOrGroupFacetExtraFilter;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });


            modalInstance.result.then(function (selection) {

                if (selection) {
                    var selectedObjectType = selection.masterSelectedItem.object_type_s;
                    if (selectedObjectType === 'USER') {  // Selected user
                        var selectedUser = selection.masterSelectedItem;
                        var selectedGroup = selection.detailSelectedItems;

                        $scope.config.data.assignee = selectedUser.object_id_s;
                        $scope.userOrGroupName = selectedUser.name;
                        if (selectedGroup) {
                            $scope.config.data.candidateGroups = [selectedGroup.object_id_s];
                            $scope.groupName = selectedGroup.name;
                        }

                        $scope.config.data.candidateGroups = [selectedGroup.object_id_s];
                        $scope.groupName = selectedGroup.name;

                        return;
                    } else if (selectedObjectType === 'GROUP') {  // Selected group
                        var selectedUser = selection.detailSelectedItems;
                        var selectedGroup = selection.masterSelectedItem;
                        if (selectedUser) {
                            $scope.config.data.assignee = selectedUser.object_id_s;
                            $scope.userOrGroupName = selectedUser.name;
                        }

                        $scope.config.data.assignee = selectedUser.object_id_s;
                        $scope.config.data.candidateGroups = [selectedGroup.object_id_s];
                        $scope.userOrGroupName = selectedUser.name;
                        $scope.groupName = selectedGroup.name;

                        return;
                    }
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        $scope.objectSearch = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-object-search.client.view.html',
                controller: 'Tasks.ObjectSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.objectSearch.objectFacetFilter + $scope.config.data.attachedToObjectType;
                    },
                    $config: function () {
                        return $scope.objectSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenObject) {
                if (chosenObject) {
                    $scope.config.data.attachedToObjectName = chosenObject.name;
                    $scope.config.data.attachedToObjectId = chosenObject['object_id_s'];

                    return;
                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        $scope.cancelModal = function () {
            $scope.onModalDismiss();
        };
    }
]);
