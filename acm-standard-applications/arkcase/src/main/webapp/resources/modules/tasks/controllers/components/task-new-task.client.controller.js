'use strict';

angular.module('tasks').controller(
    'Tasks.NewTaskController',
    [
        '$scope',
        '$state',
        '$sce',
        '$translate',
        '$q',
        '$modal',
        'ConfigService',
        'UtilService',
        'TicketService',
        'LookupService',
        'MessageService',
        'Frevvo.FormService',
        'Task.NewTaskService',
        'Authentication',
        'Util.DateService',
        'Dialog.BootboxService',
        'ObjectService',
        'Object.LookupService',
        'Admin.FunctionalAccessControlService',
        'modalParams',
        'moment',
        'Person.InfoService',
        'PersonAssociation.Service',
        'Mentions.Service',
        function ($scope, $state, $sce, $translate, $q, $modal, ConfigService, Util, TicketService, LookupService, MessageService, FrevvoFormService, TaskNewTaskService, Authentication, UtilDateService, DialogService, ObjectService, ObjectLookupService, AdminFunctionalAccessControlService, modalParams, moment,
                  PersonInfoService, PersonAssociationService, MentionsService) {

            $scope.modalParams = modalParams;
            $scope.taskType = $scope.modalParams.taskType || 'ACM_TASK';
            $scope.config = null;
            $scope.userSearchConfig = null;
            $scope.objectSearchConfig = null;
            $scope.isAssocType = false;
            $scope.loading = false;

            $scope.groupTask = false;
            $scope.chosenGroup = '';

            $scope.filesToUpload = [];

            $scope.selectedBusinessProcessType = null;

            if ($scope.taskType === 'REVIEW_DOCUMENT') {
                $scope.documentsToReview = $scope.modalParams.documentsToReview;
                $scope.documentsToReviewIds = extractDocumentIds($scope.documentsToReview);
            }

            var taskModuleConfigPromise = ConfigService.getModuleConfig("tasks");
            var taskParentTypesPromise = ObjectLookupService.getLookupByLookupName("taskParentTypes");
            var groupsPromise = ObjectLookupService.getGroups();
            var userInfoPromise = Authentication.queryUserInfo();
            var caseTaskTypesPromise = ObjectLookupService.getLookupByLookupName('caseTaskTypes');
            var businessProcessTypesPromise = ObjectLookupService.getBusinessProcessTypes();
            var taskPersonTypesPromise = ObjectLookupService.getLookupByLookupName('taskPersonTypes');

            $q.all([taskModuleConfigPromise, taskParentTypesPromise, groupsPromise, userInfoPromise, caseTaskTypesPromise, businessProcessTypesPromise, taskPersonTypesPromise])
                .then(function (data) {
                    var moduleConfig = data[0];
                    var taskParentTypes = data[1];
                    var groups = data[2];
                    var userInfo = data[3];
                    var caseTaskTypes = data[4];
                    var businessProcessTypes = data[5];
                    var taskPersonTypes = data[6];

                    $scope.config = _.find(moduleConfig.components, {
                        id: "newTask"
                    });

                    $scope.userSearchConfig = _.find(moduleConfig.components, {
                        id: "userSearch"
                    });
                    $scope.objectSearchConfig = _.find(moduleConfig.components, {
                        id: "objectSearch"
                    });

                    $scope.userName = $scope.userFullName;
                    $scope.config.data.assignee = "";
                    $scope.config.data.taskStartDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");
                    $scope.config.data.dueDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");
                    $scope.config.data.priority = $scope.config.priority[1].id;
                    $scope.config.data.percentComplete = 0;

                    if (!Util.isEmpty($scope.modalParams.parentObject) && !Util.isEmpty($scope.modalParams.parentType) && !Util.isEmpty($scope.modalParams.parentId)) {
                        $scope.config.data.attachedToObjectName = $scope.modalParams.parentObject;
                        $scope.config.data.attachedToObjectType = $scope.modalParams.parentType;
                        $scope.config.data.attachedToObjectId = $scope.modalParams.parentId;

                        $scope.config.data.parentObjectName = $scope.modalParams.parentObject;
                        $scope.config.data.parentObjectType = $scope.modalParams.parentType;
                        $scope.config.data.parentObjectId = $scope.modalParams.parentId;
                       
                        if (!Util.isEmpty($scope.modalParams.parentTitle)) {
                            $scope.config.data.parentObjectTitle = $scope.modalParams.parentTitle;
                        }
                    }

                    $scope.taskParentTypes = taskParentTypes;
                    var defaultTaskParentType = ObjectLookupService.getPrimaryLookup($scope.taskParentTypes);
                    if (!$scope.config.data.attachedToObjectType && defaultTaskParentType) {
                        $scope.config.data.attachedToObjectType = defaultTaskParentType.key;
                    }

                    var options = [];
                    _.each(groups, function (group) {
                        options.push({
                            value: group.name,
                            text: group.name
                        });
                    });
                    $scope.assignableGroups = options;

                    $scope.userInfo = userInfo;
                    $scope.userFullName = userInfo.fullName;
                    $scope.userId = userInfo.userId;

                    $scope.caseTaskTypes = caseTaskTypes;

                    $scope.businessProcessTypes = businessProcessTypes;
                    var defaultBusinessProcessType = ObjectLookupService.getPrimaryLookup($scope.businessProcessTypes);
                    if (defaultBusinessProcessType) {
                        $scope.selectedBusinessProcessType = defaultBusinessProcessType;
                    } else {
                        $scope.selectedBusinessProcessType = businessProcessTypes[1].key;
                    }

                    $scope.taskPersonTypes = taskPersonTypes;
                    $scope.minStartDate = moment(new Date());
                    $scope.minDueDate = moment.utc($scope.config.data.taskStartDate).local();
                });

            $scope.opened = {};
            $scope.opened.openedStart = false;
            $scope.opened.openedEnd = false;
            $scope.saved = false;
            $scope.minStartDate = new Date();
            $scope.minDueDate = new Date($scope.minStartDate);

            // --------------  mention --------------
            $scope.params = {
                emailAddresses: [],
                usersMentioned: []
            };

            $scope.paramsSummernote = {
                emailAddresses: [],
                usersMentioned: []
            };

            $scope.startDateChanged = function (data) {
                if ($scope.config && $scope.config.data && $scope.config.data &&
                    moment($scope.config.data.taskStartDate).isAfter($scope.config.data.dueDate)) {
                    $scope.config.data.dueDate = data.dateInPicker.format($translate.instant("common.defaultDateTimeUTCFormat"));
                    $scope.dateChangedManually = true;
                }
                $scope.minDueDate = moment.utc($scope.config.data.taskStartDate).local();
            };

            $scope.saveNewTask = function () {
                $scope.saved = true;
                $scope.loading = true;
                if ($scope.config.data.attachedToObjectName === "") {
                    $scope.config.data.attachedToObjectName = "";
                    $scope.config.data.attachedToObjectId = "";
                }
                if (Util.isEmpty($scope.config.data.assignee) && $scope.config.data.candidateGroups.length < 1) {
                    $scope.config.data.assignee = $scope.userId;
                }
                var taskData = angular.copy($scope.config.data);
                taskData.dueDate = $scope.config.data.dueDate;
                taskData.taskStartDate = $scope.config.data.taskStartDate;
               
                if ($scope.documentsToReview && $scope.selectedBusinessProcessType != 'notDefinedWorkflow' && $scope.filesToUpload.length < 1) {
                    taskData.documentsToReview = processDocumentsUnderReview();
                    TaskNewTaskService.reviewDocuments(taskData, $scope.selectedBusinessProcessType).then(reviewDocumentTaskSuccessCallback, errorCallback);
                } else if ($scope.selectedBusinessProcessType != 'notDefinedWorkflow' && $scope.filesToUpload) {
                    //$scope.documentsToReview.push($scope.filesToUpload);
                    taskData.documentsToReview = processDocumentsUnderReview();
                    var formData = new FormData();
                    var data = new Blob([angular.toJson(JSOG.encode(Util.omitNg(taskData)))], {
                        type: 'application/json'
                    });
                    formData.append('task', data);
                    formData.append('businessProcessName', $scope.selectedBusinessProcessType);
                    angular.forEach($scope.filesToUpload, function (value) {
                        formData.append('files', value);
                    });
                    TaskNewTaskService.reviewNewDocuments(formData, $scope.selectedBusinessProcessType).then(reviewDocumentTaskSuccessCallback, errorCallback);
                } else {
                    taskData.documentsToReview = processDocumentsUnderReview();
                    var formData = new FormData();
                    var data = new Blob([angular.toJson(JSOG.encode(Util.omitNg(taskData)))], {
                        type: 'application/json'
                    });
                    formData.append('task', data);
                    angular.forEach($scope.filesToUpload, function (value) {
                        formData.append('files', value);
                    });
                    TaskNewTaskService.saveAdHocTask(formData).then(saveNewTaskSuccessCallback, errorCallback);

                }
            };


            function reviewDocumentTaskSuccessCallback(data) {
                $scope.saved = false;
                $scope.loading = false;
                MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned, ObjectService.ObjectTypes.TASK, ObjectService.ObjectTypes.TASK, data.data.taskId, data.data.title);
                MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.TASK, "DETAILS", data.data.taskId, data.data.details);
                var taskCreatedMessage = $translate.instant('tasks.newTask.informCreated');
                MessageService.info(taskCreatedMessage);
                $scope.onModalClose();
            }

            function saveNewTaskSuccessCallback(data) {
                $scope.saved = false;
                $scope.loading = false;
                if ($scope.contactPerson) {
                    savePersonAssociation({}, data);
                }
                MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned, ObjectService.ObjectTypes.TASK, ObjectService.ObjectTypes.TASK, data.data.taskId, data.data.title);
                MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.TASK, "DETAILS", data.data.taskId, data.data.details);
                if (data.data.attachedToObjectId == null) {
                    if ($scope.modalParams.returnState != null && $scope.modalParams.returnState != ':returnState') {
                        $state.go($scope.modalParams.returnState, {
                            type: $scope.modalParams.parentType,
                            id: $scope.modalParams.parentId
                        });
                    } else {
                        ObjectService.showObject(ObjectService.ObjectTypes.ADHOC_TASK, data.data.taskId);
                    }
                }
                var taskCreatedMessage = $translate.instant('tasks.newTask.informCreated');
                MessageService.info(taskCreatedMessage);
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
                if($scope.filesToUpload.length>=1 && (!$scope.documentsToReview || $scope.documentsToReview<1))
                {
                    $scope.documentsToReview = $scope.filesToUpload;
                } else {
                    for (var m = 0; m < $scope.filesToUpload.length; m++) {
                        $scope.documentsToReview.splice($scope.documentsToReview.length, 0, $scope.filesToUpload[m]);
                    }
                }
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
                if (Util.isEmpty($scope.modalParams.parentType)) {
                    $scope.config.data.attachedToObjectName = "";
                    $scope.config.data.attachedToObjectId = "";
                }
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

            $scope.onSelectAttachment = function (file) {
                if ($scope.opened.isFileAllowed) {
                    $scope.filesToUpload.push(file);
                } else {
                    var index = $scope.filesToUpload.indexOf(file);
                    $scope.filesToUpload.splice(index, 1);
                }
            };

            $scope.onFileUpload = function (files) {
                angular.forEach(files, function (file) {
                    $scope.filesToUpload.push(file)
                });
            };

            $scope.userOrGroupSearch = function () {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
                    controller: 'Tasks.UserSearchController',
                    size: 'lg',
                    backdrop: 'static',
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
                        if (selectedObjectType === 'USER') { // Selected user
                            var selectedUser = selection.masterSelectedItem;
                            var selectedGroup = selection.detailSelectedItems;

                            $scope.config.data.assignee = selectedUser.object_id_s;
                            $scope.userOrGroupName = selectedUser.name;
                            if (selectedGroup) {
                                $scope.config.data.candidateGroups = [selectedGroup.object_id_s];
                                $scope.groupName = selectedGroup.name;
                            }

                            return;
                        } else if (selectedObjectType === 'GROUP') { // Selected group
                            var selectedUser = selection.detailSelectedItems;
                            var selectedGroup = selection.masterSelectedItem;
                            if (selectedUser) {
                                $scope.config.data.assignee = selectedUser.object_id_s;
                                $scope.userOrGroupName = selectedUser.name;
                            }

                            $scope.config.data.candidateGroups = [selectedGroup.object_id_s];
                            $scope.groupName = selectedGroup.name;

                            return;
                        }
                    }

                }, function () {
                    // Cancel button was clicked.
                    return [];
                });

            };

            $scope.addPerson = function () {
                var params = {
                    showSetPrimary: false,
                    isDefault: false,
                    typeEnabled: false,
                    types: $scope.taskPersonTypes,
                    type: "Contact Person"
                };

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                    controller: 'Common.AddPersonModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    if (!data.personId) {
                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function (response) {
                            data['person'] = response.data;
                            $scope.contactPerson = data;
                            $scope.contactPersonName = response.data.givenName + ' ' + response.data.familyName;
                        });
                    } else {
                        PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                            data['person'] = person;
                            $scope.contactPerson = data;
                            $scope.contactPersonName = person.givenName + ' ' + person.familyName;
                        });
                    }
                });
            };

            function savePersonAssociation(association, data) {
                $scope.config.data;

                association.person = $scope.contactPerson.person;
                association.parentId = data.data.taskId;
                association.parentType = "TASK";
                association.personType = $scope.contactPerson.type;

                PersonAssociationService.savePersonAssociation(association);
            }

            $scope.updateDetailsFromTaskType = function () {
                var caseTaskType = _.find($scope.caseTaskTypes, function (caseTaskType) {
                    return caseTaskType.key === $scope.config.data.type;
                });

                if (caseTaskType && caseTaskType.description) {
                    $scope.config.data.details = caseTaskType.description;
                } else {
                    $scope.config.data.details = '';
                }
            };

            $scope.objectSearch = function () {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/tasks/views/components/task-object-search.client.view.html',
                    controller: 'Tasks.ObjectSearchController',
                    size: 'lg',
                    backdrop: 'static',
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

    ])
;
