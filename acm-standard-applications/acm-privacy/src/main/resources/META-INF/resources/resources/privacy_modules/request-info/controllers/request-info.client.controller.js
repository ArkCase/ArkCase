'use strict';

/**
 * @ngdoc controller
 * @name request-info.controller:RequestInfoController
 *
 * @description
 * {@link https://github.com/Armedia/bactes360/blob/develop/bactes-user-interface/src/main/resources/META-INF/resources/resources/modules/request-info/controllers/request-info.client.controller.js modules/request-info/controllers/request-info.client.controller.js}
 *
 * The request-info module main controller
 */
angular.module('request-info').controller(
    'RequestInfoController',
    [
        '$rootScope',
        '$scope',
        '$log',
        '$sce',
        '$q',
        '$state',
        '$timeout',
        '$stateParams',
        '$modal',
        'ConfigService',
        'Authentication',
        'RequestInfo.RequestsService',
        'RequestInfo.WorkflowsService',
        'Requests.RequestsService',
        'LookupService',
        'TicketService',
        'Queues.QueuesService',
        'PermissionsService',
        'Case.InfoService',
        'ObjectService',
        'Helper.ObjectBrowserService',
        'Object.LookupService',
        'Object.ModelService',
        'Case.LookupService',
        'Util.DateService',
        'QueuesService',
        'Object.SubscriptionService',
        'UtilService',
        'SnowboundService',
        'EcmService',
        'DocumentPrintingService',
        'Object.NoteService',
        'Profile.UserInfoService',
        'MessageService',
        '$translate',
        'DueDate.Service',
        'Admin.HolidayService',
        'Admin.PrivacyConfigService',
        'Admin.TranscriptionManagementService',
        '$window',
        'ArkCaseCrossWindowMessagingService',
        'Object.LockingService',
        'Util.TimerService',
        'Dialog.BootboxService',
        'FileEditingEnabled',
        function ($rootScope, $scope, $log, $sce, $q, $state, $timeout, $stateParams, $modal, ConfigService, Authentication, RequestsService, WorkflowsService, GenericRequestsService, LookupService, TicketService, QueuesService, PermissionsService, CaseInfoService, ObjectService,
                  HelperObjectBrowserService, ObjectLookupService, ObjectModelService, CaseLookupService, UtilDateService, QueuesSvc, ObjectSubscriptionService, Util, SnowboundService, EcmService, DocumentPrintingService, NotesService, UserInfoService, MessageService, $translate,
                  DueDateService, AdminHolidayService, AdminPrivacyConfigService, TranscriptionManagementService, $window, ArkCaseCrossWindowMessagingService, ObjectLockingService, UtilTimerService, DialogService, FileEditingEnabled) {

            if (sessionStorage.getItem("startRow") == null) {
                sessionStorage.setItem("startRow", 0);
            }
            var nextQueueId = -1;
            $scope.isLastRequest = false;
            $scope.openOtherDocuments = [];
            var nextAvailableRequests = [];
            // $scope.fileChangeEvents = [];
            // $scope.fileChangeDate = null;
            // $scope.versionChangeRequest = false;

            $scope.loading = false;
            $scope.loadingIcon = "fa fa-check";

            $scope.saveIcon = false;
            $scope.saveLoadingIcon = "fa fa-floppy-o";
            $scope.viewerOnly = false;
            $scope.loaderOpened = false;
            $scope.fileEditingEnabled = false;
            $scope.fileReloadDisabled = false;

            FileEditingEnabled.getFileEditingEnabled().then(function (response) {
                $scope.fileEditingEnabled = response.data;
            });

            $scope.showEditingButton = function () {
                return !isAnyFileRecord() && !$scope.editingMode && $scope.fileEditingEnabled;
            };

            function isAnyFileRecord() {
                return _.some($scope.openOtherDocuments, function (value) {
                    return value.status === 'RECORD';
                });
            }

            $scope.documentExpand = function () {
                $scope.viewerOnly = true;
            };
            $scope.documentCompress = function () {
                $scope.viewerOnly = false;
            };
            $scope.checkEscape = function (event) {
                if (27 == event.keyCode) { //27 is Escape key code
                    $scope.viewerOnly = false;
                }
            };
            $scope.videoAPI = null;

            $scope.videoExpand = function () {
                if (!Util.isEmpty($scope.videoAPI)) {
                    $scope.videoAPI.toggleFullScreen();
                }
            };

            $scope.showFailureMessage = function showFailureMessage() {
                DialogService.alert($scope.transcribeObjectModel.failureReason.split(".", 1));
            };

            function onShowLoader() {
                var loaderModal = $modal.open({
                    animation: true,
                    templateUrl: 'modules/common/views/object.modal.loading-spinner.html',
                    size: 'sm',
                    backdrop: 'static'
                });
                $scope.loaderModal = loaderModal;
                $scope.loaderOpened = true;
            }

            function onHideLoader() {
                $scope.loaderModal.close();
                $scope.loaderOpened = false;
            }

            function onShowProgressBar(data) {
                $scope.fileReloadDisabled = true;
                var fileDetails = {};
                fileDetails.fileId = data.fileId;
                var ecmFile = _.find($scope.openOtherDocuments, function (file) {
                    return file.fileId == data.fileId;
                });
                fileDetails.file = ecmFile;
                fileDetails.fileName = ecmFile.name;
                fileDetails.fileType = ecmFile.fileType;
                fileDetails.pageCount = ecmFile.pageCount;
                fileDetails.lang = ecmFile.fileLang;
                fileDetails.originObjectId = $scope.requestInfo.id;
                fileDetails.originObjectType = $scope.requestInfo.requestType;
                fileDetails.parentObjectNumber = $scope.requestInfo.caseNumber;
                fileDetails.status = ObjectService.UploadFileStatus.READY;
                $scope.$bus.publish('open-progress-bar-modal', fileDetails);
            }

            function onUpdateProgressBar(data) {
                var message = {};
                message.id = data.fileId;
                message.objectId = $scope.requestInfo.id;
                message.objectType = $scope.requestInfo.requestType;
                message.success = true;
                message.currentProgress = 99;
                message.status = ObjectService.UploadFileStatus.IN_PROGRESS
                $scope.$bus.publish('update-modal-progressbar-current-progress', message);
            }

            function onHideProgressBar(data) {
                var message = {};
                message.id = data.fileId;
                message.objectId = $scope.requestInfo.id;
                message.objectType = $scope.requestInfo.requestType;
                message.currentProgress = 100;
                if (data.status === 'OK') {
                    message.success = true;
                    message.status = ObjectService.UploadFileStatus.FINISHED;
                } else {
                    message.success = false;
                    message.status = ObjectService.UploadFileStatus.FAILED;
                }
                $scope.$bus.publish('finish-modal-progressbar-current-progress', message);
            }


            $scope.iframeLoaded = function () {
                ArkCaseCrossWindowMessagingService.addHandler('show-loader', onShowLoader);
                ArkCaseCrossWindowMessagingService.addHandler('hide-loader', onHideLoader);
                ArkCaseCrossWindowMessagingService.addHandler('show-progress-bar', onShowProgressBar);
                ArkCaseCrossWindowMessagingService.addHandler('update-progress-bar', onUpdateProgressBar);
                ArkCaseCrossWindowMessagingService.addHandler('hide-progress-bar', onHideProgressBar);

                ArkCaseCrossWindowMessagingService.addHandler('close-document', onCloseDocument);
                ArkCaseCrossWindowMessagingService.addHandler('document-saved', onDocumentSave);
                ArkCaseCrossWindowMessagingService.addHandler('annotation-status-changed', onAnnotationStatusChange);

                ObjectLookupService.getLookupByLookupName("annotationTags").then(function (allAnnotationTags) {
                    $scope.allAnnotationTags = allAnnotationTags;
                    ArkCaseCrossWindowMessagingService.addHandler('select-annotation-tags', onSelectAnnotationTags);
                    ArkCaseCrossWindowMessagingService.start('snowbound', $scope.ecmFileProperties['ecm.viewer.snowbound']);
                });
                onHideLoader();
            };

            function onCloseDocument(data) {
                $scope.$bus.publish('remove-from-opened-documents-list', {id: data.id, version: data.version});
            }

            function onDocumentSave(data) {
                $scope.$bus.publish('reload-exemption-code-grid', {
                    id: $scope.objectInfo.id,
                    fileId: data.fileId
                });
            }

            function onAnnotationStatusChange(data) {
                $scope.$bus.publish('reload-exemption-code-grid', {
                    id: $scope.objectInfo.id,
                    fileId: data.fileId
                });
            }

            function onSelectAnnotationTags(data) {
                var params = $scope.allAnnotationTags;
                // from Snowbound v5.2 we have data.selectedAnnotations
                if (data.selectedAnnotations) {
                    params.annotationTags = $scope.allAnnotationTags;
                    params.existingAnnotationTags = data.selectedAnnotations;
                }
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/document-details/views/components/annotation-tags-modal.client.view.html',
                    controller: 'Document.AnnotationTagsModalController',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (result) {
                    var message = {
                        source: 'arkcase',
                        action: 'add-annotation-tags',
                        data: {
                            type: data.type,
                            annotationTags: result.annotationTags,
                            annotationNotes: result.annotationNotes
                        }
                    };
                    ArkCaseCrossWindowMessagingService.send(message);
                }, function () {
                    // Do nothing
                });
            }

            var transcriptionConfigurationPromise = TranscriptionManagementService.getTranscribeConfiguration();

            $scope.getEcmFileActiveVersion = function (ecmFile) {
                if (Util.isEmpty(ecmFile) || Util.isEmpty(ecmFile.activeVersionTag) || Util.isArrayEmpty(ecmFile.versions)) {
                    return null;
                }

                var activeVersion = _.find(ecmFile.versions, function (version) {
                    return ecmFile.activeVersionTag === version.versionTag;
                });

                return activeVersion;
            };

            $scope.$on('transcribe-data-model', function (event, transcribeObj) {
                $scope.transcribeObjectModel = transcribeObj;

                var track = null;
                var videoElement = angular.element(document.getElementsByTagName("video")[0])[0];
                if (videoElement) {
                    track = videoElement.addTextTrack("subtitles", "Transcription", $scope.transcribeObjectModel.language);
                    videoElement.addEventListener("play", function () {
                        track.mode = "showing";
                    });
                }

                angular.forEach($scope.transcribeObjectModel.transcribeItems, function (value, key) {
                    if (track != null) {
                        addCue(track, value);
                    }
                });

                //color the status
                $scope.colorTranscribeStatus = function () {
                    switch ($scope.transcribeObjectModel.status) {
                        case 'QUEUED':
                            return '#dcce22';
                        case 'PROCESSING':
                            return 'orange';
                        case 'COMPLETED':
                            return '#05a205';
                        case 'FAILED':
                            return 'red';
                    }
                };

                if (!Util.isEmpty($stateParams.seconds)) {
                    $scope.playAt($stateParams.seconds);
                }
            });

            var addCue = function (track, value) {
                var cueAdded = false;
                try {
                    track.addCue(new VTTCue(value.startTime, value.endTime, value.text));
                    cueAdded = true;
                } catch (e) {
                    $log.warn("Browser does not support VTTCue");
                }

                if (!cueAdded) {
                    try {
                        track.addCue(new TextTrackCue(value.startTime, value.endTime, value.text));
                        cueAdded = true;
                    } catch (e) {
                        $log.warn("Browser does not support TextTrackCue");
                    }
                }
            };

            $scope.playAt = function (seconds) {
                var videoElement = angular.element(document.getElementsByTagName("video")[0])[0];
                if (videoElement) {
                    videoElement.pause();
                    videoElement.currentTime = seconds;
                    videoElement.play();
                }
            };

            $scope.$bus.subscribe('update-viewer-opened-versions', function (openedVersions) {
                $scope.fileInfo.selectedIds = openedVersions.map(function (openedVersion, index) {
                    if (index == 0) {
                        $scope.fileInfo.id = $stateParams['selectedIds'] + ":" + openedVersion.versionTag;
                    }
                    return $stateParams['selectedIds'] + ":" + openedVersion.versionTag;
                }).join(',');

                $scope.openSnowboundViewer();
            });

            $scope.$on('notificationGroupSaved', function () {
                $scope.onClickNextQueue($scope.nameButton, "true");
            });

            $scope.$bus.subscribe('open-new-version-of-file', function () {
                if ($scope.openOtherDocuments && $scope.openOtherDocuments.length > 1) {
                    var fileIds = [];
                    for (var i = 0; i < $scope.openOtherDocuments.length; i++) {
                        fileIds.push($scope.openOtherDocuments[i].fileId);
                    }
                    var params = {};
                    params.fileIds = fileIds;
                    EcmService.getEcmFiles(params).then(function (files) {
                        $scope.openOtherDocuments = [];
                        for (var i = 0; i < files.length; i++) {
                            var file = files[i];
                            if (file.fileId) {
                                var fileInfo = buildFileInfo(file, file.container.id);
                                $scope.openOtherDocuments.push(fileInfo);
                            }
                        }
                        $scope.fileReloadDisabled = false;
                        openViewerMultiple();
                        onHideLoader();
                    });
                } else {
                    var ecmFile = EcmService.getFile({
                        fileId: $scope.ecmFile.fileId
                    });
                    ecmFile.$promise.then(function (file) {
                        if ($scope.fileInfo.id !== file.fileId + ':' + file.activeVersionTag) {
                            $scope.ecmFile = file;
                            $scope.fileId = file.fileId;
                            $scope.fileInfo.id = file.fileId + ':' + file.activeVersionTag;
                            $scope.fileInfo.selectedIds = file.fileId + ':' + file.activeVersionTag;
                            $scope.fileInfo.versionTag = file.activeVersionTag;
                            $scope.fileReloadDisabled = false;
                            openViewerMultiple();
                        }
                        onHideLoader();
                    });
                }
            });

            new HelperObjectBrowserService.Content({
                scope: $scope,
                state: $state,
                stateParams: $stateParams,
                moduleId: "request-info",
                resetObjectInfo: CaseInfoService.resetCaseInfo,
                getObjectInfo: CaseInfoService.getCaseInfo,
                updateObjectInfo: CaseInfoService.updateCaseInfo,
                getObjectTypeFromInfo: function (objectInfo) {
                    return ObjectService.ObjectTypes.CASE_FILE;
                }
            });

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "request-info",
                componentId: "main",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onObjectInfoRetrieved: function (objectInfo, e) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var requestId = $stateParams['id'];

            // Be sure that user activity service stopped after module destroy and lock released
            $scope.$on('$destroy', function () {
                releaseRequestLock(requestId);
            });

            $scope.$on('req-component-config', onConfigRequest);

            function onConfigRequest(e, componentId) {
                ConfigService.getModuleConfig('request-info').then(function (config) {
                    var componentConfig = _.find(config.components, {
                        id: componentId
                    });
                    $scope.$broadcast('component-config', componentId, componentConfig);
                });
            }

            ConfigService.getModuleConfig('request-info').then(function (config) {
                $scope.config = config;
                $scope.requiredFields = config.requiredFields;

                var reqConfig = _.find(config.components, {
                    id: "requests"
                });
                $scope.categories = reqConfig.categories;
                $scope.requestTypes = reqConfig.requestTypes;
            });

            ObjectLookupService.getPriorities().then(function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({
                        value: priority,
                        text: priority
                    });
                });
                $scope.priorities = options;
                return priorities;
            });

            ObjectLookupService.getGroups().then(function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({
                        value: group.name,
                        text: group.name
                    });
                });
                $scope.owningGroups = options;
                return groups;
            });

            AdminHolidayService.getHolidays().then(function (response) {
                $scope.holidays = response.data.holidays;
                $scope.includeWeekends = response.data.includeWeekends;
            });

            AdminPrivacyConfigService.getPrivacyConfig().then(function (response) {
                $scope.extensionWorkingDays = response.data.requestExtensionWorkingDays;
                $scope.requestExtensionWorkingDaysEnabled = response.data.requestExtensionWorkingDaysEnabled;
                $scope.expediteWorkingDays = response.data.expediteWorkingDays;
                $scope.expediteWorkingDaysEnabled = response.data.expediteWorkingDaysEnabled;
            }, function (err) {
                MessageService.errorAction();
            });

            $scope.opened = {};
            $scope.opened.openedStart = false;
            $scope.opened.openedEnd = false;

            $scope.openedScanned = {};
            $scope.openedScanned.openedStart = false;
            $scope.openedScanned.openedEnd = false;

            $scope.openedRecordSearchDateFrom = {};
            $scope.openedRecordSearchDateFrom.openedStart = false;
            $scope.openedRecordSearchDateFrom.openedEnd = false;

            $scope.openedRecordSearchDateTo = {};
            $scope.openedRecordSearchDateTo.openedStart = false;
            $scope.openedRecordSearchDateTo.openedEnd = false;

            $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;

            $scope.availableQueues = [];

            $scope.picker = {
                opened: false
            };
            $scope.onPickerClick = function () {
                $timeout(function () {
                    $scope.picker.opened = true;
                });
            };

            $scope.requestButtons = null;
            $scope.$sce = $sce; // used to allow snowbound url (on a different domain) to be injected by angular

            $scope.requestSubscribed = false;
            $scope.subscribeEnabled = false;

            $scope.printEnabled = false;

            $scope.readOnly = true;
            $scope.assignedPerson = null;
            $scope.expandPreview = expandPreview;

            $scope.acmTicket = '';
            $scope.ecmFileProperties = '';
            $scope.userId = '';
            $scope.userFullName = '';
            $scope.ecmFileProperties = {};
            $scope.snowboundUrl = '';
            $scope.ecmFileEvents = [];
            $scope.ecmFileParticipants = [];
            $scope.userList = [];
            $scope.caseInfo = {};
            $scope.fileInfo = {
                id: $stateParams['id'],
                containerId: $stateParams['containerId'],
                containerType: $stateParams['containerType'],
                name: $stateParams['name'],
                selectedIds: $stateParams['selectedIds']
            };
            $scope.showVideoPlayer = false;
            $scope.showPdfJs = false;
            $scope.transcriptionTabActive = false;
            $scope.transcriptionTabViewEnabled = false;

            // Obtains authentication token for ArkCase
            var ticketInfo = TicketService.getArkCaseTicket();

            // Obtains the currently logged in user
            var userInfo = Authentication.queryUserInfo();

            // Obtains a list of all users in ArkCase
            var totalUserInfo = LookupService.getUsers();

            // Retrieves the properties from the ecmFileService.properties file (including Snowbound configuration)
            var ecmFileConfig = LookupService.getConfig("ecmFileService");

            var formsConfig = LookupService.getConfig("acm-forms");

            // Retrieves the metadata for the file which is being opened in the viewer
            var ecmFileInfo = EcmService.getFile({
                fileId: $stateParams['fileId']
            });
            var ecmFileEvents = EcmService.getFileEvents({
                fileId: $stateParams['fileId']
            });

            var ecmFileParticipants = EcmService.getFileParticipants({
                fileId: $stateParams['fileId']
            });

            // Be sure that request info is loaded before we check lock permission
            var requestLockDeferred = $q.defer();

            if ($state.current.name === 'request-info.tasks') {
                $scope.tasksTabActive = true;
            } else {
                $scope.detailsTabActive = true;
            }

            var onObjectInfoRetrieved = function (objectInfo) {

                var group = ObjectModelService.getGroup(objectInfo);
                $scope.owningGroup = group;
                var assignee = ObjectModelService.getAssignee(objectInfo);
                $scope.assignee = assignee;
                nextQueueId = objectInfo.queue.id;
                if (sessionStorage.getItem("firstOpenedRequestId") === null) {
                    sessionStorage.setItem("firstOpenedRequestId", objectInfo.id);
                }
                RequestsService.getNextAvailableRequestInQueue({queueId: nextQueueId, createdDate: objectInfo.created})
                    .$promise.then(function (data) {
                    $scope.hasNextRequest = data.availableRequests > 0;
                });

                $scope.originalDueDate = objectInfo.dueDate;

                $scope.requestInfo = objectInfo;
                $scope.dateInfo = $scope.dateInfo || {};
                $scope.dateInfo.dueDate = UtilDateService.isoToLocalDateTIme($scope.requestInfo.dueDate);
                if (!Util.isEmpty($scope.objectInfo.recordSearchDateFrom)) {
                    $scope.objectInfo.recordSearchDateFrom = moment(objectInfo.recordSearchDateFrom).format(UtilDateService.defaultDateTimeFormat);
                }
                if (!Util.isEmpty($scope.objectInfo.recordSearchDateTo)) {
                    $scope.objectInfo.recordSearchDateTo = moment(objectInfo.recordSearchDateTo).format(UtilDateService.defaultDateTimeFormat);
                }
                if (!Util.isEmpty($scope.objectInfo.releasedDate)) {
                    $scope.objectInfo.releasedDate = moment(objectInfo.releasedDate).format(UtilDateService.defaultDateTimeFormat);
                }
                ObjectLookupService.getRequestCategories().then(function (requestCategories) {
                    $scope.requestCategories = requestCategories;
                });

                ObjectLookupService.getDeliveryMethodOfResponses().then(function (deliveryMethodOfResponses) {
                    $scope.deliveryMethodOfResponses = deliveryMethodOfResponses;
                    if ($scope.requestInfo.deliveryMethodOfResponse != null && $scope.requestInfo.deliveryMethodOfResponse != '') {
                        $scope.deliveryMethodOfResponseValue = _.find($scope.deliveryMethodOfResponses, function (deliveryMethodOfResponse) {
                            if (deliveryMethodOfResponse.key == $scope.requestInfo.deliveryMethodOfResponse) {
                                return deliveryMethodOfResponse.key;
                            }
                        });
                        $scope.requestInfo.deliveryMethodOfResponse = $scope.deliveryMethodOfResponseValue.key;
                    } else {
                        $scope.requestInfo.deliveryMethodOfResponse = $scope.deliveryMethodOfResponses[0].key;
                    }
                });

                $scope.printEnabled = objectInfo.queue.name === "Release";

                QueuesSvc.queryNextPossibleQueues(objectInfo.id).then(function (data) {
                    var availableQueues = data.nextPossibleQueues;
                    var defaultNextQueue = data.defaultNextQueue;
                    var defaultReturnQueue = data.defaultReturnQueue;

                    if (defaultNextQueue || defaultReturnQueue) {
                        //if there is default next or return queue, then remove it from the list
                        //and add Complete, Next and Return queue aliases into list
                        _.remove(availableQueues, function (currentObject) {
                            return currentObject === defaultNextQueue || currentObject === defaultReturnQueue;
                        });
                        if (defaultReturnQueue) {
                            availableQueues.unshift("Return");
                        }
                        if (defaultNextQueue) {
                            availableQueues.push("Complete");
                        }
                    }
                    availableQueues = availableQueues.map(function (item) {
                        var tmpObj = {};
                        tmpObj.name = item;
                        if (item != 'Complete' && item != 'Next' && item != 'Hold') {
                            tmpObj.disabled = true;
                        }
                        return tmpObj;
                    });

                    $scope.availableQueues = availableQueues;
                    $scope.defaultNextQueue = defaultNextQueue;
                    $scope.defaultReturnQueue = defaultReturnQueue;

                });

                CaseLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(function (approvers) {
                    var options = [];
                    _.each(approvers, function (approver) {
                        options.push({
                            id: approver.userId,
                            name: approver.fullName
                        });
                    });
                    $scope.assignees = options;
                    return approvers;
                });


                $scope.$broadcast('request-info-retrieved', $scope.requestInfo);

                if ($scope.requestInfo.queue) {
                    $scope.$bus.publish('required-fields-retrieved', $scope.requiredFields[$scope.requestInfo.queue.name]);
                }
                
                $scope.previousDueDate = objectInfo.dueDate;
                if(objectInfo.requestTrack === 'expedite'){
                    if ($scope.includeWeekends) {
                        $scope.previousDueDate = DueDateService.dueDateWithWeekends($scope.objectInfo.dueDate, $scope.expediteWorkingDays, $scope.holidays);
                    } else {
                        $scope.previousDueDate = DueDateService.dueDateWorkingDays($scope.objectInfo.dueDate, $scope.expediteWorkingDays, $scope.holidays);
                    }
                }
                $scope.originalDueDate = $scope.previousDueDate;
            };

            var getCaseInfo = CaseInfoService.getCaseInfo($stateParams['id']);

            $q.all([ticketInfo, userInfo, totalUserInfo, ecmFileConfig, ecmFileInfo.$promise, ecmFileEvents.$promise, ecmFileParticipants.$promise, formsConfig, transcriptionConfigurationPromise, getCaseInfo]).then(function (data) {
                $scope.acmTicket = data[0].data;
                $scope.userId = data[1].userId;
                $scope.userFullName = data[1].fullName;
                $scope.userList = data[2];
                $scope.ecmFileProperties = data[3];
                $scope.editingMode = !$scope.ecmFileProperties['ecm.viewer.snowbound.readonly.initialState'];
                $scope.ecmFile = data[4];
                $scope.ecmFileEvents = data[5];
                $scope.ecmFileParticipants = data[6];
                $scope.formsConfig = data[7];
                $scope.transcriptionConfiguration = data[8];
                $scope.fileInfo = buildFileInfo($scope.ecmFile, $scope.ecmFile.container.id);
                $scope.requestInfo.caseNumber = data[9].caseNumber;
                $scope.openOtherDocuments.push($scope.fileInfo);

                // default view == snowbound
                $scope.view = "modules/document-details/views/document-viewer-snowbound.client.view.html";

                $scope.transcribeEnabled = $scope.transcriptionConfiguration.data['transcribe.enabled'];

                $timeout(function () {
                    $scope.$broadcast('document-data', $scope.ecmFile);
                }, 1000);

                WorkflowsService.getSubscribers({
                    userId: $scope.userId,
                    requestId: $stateParams['id']
                }).then(function (subscribers) {
                    if (subscribers && subscribers.data) {
                        if (_.find(subscribers.data, {
                            creator: $scope.userId
                        })) {
                            $scope.requestSubscribed = true;
                        }
                    }
                    $scope.subscribeEnabled = true;
                });

                if ($scope.openOtherDocuments && $scope.openOtherDocuments.length === 0) {
                    if ($stateParams.fileId) {

                        // Retrieves the metadata for the file which is being opened in the viewer
                        var ecmFile = EcmService.getFile({
                            fileId: $stateParams['fileId']
                        });
                        ecmFile.$promise.then(function (file) {
                            var fileInfo = buildFileInfo(file, file.container.id);
                            $scope.openOtherDocuments.push(fileInfo);
                            openViewerMultiple();
                        });
                    } else {
                        //add logic to get request folder from documentInfo and to go to get files
                        //to filter only 2 required files
                        var otherFiles = buildFileList(documentInfo);
                        otherFiles.then(function (files) {
                            $scope.openOtherDocuments = files;
                            openViewerMultiple();
                        });
                    }
                }

                var key = $scope.ecmFile.fileType + ".name";
                // Search for descriptive file type in acm-forms.properties
                $scope.fileType = $scope.formsConfig[key];
                if ($scope.fileType === undefined) {
                    // If descriptive file type does not exist, fallback to previous raw file type
                    $scope.fileType = $scope.ecmFile.fileType;
                }

                $scope.mediaType = $scope.ecmFile.fileActiveVersionMimeType.indexOf("video") === 0 ? "video" : ($scope.ecmFile.fileActiveVersionMimeType.indexOf("audio") === 0 ? "audio" : "other");

                if ($scope.mediaType === "video" || $scope.mediaType === "audio") {
                    $scope.config = {
                        sources: [{
                            src: $sce.trustAsResourceUrl('api/latest/plugin/ecm/stream/' + $scope.ecmFile.fileId),
                            type: $scope.ecmFile.fileActiveVersionMimeType
                        }],
                        theme: "node_modules/@bower_components/videogular-themes-default/videogular.css",
                        plugins: {
                            poster: "branding/loginlogo.png"
                        },
                        autoPlay: false
                    };
                    $scope.showVideoPlayer = true;
                    $scope.view = "modules/document-details/views/document-viewer-videogular.client.view.html";
                } else if ($scope.mediaType === "pdf" && "pdfjs" === $scope.ecmFileProperties['ecm.viewer.pdfViewer']) {
                    $scope.config = {
                        src: $sce.trustAsResourceUrl('api/latest/plugin/ecm/stream/' + $scope.ecmFile.fileId)
                    };
                    $scope.showPdfJs = true;
                    $scope.view = "modules/document-details/views/document-viewer-pdfjs.client.view.html";
                } else {
                    $scope.openSnowboundViewer();
                }

                $scope.transcriptionTabActive = $scope.showVideoPlayer && $scope.transcribeEnabled;
                $scope.transcriptionTabViewEnabled = $scope.transcriptionTabActive;

            });
            $scope.onPlayerReady = function (API) {
                $scope.videoAPI = API;
            }

            $scope.enableEditing = function () {
                ObjectLockingService.lockObject($scope.ecmFile.fileId, ObjectService.ObjectTypes.FILE, ObjectService.LockTypes.WRITE, true).then(function (lockedFile) {
                    $scope.editingMode = true;
                    openViewerMultiple();

                    // count user idle time. When user is idle for more then 1 minute, don't acquire lock
                    $scope._idleSecondsCounter = 0;
                    document.onclick = function () {
                        $scope._idleSecondsCounter = 0;
                    };
                    document.onmousemove = function () {
                        $scope._idleSecondsCounter = 0;
                    };
                    document.onkeypress = function () {
                        $scope._idleSecondsCounter = 0;
                    };

                    function incrementIdleSecondsCounter() {
                        $scope._idleSecondsCounter++;
                    }

                    window.setInterval(incrementIdleSecondsCounter, 1000);

                    // Refresh editing lock on timer
                    UtilTimerService.useTimer('refreshFileEditingLock', 60000 // every 1 minute
                        , function () {
                            if ($scope._idleSecondsCounter <= 60) {
                                ObjectLockingService.lockObject($scope.ecmFile.fileId, ObjectService.ObjectTypes.FILE, ObjectService.LockTypes.WRITE, true).then(undefined, function (errorMessage) {
                                    MessageService.error(errorMessage.data);
                                });
                            }
                            return true;
                        });
                }, function (errorMessage) {
                    MessageService.error(errorMessage.data);
                });

            }

            /**
             * @ngdoc method
             * @name loadViewerIframe
             * @methodOf request-info.controller:RequestInfoController
             *
             * @description
             * Opens a document or multiple documents in the snowbound viewer by updating the snowbound url which is bound
             * to the viewer iframe ng-src property.  Prior to being injected, the url is
             * declared as a trusted resource because otherwise the angular framework would not load
             * the snowbound url due to cross domain security issues (snowbound is probably on a different host/port)
             *
             * @param {String} url points to the snowbound viewer and contains the metadata to open the selected documents
             */
            $scope.loadViewerIframe = function (url) {
                $scope.$broadcast('change-viewer-document', url);
                $scope.documentViewerUrl = $sce.trustAsResourceUrl(url);
            };

            /**
             * Handles requests to open a new document in the viewer in response to a user click
             * @param e - describes the event which is being handled
             */
            $scope.$on('document-updated-refresh-needed', function (e) {
                openViewerMultiple();
            });

            /**
             * @ngdoc method
             * @name openViewerMultiple
             * @methodOf request-info.controller:RequestInfoController
             *
             * @description
             * Opens the snowbound viewer and loads one or more documents into it.  The documents loaded
             * include the primary document which was clicked by the user as well as any checked documents
             * which will be loaded into separate viewer tabs.
             */
            function openViewerMultiple() {
                // Generates and loads a url that will open the selected documents in the viewer
                if ($scope.openOtherDocuments.length > 0) {
                    registerFileChangeEvents();
                    var readonly = isAnyFileRecord() || !$scope.editingMode;
                    $scope.editingMode = !readonly;
                    var snowUrl = buildViewerUrlMultiple($scope.ecmFileProperties, $scope.acmTicket, $scope.userId, $scope.userFullName, $scope.openOtherDocuments, readonly, $scope.requestInfo.caseNumber);
                    if (snowUrl) {
                        $scope.loadViewerIframe(snowUrl);
                    }
                } else {
                    $scope.loadViewerIframe('about:blank');
                }
            }

            /**
             * Builds the snowbound url based on the parameters passed into the controller state and opens the specified document in
             * an iframe which points to snowbound
             */
            $scope.openSnowboundViewer = function () {

                if ($scope.loaderOpened == false) {
                    onShowLoader();
                } else {
                    onHideLoader();
                }

                var viewerUrl = SnowboundService.buildSnowboundUrl($scope.ecmFileProperties, $scope.acmTicket, $scope.userId, $scope.userFullName, $scope.fileInfo, !$scope.editingMode, $scope.requestInfo.caseNumber);
                $scope.documentViewerUrl = $sce.trustAsResourceUrl(viewerUrl);
            };

            /**
             * @ngdoc method
             * @name registerFileChangeEvents
             * @methodOf request-info.controller:RequestInfoController
             *
             * @description
             * Register file change event for every file that should be opened in the snowbound viewer. This will
             * help to open the correct file version after changing
             *
             */
            function registerFileChangeEvents() {
                if ($scope.openOtherDocuments.length > 0) {
                    angular.forEach($scope.openOtherDocuments, function (value, key) {
                        if (typeof value.id !== 'string' && !(value.id instanceof String)) {
                            var updateEvent = "object.changed/" + ObjectService.ObjectTypes.FILE + "/" + value.id;

                            if (!_.includes($scope.fileChangeEvents, updateEvent)) {
                                $scope.fileChangeEvents.push(updateEvent);
                                $scope.$bus.subscribe(updateEvent, function (data) {
                                    // first make sure the event is a file event and have different date than the previous one
                                    // I can see the same event fired twice in the same time
                                    if (ObjectService.ObjectTypes.FILE === data.objectType && $scope.fileChangeDate != data.date) {
                                        $scope.fileChangeDate = data.date;
                                        $scope.refreshFileOnViewer(data.objectId);
                                    }
                                });
                            }
                        }
                    });
                }
            }

            /**
             * @ngdoc method
             * @name refreshFileOnViewer
             * @methodOf request-info.controller:RequestInfoController
             *
             * @description
             * Publish event to refresh the tab opened in snowbound viewer. There are two possibilities for changing the version of the file:
             * change the version because of burning annotations, and change the version manually from Document sections.
             *
             * While burning annotations all opened versions should be closed and only last one to be opened
             * While changing the version manually from Documents section, all opened versions should stay opened, and open the last one too
             *
             * For that is using '$scope.versionChangeRequest', which is true if we are changing version manually, and false if we are burning annotations
             *
             */
            $scope.refreshFileOnViewer = function (fileId) {
                $scope.$bus.publish('update-viewer-open-documents', [{
                    id: $stateParams.id,
                    fileId: fileId,
                    removeOlderFileVersionFromSnowboundTabs: true && !$scope.versionChangeRequest
                }]);
            };

            $scope.$bus.subscribe('update-viewer-open-documents', function (data) {
                // Retrieves the metadata for the file which is being opened in the viewer
                var ecmFilePromises = [];
                if (Util.isArray(data)) {
                    angular.forEach(data, function (value, index) {
                        var ecmFile = EcmService.getFile({
                            fileId: value.fileId
                        });
                        ecmFilePromises.push(ecmFile.$promise);
                    });
                }

                $q.all(ecmFilePromises).then(function (result) {
                    angular.forEach(result, function (value, index) {
                        var fileInfo = buildFileInfo(value, data[index].id);
                        $scope.openOtherDocuments = _.filter($scope.openOtherDocuments, function (currentObject) {
                            if (typeof currentObject.id === 'string' || currentObject.id instanceof String) {
                                if (data[index].removeOlderFileVersionFromSnowboundTabs) {
                                    return !_.startsWith(currentObject.id, fileInfo.fileId + ":");
                                }
                                return !(_.startsWith(currentObject.id, fileInfo.fileId + ":") && currentObject.versionTag === fileInfo.versionTag);

                            }

                            if (data[index].removeOlderFileVersionFromSnowboundTabs) {
                                return !(currentObject.id === fileInfo.fileId);
                            }
                            return !(currentObject.id === fileInfo.fileId && currentObject.versionTag === fileInfo.versionTag);
                        });
                        $scope.openOtherDocuments.push(fileInfo);
                    });
                    openViewerMultiple();
                    $scope.versionChangeRequest = false;
                });
            });

            $scope.$bus.subscribe('update-viewer-open-documents-after-change-version', function () {
                $scope.versionChangeRequest = true;
            });

            $scope.$bus.subscribe('remove-from-opened-documents-list', function (data) {
                removeFromOpenedDocumentsList(Number(data.id) * 1, data.version);
                $scope.$apply();
            });

            function removeFromOpenedDocumentsList(id, version) {
                $scope.openOtherDocuments = _.filter($scope.openOtherDocuments, function (currentObject) {
                    if (typeof currentObject.id === 'string' || currentObject.id instanceof String) {
                        return !(_.startsWith(currentObject.id, id + ":") && currentObject.versionTag === version);
                    }
                    return !(currentObject.id === id && currentObject.versionTag === version);
                });
            }

            function openReturnReasonModal(deferred) {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/return-reason-modal.client.view.html',
                    controller: 'Cases.ReturnReasonModalController',
                    size: 'lg',
                    backdrop: 'static'
                });

                modalInstance.result.then(function (returnReason) {
                    $scope.requestInfo.returnReason = returnReason;
                    //save note
                    NotesService.saveNote({
                        note: returnReason,
                        parentId: $stateParams['id'],
                        parentType: 'CASE_FILE',
                        type: 'RETURN_REASON'
                    }).then(function (addedNote) {
                        // Note saved
                        deferred.resolve();
                    });
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function openDeleteCommentModal(deferred) {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/delete-comment-modal.client.view.html',
                    controller: 'Cases.DeleteCommentModalController',
                    size: 'lg',
                    backdrop: 'static'
                });

                modalInstance.result.then(function (deleteComment) {
                    //save note
                    NotesService.saveNote({
                        note: deleteComment,
                        parentId: $stateParams['id'],
                        parentType: 'CASE_FILE',
                        type: 'DELETE_COMMENT'
                    }).then(function (addedNote) {
                        // Note saved
                        deferred.resolve();
                    });
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            AdminPrivacyConfigService.getPrivacyConfig().then(function (response) {
                $scope.limitedDeliveryToSpecificPageCountEnabled = response.data.limitedDeliveryToSpecificPageCountEnabled;
                $scope.limitedDeliveryToSpecificPageCount = response.data.limitedDeliveryToSpecificPageCount;
                $scope.provideReasonToHoldRequestEnabled = response.data.provideReasonToHoldRequestEnabled;
            });

            function openHoldReasonModal(deferred, tollingFlag) {
                var params = {};
                params.tollingFlag = tollingFlag;

                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/hold-reason-modal.client.view.html',
                    controller: 'Cases.HoldReasonModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.objectInfo.status = data.status;
                    if (data.isSelectedTolling) {
                        $scope.objectInfo.tollingFlag = true;
                    }
                    if (data.holdReason) {
                        //save note
                        NotesService.saveNote({
                            note: data.holdReason,
                            parentId: $stateParams['id'],
                            parentType: 'CASE_FILE',
                            type: 'HOLD_REASON'
                        }).then(function (addedNote) {
                            // Note saved
                            deferred.resolve();
                        });
                    } else {
                        deferred.resolve();
                    };
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function openLimitedPageReleaseModal(deferred) {
                var params = {};
                params.pageCount = $scope.limitedDeliveryToSpecificPageCount;

                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/limited-release-modal.client.view.html',
                    controller: 'Cases.LimitedReleaseModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (limitedDeliveryFlag) {
                    $scope.objectInfo.limitedDeliveryFlag = limitedDeliveryFlag;
                    deferred.resolve();
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function setupNextQueue(name, deferred) {

                var nextQueue = name;
                QueuesSvc.queryNextPossibleQueues($scope.requestInfo.id).then(function (data) {
                    var availableQueues = data.nextPossibleQueues;
                    var defaultNextQueue = data.defaultNextQueue;
                    var defaultReturnQueue = data.defaultReturnQueue;

                    if (defaultNextQueue || defaultReturnQueue) {
                        //if there is default next or return queue, then remove it from the list
                        //and add Complete, Next and Return queue aliases into list
                        _.remove(availableQueues, function (currentObject) {
                            return currentObject === defaultNextQueue || currentObject === defaultReturnQueue;
                        });
                        if (defaultReturnQueue) {
                            availableQueues.unshift("Return");
                        }
                        if (defaultNextQueue) {
                            availableQueues.push("Complete");
                        }
                    }

                    $scope.availableQueues = availableQueues;
                    $scope.defaultNextQueue = defaultNextQueue;
                    $scope.defaultReturnQueue = defaultReturnQueue;

                    if (name === 'Complete') {
                        nextQueue = $scope.defaultNextQueue;
                    } else if (name === 'Return') {
                        nextQueue = $scope.defaultReturnQueue;
                    }

                    QueuesSvc.nextQueue($scope.requestInfo.id, nextQueue, name).then(function (data) {

                        if (data.success) {
                            $scope.loading = false;
                            $scope.loadingIcon = "fa fa-check";

                            releaseRequestLock($scope.requestInfo.id).then(function () {
                                $scope.$emit('report-object-refreshed', $stateParams.id);
                                if (name === 'Next' || name === 'Return' || name === 'Deny') {
                                    goToNextAvailableRequestOrQueueList(deferred);
                                } else {
                                    deferred.resolve();
                                }
                            });
                        } else {
                            deferred.resolve();
                            $scope.loading = false;
                            $scope.loadingIcon = "fa fa-check";
                            var errorMessage = "";
                            if (data.errors[0]) {
                                errorMessage = data.errors[0];
                            }
                            if (!errorMessage && data.reason) {
                                errorMessage = data.reason;
                            }
                            if (errorMessage === "Executive Group is required") {
                                showNotificationGroupDialog($scope.requestId);
                            } else {
                                showErrorDialog(errorMessage);
                            }
                        }
                    });
                });
            }

            $scope.requestTrackChanged = function (requestTrack) {
                if (requestTrack === 'expedite') {
                    expediteDueDate();
                    $scope.objectInfo.expediteDate = new Date();
                } else {
                    resetDueDate();
                }
            };

            function resetDueDate() {
                $scope.objectInfo.dueDate = $scope.originalDueDate;
                $rootScope.$broadcast('dueDate-changed', $scope.originalDueDate);
            }

            function expediteDueDate() {
                if ($scope.expediteWorkingDaysEnabled && !Util.isEmpty($scope.objectInfo.receivedDate)) {
                    if ($scope.includeWeekends) {
                        $scope.expeditedDueDate = DueDateService.dueDateWithWeekends($scope.objectInfo.receivedDate, $scope.expediteWorkingDays, $scope.holidays);
                    } else {
                        $scope.expeditedDueDate = DueDateService.dueDateWorkingDays($scope.objectInfo.receivedDate, $scope.expediteWorkingDays, $scope.holidays);
                    }
                    $scope.objectInfo.dueDate = $scope.expeditedDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.expeditedDueDate);
                }
            }

            $scope.onClickNextQueue = function (name, isRequestFormModified) {

                $scope.loading = true;
                $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                $scope.nameButton = name;
                $scope.isRequestFormModified = isRequestFormModified;

                var nextQueue = name;
                var deferred = $q.defer();

                disableWorkflowControls(deferred.promise);

                if (name === 'Return') {
                    openReturnReasonModal(deferred);
                } else if (name === 'Delete') {
                    openDeleteCommentModal(deferred);
                } else if (name === 'Hold') {
                    if ($scope.provideReasonToHoldRequestEnabled) {
                        openHoldReasonModal(deferred, $scope.objectInfo.tollingFlag);
                    } else {
                        $scope.objectInfo.status = 'Hold';
                        deferred.resolve();
                    }
                } else {
                    deferred.resolve();
                }

                deferred.promise.then(function () {

                    var deferred = $q.defer();
                    var queueId = $scope.requestInfo.queue.id;

                    if ($scope.isRequestFormModified == true) {
                        saveRequest().then(function (objectInfo) {
                            $scope.objectInfo = objectInfo;
                            setupNextQueue(name, deferred);
                        });
                    } else {
                        setupNextQueue(name, deferred);
                    }

                });
            };

            // Controls workflow buttons
            $scope.requestInProgress = false;

            // Workflow buttons actions
            $scope.saveAndCloseRequest = saveAndCloseRequest;
            $scope.saveAndReferesh = saveAndReferesh;
            $scope.printDocument = printDocument;
            $scope.subscribe = subscribe;
            $scope.unsubscribe = unsubscribe;

            $scope.updateAssignee = function updateAssignee() {
                ObjectModelService.setAssignee($scope.requestInfo, $scope.assignee);
                objectInfo.modified = null;//this is because we need to trigger update on case file
                saveAndReferesh();
            };

            $scope.updateOwningGroup = function () {
                ObjectModelService.setGroup($scope.requestInfo, $scope.owningGroup);
                saveAndReferesh();
            };
            $scope.updateDueDate = function (dueDate) {
                $scope.requestInfo.dueDate = UtilDateService.dateToIso($scope.dateInfo.dueDate);
                saveAndReferesh();
            };

            function gotoBack() {
                // Return to Queues page
                var deferred = $q.defer();
                $timeout(function () {
                    deferred.resolve();
                    $state.go('queues');
                }, 4000);
                return deferred.promise;
            }

            /**
             * Navigate to next available request in current queue if any, else navigate to queues list view
             * @param deferred release of workflow controls
             */
            function goToNextAvailableRequestOrQueueList(deferred) {
                $timeout(function () {
                    if (nextAvailableRequests.length > 0) {
                        var nextRequest = nextAvailableRequests[0];
                        $state.go('request-info', {
                            id: nextRequest.object_id_s
                        });
                    }
                    deferred.resolve();
                }, 4000);
            }


            function goToQueue(queueId) {
                // Return to Queue for provided Queue id
                var deferred = $q.defer();
                $timeout(function () {
                    deferred.resolve();

                }, 4000);
                return deferred.promise;
            }

            /**
             * Disable workflow control and enable them when prome is resolved
             * @param promise
             */
            function disableWorkflowControls(promise) {
                $scope.requestInProgress = true;
                promise['finally'](function () {
                    $scope.requestInProgress = false;
                });
            }

            // Workflow actions

            function saveAndCloseRequest() {
                var promise = saveRequest().then(function () {
                    return gotoBack();
                });
                disableWorkflowControls(promise);
            }

            function saveAndReferesh() {

                $scope.saveIcon = true;
                $scope.saveLoadingIcon = "fa fa-circle-o-notch fa-spin";
                var promise = saveRequest().then(function (caseInfo) {

                    $scope.saveIcon = false;
                    $scope.saveLoadingIcon = "fa fa-floppy-o";
                    $scope.$broadcast("report-object-updated", caseInfo);
                    $scope.originalDueDate = caseInfo.dueDate;
                    $scope.extendedDueDate = undefined;

                    //$scope.$broadcast('request-info-retrieved', $scope.requestInfo);
                    MessageService.info($translate.instant('requests.save.success'));
                    return caseInfo;
                }, function (error) {
                    $scope.saveIcon = false;
                    $scope.saveLoadingIcon = "fa fa-floppy-o";
                    $scope.$emit("report-object-update-failed", error);
                    MessageService.error($translate.instant('requests.save.error'));
                    return error;
                });

                disableWorkflowControls(promise);
            }

            function printDocument() {
                var objectInfo = Util.omitNg($scope.requestInfo);
                DocumentPrintingService.printDocuments({
                    caseFileIds: objectInfo.id
                }).$promise.then(function (printResult) {
                    var file = new Blob([printResult.data], {
                        type: 'application/pdf'
                    });
                    var fileURL = URL.createObjectURL(file);
                    window.open(fileURL);
                });
            }

            function saveRequest() {
                var deferred = $q.defer();
                if (CaseInfoService.validateCaseInfo($scope.requestInfo)) {
                    var objectInfo = Util.omitNg($scope.requestInfo);
                    objectInfo.scannedDate = UtilDateService.localDateToIso(objectInfo.scannedDate);
                    objectInfo.releasedDate = UtilDateService.localDateToIso(objectInfo.releasedDate);
                    objectInfo.receivedDate = UtilDateService.localDateToIso(objectInfo.receivedDate);
                    objectInfo.recordSearchDateFrom = UtilDateService.localDateToIso(objectInfo.recordSearchDateFrom);
                    objectInfo.recordSearchDateTo = UtilDateService.localDateToIso(objectInfo.recordSearchDateTo);
                    deferred.resolve(CaseInfoService.saveSubjectAccessRequestInfo(objectInfo));
                }
                return deferred.promise;
            }

            function subscribe() {
                var promise = WorkflowsService.subscribe({
                    userId: $scope.userId,
                    requestId: $stateParams['id']
                }).then(function () {
                    $scope.requestSubscribed = true;
                });
                disableWorkflowControls(promise);
            }

            function unsubscribe() {
                var promise = WorkflowsService.unsubscribe({
                    userId: $scope.userId,
                    requestId: $stateParams['id']
                }).then(function () {
                    $scope.requestSubscribed = false;
                });
                disableWorkflowControls(promise);
            }

            function releaseRequestLock(requestId) {
                var releaseLockDeferred = $q.defer();


                RequestsService.releaseRequestLock({
                    requestId: requestId
                }).$promise.then(function () {
                    releaseLockDeferred.resolve();
                    }, function () {
                        releaseLockDeferred.reject();
                    });


                return releaseLockDeferred.promise;
            }

            /**
             * Expand Preview area
             */
            function expandPreview() {
                /*var allSelectedDocuments = AttachmentsService.filterPdfDocuments(
                 $scope.openAuthorizationDocuments
                 .concat($scope.openAbstractDocuments)
                 .concat($scope.openAttachmentDocuments)
                 );

                 var selectedDocumentsIds = AttachmentsService.buildOpenDocumentIdString(allSelectedDocuments);

                 window.open('/arkcase/home.html#!/request-info/' + requestId + '/fullscreen?documents=' + selectedDocumentsIds, '_blank', 'toolbar=no, menubar=no, fullscreen=yes, location=no, directories=no, status=no');
                 */
            }

            function buildFileList(requestData) {
                var defer = $q.defer();
                var files = [];

                var folder = _.find(requestData.children, function (currentObject) {
                    return currentObject.name.toLowerCase().indexOf('request') !== -1 && currentObject.objectType === 'folder';
                });

                var param = {};
                param.objType = 'CASE_FILE';
                param.objId = requestId;
                var folderId = Util.goodValue(folder.objectId, 0);
                if (0 < folderId) {
                    param.folderId = folderId;
                }
                param.start = 0;
                param.n = 100;
                param.sortBy = 'name';
                param.sortDir = 'asc';

                GenericRequestsService.retrieveFolderList(param).$promise.then(function (data) {
                    angular.forEach(data.children, function (value, key) {
                        if (value.objectType == "file" && value.type.indexOf('acknowledgement') == -1) {
                            var fileInfo = {
                                id: value.objectId,
                                containerId: requestData.containerObjectId,
                                containerType: requestData.containerObjectType,
                                name: value.name,
                                mimeType: value.mimeType,
                                selectedIds: '',
                                versionTag: value.version
                            };

                            files.push(fileInfo);
                        }
                    });
                    defer.resolve(files);
                });

                return defer.promise;
            }

            function buildFileInfo(file, containerId) {
                return {
                    fileId: file.fileId,
                    id: file.fileId + ':' + file.activeVersionTag,
                    containerId: containerId,
                    containerType: 'CASE_FILE',
                    status: file.status,
                    name: file.fileName,
                    mimeType: file.fileActiveVersionMimeType,
                    selectedIds: file.fileId + ':' + file.activeVersionTag,
                    versionTag: file.activeVersionTag,
                    pageCount: file.pageCount
                };
            }

            function buildOpenDocumentIdString(files) {
                var openDocIds = files.map(function (value) {
                    if (!_.includes(value.id, ':')) {
                        return value.id + ":" + value.versionTag;
                    } else {
                        return value.id;
                    }
                }).join(",");
                return openDocIds;
            }

            function buildViewerUrlMultiple(ecmFileProperties, acmTicket, userId, userFullName, files, readonly, requestNumber) {
                var viewerUrl = '';

                if (files && files.length > 0) {

                    if (files[0]) {
                        // Adds the list of additional documents to load to the viewer url
                        files[0].selectedIds = buildOpenDocumentIdString(files);
                        if (!_.includes(files[0].id, ':')) {
                            files[0].id = files[0].id + ":" + files[0].versionTag;
                        }

                        // Generates the viewer url with the first document as the primary document
                        viewerUrl = SnowboundService.buildSnowboundUrl(ecmFileProperties, acmTicket, userId, userFullName, files[0], readonly, requestNumber);
                    }
                }
                return viewerUrl;
            }

            function showErrorDialog(error) {
                $modal.open({
                    animation: true,
                    templateUrl: 'modules/request-info/views/components/request-info.error-dialog.client.view.html',
                    controller: 'RequestInfo.ErrorDialogController',
                    backdrop: 'static',
                    resolve: {
                        errorMessage: function () {
                            return error;
                        }
                    }
                });
            }

            function showNotificationGroupDialog() {
                $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/request-info/views/components/request-info.notification-info.client.view.html',
                    controller: 'RequestInfo.NotificationGroupClientController',
                    backdrop: 'static',
                    resolve: {
                        errorMessage: function () {

                        }
                    }
                });
            }

            UserInfoService.getUserInfo().then(function (infoData) {
                $scope.currentUserProfile = infoData;
            });

            $scope.refresh = function () {
                $scope.$emit('report-object-refreshed', $stateParams.id);
            };

            $scope.claim = function (objectInfo) {
                ObjectModelService.setAssignee(objectInfo, $scope.currentUserProfile.userId);
                objectInfo.modified = null;//this is because we need to trigger update on case file
                saveAndReferesh();
            };

            $scope.unclaim = function (objectInfo) {
                ObjectModelService.setAssignee(objectInfo, "");
                objectInfo.modified = null;//this is because we need to trigger update on case file
                saveAndReferesh();
            };

            $scope.extensionClicked = function ($event) {
                if (!$event.target.checked) {
                    $scope.objectInfo.dueDate = $scope.originalDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.originalDueDate);
                } else {
                    if (!$scope.extendedDueDate) {
                        if ($scope.includeWeekends) {
                            $scope.extendedDueDate = DueDateService.dueDateWithWeekends($scope.originalDueDate, $scope.extensionWorkingDays, $scope.holidays);
                        } else {
                            $scope.extendedDueDate = DueDateService.dueDateWorkingDays($scope.originalDueDate, $scope.extensionWorkingDays, $scope.holidays);
                        }
                    }
                    $scope.objectInfo.dueDate = $scope.extendedDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.extendedDueDate);
                }
            };

            // Release editing lock on window unload, if acquired
            $window.addEventListener('unload', function () {
                $scope.data = {
                    objectId: $scope.ecmFile.fileId,
                    objectType: ObjectService.ObjectTypes.FILE,
                    lockType: ObjectService.LockTypes.WRITE
                };

                var data = angular.toJson($scope.data);

                var url = 'api/v1/plugin/' + ObjectService.ObjectTypes.FILE + '/' + $scope.ecmFile.fileId + '/lock?lockType=' + ObjectService.LockTypes.WRITE;

                if ($scope.editingMode) {
                    if ("sendBeacon" in navigator) {
                        navigator.sendBeacon(url, data);
                    } else {
                        var xmlhttp = new XMLHttpRequest();
                        xmlhttp.open("POST", url, false); //false - synchronous call
                        xmlhttp.setRequestHeader("Content-type", "application/json");
                        xmlhttp.send(data);
                    }
                }
            });

            $rootScope.$bus.subscribe("object.changed/FILE/" + $stateParams.fileId, function () {
                //we don't need to reload file while progress bar is loading
                if (!$scope.fileReloadDisabled) {
                    var ecmFile = EcmService.getFile({
                        fileId: $scope.ecmFile.fileId
                    });
                    ecmFile.$promise.then(function (file) {
                        if ($scope.fileInfo.id !== file.fileId + ':' + file.activeVersionTag) {
                            $scope.ecmFile = file;
                            $scope.fileId = file.fileId;
                            $scope.fileInfo.id = file.fileId + ':' + file.activeVersionTag;
                            $scope.fileInfo.selectedIds = file.fileId + ':' + file.activeVersionTag;
                            $scope.fileInfo.versionTag = file.activeVersionTag;
                            DialogService.alert($translate.instant("documentDetails.fileChangedAlert")).then(function () {
                                $scope.openSnowboundViewer();
                            });
                        }
                        onHideLoader();
                    });
                }
            });
            $scope.nextAvailableRequest = function () {
                RequestsService.getNextAvailableRequestInQueue({
                    queueId: nextQueueId,
                    createdDate: $scope.objectInfo.created
                })
                    .$promise.then(function (data) {
                    $state.go('request-info', {
                        id: data.requestId,
                        fileId: data.requestFormId
                    });
                });
            }
            window.addEventListener("beforeunload", function (e) {
                releaseRequestLock(requestId);

                (e || window.event).returnValue = null;
                return null;
            });
        }]);
/**
 * 2018-06-01 David Miller. This block is needed to tell the PDF.js angular module, where the PDF.js library is. Without this, on minified
 * systems the PDF.js viewer will not work. I copied this from the project web page,
 * https://github.com/legalthings/angular-pdfjs-viewer#advanced-configuration.
 *
 * @param pdfjsViewerConfigProvider
 * @returns
 */
angular.module('document-details').config(function (pdfjsViewerConfigProvider) {
    pdfjsViewerConfigProvider.setWorkerSrc("node_modules/@bower_components/pdf.js-viewer/pdf.worker.js");
    pdfjsViewerConfigProvider.setCmapDir("node_modules/@bower_components/pdf.js-viewer/cmaps");
    pdfjsViewerConfigProvider.setImageDir("node_modules/@bower_components/pdf.js-viewer/images");

    // pdfjsViewerConfigProvider.disableWorker();
    pdfjsViewerConfigProvider.setVerbosity("infos"); // "errors", "warnings" or "infos"
});
