'use strict';

angular.module('directives').controller('Directives.CoreCalendarEventDetailsModalController', ['$scope', '$modalInstance', 'Object.CalendarService', 'MessageService', 'Util.DateService', 'coreCalendarConfig',
	'$modal', 'Directives.CalendarUtilService', 'eventDetails', '$filter', '$rootScope',
	function($scope, $modalInstance, CalendarService, MessageService, DateService, coreCalendarConfig, $modal, CalendarUtilService, eventDetails, $filter, $rootScope) {

		$scope.eventDetails = eventDetails;
		$scope.priorityOptions = CalendarUtilService.PRIORITY_OPTIONS;
		$scope.reminderOptions = CalendarUtilService.REMINDER_OPTIONS;
		$scope.coreCalendarConfig = coreCalendarConfig;

		var processEventDetails = function(eventDetails) {
			var eventDetailsDataModel = {};

			$scope.eventReminder = _.find($scope.reminderOptions, function(option) {
				return option.value === eventDetails.remindIn;
			}).label;

			$scope.eventPriority = _.find($scope.priorityOptions, function(option) {
				return option.value === eventDetails.priority;
			}).label;

			if (eventDetails.recurrenceDetails.recurrenceType !== 'ONLY_ONCE') {
				$scope.recurrenceDescription = CalendarUtilService.buildEventRecurrenceString(eventDetails, $filter('date')(eventDetails.start, 'MM/dd/yyyy'), $filter('date')(eventDetails.recurrenceDetails.endBy, 'MM/dd/yyyy'));
			} else {
				$scope.recurrenceDescription = 'Only Once';
			}

			splitAttendeesByResponseStatus(eventDetails.attendees);

			return eventDetailsDataModel;
		};

		var splitAttendeesByResponseStatus = function(attendees) {
			$scope.attendeesAccepted = _.filter(attendees, function(attendee) {
				return attendee.status === 'ACCEPTED';
			});

			$scope.attendeesDeclined = _.filter(attendees, function(attendee) {
				return attendee.status === 'DECLINED';
			});

			$scope.attendeesMaybe = _.filter(attendees, function(attendee) {
				return attendee.status === 'TENTATIVE';
			});
		};

		$scope.editEvent = function() {
			var scope = $rootScope.$new();
			scope.existingEvent = $scope.eventDetails;

			var modalInstance = $modal.open({
				animation: true,
				templateUrl: 'directives/core-calendar/core-calendar-new-event-modal.client.view.html',
				controller: 'Directives.CoreCalendarNewEventModalController',
				size: 'lg',
				backdrop: 'static',
				scope: scope,
				resolve: {
					coreCalendarConfig: function() {
						return $scope.coreCalendarConfig;
					}
				}
			});

			modalInstance.result.then(function(data) {
				$modalInstance.close('EDIT_EVENT');
			}, function() {

			});
		};

		$scope.deleteEvent = function() {
			CalendarService.deleteEvent().then(function(res) {
				MessageService.succsessAction();
				$modalInstance.close('DELETE_EVENT');	
			}, function(err) {
				MessageService.errorAction();
			});
		};

		$scope.cancel = function() {
			$modalInstance.dismiss();
		};

		processEventDetails($scope.eventDetails);
	}
]);