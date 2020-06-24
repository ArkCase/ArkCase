'use strict';

angular.module('admin').controller(
        'Admin.TimeToCompleteController',
        [
                '$scope',
                'MessageService',
                'Admin.QueuesTimeToCompleteService',
                'Dialog.BootboxService',
                '$translate',
                'ConfigService',
                function($scope, MessageService, AdminQueuesTimeToCompleteService, DialogService, $translate, ConfigService) {
                    timeToCompleteType: null;
                    $scope.timeToComplete = {
                        request: {},
                        appeal: {}
                    };

                    $scope.ignoreNonDigits = function(event){
                        var numberRegExp = new RegExp("^[1-9][0-9]*$");
                        if(event.key === '.' || !numberRegExp.test(event.key)){
                            event.preventDefault();
                        }
                    };

                    var queuesConfigurationPromise = AdminQueuesTimeToCompleteService.getQueuesConfig();
                    queuesConfigurationPromise.then(function(response) {
                        $scope.timeToComplete = response.data;
                    });

                    ConfigService.getComponentConfig("admin", "timeToComplete").then(function(config) {
                        $scope.config = config;
                        $scope.timeToCompleteTypes = $scope.config.timeToCompleteTypes;

                        $scope.timeToCompleteType = $scope.timeToCompleteTypes[0].id;

                    });
                    $scope.requestCalculation = function() {
                        $scope.timeToComplete.request.totalTimeToComplete = $scope.timeToComplete.request.intake + $scope.timeToComplete.request.fulfill + $scope.timeToComplete.request.approve + $scope.timeToComplete.request.generalCounsel + $scope.timeToComplete.request.billing
                                + $scope.timeToComplete.request.release;
                    };

                    $scope.appealCalculation = function() {
                        $scope.timeToComplete.appeal.totalTimeToComplete = $scope.timeToComplete.appeal.appeal + $scope.timeToComplete.appeal.fulfill + $scope.timeToComplete.appeal.approve + $scope.timeToComplete.appeal.generalCounsel + $scope.timeToComplete.appeal.billing
                                + $scope.timeToComplete.appeal.release;
                    };

                    $scope.save = function() {
                        AdminQueuesTimeToCompleteService.saveQueueConfig($scope.timeToComplete).then(function(timeToComplete) {
                            $scope.saveIcon = false;
                            $scope.saveLoadingIcon = "fa fa-floppy-o";
                            MessageService.info($translate.instant("admin.queues.timeToComplete.message.success"));
                        }, function(error) {
                            $scope.saveIcon = false;
                            $scope.saveLoadingIcon = "fa fa-floppy-o";
                            MessageService.error($translate.instant("admin.queues.timeToComplete.message.error"));
                        });
                    }
                } ]);