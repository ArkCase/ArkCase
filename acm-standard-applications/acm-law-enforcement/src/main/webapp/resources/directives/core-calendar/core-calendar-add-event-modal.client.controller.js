'use strict';

angular.module('directives').controller('Directives.CoreCalendarAddEventModalController', ['$scope', '$modalInstance', 'Object.CalendarService', 'MessageService', 'Util.DateService',
    function($scope, $modalInstance, CalendarService, MessageService, DateService) {

		/*Set initial Event data*/
        $scope.eventDataModel = {
            startDate: new Date(),
            endDate: new Date(),
            subject: '',
            information: '',
            allDayEvent: false
        };

        $scope.minStartDate = new Date();
        $scope.minEndDate = new Date();

        $scope.startDateChanged = function(date) {
            $scope.minEndDate = date;
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