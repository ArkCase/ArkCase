'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Email
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.email.client.service.js directives/doc-tree/doc-tree-ext.email.client.service.js}
 *
 * DocTree extensions for email functions.
 */
angular.module('services').factory('DocTreeExt.Email',
        [ '$q', '$modal', '$translate', '$browser', 'UtilService', 'LookupService', 'Ecm.EmailService', 'ObjectService',
            'Object.InfoService', 'Person.InfoService','DocumentRepository.InfoService',
            function($q, $modal, $translate, $browser, Util, LookupService, EcmEmailService, ObjectService, ObjectInfoService,
                     PersonInfoService,DocumentRepositoryInfoService) {

            LookupService.getConfig("notification").then(function(data) {
                Email.arkcaseUrl = Util.goodValue(data["arkcase.url"]);
                Email.arkcasePort = Util.goodValue(data["arkcase.port"]);
            });

            function checkForOriginatorEmail(DocTree) {
                var objectId = DocTree._objId;
                var objectType = DocTree._objType;
                var objectTypeInEndpoint = ObjectService.ObjectTypesInEndpoints[objectType];
                var deferred = $q.defer();
                if(objectType == ObjectService.ObjectTypes.DOC_REPO) {
                   DocumentRepositoryInfoService.getDocumentRepositoryInfo(objectId).then(function(data) {
                       getOriginatorEmail(data);
                    });
                } else if (objectType === "BUSINESS_PROCESS") {
                    objectTypeInEndpoint = ObjectService.ObjectTypesInEndpoints.TASK;
                    objectId = DocTree.objectInfo.taskId;
                    ObjectInfoService.getObjectInfo(objectTypeInEndpoint, objectId).then(function (data) {
                        getOriginatorEmail(data);
                    });
                } else if (objectType === "COSTSHEET" || objectType === "TIMESHEET"){
                    ObjectInfoService.getCostOrTimeSheetObjectInfo(objectTypeInEndpoint, objectId).then(function(data) {
                        getOriginatorEmail(data);
                    });
                } else {
                    ObjectInfoService.getObjectInfo(objectTypeInEndpoint, objectId).then(function(data) {
                        getOriginatorEmail(data);
                    });
                }
                function getOriginatorEmail(data) {
                        var originator = data.originator;
                        var emailOfOriginator = "";
                        if (originator != undefined && !Util.isArrayEmpty(originator.person.contactMethods)) {
                            var i, emails = [];
                            var contactMethods = originator.person.contactMethods;
                            for (i in contactMethods) {
                                if (contactMethods[i].type.toLowerCase() == "email") {
                                    emails.push(contactMethods[i]);
                                }
                            }

                            if (!Util.isArrayEmpty(emails)) {
                                PersonInfoService.getPersonInfo(originator.person.id).then(function(person) {
                                    if (person.defaultEmail != null) {
                                        emailOfOriginator = person.defaultEmail.value;
                                    } else {
                                        emailOfOriginator = emails[0].value;
                                    }

                                    deferred.resolve(emailOfOriginator);
                                });
                            } else {
                                deferred.resolve(emailOfOriginator);
                            }
                        } else {
                            deferred.resolve(emailOfOriginator);
                        }
                    }

                return deferred.promise;
            }

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
                    var modalInstance;
                    checkForOriginatorEmail(DocTree).then(function(emailOfOriginator) {
                        var objectType = DocTree.scope.objectType === "BUSINESS_PROCESS" ? ObjectService.ObjectTypes.TASK : DocTree.scope.objectType;
                        var params = {
                            config: Util.goodMapValue(DocTree.treeConfig, "emailDialog", {}),
                            nodes: nodes,
                            emailSendConfiguration: DocTree.treeConfig.emailSendConfiguration,
                            DocTree: DocTree,
                            emailOfOriginator: emailOfOriginator,
                            emailSubject: $translate.instant('common.objectTypes.' + objectType) + " " + getAcmObjectNumber()
                        };

                        modalInstance = $modal.open({
                            templateUrl: "directives/doc-tree/doc-tree-ext.email.dialog.html",
                            controller: 'directives.DocTreeEmailDialogController',
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(res) {
                            var emailData = {};
                            emailData.subject = res.subject;
                            emailData.body = res.body;
                            emailData.footer = '\n\n' + res.footer;
                            emailData.emailAddresses = res.recipients;
                            emailData.ccEmailAddresses = res.ccRecipients;
                            emailData.bccEmailAddresses = res.bccRecipients;
                            emailData.objectId = DocTree._objId;
                            emailData.objectType = DocTree._objType;
                            emailData.objectNumber = DocTree.objectInfo.acmObjectNumber;
                            emailData.modelReferenceName = res.template;
                            emailData.attachmentIds = res.selectedFilesToEmail;

                            if (emailData.attachmentIds && emailData.modelReferenceName == 'plainEmail') {
                                EcmEmailService.sendEmailWithAttachments(emailData, emailData.objectType);
                            } else {
                                EcmEmailService.sendManualEmail(emailData);
                            }
                        });
                    });

                    function getAcmObjectNumber() {
                        switch (DocTree.scope.objectType) {
                            case ObjectService.ObjectTypes.CASE_FILE:
                                return DocTree.scope.objectInfo.acmObjectNumber;
                            case ObjectService.ObjectTypes.COMPLAINT:
                                return DocTree.scope.objectInfo.acmObjectNumber;
                            case ObjectService.ObjectTypes.CONSULTATION:
                                return DocTree.scope.objectInfo.acmObjectNumber;
                            case ObjectService.ObjectTypes.COSTSHEET:
                                return DocTree.scope.objectInfo.taskId || DocTree.scope.objectInfo.id;
                            case ObjectService.ObjectTypes.TIMESHEET:
                                return DocTree.scope.objectInfo.taskId;;
                            case ObjectService.ObjectTypes.TASK:
                                return DocTree.scope.objectInfo.taskId;
                            case ObjectService.ObjectTypes.DOC_REPO:
                                return DocTree.scope.objectInfo.id;
                            case ObjectService.ObjectTypes.BUSINESS_PROCESS:
                                return DocTree.scope.objectInfo.taskId;
                            default:
                                return DocTree.scope.objectInfo.acmObjectNumber;
                        }
                    }
                }

                ,
                _extractFileIds: function(nodes) {
                    var fileIds = [];
                    if (Util.isArray(nodes)) {
                        for (var i = 0; i < nodes.length; i++) {
                            fileIds.push(Util.goodMapValue(nodes[i], "data.objectId"));
                        }
                    }
                    return fileIds;
                }

            }; // end Email

            return Email;
        } ]);

