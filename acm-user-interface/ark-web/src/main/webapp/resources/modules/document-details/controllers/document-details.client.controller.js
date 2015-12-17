'use strict';

angular.module('document-details').controller('DocumentDetailsController', ['$scope', '$stateParams', '$sce', '$log', '$q'
	, 'TicketService', 'ConfigService', 'LookupService', 'SnowboundService', 'Authentication', 'EcmService'
	, 'Object.ModelService', 'Case.InfoService',
	function ($scope, $stateParams, $sce, $log, $q
		, TicketService, ConfigService, LookupService, SnowboundService, Authentication, EcmService) {

		$scope.acmTicket = '';
		$scope.userId = '';
		$scope.ecmFileProperties = {};
		$scope.snowboundUrl = '';
		$scope.ecmFileEvents = [];
		$scope.ecmFileNotes = [];
		$scope.ecmFileParticipants = [];
		$scope.userList = [];
		$scope.caseInfo = {};

		/**
		 * Builds the snowbound url based on the parameters passed into the controller state and opens the
		 * specified document in an iframe which points to snowbound
		 */
		$scope.openSnowboundViewer = function () {
			var fileInfo = {
				id: $stateParams['id'],
				containerId: $stateParams['containerId'],
				containerType: $stateParams['containerType'],
				name: $stateParams['name'],
				selectedIds: $stateParams['selectedIds']
			};
			var viewerUrl = SnowboundService.buildSnowboundUrl($scope.ecmFileProperties, $scope.acmTicket, $scope.userId, fileInfo);
			$scope.documentViewerUrl = $sce.trustAsResourceUrl(viewerUrl);
		};

		// Obtains authentication token for ArkCase
		var ticketInfo = TicketService.getArkCaseTicket();

		// Obtains the currently logged in user
		var userInfo = Authentication.queryUserInfo();

		// Obtains a list of all users in ArkCase
		var totalUserInfo = LookupService.getUsers();

		// Retrieves the properties from the ecmFileService.properties file (including Snowbound configuration)
		var ecmFileConfig = LookupService.getConfig("ecmFileService");

		// Retrieves the metadata for the file which is being opened in the viewer
		var ecmFileInfo = EcmService.getFile({fileId: $stateParams['id']});
		var ecmFileEvents = EcmService.getFileEvents({fileId: $stateParams['id']});
		var ecmFileNotes = EcmService.getFileNotes({fileId: $stateParams['id']});
		var ecmFileParticipants = EcmService.getFileParticipants({fileId: $stateParams['id']});

		$q.all([ticketInfo, userInfo, totalUserInfo, ecmFileConfig,
			ecmFileInfo.$promise, ecmFileEvents.$promise, ecmFileNotes.$promise, ecmFileParticipants.$promise])
			.then(function(data) {
				$scope.acmTicket = data[0].data;
				$scope.userId = data[1].userId;
				$scope.userList = data[2];
				$scope.ecmFileProperties = data[3];
				$scope.ecmFile = data[4];
				$scope.ecmFileEvents = data[5];
				$scope.ecmFileNotes = data[6];
				$scope.ecmFileParticipants = data[7];

				$scope.$broadcast('document-data', $scope.ecmFile);
				// Opens the selected document in the snowbound viewer
				$scope.openSnowboundViewer();
			}
		);
	}
]);