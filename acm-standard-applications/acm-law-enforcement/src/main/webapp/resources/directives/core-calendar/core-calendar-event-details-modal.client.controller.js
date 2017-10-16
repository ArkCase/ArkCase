'use strict';

angular.module('directives').controller('Directives.CoreCalendarEventDetailsModalController', ['$scope', '$modalInstance', 'Object.CalendarService', 'MessageService', 'Util.DateService', 'coreCalendarConfig',
	'$modal', 'Directives.CalendarUtilService', 'eventDetails', '$filter', '$rootScope', 'params',
	function($scope, $modalInstance, CalendarService, MessageService, DateService, coreCalendarConfig, $modal, CalendarUtilService, eventDetails, $filter, $rootScope, params) {

		$scope.eventDetails = eventDetails;
		$scope.priorityOptions = CalendarUtilService.PRIORITY_OPTIONS;
		$scope.reminderOptions = CalendarUtilService.REMINDER_OPTIONS;
		$scope.coreCalendarConfig = coreCalendarConfig;
		$scope.objectId = params.objectId;
		$scope.objectType = params.objectType;

		var processEventDetails = function() {
			if (!$scope.eventDetails.recurrenceDetails) {
				$scope.eventDetails.recurrenceDetails = {
					recurrenceType: 'ONLY_ONCE'
				};
			}
			$scope.eventDetails.start = moment($scope.eventDetails.start).toDate();
			$scope.eventDetails.end = moment($scope.eventDetails.end).toDate();
			if ($scope.eventDetails.recurrenceDetails.endBy) {
				$scope.eventDetails.recurrenceDetails.endBy = moment(eventDetails.recurrenceDetails.endBy).toDate();
			}

			$scope.privateEvent = $scope.eventDetails.sensitivity !== 'PRIVATE';

			$scope.eventReminder = _.find($scope.reminderOptions, function(option) {
				return option.value === $scope.eventDetails.remindIn;
			}).label;

			$scope.eventPriority = _.find($scope.priorityOptions, function(option) {
				return option.value === $scope.eventDetails.priority;
			}).label;

			if ($scope.eventDetails.recurrenceDetails.recurrenceType !== 'ONLY_ONCE') {
				$scope.recurrenceDescription = CalendarUtilService.buildEventRecurrenceString($scope.eventDetails);
			} else {
				$scope.recurrenceDescription = 'Only Once';
			}

			splitAttendeesByResponseStatus($scope.eventDetails.attendees);
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
			if ($scope.eventDetails.recurrenceDetails.recurrenceType !== 'ONLY_ONCE') {
				var params = {
					eventDataModel: $scope.eventDetails,
					objectId: $scope.objectId,
					objectType: $scope.objectType
				};

				var modalInstance = $modal.open({
					animation: $scope.animationsEnabled,
					templateUrl: 'directives/core-calendar/core-calendar-update-event-modal.client.view.html',
					controller: 'Directives.CoreCalendarUpdateEventController',
					size: 'md',
					resolve: {
						coreCalendarConfig: function() {
							return $scope.coreCalendarConfig;
						},
						params: function() {
							return params;
						}
					}
				});

				modalInstance.result.then(function() {
					$modalInstance.close('EDIT_EVENT');
				}, function() {

				});
			} else {
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
						},
						params: function() {
							return {
								objectType: $scope.objectType,
								objectId: $scope.objectId,
								updateMaster: false
							};
						}
					}
				});

				modalInstance.result.then(function(data) {
					$modalInstance.close('EDIT_EVENT');
				}, function() {});
			}
		};

		$scope.deleteEvent = function() {
			var params = {
				eventDataModel: $scope.eventDetails,
				objectId: $scope.objectId,
				objectType: $scope.objectType
			};

			var modalInstance = $modal.open({
				animation: $scope.animationsEnabled,
				templateUrl: 'directives/core-calendar/core-calendar-delete-event-modal.client.view.html',
				controller: 'Directives.CoreCalendarDeleteEventController',
				size: 'md',
				resolve: {
					params: function() {
						return params;
					}
				}
			});

			modalInstance.result.then(function() {
				$modalInstance.close('DELETE_EVENT');
			}, function() {

			});
		};

		$scope.cancel = function() {
			$modalInstance.dismiss();
		};

		processEventDetails();
	}
]);