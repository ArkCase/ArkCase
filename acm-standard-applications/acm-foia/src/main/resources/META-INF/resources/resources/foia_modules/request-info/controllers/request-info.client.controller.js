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
        'Admin.FoiaConfigService',
        'Admin.TranscriptionManagementService',
        '$window',
        'ArkCaseCrossWindowMessagingService',
        'Object.LockingService',
        'Util.TimerService',
        'Dialog.BootboxService',
        function ($rootScope, $scope, $log, $sce, $q, $state, $timeout, $stateParams, $modal, ConfigService, Authentication, RequestsService, WorkflowsService, GenericRequestsService, LookupService, TicketService, QueuesService, PermissionsService, CaseInfoService, ObjectService,
                  HelperObjectBrowserService, ObjectLookupService, ObjectModelService, CaseLookupService, UtilDateService, QueuesSvc, ObjectSubscriptionService, Util, SnowboundService, EcmService, DocumentPrintingService, NotesService, UserInfoService, MessageService, $translate,
                  DueDateService, AdminHolidayService, AdminFoiaConfigService, TranscriptionManagementService, $window, ArkCaseCrossWindowMessagingService, ObjectLockingService, UtilTimerService, DialogService) {

            // $scope.openOtherDocuments = [];
            // $scope.fileChangeEvents = [];
            // $scope.fileChangeDate = null;
            // $scope.versionChangeRequest = false;

            $scope.loading = false;
            $scope.loadingIcon = "fa fa-check";

            $scope.saveIcon = false;
            $scope.saveLoadingIcon = "fa fa-floppy-o";
            $scope.viewerOnly = false;

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

            $scope.iframeLoaded = function () {
                ObjectLookupService.getLookupByLookupName("annotationTags").then(function (allAnnotationTags) {
                    $scope.allAnnotationTags = allAnnotationTags;
                    ArkCaseCrossWindowMessagingService.addHandler('select-annotation-tags', onSelectAnnotationTags);
                    ArkCaseCrossWindowMessagingService.start('snowbound', $scope.ecmFileProperties['ecm.viewer.snowbound']);
                });
            };

            function onSelectAnnotationTags(data) {
                var params = $scope.allAnnotationTags;
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
                $scope.prefixes = reqConfig.prefixes;

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

            AdminFoiaConfigService.getFoiaConfig().then(function (response) {
                $scope.extensionWorkingDays = response.data.requestExtensionWorkingDays;
                $scope.requestExtensionWorkingDaysEnabled = response.data.requestExtensionWorkingDaysEnabled;
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
            var nextAvailableRequests = [];

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

                $scope.originalDueDate = objectInfo.dueDate;

                $scope.requestInfo = objectInfo;
                $scope.dateInfo = $scope.dateInfo || {};
                $scope.dateInfo.dueDate = UtilDateService.isoToDate($scope.requestInfo.dueDate);
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
                ObjectLookupService.getPayFees().then(function (payFees) {
                    $scope.payFees = payFees;
                    if ($scope.requestInfo.payFee != null && $scope.requestInfo.payFee != "0" && $scope.requestInfo.payFee != '') {
                        $scope.payFeeValue = _.find($scope.payFees, function (payFee) {
                            if (payFee.key == $scope.requestInfo.payFee) {
                                return payFee.key;
                                $scope.requestInfo.payFee = $scope.payFeeValue.key;
                            }
                        });
                        $scope.requestInfo.payFee = $scope.payFeeValue.key;
                    } else {
                        $scope.requestInfo.payFee = $scope.payFees[0].key;
                    }
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
                    var defaultDenyQueue = data.defaultDenyQueue;

                    if (defaultNextQueue || defaultReturnQueue || defaultDenyQueue) {
                        //if there is default next or return queue, then remove it from the list
                        //and add Complete, Next and Return queue aliases into list
                        _.remove(availableQueues, function (currentObject) {
                            return currentObject === defaultNextQueue || currentObject === defaultReturnQueue || currentObject === defaultDenyQueue;
                        });
                        if (defaultDenyQueue) {
                            availableQueues.unshift("Deny");
                        }
                        if (defaultReturnQueue) {
                            availableQueues.unshift("Return");
                        }
                        if (defaultNextQueue) {
                            availableQueues.push("Complete");
                            availableQueues.push("Next");
                        }
                    }
                    availableQueues = availableQueues.map(function (item) {
                        var tmpObj = {};
                        tmpObj.name = item;
                        if (item != 'Complete' && item != 'Next') {
                            tmpObj.disabled = true;
                        }
                        return tmpObj;
                    });

                    $scope.availableQueues = availableQueues;
                    $scope.defaultNextQueue = defaultNextQueue;
                    $scope.defaultReturnQueue = defaultReturnQueue;
                    $scope.defaultDenyQueue = defaultDenyQueue;

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

                PermissionsService.getActionPermission('lock', objectInfo, {
                    objectType: "CASE_FILE"
                }).then(function (result) {
                    if (result) {
                        RequestsService.lockRequest({
                            requestId: $stateParams['id']
                        }).$promise.then(function (lockInfo) {
                            $scope.readOnly = false;
                            requestLockDeferred.resolve(lockInfo);
                        }, function (error) {
                            $scope.readOnly = true;
                            requestLockDeferred.resolve(null);
                        });
                    } else {
                        $scope.readOnly = true;
                        requestLockDeferred.resolve(null);
                    }
                }, function (error) {
                    $scope.readOnly = true;
                    requestLockDeferred.resolve(null);
                });

                QueuesService.queryQueueRequests({
                    queueId: objectInfo.queue.id,
                    sortDir: 'asc',
                    sortBy: 'received_date_tdt',
                    startWith: 0
                }).then(function (data) {
                    nextAvailableRequests = _.filter(data.response.docs, function (item) {
                        return item.object_id_s != $scope.requestInfo.id;
                    });
                    $scope.numOfRequestsInQueue = data.response.numFound;
                });

                $scope.$broadcast('request-info-retrieved', $scope.requestInfo);

                if ($scope.requestInfo.queue) {
                    $scope.$bus.publish('required-fields-retrieved', $scope.requiredFields[$scope.requestInfo.queue.name]);
                }

                populateDispositionTypes($scope.requestInfo);
                populateRequestTrack($scope.requestInfo);
                $scope.populateDispositionSubTypes($scope.objectInfo.disposition, $scope.objectInfo.requestType);
            };

            function populateDispositionTypes(objectInfo) {
                ObjectLookupService.getDispositionTypes(objectInfo.requestType).then(function (requestDispositionType) {
                    $scope.dispositionTypes = requestDispositionType;
                    if (objectInfo.disposition) {
                        $scope.dispositionValue = _.find($scope.dispositionTypes, function (disposition) {
                            if (disposition.key == objectInfo.disposition) {
                                return disposition.key;
                            }
                        });
                        $scope.requestInfo.disposition = $scope.dispositionValue.key;
                    } else {
                        $scope.requestInfo.disposition = $scope.dispositionTypes[0].key;
                    }
                });
            }

            function populateRequestTrack(objectInfo) {
                ObjectLookupService.getRequestTrack().then(function (requestTrack) {
                    $scope.requestTracks = requestTrack;
                    if (objectInfo.requestTrack != null && objectInfo.requestTrack != '') {
                        $scope.requestTrackValue = _.find($scope.requestTracks, function (requestTrack) {
                            if (requestTrack.key == objectInfo.requestTrack) {
                                return requestTrack.key;
                            }
                        });
                        $scope.requestInfo.requestTrack = $scope.requestTrackValue.key;
                    } else {
                        $scope.requestInfo.requestTrack = $scope.requestTracks[0].key;
                    }
                });
            }

            $scope.isDisabled = true;
            $scope.isChanged = function (dispositionSubtype) {

                if (dispositionSubtype === 'other') {
                    $scope.isDisabled = false;
                } else {
                    $scope.isDisabled = true;
                    $scope.objectInfo.otherReason = "";
                }

            };

            $scope.populateDispositionSubTypes = function (selectedValue, requestType) {
                if (selectedValue === "full-denial" && requestType == "Appeal") {
                    ObjectLookupService.getAppealDispositionSubTypes().then(function (appealDispositionSubType) {
                        $scope.dispositionSubTypes = appealDispositionSubType;
                    });
                } else if (selectedValue === "full-denial" && requestType == "New Request") {
                    ObjectLookupService.getRequestDispositionSubTypes().then(function (requestDispositionSubType) {
                        $scope.dispositionSubTypes = requestDispositionSubType;
                    });
                } else {
                    $scope.dispositionSubTypes = "";
                }
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

                // default view == snowbound
                $scope.view = "modules/document-details/views/document-viewer-snowbound.client.view.html";

                $scope.transcribeEnabled = $scope.transcriptionConfiguration.data.enabled;

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

            });
            $scope.onPlayerReady = function (API) {
                $scope.videoAPI = API;
            }

            $scope.enableEditing = function () {
                ObjectLockingService.lockObject($scope.ecmFile.fileId, ObjectService.ObjectTypes.FILE, ObjectService.LockTypes.WRITE, true).then(function (lockedFile) {
                    $scope.editingMode = true;
                    $scope.openSnowboundViewer();

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
                    var snowUrl = buildViewerUrlMultiple($scope.ecmFileConfig, $scope.acmTicket, $scope.userId, $scope.userFullName, $scope.openOtherDocuments, !$scope.editingMode, $scope.requestInfo.caseNumber);
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

                            if (!$scope.fileChangeEvents.includes(updateEvent)) {
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
                                    return !currentObject.id.startsWith(fileInfo.fileId + ":");
                                }
                                return !(currentObject.id.startsWith(fileInfo.fileId + ":") && currentObject.versionTag === fileInfo.versionTag);
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
            });

            function removeFromOpenedDocumentsList(id, version) {
                $scope.openOtherDocuments = _.filter($scope.openOtherDocuments, function (currentObject) {
                    if (typeof currentObject.id === 'string' || currentObject.id instanceof String) {
                        return !(currentObject.id.startsWith(id + ":") && currentObject.versionTag === version);
                    }
                    return !(currentObject.id === id && currentObject.versionTag === version);
                });
            }

            function openReturnReasonModal(deferred) {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/request-info/views/components/return-reason-modal.client.view.html',
                    controller: 'RequestInfo.ReturnReasonModalController',
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
                    templateUrl: 'modules/request-info/views/components/delete-comment-modal.client.view.html',
                    controller: 'RequestInfo.DeleteCommentModalController',
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

            function setupNextQueue(name, deferred) {

                var nextQueue = name;
                QueuesSvc.queryNextPossibleQueues($scope.requestInfo.id).then(function (data) {
                    var availableQueues = data.nextPossibleQueues;
                    var defaultNextQueue = data.defaultNextQueue;
                    var defaultReturnQueue = data.defaultReturnQueue;
                    var defaultDenyQueue = data.defaultDenyQueue;

                    if (defaultNextQueue || defaultReturnQueue || defaultDenyQueue) {
                        //if there is default next or return queue, then remove it from the list
                        //and add Complete, Next and Return queue aliases into list
                        _.remove(availableQueues, function (currentObject) {
                            return currentObject === defaultNextQueue || currentObject === defaultReturnQueue || currentObject === defaultDenyQueue;
                        });
                        if (defaultDenyQueue) {
                            availableQueues.unshift("Deny");
                        }
                        if (defaultReturnQueue) {
                            availableQueues.unshift("Return");
                        }
                        if (defaultNextQueue) {
                            availableQueues.push("Complete");
                            availableQueues.push("Next");
                        }
                    }

                    $scope.availableQueues = availableQueues;
                    $scope.defaultNextQueue = defaultNextQueue;
                    $scope.defaultReturnQueue = defaultReturnQueue;
                    $scope.defaultDenyQueue = defaultDenyQueue;

                    if (name === 'Complete' || name === 'Next') {
                        nextQueue = $scope.defaultNextQueue;
                    } else if (name === 'Return') {
                        nextQueue = $scope.defaultReturnQueue;
                    } else if (name === 'Deny') {
                        nextQueue = $scope.defaultDenyQueue;
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

            $scope.onClickNextQueue = function (name, isRequestFormModified) {

                $scope.loading = true;
                $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                $scope.nameButton = name;

                var nextQueue = name;
                var deferred = $q.defer();

                disableWorkflowControls(deferred.promise);

                if (name === 'Return') {
                    openReturnReasonModal(deferred);
                } else if (name === 'Delete') {
                    openDeleteCommentModal(deferred);
                } else {
                    deferred.resolve();
                }

                deferred.promise.then(function () {

                    var deferred = $q.defer();
                    var queueId = $scope.requestInfo.queue.id;

                    if (isRequestFormModified == true) {
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
                    } else {
                        $state.go('queues');
                    }
                    deferred.resolve();
                }, 4000);
            }

            function goToQueue(queueId) {
                // Return to Queue for provided Queue id
                var deferred = $q.defer();
                $timeout(function () {
                    deferred.resolve();
                    $state.go('queues.queue', {
                        queueId: queueId
                    });
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
                    deferred.resolve(CaseInfoService.saveFoiaRequestInfo(objectInfo));
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

                if ($scope.requestLockInfo) {
                    RequestsService.releaseRequestLock({
                        requestId: requestId
                    }).$promise.then(function () {
                        $scope.requestLockInfo = null;
                        releaseLockDeferred.resolve();
                    }, function () {
                        releaseLockDeferred.reject();
                    });
                } else {
                    releaseLockDeferred.resolve();
                }

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
                    name: file.fileName,
                    mimeType: file.fileActiveVersionMimeType,
                    selectedIds: file.fileId + ':' + file.activeVersionTag,
                    versionTag: file.activeVersionTag
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
            $window.addEventListener('beforeunload', function () {
                if ($scope.editingMode) {
                    ObjectLockingService.unlockObject($scope.fileInfo.fileId, ObjectService.ObjectTypes.FILE, ObjectService.LockTypes.WRITE, true);
                }
            });
            
            $rootScope.$bus.subscribe("object.changed/FILE/" + $stateParams.fileId, function() {
                var ecmFile = EcmService.getFile({
                    fileId: $scope.ecmFile.fileId
                });
                ecmFile.$promise.then(function(file) {
                    $scope.ecmFile = file;
                    $scope.fileId = file.fileId;
                    $scope.fileInfo.id= file.fileId + ':' + file.activeVersionTag;
                    $scope.fileInfo.selectedIds= file.fileId + ':' + file.activeVersionTag;
                    $scope.fileInfo.versionTag= file.activeVersionTag;
                    DialogService.alert($translate.instant("documentDetails.fileChangedAlert")).then(function() {
                        $scope.openSnowboundViewer();
                    });
                });
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
