angular.module('common').controller('Common.SendEmailModalController', ['$scope', '$modal', '$modalInstance', '$translate', 'UtilService', 'ConfigService', 'params', 'Admin.CMTemplatesService', function ($scope, $modal, $modalInstance, $translate, Util, ConfigService, params, correspondenceService) {

    $scope.objectId = params.objectId;
    $scope.objectType = params.objectType;
    $scope.objectNumber = params.objectNumber;
    $scope.recipients = [];
    $scope.recipientsStr = "";
    $scope.ccRecipients = [];
    $scope.ccRecipientsStr = "";
    $scope.bccRecipients = [];
    $scope.bccRecipientsStr = "";
    $scope.emailDataModel = {};
    $scope.subject = params.emailSubject;

    var paramsPlainEmail = {};
    paramsPlainEmail.objectType = $scope.objectType;
    paramsPlainEmail.objectId = $scope.objectId;
    paramsPlainEmail.templateName = 'plainEmail.html';

    var getTemplateContentPromise = correspondenceService.retrieveConvertedTemplateContent(paramsPlainEmail);
    getTemplateContentPromise.then(function (response) {
        if (response.data.templateEmailSubject) {
            $scope.emailDataModel.subject = response.data.templateEmailSubject;
        } else {
            $scope.emailDataModel.subject = $scope.subject;
        }
    });

    $scope.summernoteOptions = {
        focus: true,
        height: 300
    };
    $scope.emailDataModel.footer = $translate.instant('common.directive.docTree.email.defaultFooter');

    ConfigService.getModuleConfig("common").then(function (moduleConfig) {
        $scope.config = moduleConfig.docTree.emailDialog;
    });

    var templatesPromise = correspondenceService.retrieveActiveVersionTemplatesList('emailTemplate');
    templatesPromise.then(function(templates) {
        $scope.emailTemplates = _.filter(templates.data, function(et) {
            return et.activated && (et.objectType == $scope.objectType || et.objectType == 'ALL');
        });
        $scope.emailTemplates = _.sortBy($scope.emailTemplates, "label");
        var found = _.find($scope.emailTemplates, {
            templateFilename: 'plainEmail.html'
        });
        $scope.template = found ? found.templateFilename : '';
    });

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

    $scope.loadContent = function () {
        var params = {};
        params.objectType = $scope.objectType;
        params.objectId = $scope.objectId;
        params.templateName = $scope.template;

        var getTemplateContentPromise = correspondenceService.retrieveConvertedTemplateContent(params);

        getTemplateContentPromise.then(function (response) {
            if (response.data.templateEmailSubject) {
                $scope.emailDataModel.subject = response.data.templateEmailSubject;
            } else {
                $scope.emailDataModel.subject = $scope.subject;
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
