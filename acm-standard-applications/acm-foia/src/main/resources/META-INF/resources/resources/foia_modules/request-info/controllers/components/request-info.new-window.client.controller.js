'use strict';

/**
 * @ngdoc controller
 * @name request-info.controller:RequestInfoFullScreenController
 *
 * @description
 * {@link https://github.com/Armedia/bactes360/blob/develop/bactes-user-interface/src/main/resources/META-INF/resources/resources/modules/request-info/controllers/components/request-info.new-window.client.controller.js modules/request-info/controllers/components/request-info.new-window.client.controller.js}
 *
 * The request-info module full screen controller
 */
angular.module('request-info').controller(
        'RequestInfoFullScreenController',
        [ '$scope', '$log', '$sce', '$q', '$state', '$timeout', '$stateParams', 'ConfigService', 'Authentication', 'RequestInfo.RequestsService', 'Requests.AttachmentsService', 'Requests.RequestsService', 'LookupService', 'TicketService',
                function($scope, $log, $sce, $q, $state, $timeout, $stateParams, ConfigService, Authentication, RequestsService, AttachmentsService, GenericRequestsService, LookupService, TicketService) {

                    var requestId = $stateParams['requestId'];
                    $scope.openAuthorizationDocuments = [];
                    $scope.openAbstractDocuments = [];
                    $scope.openAttachmentDocuments = [];

                    $scope.acmTicket = '';
                    $scope.ecmFileConfig = {};
                    $scope.userId = '';
                    $scope.userFullName = '';
                    $scope.requestId = '';

                    $scope.$sce = $sce; // used to allow snowbound url (on a different domain) to be injected by angular

                    $scope.config = ConfigService.getModule({
                        moduleId: 'request-info'
                    }, function(config) {
                    });

                    // Controls workflow buttons
                    $scope.requestInProgress = false;

                    // Control events from document
                    $scope.$on('req-abstract-docs-updated', documentsUpdated);
                    $scope.$on('req-authorization-docs-updated', documentsUpdated);

                    // Obtains the request level metadata
                    var requestInfo = RequestsService.getRequestInfo({
                        requestId: $stateParams['requestId']
                    });

                    // Obtains list of all documents within the request
                    var documentInfo = GenericRequestsService.queryDocument({
                        requestId: $stateParams['requestId']
                    });

                    // Obtains authentication token for ArkCase
                    var ticketInfo = TicketService.getArkCaseTicket();

                    // Retrieves the properties from the ecmFileService.properties file (including Snowbound configuration)
                    var ecmFileInfo = LookupService.getConfig('ecmFileService');

                    var promiseUserInfo = Authentication.queryUserInfo();

                    $q.all([ $scope.config.$promise, requestInfo.$promise, documentInfo.$promise, ticketInfo, ecmFileInfo, promiseUserInfo ]).then(function(data) {
                        var config = data[0];
                        var requestInfo = data[1];
                        var documentInfo = data[2];
                        $scope.acmTicket = data[3].data;
                        $scope.ecmFileConfig = data[4];
                        $scope.editingMode = !$scope.ecmFileConfig['ecm.viewer.snowbound.readonly.initialState'];
                        $scope.userId = data[5].userId;
                        $scope.userFullName = data[5].fullName;

                        $scope.requestInfo = requestInfo;

                        $scope.requiredFields = config.requiredFields;

                        if ($scope.requestInfo.queue) {
                            $scope.$broadcast('required-fields-retrieved', $scope.requiredFields[$scope.requestInfo.queue.id]);
                        }

                        // Splits request documents into groups for authorization, abstract, correspondence
                        var authorizationFiles = AttachmentsService.buildAttachmentList(documentInfo, 'authorization');
                        var abstractFiles = AttachmentsService.buildAttachmentList(documentInfo, 'abstract');
                        var correspondenceFiles = AttachmentsService.buildAttachmentList(documentInfo, 'correspondence');

                        $scope.$broadcast('request-info-retrieved', $scope.requestInfo);

                        var documents = $stateParams.documents.split(',');
                        if (documents.length < 1) {
                            //no documents to show... should not proceed further
                            return;
                        }
                        // Loads the first available document into the viewer when the page loads
                        var allFiles = authorizationFiles.concat(abstractFiles).concat(correspondenceFiles);
                        for (var index = 0; index < allFiles.length; index++) {
                            var availableDocument = allFiles[index];
                            if (availableDocument.ecmFile.mimeType != "application/pdf") {
                                continue;
                            }

                            if (_.find(documents, function(element) {
                                return availableDocument.documentId == element;
                            })) {
                                if (availableDocument.documentType == 'authorization') {
                                    $scope.openAuthorizationDocuments = [ availableDocument ];
                                } else if (availableDocument.documentType == 'abstract') {
                                    $scope.openAbstractDocuments = [ availableDocument ];
                                } else if (availableDocument.documentType == 'correspondence') {
                                    $scope.openAttachmentDocuments = [ availableDocument ];
                                }
                            }
                        }
                        $scope.openViewerMultiple();
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
                    $scope.openViewerMultiple = function() {
                        // All selected documents (authorization, abstract, correspondence) will be loaded in addition to the document which was clicked on
                        // and filter only pdf documents.
                        var allOpenDocuments = AttachmentsService.filterPdfDocuments($scope.openAuthorizationDocuments.concat($scope.openAbstractDocuments).concat($scope.openAttachmentDocuments));

                        // Generates and loads a url that will open the selected documents in the viewer
                        if (allOpenDocuments.length > 0) {
                            var snowUrl = AttachmentsService.buildViewerUrlMultiple($scope.ecmFileConfig, $scope.acmTicket, $scope.userId, $scope.userFullName, allOpenDocuments, !$scope.editingMode, $scope.requestId);
                            $scope.loadViewerIframe(snowUrl);
                        } else {
                            $scope.loadViewerIframe('about:blank');
                        }
                    };

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
                    $scope.loadViewerIframe = function(url) {
                        $scope.$broadcast('change-viewer-document', url);
                    };

                    /**
                     * Send 'documents-updated' event if documents were uploaded or deleted
                     * @param e
                     * @param pages
                     */
                    function documentsUpdated(e, pages) {
                        var pagesInfo = null;
                        if (e.name == 'req-authorization-docs-updated') {
                            pagesInfo = {
                                type: 'authorization',
                                pages: pages
                            }
                        } else if (e.name == 'req-abstract-docs-updated') {
                            pagesInfo = {
                                type: 'abstract',
                                pages: pages
                            }
                        }
                        if (pagesInfo) {
                            $scope.$broadcast('documents-updated', pagesInfo);
                        }
                    }
                } ]);