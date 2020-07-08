'use strict';
/**
 * @ngdoc service
 * @name requests.service:Requests.AttachmentsService
 *
 * @description
 * {@link https://github.com/Armedia/bactes360/blob/develop/bactes-user-interface/src/main/resources/META-INF/resources/resources/modules/requests/services/attachments.client.service.js requests/services/attachments.client.service.js}
 *
 * Performs request's attachments manipulations
 */
angular.module('request-info').factory(
        'RequestInfo.AttachmentsService',
        [
                'Upload',
                '$http',
                'SnowboundService',
                function(Upload, $http, SnowboundService) {
                    return {
                        uploadAttachments: function(files, fileType, parentType, parentId) {
                            var uploadPath = "api/v1/service/ecm/upload";

                            // The ArkCase upload webscript requires the parent type/id and the type of file
                            var urlArgs = "parentObjectType=" + parentType + "&" + "parentObjectId=" + parentId + "&" + "fileType=" + fileType;

                            // Sends the files and metadata to ArkCase in a POST request
                            return Upload.upload({
                                url: uploadPath + "?" + urlArgs,
                                file: files
                            });
                        },
                        extractViewerBaseUrl: function(data) {
                            var viewerUrl = "";
                            if (data && data["ecm.viewer.snowbound"]) {
                                var viewerUrlConfig = data["ecm.viewer.snowbound"];
                                var urlConfigComponents = viewerUrlConfig.split("?");
                                if (urlConfigComponents && urlConfigComponents.length > 0) {
                                    viewerUrl = urlConfigComponents[0];
                                }
                            }
                            return viewerUrl;
                        },
                        buildViewerUrl: function(viewerBaseUrl, acmTicket, userId, userFullName, file, caseNumber) {

                            // Forces the viewer iframe to be reloaded with the latest version of the document
                            var randomUrlArgToCauseIframeRefresh = (new Date()).getTime();

                            return viewerBaseUrl + "?documentId=ecmFileId=" + file.fileId + "&acm_ticket=" + acmTicket + "&userid=" + userId + "&userFullName=" + userFullName + "&caseNumber=" + caseNumber + "&refreshCacheTimestamp=" + randomUrlArgToCauseIframeRefresh + "&documentName=" + file.fileName + "&parentObjectId=" + file.container.containerObjectId
                                    + "&parentObjectType=" + file.container.containerObjectType + "&selectedIds=";
                        },
                        buildOpenDocumentIdString: function(files) {
                            var openDocIds = '';
                            _.forEach(files, function(value) {
                                openDocIds += value.documentId + ',';
                            });
                            if (openDocIds.length > 0) {
                                openDocIds = openDocIds.substring(0, openDocIds.length - 1);
                            }
                            return openDocIds;
                        },

                        buildViewerUrlMultiple: function(ecmFileProperties, acmTicket, userId, userFullName, files, readonly, caseNumber) {
                            var viewerUrl = '';
                            if (files && files.length > 0) {

                                // Adds the list of additional documents to load to the viewer url
                                files[0].ecmFile.selectedIds = this.buildOpenDocumentIdString(files);

                                // Generates the viewer url with the first document as the primary document
                                viewerUrl = SnowboundService.buildSnowboundUrl(ecmFileProperties, acmTicket, userId, userFullName, files[0].ecmFile, readonly, caseNumber);
                            }
                            return viewerUrl;
                        },
                        buildAttachmentList: function(requestData, attachmentType) {
                            var attachmentFiles = [];

                            // Builds UI display list for attachment files of the specified type associated with the request
                            angular.forEach(requestData.children, function(value, key) {
                                if (value.objectType == "file" && value.type == attachmentType) {

                                    var fileInfo = {
                                        id: value.objectId,
                                        containerId: requestData.containerObjectId,
                                        containerType: requestData.containerObjectType,
                                        name: value.name,
                                        mimeType: value.mimeType,
                                        selectedIds: ''
                                    };

                                    // Generates the ui-grid data rows
                                    attachmentFiles.push({
                                        display: false,
                                        version: value.version,
                                        filename: value.name,
                                        pages: value.pageCount,
                                        documentType: value.type,
                                        documentId: value.objectId,
                                        ecmFile: fileInfo
                                    });
                                }
                            });
                            return attachmentFiles;
                        },
                        buildFileInfo: function(value, containerId) {

                            if (value.objectType == "file") {
                                var fileInfo = {
                                    id: value.objectId,
                                    containerId: containerId,
                                    containerType: 'CASE_FILE',
                                    name: value.name,
                                    mimeType: value.mimeType,
                                    selectedIds: ''
                                };
                            }
                            return fileInfo;
                        },
                        findPDFAttachmentByName: function(name, attachmentList) {
                            var attachmentMatch;
                            if (name && attachmentList) {
                                var extensionIndex = name.lastIndexOf('.');
                                if (extensionIndex >= 0 && extensionIndex + 1 < name.length) {
                                    var fileNameExtension = name.substring(extensionIndex + 1);

                                    // A match will have pdf type and the same filename as the new upload
                                    if (fileNameExtension.toLowerCase() == "pdf") {
                                        for (var i = 0; i < attachmentList.length; i++) {
                                            if (attachmentList[i].filename == name) {
                                                attachmentMatch = {
                                                    'file': attachmentList[i],
                                                    'gridRow': i
                                                };
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            return attachmentMatch;
                        },

                        /**
                         * @ngdoc method
                         * @name filterPdfDocuments
                         * @methodOf requests.service:Requests.AttachmentsService
                         *
                         * @description
                         * Remove all non pdf documents from from documents array. Non-mutable. Returns new created array.
                         *
                         * @param {Array<Object>} docsList Array of documents
                         *
                         * @returns {Array<Object>} New array with pdf documents
                         */
                        filterPdfDocuments: function(docsList) {
                            return _.filter(docsList, function(docIter) {
                                // Information about document comes from server in different formats, that's why we check different properties
                                return (_.get(docIter, 'ecmFile.mimeType') == 'application/pdf') || (_.get(docIter, 'fileMimeType') == 'application/pdf');
                            })
                        }
                    }
                } ]);