angular.module('directives').controller('directives.DocTreeEmailDialogController', [ '$scope', '$modalInstance', 'UtilService', 'params', 'DocTreeExt.Email', '$modal', '$translate', 'Admin.CMTemplatesService', 'ObjectService', function($scope, $modalInstance, Util, params, DocTreeExtEmail, $modal, $translate, correspondenceService, ObjectService) {
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

    var templatesPromise = correspondenceService.retrieveActiveVersionTemplatesList('emailTemplate');
    templatesPromise.then(function(templates) {
        $scope.emailTemplates = _.filter(templates.data, function(et) {
            return et.activated && (et.objectType == 'ALL' || (et.objectType == 'FILE' && (et.parentType == $scope.DocTree._objType || Util.isEmpty((et.parentType)))));
        });
        $scope.emailTemplates = _.sortBy($scope.emailTemplates, "label");
        var found = _.find($scope.emailTemplates, {
            templateFilename: 'plainEmail.html'
        });
        $scope.template = found ? found.templateFilename : '';
    });

    $scope.recipients = [];
    $scope.recipientsStr = "";
    $scope.ccRecipients = [];
    $scope.ccRecipientsStr = "";
    $scope.bccRecipients = [];
    $scope.bccRecipientsStr = "";
    $scope.subject = params.emailSubject;
    var paramsPlainEmail = {};
    paramsPlainEmail.objectType = $scope.DocTree._objType;
    paramsPlainEmail.objectId = $scope.DocTree._objId;
    paramsPlainEmail.templateName = 'plainEmail.html';
    paramsPlainEmail.fileIds = $scope.emailDataModel.selectedFilesToEmail;

    var getTemplateContentPromise = correspondenceService.retrieveConvertedTemplateContent(paramsPlainEmail);
    getTemplateContentPromise.then(function (response) {
        if (response.data.templateEmailSubject) {
            $scope.emailDataModel.subject = response.data.templateEmailSubject;
        } else {
            $scope.emailDataModel.subject = $scope.subject;
        }
    });

    if (!Util.isEmpty(params.emailOfOriginator)) {
        $scope.recipients.push(params.emailOfOriginator);
        $scope.recipientsStr = params.emailOfOriginator;
    }

    var buildRecipientsStr = function (recipients) {
        var recipientsStr = '';
        _.forEach(recipients, function (recipient, index) {
            if (index === 0) {
                recipientsStr = recipient.email;
            } else {
                recipientsStr = recipientsStr + '; ' + recipient.email;
            }
        });

        return recipientsStr;
    };

    $scope.chooseRecipients = function (recipientType) {
        var modalInstance = $modal.open({
            templateUrl: 'directives/doc-tree/doc-tree-ext.email-recipients.dialog.html',
            controller: 'directives.DocTreeEmailRecipientsDialogController',
            animation: true,
            size: 'lg',
            backdrop: 'static',
            resolve: {
                config: function () {
                    return $scope.config;
                },
                recipients: function () {
                    return $scope.recipients;
                },
                ccRecipients: function () {
                    return $scope.ccRecipients;
                },
                bccRecipients: function () {
                    return $scope.bccRecipients;
                },
                recipientType: function() {
                    return recipientType
                }
            }
        });

        modalInstance.result.then(function(recipients) {
            if (recipientType == 'cc') {
                $scope.ccRecipients = recipients;
                $scope.ccRecipientsStr = buildRecipientsStr(recipients);
            } else if (recipientType == 'bcc') {
                $scope.bccRecipients = recipients;
                $scope.bccRecipientsStr = buildRecipientsStr(recipients);
            } else {
                $scope.recipients = recipients;
                $scope.recipientsStr = buildRecipientsStr(recipients);
            }
        });
    };


    $scope.onSelectFile = function(fileId) {
        var idx = $scope.emailDataModel.selectedFilesToEmail.indexOf(fileId);

        if (idx > -1) {
            $scope.emailDataModel.selectedFilesToEmail.splice(idx, 1);
        } else {
            $scope.emailDataModel.selectedFilesToEmail.push(fileId);
        }
    };

    $scope.loadContent = function () {
        var params = {};
        params.objectType = $scope.DocTree._objType;
        params.objectId = $scope.DocTree._objId;
        params.templateName = $scope.template;
        params.fileIds = $scope.emailDataModel.selectedFilesToEmail;
        
        var getTemplateContentPromise = correspondenceService.retrieveConvertedTemplateContent(params);

        getTemplateContentPromise.then(function (response) {
            if (response.data.templateEmailSubject) {
                $scope.emailDataModel.subject = response.data.templateEmailSubject;
            } else {
                $scope.emailDataModel.subject = $scope.subject;
            }
            if($scope.template === "documentLinked.html") {
                $scope.emailSendConfiguration.allowDocuments = false;
                $scope.emailDataModel.selectedFilesToEmail = [];
            } else {
                $scope.emailSendConfiguration.allowDocuments = true;
                $scope.emailDataModel.selectedFilesToEmail = DocTreeExtEmail._extractFileIds($scope.nodes);
            }
            if ($scope.template === "plainEmail.html") {
                $('#plain').summernote('code', "");
            } else {
                $scope.templateContent = response.data.templateContent.replace("${baseURL}", window.location.href.split('/home.html#!')[0]);
                $('#content').summernote('code', $scope.templateContent);
            }
        });
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss();
    };

    $scope.onClickOk = function() {
        $scope.emailDataModel.recipients = $scope.recipientsStr.split('; ');
        $scope.emailDataModel.ccRecipients = $scope.ccRecipientsStr.split('; ');
        $scope.emailDataModel.bccRecipients = $scope.bccRecipientsStr.split('; ');
        $scope.emailDataModel.template = _.contains($scope.template, '.html') ? $scope.template.replace('.html', '') : $scope.template;
        $modalInstance.close($scope.emailDataModel);
    };

    $scope.disableOk = function() {
        return Util.isEmpty($scope.recipientsStr) || Util.isEmpty($scope.template);
    };

} ]);

