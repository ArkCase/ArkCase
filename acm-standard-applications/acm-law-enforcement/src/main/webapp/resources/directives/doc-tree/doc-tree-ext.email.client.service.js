'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Email
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.email.client.service.js directives/doc-tree/doc-tree-ext.email.client.service.js}
 *
 * DocTree extensions for email functions.
 */
angular.module('services').factory('DocTreeExt.Email', [ '$q', '$modal', '$translate', '$browser', 'UtilService', 'LookupService', 'Ecm.EmailService', function($q, $modal, $translate, $browser, Util, LookupService, EcmEmailService) {

    LookupService.getConfig("notification").then(function(data) {
        Email.arkcaseUrl = Util.goodValue(data["arkcase.url"]);
        Email.arkcasePort = Util.goodValue(data["arkcase.port"]);
    });

    var Email = {

        /**
         * @ngdoc method
         * @name getColumnRenderers
         * @methodOf services:DocTreeExt.Email
         *
         * @description
         * No renderer is needed; return empty list of renderers.
         *
         * @param {Object} DocTree  DocTree object defined in doc-tree directive
         *
         */
        getColumnRenderers: function(DocTree) {
            return [];
        }

        /**
         * @ngdoc method
         * @name getCommandHandlers
         * @methodOf services:DocTreeExt.Email
         *
         * @description
         * Return list of command handlers this extension provides. This function is required for a docTree extension
         *
         * @param {Object} DocTree  DocTree object defined in doc-tree directive
         *
         */
        ,
        getCommandHandlers: function(DocTree) {
            return [ {
                name: "email",
                execute: function(nodes, args) {
                    Email.openModal(DocTree, nodes);
                }
            } ];
        }

        ,
        arkcaseUrl: "localhost",
        arkcasePort: "",
        allowMailFilesAsAttachments: true,
        allowMailFilesToExternalAddresses: true,
        API_DOWNLOAD_DOCUMENT: "/api/v1/plugin/ecm/download?ecmFileId="

        ,
        openModal: function(DocTree, nodes) {
            var params = {
                config: Util.goodMapValue(DocTree.treeConfig, "emailDialog", {}),
                nodes: nodes,
                emailSendConfiguration: DocTree.treeConfig.emailSendConfiguration,
                DocTree: DocTree
            };

            var modalInstance = $modal.open({
                templateUrl: "directives/doc-tree/doc-tree-ext.email.dialog.html",
                controller: 'directives.DocTreeEmailDialogController',
                animation: true,
                size: 'lg',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function(res) {
                var emailData = {};
                if (res.action === 'SEND_ATTACHMENTS') {
                    emailData = Email._makeEmailDataForEmailWithAttachments(DocTree, res);
                    EcmEmailService.sendEmailWithAttachments(emailData, DocTree.getObjType());
                } else if (res.action === 'SEND_HYPERLINKS') {
                    emailData = Email._makeEmailDataForEmailWithLinks(DocTree, res);
                    EcmEmailService.sendEmail(emailData, DocTree.getObjType());
                } else if (res.action === 'SEND_ATTACHMENTS_AND_HYPERLINKS') {
                    emailData = Email._makeEmailDataForEmailWithAttachmentsAndLinks(DocTree, res);
                    EcmEmailService.sendEmailWithAttachmentsAndLinks(emailData, DocTree.getObjType());
                } else {
                    emailData = Email._makeEmailDataForPlainEmail(DocTree, res);
                    EcmEmailService.sendEmail(emailData, DocTree.getObjType());
                }
            });
        }

        ,
        _buildSubject: function(DocTree) {
            var subject = Util.goodMapValue(DocTree, "treeConfig.email.emailSubject");
            var regex = new RegExp(Util.goodMapValue(DocTree, "treeConfig.email.subjectRegex"));
            var match = subject.match(regex);
            if (match) {
                var objectType = match[Util.goodMapValue(DocTree, "treeConfig.email.objectTypeRegexGroup")];
                var objectNumber = match[Util.goodMapValue(DocTree, "treeConfig.email.objectNumberRegexGroup")];
                if (objectType && objectNumber) {
                    return objectType + DocTree.objectInfo[objectNumber];
                }
            }

            return "";
        },
        _makeEmailDataForEmailWithLinks: function(DocTree, emailModel) {
            var emailData = {};
            emailData.subject = emailModel.subject;
            emailData.body = emailModel.body;
            emailData.footer = '\n\n' + emailModel.footer;
            emailData.emailAddresses = emailModel.recipients;
            emailData.fileIds = emailModel.selectedFilesToEmail;
            emailData.baseUrl = Email._makeBaseUrl();
            return emailData;
        },
        _makeEmailDataForEmailWithAttachmentsAndLinks: function(DocTree, emailModel) {
            var emailData = {};
            emailData.subject = emailModel.subject;
            emailData.body = emailModel.body;
            emailData.footer = '\n\n' + emailModel.footer;
            emailData.emailAddresses = emailModel.recipients;
            emailData.fileIds = emailModel.selectedFilesToEmail;
            emailData.attachmentIds = emailModel.selectedFilesToEmail;
            emailData.baseUrl = Email._makeBaseUrl();
            return emailData;
        },
        _makeEmailDataForEmailWithAttachments: function(DocTree, emailModel) {
            var emailData = {};
            emailData.subject = emailModel.subject;
            emailData.body = emailModel.body;
            emailData.footer = '\n\n' + emailModel.footer;
            emailData.emailAddresses = emailModel.recipients;
            emailData.attachmentIds = emailModel.selectedFilesToEmail;
            return emailData;
        },
        _makeEmailDataForPlainEmail: function(DocTree, emailModel) {
            var emailData = {};
            emailData.subject = emailModel.subject;
            emailData.body = emailModel.body;
            emailData.footer = '\n\n' + emailModel.footer;
            emailData.emailAddresses = emailModel.recipients;
            return emailData;
        },
        _extractFileIds: function(nodes) {
            var fileIds = [];
            if (Util.isArray(nodes)) {
                for (var i = 0; i < nodes.length; i++) {
                    fileIds.push(Util.goodMapValue(nodes[i], "data.objectId"));
                }
            }
            return fileIds;
        },
        _makeBaseUrl: function() {
            var url = Util.goodValue(Email.arkcaseUrl);
            if (!Util.isEmpty(Email.arkcasePort)) {
                url += ":" + Util.goodValue(Email.arkcasePort);
            }
            var baseHref = $browser.baseHref().slice(0, -1);
            url += baseHref + Email.API_DOWNLOAD_DOCUMENT;
            return url;
        }

    }; // end Email

    return Email;
} ]);

angular.module('directives').controller('directives.DocTreeEmailDialogController',
        [ '$scope', '$modalInstance', 'UtilService', 'params', 'DocTreeExt.Email', 'ObjectService', 'Object.InfoService', '$modal', '$translate', function($scope, $modalInstance, Util, params, DocTreeExtEmail, ObjectService, ObjectInfoService, $modal, $translate) {
            $scope.modalInstance = $modalInstance;
            $scope.config = params.config;
            $scope.DocTree = params.DocTree;
            $scope.emailSendConfiguration = angular.copy(params.emailSendConfiguration);
            $scope.summernoteOptions = {
                focus: true,
                height: 300
            };
            $scope.nodes = _.filter(params.nodes, function(node) {
                return !node.folder;
            });
            $scope.emailDataModel = {};
            $scope.emailDataModel.selectedFilesToEmail = DocTreeExtEmail._extractFileIds($scope.nodes);
            $scope.emailDataModel.deliveryMethod = 'SEND_ATTACHMENTS';

            $scope.recipients = [];
            $scope.recipientsStr = [];
            var objectId = params.DocTree._objId;
            var objectType = params.DocTree._objType;
            var objectTypeInEndpoint = ObjectService.ObjectTypesInEndpoints[objectType];
            if (objectType == ObjectService.ObjectTypes.CASE_FILE || objectType == ObjectService.ObjectTypes.COMPLAINT) {
                ObjectInfoService.getObjectInfo(objectTypeInEndpoint, objectId).then(function(data) {
                    var originator = data.originator;
                    if (originator != undefined && !Util.isArrayEmpty(originator.person.contactMethods)) {
                        var emailOfOriginator = originator.person.contactMethods[2].value;
                        if (emailOfOriginator != null) {
                            $scope.recipients.push(emailOfOriginator);
                            $scope.recipientsStr.push(emailOfOriginator);
                        }
                    }
                });
            }

            var processDeliveryMethods = function() {
                $scope.emailSendConfiguration.allowDocuments = $scope.nodes.length > 0 ? true : false;
                if ($scope.emailSendConfiguration.allowDocuments) {
                    if (!$scope.emailSendConfiguration.allowAttachments && $scope.emailSendConfiguration.allowHyperlinks) {
                        $scope.emailDataModel.deliveryMethod = 'SEND_HYPERLINKS';
                    } else if (!$scope.emailSendConfiguration.allowAttachments && !$scope.emailSendConfiguration.allowHyperlinks) {
                        $scope.emailSendConfiguration.allowDocuments = false;
                    }
                }
            };

            processDeliveryMethods();

            var processDefaultEmailValues = function() {
                var emailAction = !$scope.emailSendConfiguration.allowDocuments ? 'NO_ATTACHMENTS' : $scope.emailDataModel.deliveryMethod;
                $scope.emailDataModel.subject = DocTreeExtEmail._buildSubject($scope.DocTree);
                $scope.emailDataModel.footer = $translate.instant('common.directive.docTree.email.defaultFooter');

                switch (emailAction) {
                case 'SEND_ATTACHMENTS':
                    $scope.emailDataModel.body = $translate.instant('common.directive.docTree.email.defaultBodyAttachments');
                    break;
                case 'SEND_HYPERLINKS':
                    $scope.emailDataModel.body = $translate.instant('common.directive.docTree.email.defaultBodyLinks');
                    break;
                }
            };

            processDefaultEmailValues();

            var buildRecipientsStr = function(recipients) {
                var recipientsStr = '';
                _.forEach(recipients, function(recipient, index) {
                    if (index === 0) {
                        recipientsStr = recipient.email;
                    } else {
                        recipientsStr = recipientsStr + '; ' + recipient.email;
                    }
                });

                return recipientsStr;
            };

            $scope.chooseRecipients = function() {
                var modalInstance = $modal.open({
                    templateUrl: 'directives/doc-tree/doc-tree-ext.email-recipients.dialog.html',
                    controller: 'directives.DocTreeEmailRecipientsDialogController',
                    animation: true,
                    size: 'lg',
                    resolve: {
                        config: function() {
                            return $scope.config;
                        },
                        recipients: function() {
                            return $scope.recipients;
                        }
                    }
                });

                modalInstance.result.then(function(recipients) {
                    $scope.recipients = recipients;
                    $scope.recipientsStr = buildRecipientsStr(recipients);
                });
            };

            $scope.onDeliveryMethodChange = function(deliveryMethod) {
                var emailBodyIsPristine = $scope.emailBodyForm.emailBody.$pristine;

                if (emailBodyIsPristine) {
                    switch (deliveryMethod) {
                    case 'SEND_ATTACHMENTS':
                        $scope.emailDataModel.body = $translate.instant('common.directive.docTree.email.defaultBodyAttachments');
                        break;
                    case 'SEND_HYPERLINKS':
                        $scope.emailDataModel.body = $translate.instant('common.directive.docTree.email.defaultBodyLinks');
                        break;
                    }
                }
            };

            $scope.onSelectFile = function(fileId) {
                var idx = $scope.emailDataModel.selectedFilesToEmail.indexOf(fileId);

                if (idx > -1) {
                    $scope.emailDataModel.selectedFilesToEmail.splice(idx, 1);
                } else {
                    $scope.emailDataModel.selectedFilesToEmail.push(fileId);
                }
            };

            $scope.onClickCancel = function() {
                $modalInstance.dismiss();
            };
            $scope.onClickOk = function() {
                $scope.emailDataModel.action = !$scope.emailSendConfiguration.allowDocuments ? 'NO_ATTACHMENTS' : $scope.emailDataModel.deliveryMethod;
                $scope.emailDataModel.recipients = $scope.recipientsStr.split('; ');
                $modalInstance.close($scope.emailDataModel);
            };
            $scope.disableOk = function() {
                return Util.isEmpty($scope.recipientsStr);
            };

        } ]);

angular.module('directives').controller('directives.DocTreeEmailRecipientsDialogController',
        [ '$scope', '$modalInstance', 'DocTreeExt.Email', 'Object.LookupService', 'config', 'recipients', 'UtilService', 'MessageService', '$translate', function($scope, $modalInstance, DocTreeExtEmail, ObjectLookupService, config, recipients, Util, MessageService, $translate) {

            $scope.config = config;
            $scope.recipients = angular.copy(recipients);

            $scope.onSelectRecipient = function(selectedItems, lastSelectedItems, isSelected) {
                if (Util.isEmpty(lastSelectedItems[0].email_lcs)) {
                    MessageService.info($translate.instant('common.directive.docTree.email.noEmailAddress') + lastSelectedItems[0].object_type_s.toLowerCase());
                } else {
                    var selectedRecipientEmail = lastSelectedItems[0].email_lcs;
                    var isRecipientSelected = _.find($scope.recipients, function(recipient) {
                        return recipient.email === selectedRecipientEmail;
                    });

                    var selectedRecipientDataModel = {
                        email: lastSelectedItems[0].email_lcs
                    };

                    if (isRecipientSelected) {
                        if (!isSelected) {
                            _.remove($scope.recipients, function(recipient) {
                                return recipient.email === isRecipientSelected.email;
                            });
                        }
                    } else {
                        if (isSelected) {
                            $scope.recipients.push(selectedRecipientDataModel);
                        }
                    }
                }
            };

            $scope.addAdditionalRecipients = function(tag) {
                var selectedRecipientDataModel = {
                    email: tag.email
                };
                $scope.recipients[$scope.recipients.length - 1] = selectedRecipientDataModel;
            };

            $scope.addRecipients = function() {
                $modalInstance.close($scope.recipients);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss();
            };

        } ]);

//, validateSentEmails: function (data) {
//    if (!Util.isArray(data)) {
//        return false;
//    }
//    for (var i = 0; i < data.length; i++) {
//        if (!Validator.validateSentEmail(data[i])) {
//            return false;
//        }
//    }
//    return true;
//}
//, validateSentEmail: function (data) {
//    if (Util.isEmpty(data.state)) {
//        return false;
//    }
//    if (Util.isEmpty(data.userEmail)) {
//        return false;
//    }
//    return true;
//}