'use strict';

angular.module('complaints').controller('Complaints.ViewerController', ['$scope', '$stateParams', '$sce', '$log', '$q'
    , 'TicketService', 'LookupService', 'SnowboundService', 'Authentication', 'EcmService', 'ObjectsModelsService', 'Complaint.InfoService'
    , function ($scope, $stateParams, $sce, $log, $q
        , TicketService, LookupService, SnowboundService, Authentication, EcmService, ObjectsModelsService, ComplaintInfoService) {

        $scope.acmTicket = '';
        $scope.userId = '';
        $scope.ecmFileProperties = {};
        $scope.snowboundUrl = '';
        $scope.ecmFile = {};
        $scope.ecmFileEvents = [];
        $scope.ecmFileNotes = [];
        $scope.ecmFileParticipants = [];
        $scope.userList = [];
        $scope.complaintInfo = {};
        $scope.assignee = '';

        $scope.config = null;
        $scope.$emit('req-component-config', 'viewer');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'viewer') {
                $scope.config = config;
            }
        });


        // Methods
        $scope.openSnowboundViewer = openSnowboundViewer;

        /**
         * This method generates the url to open the snowbound viewer
         * with the specified document loaded.
         */
        function openSnowboundViewer() {
            var fileInfo = {
                id: $stateParams['id'],
                containerId: $stateParams['containerId'],
                containerType: $stateParams['containerType'],
                name: $stateParams['name'],
                selectedIds: $stateParams['selectedIds']
            };
            var viewerUrl = SnowboundService.buildSnowboundUrl($scope.ecmFileProperties, $scope.acmTicket, $scope.userId, fileInfo);
            $scope.snowboundUrl = $sce.trustAsResourceUrl(viewerUrl);
        }

        // Obtains authentication token for ArkComplaint
        var ticketInfo = TicketService.getArkComplaintTicket();

        // Obtains the currently logged in user
        //var userInfo = Authentication.queryUserInfo({});
        var userInfo = Authentication.queryUserInfo();

        // Obtains a list of all users in ArkComplaint
        var totalUserInfo = LookupService.getUsers({});

        // Retrieves the properties from the ecmFileService.properties file (including Snowbound configuration)
        var ecmFileConfig = LookupService.getConfig({name: 'ecmFileService'});

        // Retrieves the metadata for the file which is being opened in the viewer
        var ecmFileInfo = EcmService.getFile({fileId: $stateParams['id']});
        var ecmFileEvents = EcmService.getFileEvents({fileId: $stateParams['id']});
        var ecmFileNotes = EcmService.getFileNotes({fileId: $stateParams['id']});
        var ecmFileParticipants = EcmService.getFileParticipants({fileId: $stateParams['id']});

        $q.all([ticketInfo, userInfo, totalUserInfo.$promise, ecmFileConfig.$promise,
            ecmFileInfo.$promise, ecmFileEvents.$promise, ecmFileNotes.$promise, ecmFileParticipants.$promise])
            .then(function (data) {
                $scope.acmTicket = data[0].data;
                $scope.userId = data[1].userId;

                var userListJSONStrings = data[2];
                $scope.userList = [];
                _.forEach(userListJSONStrings, function (value) {
                    $scope.userList.push(JSON.parse(value));
                });

                $scope.ecmFileProperties = data[3];
                $scope.ecmFile = data[4];
                $scope.ecmFileEvents = data[5];
                $scope.ecmFileNotes = data[6];
                $scope.ecmFileParticipants = data[7];

                // Obtains the complaint file metadata which contains the assignee information
                ComplaintInfoService.getComplaintInfo($scope.ecmFile.container.containerObjectId)
                    .then(function (data) {
                        $scope.complaintInfo = data;
                        $scope.assignee = ObjectsModelsService.getAssignee(data);
                    });

                // Opens the selected document in the snowbound viewer
                openSnowboundViewer();
            });
    }
]);