angular.module('directives').controller('directives.DocTreeEmailRecipientsDialogController',
        [ '$scope', '$modalInstance', 'DocTreeExt.Email', 'Object.LookupService', 'config', 'recipients', 'ccRecipients', 'bccRecipients', 'recipientType', 'UtilService', 'MessageService', '$translate', function($scope, $modalInstance, DocTreeExtEmail, ObjectLookupService, config, recipients, ccRecipients, bccRecipients, recipientType, Util, MessageService, $translate) {

            $scope.config = config;
            if(recipientType == 'cc') {
                $scope.recipients = angular.copy(ccRecipients);
            }
            else if(recipientType == 'bcc') {
                $scope.recipients = angular.copy(bccRecipients);
            }
            else {
                $scope.recipients = angular.copy(recipients);
            }
            $scope.onSelectRecipient = function (selectedItems, lastSelectedItems, isSelected) {
                if (Util.isEmpty(lastSelectedItems[0].email_lcs)) {
                    MessageService.info($translate.instant('common.directive.docTree.email.noEmailAddress') + lastSelectedItems[0].object_type_s.toLowerCase());
                } else {
                    var selectedRecipientEmail = lastSelectedItems[0].email_lcs;
                    var isRecipientSelected = _.find($scope.recipients, function (recipient) {
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
