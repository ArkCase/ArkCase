'use strict';

angular.module('directives').controller('Directives.CoreCalendarNewEventModalController', ['$scope', '$modalInstance', 'Object.CalendarService', 'MessageService', 'Util.DateService', 'coreCalendarConfig', '$modal',
    function($scope, $modalInstance, CalendarService, MessageService, DateService, coreCalendarConfig, $modal) {

        $scope.formStep = 1;
        $scope.formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepOneTitle';

        $scope.priorityOptions = coreCalendarConfig.addNewEventDialog.priorityOptions;
        $scope.repeatsOptions = coreCalendarConfig.addNewEventDialog.repeatsOptions;
        $scope.daysOfTheWeekOptions = coreCalendarConfig.addNewEventDialog.daysOfTheWeekOptions;
        $scope.reminderOptions = coreCalendarConfig.addNewEventDialog.reminderOptions;
        $scope.monthsOptions = coreCalendarConfig.addNewEventDialog.monthsOptions;

        $scope.summernoteOptions = {
            focus: true,
            dialogsInBody:true,
            height: 300
        };

        /*Set initial Event data*/
        $scope.eventDataModel = {
            startDate: new Date(),
            endDate: new Date(),
            recurringDaysOfTheWeek: {}
        };

        /*set the current day as default recurring day of the week*/
        $scope.eventDataModel.recurringDaysOfTheWeek[$scope.daysOfTheWeekOptions[new Date().getDay()].value] = true;

        $scope.minStartDate = new Date();
        $scope.minEndDate = new Date();

        /*Set minimum End Date*/
        $scope.startDateChanged = function(date) {
            $scope.minEndDate = date;
        };

        var changeFormTitle = function(formStep) {
            var formTitle = '';

            switch (formStep) {
                case 1:
                    formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepOneTitle';
                    break;
                case 2:
                    formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepTwoTitle';
                    break;
                case 3:
                    formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepThreeTitle';
                    break;
            }

            return formTitle;
        };

        $scope.chooseAttendees = function() {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'directives/core-calendar/core-calendar-choose-event-attendees-modal.client.view.html',
                controller: 'Directives.CoreCalendarChooseEventAttendeesController',
                size: 'lg',
                resolve: {
                    $config: function () {
                        return coreCalendarConfig.chooseEventAttendeesDialog.dialogUserPicker;
                    }
                }
            });

            modalInstance.result.then(function (attendees) {
                $scope.eventDataModel.attendees = attendees.object_id_s;
                $scope.attendeesViewModel = attendees.name;

            }, function () {

            });


        };

        $scope.previousFormStep = function() {
            $scope.formStep -= 1;
            $scope.formTitle = changeFormTitle($scope.formStep);
        };

        $scope.nextFormStep = function() {
            $scope.formStep += 1;
            $scope.formTitle = changeFormTitle($scope.formStep);
        };

        /*Perform adding of the event to the calendar*/
        $scope.addEvent = function() {
            $scope.eventDataModel.startDate = DateService.dateToIso($scope.eventDataModel.startDate);
            $scope.eventDataModel.endDate = DateService.dateToIso($scope.eventDataModel.endDate);
            CalendarService.addEvent($scope.eventDataModel).then(function(res) {
                //Handle success when backend service is completed
                MessageService.succsessAction();
                $modalInstance.close('eventAdded');
            }, function(err) {
                //TO DO
                MessageService.errorAction();
            });
        };

        /*Cancel the modal dialog*/
        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);