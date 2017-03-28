'use strict';

angular.module('directives').controller('Directives.CoreCalendarNewEventModalController', ['$scope', '$modalInstance', 'Object.CalendarService', 'MessageService', 'Util.DateService',
    function($scope, $modalInstance, CalendarService, MessageService, DateService) {

        $scope.formStep = 1;
        $scope.formTitle = 'common.directive.coreCalendar.newEventModal.stepOneTitle';

        $scope.reminderOptions = [];
        $scope.priorityOptions = [
            {
                value: 'highImportance',
                label: 'common.directive.coreCalendar.newEventModal.form.priorityOptions.highImportance'
            },
            {
                value: 'lowImportance',
                label: 'common.directive.coreCalendar.newEventModal.form.priorityOptions.lowImportance'
            }
        ];
        $scope.repeatsOptions = [
            {
                value: 'daily',
                label: 'common.directive.coreCalendar.newEventModal.form.repeatsOptions.daily'
            },
            {
                value: 'weekly',
                label: 'common.directive.coreCalendar.newEventModal.form.repeatsOptions.weekly'
            },
            {
                value: 'monthly',
                label: 'common.directive.coreCalendar.newEventModal.form.repeatsOptions.monthly'
            },
            {
                value: 'yearly',
                label: 'common.directive.coreCalendar.newEventModal.form.repeatsOptions.yearly'
            }
        ];

        $scope.daysOfTheWeekOptions = [
            {
                value: 'sunday',
                label: 'common.directive.coreCalendar.newEventModal.form.sunday.checkbox'
            },
            {
                value: 'monday',
                label: 'common.directive.coreCalendar.newEventModal.form.monday.checkbox'
            },
            {
                value: 'tuesday',
                label: 'common.directive.coreCalendar.newEventModal.form.tuesday.checkbox'
            },
            {
                value: 'wednesday',
                label: 'common.directive.coreCalendar.newEventModal.form.wednesday.checkbox'
            },
            {
                value: 'thursday',
                label: 'common.directive.coreCalendar.newEventModal.form.thursday.checkbox'
            },
            {
                value: 'friday',
                label: 'common.directive.coreCalendar.newEventModal.form.friday.checkbox'
            },
            {
                value: 'saturday',
                label: 'common.directive.coreCalendar.newEventModal.form.saturday.checkbox'
            }
        ];

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
                    formTitle = 'common.directive.coreCalendar.newEventModal.stepOneTitle';
                    break;
                case 2:
                    formTitle = 'common.directive.coreCalendar.newEventModal.stepTwoTitle';
                    break;
                case 3:
                    formTitle = 'common.directive.coreCalendar.newEventModal.stepThreeTitle';
                    break;
            }

            return formTitle;
        };

        $scope.previousFormStep = function() {
            $scope.formStep -= 1;
            $scope.formTitle = changeFormTitle($scope.formStep);
        };

        $scope.nextFormStep = function() {
            console.log($scope.eventDataModel)
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