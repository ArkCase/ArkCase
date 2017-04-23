'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Email
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.email.client.service.js directives/doc-tree/doc-tree-ext.email.client.service.js}
 *
 * DocTree extensions for email functions.
 */
angular.module('services').factory('DocTreeExt.Email', ['$q', '$modal', '$translate', '$browser', 'UtilService', 'LookupService', 'Ecm.EmailService'
    , function ($q, $modal, $translate, $browser, Util, LookupService, EcmEmailService
    ) {

        LookupService.getConfig("notification").then(function (data) {
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
            ,getCommandHandlers: function(DocTree) {
                return [
                    {
                        name: "email",
                        execute: function (nodes, args) {
                            Email.openModal(DocTree, nodes);
                        }
                    }
                ];
            }

            , arkcaseUrl: "localhost"
            , arkcasePort: ""
            , allowMailFilesAsAttachments: true
            , allowMailFilesToExternalAddresses: true
            , API_DOWNLOAD_DOCUMENT: "/api/v1/plugin/ecm/download?ecmFileId="

            , openModal: function (DocTree, nodes) {
                var params = {
                    config: Util.goodMapValue(DocTree.treeConfig, "emailDialog", {}),
                    nodes: nodes,
                    emailSendConfiguration: DocTree.treeConfig.emailSendConfiguration
                };

                var modalInstance = $modal.open({
                    templateUrl: "directives/doc-tree/doc-tree-ext.email.dialog.html"
                    , controller: 'directives.DocTreeEmailDialogController'
                    , animation: true
                    , size: 'lg'
                    , resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (res) {
                    var emailAddresses = _.pluck(recipients, "email");

                    if (res.action === 'SEND_ATTACHMENTS') {
                        var emailData = Email._makeEmailDataForEmailWithAttachments(DocTree, res.recipients, res.selectedFilesToEmail);
                        EcmEmailService.sendEmailWithAttachments(emailData);
                    }
                    else if(res.action === 'SEND_HYPERLINKS') {
                        var emailData = Email._makeEmailDataForEmailWithLinks(DocTree, res.recipients, res.selectedFilesToEmail);
                        EcmEmailService.sendEmail(emailData);
                    }
                });
            }

            , _buildSubject: function (DocTree) {
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
            }
            , _makeEmailDataForEmailWithLinks: function (DocTree, emailAddresses, nodeIds, title) {
                var emailData = {};
                emailData.subject = this._buildSubject(DocTree);
                emailData.title = Util.goodValue(title, $translate.instant("common.directive.docTree.email.defaultTitle"));
                emailData.header = $translate.instant("common.directive.docTree.email.headerForLinks");
                emailData.footer = "\n\n" + $translate.instant("common.directive.docTree.email.footerForLinks");
                emailData.emailAddresses = emailAddresses;
                emailData.fileIds = nodeIds;
                emailData.baseUrl = Email._makeBaseUrl();
                return emailData;
            }
            , _makeEmailDataForEmailWithAttachments: function (DocTree, emailAddresses, nodeIds) {
                var emailData = {};
                emailData.subject = this._buildSubject(DocTree);
                emailData.body = $translate.instant("common.directive.docTree.email.bodyForAttachment");
                emailData.header = $translate.instant("common.directive.docTree.email.headerForAttachment");
                emailData.footer = $translate.instant("common.directive.docTree.email.footerForAttachment");
                emailData.emailAddresses = emailAddresses;
                emailData.attachmentIds = nodeIds;
                return emailData;
            }
            , _extractFileIds: function (nodes) {
                var fileIds = [];
                if (Util.isArray(nodes)) {
                    for (var i = 0; i < nodes.length; i++) {
                        fileIds.push(Util.goodMapValue(nodes[i], "data.objectId"));
                    }
                }
                return fileIds;
            }
            , _makeBaseUrl: function () {
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
    }
]);


angular.module('directives').controller('directives.DocTreeEmailDialogController', ['$scope', '$modalInstance'
        , 'UtilService', 'params', 'DocTreeExt.Email', '$modal'
        , function ($scope, $modalInstance, Util, params, DocTreeExtEmail, $modal) {
            $scope.modalInstance = $modalInstance;
            $scope.config = params.config;
            $scope.emailSendConfiguration = angular.copy(params.emailSendConfiguration);
            $scope.summernoteOptions = {
                focus: true,
                dialogsInBody: true,
                height: 300
            };
            $scope.nodes = _.filter(params.nodes, function(node) {
                return !node.folder;
            });
            $scope.emailSendConfiguration.allowSending = $scope.nodes.length > 0 ? true : false;
            $scope.emailDataModel = {};
            $scope.emailDataModel.selectedFilesToEmail = DocTreeExtEmail._extractFileIds($scope.nodes);
            $scope.emailDataModel.deliveryMethod = 'SEND_ATTACHMENTS';
            var processDeliveryMethods = function() {
                if($scope.emailSendConfiguration.allowSending) {
                    if(!$scope.emailSendConfiguration.allowAttachments && $scope.emailSendConfiguration.allowHyperlinks) {
                        $scope.emailDataModel.deliveryMethod = 'SEND_HYPERLINKS';
                    } else if (!$scope.emailSendConfiguration.allowAttachments && !$scope.emailSendConfiguration.allowHyperlinks) {
                        $scope.emailSendConfiguration.allowSending = false;
                    }
                }
            };
            processDeliveryMethods();
            $scope.recipients = [];
            var buildRecipientsStr = function(recipients) {
                var recipientsStr = '';
                _.forEach(recipients, function(recipient, index){
                    if(index === 0) {
                        recipientsStr = recipient.email;
                    } else {
                        recipientsStr = recipientsStr + '; ' + recipient.email;
                    }
                });

                return recipientsStr;
            };
            $scope.chooseRecipients = function() {
                var modalInstance = $modal.open({
                    templateUrl: 'directives/doc-tree/doc-tree-ext.email-recipients.dialog.html'
                    , controller: 'directives.DocTreeEmailRecipientsDialogController'
                    , animation: true
                    , size: 'lg'
                    , resolve: {
                        config: function() {
                            return $scope.config;
                        },
                        recipients: function () {
                            return $scope.recipients;
                        }
                    }
                });

                modalInstance.result.then(function(recipients) {
                    $scope.recipients = recipients;
                    $scope.recipientsStr = buildRecipientsStr(recipients);
                });
            };

            $scope.onClickCancel = function () {
                $modalInstance.close(false);
            };
            $scope.onClickOk = function () {
                $scope.emailDataModel.action = !$scope.emailSendConfiguration.allowSending ? 'NO_ATTACHMENTS' : $scope.emailDataModel.deliveryMethod;
                $scope.emailDataModel.recipients = $scope.recipientsStr.split('; ');
                $modalInstance.close($scope.emailDataModel);
            };
            $scope.disableOk = function () {
                return Util.isEmpty($scope.recipientsStr);
            };

        }
    ]
);

angular.module('directives').controller('directives.DocTreeEmailRecipientsDialogController', ['$scope', '$modalInstance', 'DocTreeExt.Email', 'config', 'recipients',
    function($scope, $modalInstance, DocTreeExtEmail, config, recipients) {
        $scope.config = config;
        $scope.recipients = angular.copy(recipients);

        $scope.onSelectRecipient = function(selectedItems, lastSelectedItems, isSelected) {
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
    }
]);


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