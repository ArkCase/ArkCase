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
                }else {
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
                        var params = {
                            config: Util.goodMapValue(DocTree.treeConfig, "emailDialog", {}),
                            nodes: nodes,
                            emailSendConfiguration: DocTree.treeConfig.emailSendConfiguration,
                            DocTree: DocTree,
                            emailOfOriginator: emailOfOriginator
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
                            emailData.objectId = DocTree._objId;
                            emailData.objectType = DocTree._objType;
                            emailData.objectNumber = DocTree.objectInfo.acmObjectNumber;
                            emailData.modelReferenceName = res.template;
                            emailData.attachmentIds = res.selectedFilesToEmail;

                            if(emailData.modelReferenceName != 'plainEmail') {
                                EcmEmailService.sendManualEmail(emailData);
                            } else {
                                EcmEmailService.sendPlainEmail(emailData, ObjectService.ObjectTypes.CASE_FILE);
                            }
                        });
                    });
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
            return et.activated && (et.objectType == $scope.DocTree._objType || et.objectType == 'ALL' || et.objectType == ObjectService.ObjectTypes.FILE);
        });
        var found = _.find($scope.emailTemplates, {
            templateFilename: 'plainEmail.html'
        });
        $scope.template = found ? found.templateFilename : '';
    });

    $scope.recipients = [];
    $scope.recipientsStr = "";

    if (!Util.isEmpty(params.emailOfOriginator)) {
        $scope.recipients.push(params.emailOfOriginator);
        $scope.recipientsStr = params.emailOfOriginator;
    }

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
            backdrop: 'static',
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
        $scope.emailDataModel.recipients = $scope.recipientsStr.split('; ');
        $scope.emailDataModel.template = _.contains($scope.template, '.html') ? $scope.template.replace('.html', '') : $scope.template;
        $modalInstance.close($scope.emailDataModel);
    };

    $scope.disableOk = function() {
        return Util.isEmpty($scope.recipientsStr) || Util.isEmpty($scope.template);
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