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
            Email.allowMailFilesAsAttachments = ("true" == Util.goodValue(data["notification.allowMailFilesAsAttachments"], "true"));
            Email.allowMailFilesToExternalAddresses = ("true" == Util.goodValue(data["notification.allowMailFilesToExternalAddresses"], "true"));
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
                            var node = nodes[0];
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
                    config: Util.goodMapValue(DocTree.treeConfig, "emailDialog", {})
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
                modalInstance.result.then(function (recipients) {
                    if (!Util.isArrayEmpty(recipients)) {
                        var emailAddresses = _.pluck(recipients, "email");

                        if (Email.allowMailFilesAsAttachments) {
                            var emailData = Email._makeEmailDataForEmailWithAttachments(DocTree, emailAddresses, nodes);
                            EcmEmailService.sendEmailWithAttachments(emailData);
                        }
                        else {
                            //var emailData = Email._makeEmailDataForEmailWithLinks(DocTree, emailAddresses, nodes, title);
                            var emailData = Email._makeEmailDataForEmailWithLinks(DocTree, emailAddresses, nodes);
                            EcmEmailService.sendEmail(emailData);
                        }
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
            , _makeEmailDataForEmailWithLinks: function (DocTree, emailAddresses, nodes, title) {
                var emailData = {};
                emailData.subject = this._buildSubject(DocTree);
                emailData.title = Util.goodValue(title, $translate.instant("common.directive.docTree.email.defaultTitle"));
                emailData.header = $translate.instant("common.directive.docTree.email.headerForLinks");
                emailData.footer = "\n\n" + $translate.instant("common.directive.docTree.email.footerForLinks");
                emailData.emailAddresses = emailAddresses;
                emailData.fileIds = Email._extractFileIds(nodes);
                emailData.baseUrl = Email._makeBaseUrl();
                return emailData;
            }
            , _makeEmailDataForEmailWithAttachments: function (DocTree, emailAddresses, nodes) {
                var emailData = {};
                emailData.subject = this._buildSubject(DocTree);
                emailData.body = $translate.instant("common.directive.docTree.email.bodyForAttachment");
                emailData.header = $translate.instant("common.directive.docTree.email.headerForAttachment");
                emailData.footer = $translate.instant("common.directive.docTree.email.footerForAttachment");
                emailData.emailAddresses = emailAddresses;
                var attachmentIds = [];
                if (Util.isArray(nodes)) {
                    for (var i = 0; i < nodes.length; i++) {
                        attachmentIds.push(Util.goodMapValue(nodes[i], "data.objectId"));
                    }
                }

                emailData.attachmentIds = attachmentIds;
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
        , 'UtilService', 'params'
        , function ($scope, $modalInstance, Util, params) {
            $scope.modalInstance = $modalInstance;
            $scope.config = params.config;

            $scope.recipients = [];
            $scope.onItemsSelected = function (selectedItems, lastSelectedItems, isSelected) {
                var recipientTokens = Util.goodValue($scope.recipientsStr).split(";");
                _.each(lastSelectedItems, function (selectedItem) {
                    var found = _.find($scope.recipients, function (recipient) {
                        return Util.compare(selectedItem.name, recipient.name) || Util.compare(selectedItem.email_lcs, recipient.email)
                    });
                    if (isSelected && !found) {
                        $scope.recipients.push({
                            name: Util.goodValue(selectedItem.name)
                            , email: Util.goodValue(selectedItem.email_lcs)
                        });

                    } else if (!isSelected && found) {
                        _.remove($scope.recipients, found);
                    }
                });

                $scope.recipientsStr = _.pluck($scope.recipients, "name").join(";");
            };
            $scope.onChangeRecipients = function () {
                var recipientsNew = [];
                var recipientTokens = Util.goodValue($scope.recipientsStr).split(";");
                _.each(recipientTokens, function (token) {
                    token = token.trim();
                    if (!Util.isEmpty(token)) {
                        var found = _.find($scope.recipients, function (recipient) {
                            return (token == recipient.name || token == recipient.email);
                        });
                        if (found) {
                            recipientsNew.push(found);
                        } else {
                            var recipientUserTyped = {name: token, email: token};
                            recipientsNew.push(recipientUserTyped);
                        }
                    }
                });
                $scope.recipients = recipientsNew;
            };
            $scope.onClickCancel = function () {
                $modalInstance.close(false);
            };
            $scope.onClickOk = function () {
                //var a = $scope.searchControl.getSelectedItems();
                $modalInstance.close($scope.recipients);
            };
            $scope.disableOk = function () {
                return Util.isEmpty($scope.recipientsStr);
            };

        }
    ]
);



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