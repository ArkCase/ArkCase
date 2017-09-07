'use strict';

angular.module('admin').controller('Admin.SecurityCalendarCredentialsConfigurationController', ['$scope', '$modal', 'Admin.CalendarCredentialsConfigurationService', 'Admin.CalendarConfigurationService', 'MessageService', '$translate',
    function ($scope, $modal, CalendarCredentialsConfigurationService, CalendarConfigurationService, MessageService, $translate) {

        $scope.invalidOutlookFolderCreators = [];
        $scope.validOutlookFolderCreatorCredentials = {};
        $scope.validateInProgress = {};
        $scope.saveInProgress = {};

        var getInvalidOutlookCreatorObject = function() {
            return {
                id: "",
                systemEmailAddress: "",
                systemPassword: "",
                baseSystemEmailAddress: ""
            };
        };

        var findInvalidOutlookFolderCreators = function() {
            CalendarCredentialsConfigurationService.findInvalidOutlookFolderCreators()
                .then(
                    function successCallback(response) {
                        for(var i=0; i<response.data.length; i++) {
                            var invalidOutlookCreator = getInvalidOutlookCreatorObject();
                            invalidOutlookCreator = angular.fromJson(response.data[i]);
                            invalidOutlookCreator.baseSystemEmailAddress = invalidOutlookCreator.systemEmailAddress;
                            $scope.invalidOutlookFolderCreators.push(invalidOutlookCreator);
                        }
                    }
                );
        };

        findInvalidOutlookFolderCreators();

        var saveCreator = function(creator, creatorIndex) {
            delete creator.baseSystemEmailAddress;
            CalendarCredentialsConfigurationService.saveOutlookFolderCreator(creator)
                .then(
                    function successfulCallback() {
                        MessageService.info($translate.instant('admin.security.calendarCredentialsConfiguration.message.save.successful'));
                        $scope.saveInProgress[creatorIndex] = false;
                    },
                    function failCallback() {
                        MessageService.error($translate.instant('admin.security.calendarCredentialsConfiguration.message.save.fail'));
                        $scope.saveInProgress[creatorIndex] = false;
                    }
                );
        };

        $scope.validateOutlookFolderCreator = function(creator, creatorIndex) {
            $scope.validateInProgress[creatorIndex] = true;

            var emailCredentials = {
                email: creator.systemEmailAddress,
                password: creator.systemPassword
            };

            CalendarConfigurationService.validateCalendarConfigurationSystemEmail(emailCredentials)
                .then(
                    function successCallback (response) {
                        if (response.data && response.data == "true") {
                            $scope.validOutlookFolderCreatorCredentials[creatorIndex] = 'VALID';
                        }
                        else {
                            $scope.validOutlookFolderCreatorCredentials[creatorIndex] = 'NOT_VALID';
                        }
                        $scope.validateInProgress[creatorIndex] = false;
                    },
                    function failCallback () {
                        $scope.validOutlookFolderCreatorCredentials[creatorIndex] = 'ERROR';
                        $scope.validateInProgress[creatorIndex] = false;
                    });
        };

        $scope.saveOutlookFolderCreator = function(creator, creatorIndex) {
            $scope.saveInProgress[creatorIndex] = true;

            if(creator.baseSystemEmailAddress !== creator.systemEmailAddress) {

                var modalInstance = $modal.open({
                    animation: false,
                    templateUrl: 'modules/admin/views/components/security-calendar-credentials-confirm.dialog.html',
                    controller: function($scope, $modalInstance) {
                        $scope.cancel = function() {
                            $modalInstance.dismiss("cancel");
                        };
                        $scope.confirm = function() {
                            $modalInstance.close("confirm");
                        };
                    },
                    size: 'md'
                });

                modalInstance.result.then(
                    function modalClosed () {
                        saveCreator(creator, creatorIndex);
                    },
                    function modalDismissed () {
                        $scope.saveInProgress[creatorIndex] = false;
                    }
                );
            }
            else {
                saveCreator(creator, creatorIndex);
            }
        };

    }
]);