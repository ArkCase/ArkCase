'use strict';

angular.module('document-details').controller(
        'DocumentDetailsController',
        [
                '$scope',
                '$stateParams',
                '$sce',
                '$q',
                '$timeout',
                'TicketService',
                'ConfigService',
                'LookupService',
                'SnowboundService',
                'Authentication',
                'EcmService',
                'Helper.LocaleService',
                'Admin.TranscriptionManagementService',
                'MessageService',
                'UtilService',
                '$log',
                function($scope, $stateParams, $sce, $q, $timeout, TicketService, ConfigService, LookupService, SnowboundService,
                        Authentication, EcmService, LocaleHelper, TranscriptionManagementService, MessageService, Util, $log) {

                    new LocaleHelper.Locale({
                        scope : $scope
                    });

                    $scope.viewerOnly = false;
                    $scope.expand = function() {
                        $scope.viewerOnly = true;
                    };
                    $scope.compress = function() {
                        $scope.viewerOnly = false;
                    };
                    $scope.checkEscape = function(event) {
                        if (27 == event.keyCode) { //27 is Escape key code
                            $scope.viewerOnly = false;
                        }
                    };

                    $scope.acmTicket = '';
                    $scope.userId = '';
                    $scope.ecmFileProperties = {};
                    $scope.snowboundUrl = '';
                    $scope.ecmFileEvents = [];
                    $scope.ecmFileParticipants = [];
                    $scope.userList = [];
                    $scope.caseInfo = {};
                    $scope.fileInfo = {
                        id : $stateParams['id'],
                        containerId : $stateParams['containerId'],
                        containerType : $stateParams['containerType'],
                        name : $stateParams['name'],
                        selectedIds : $stateParams['selectedIds']
                    };
                    $scope.showVideoPlayer = false;
                    $scope.transcriptionTabActive = false;

                    var transcriptionConfigurationPromise = TranscriptionManagementService.getTranscribeConfiguration();

                    $scope.getEcmFileActiveVersion = function(ecmFile) {
                        if (Util.isEmpty(ecmFile) || Util.isEmpty(ecmFile.activeVersionTag) || Util.isArrayEmpty(ecmFile.versions)) {
                            return null;
                        }

                        var activeVersion = _.find(ecmFile.versions, function(version) {
                            return ecmFile.activeVersionTag === version.versionTag;
                        });

                        return activeVersion;
                    };

                    $scope.$on('transcribe-data-model', function(event, transcribeObj) {
                        $scope.transcribeObjectModel = transcribeObj;

                        var track = null;
                        var videoElement = angular.element(document.getElementsByTagName("video")[0])[0];
                        if (videoElement) {
                            track = videoElement.addTextTrack("subtitles", "Transcription", $scope.transcribeObjectModel.language);
                            videoElement.addEventListener("play", function() {
                                track.mode = "showing";
                            });
                        }

                        angular.forEach($scope.transcribeObjectModel.transcribeItems, function(value, key) {
                            if (track != null) {
                                addCue(track, value);
                            }
                        });

                        //color the status
                        $scope.colorTranscribeStatus = function() {
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

                    });

                    var addCue = function(track, value) {
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
                    }

                    $scope.playAt = function(seconds) {
                        var videoElement = angular.element(document.getElementsByTagName("video")[0])[0];
                        if (videoElement) {
                            videoElement.pause();
                            videoElement.currentTime = seconds;
                            videoElement.play();
                        }
                    }

                    /**
                     * Builds the snowbound url based on the parameters passed into the controller state and opens the
                     * specified document in an iframe which points to snowbound
                     */
                    $scope.openSnowboundViewer = function() {
                        var viewerUrl = SnowboundService.buildSnowboundUrl($scope.ecmFileProperties, $scope.acmTicket, $scope.userId,
                                $scope.fileInfo);
                        $scope.documentViewerUrl = $sce.trustAsResourceUrl(viewerUrl);
                    };

                    $scope.$bus.subscribe('update-viewer-opened-versions', function(openedVersions) {
                        $scope.fileInfo.selectedIds = openedVersions.map(function(openedVersion, index) {
                            if (index == 0) {
                                $scope.fileInfo.id = $stateParams['selectedIds'] + ":" + openedVersion.versionTag;
                            }
                            return $stateParams['selectedIds'] + ":" + openedVersion.versionTag;
                        }).join(',');

                        $scope.openSnowboundViewer();
                    });

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
                        fileId : $stateParams['id']
                    });
                    var ecmFileEvents = EcmService.getFileEvents({
                        fileId : $stateParams['id']
                    });
                    var ecmFileParticipants = EcmService.getFileParticipants({
                        fileId : $stateParams['id']
                    });

                    $q.all(
                            [ ticketInfo, userInfo, totalUserInfo, ecmFileConfig, ecmFileInfo.$promise, ecmFileEvents.$promise,
                                    ecmFileParticipants.$promise, formsConfig, transcriptionConfigurationPromise ]).then(
                            function(data) {
                                $scope.acmTicket = data[0].data;
                                $scope.userId = data[1].userId;
                                $scope.userList = data[2];
                                $scope.ecmFileProperties = data[3];
                                $scope.ecmFile = data[4];
                                $scope.ecmFileEvents = data[5];
                                $scope.ecmFileParticipants = data[6];
                                $scope.formsConfig = data[7];
                                $scope.transcriptionConfiguration = data[8];

                                $scope.transcribeEnabled = $scope.transcriptionConfiguration.data.enabled;

                                $timeout(function() {
                                    $scope.$broadcast('document-data', $scope.ecmFile);
                                }, 1000);

                                var durationInSeconds = 0;
                                var activeVersion = $scope.getEcmFileActiveVersion($scope.ecmFile);
                                if (!Util.isEmpty(activeVersion)) {
                                    durationInSeconds = activeVersion.durationSeconds;
                                }

                                var key = $scope.ecmFile.fileType + ".name";
                                // Search for descriptive file type in acm-forms.properties
                                $scope.fileType = $scope.formsConfig[key];
                                if ($scope.fileType === undefined) {
                                    // If descriptive file type does not exist, fallback to previous raw file type
                                    $scope.fileType = $scope.ecmFile.fileType;
                                }

                                $scope.mediaType = $scope.ecmFile.fileActiveVersionMimeType.indexOf("video") === 0 ? "video"
                                        : ($scope.ecmFile.fileActiveVersionMimeType.indexOf("audio") === 0 ? "audio" : "other");

                                if ($scope.mediaType === "video" || $scope.mediaType === "audio") {
                                    $scope.config = {
                                        sources : [ {
                                            src : $sce.trustAsResourceUrl('api/latest/plugin/ecm/stream/' + $scope.ecmFile.fileId),
                                            type : $scope.ecmFile.fileActiveVersionMimeType
                                        } ],
                                        theme : "lib/videogular-themes-default/videogular.css",
                                        plugins : {
                                            poster : "branding/loginlogo.png"
                                        },
                                        autoPlay : false
                                    };
                                    $scope.showVideoPlayer = true;
                                } else {
                                    // Opens the selected document in the snowbound viewer
                                    $scope.openSnowboundViewer();
                                }

                                $scope.transcriptionTabActive = $scope.showVideoPlayer && $scope.transcribeEnabled;
                            });
                } ]);
