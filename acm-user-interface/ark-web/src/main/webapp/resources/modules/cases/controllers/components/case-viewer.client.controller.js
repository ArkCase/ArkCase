'use strict';

angular.module('cases').controller('Cases.ViewerController', ['$scope', '$stateParams', '$sce', '$log', '$q'
    , 'TicketService', 'ConfigService', 'LookupService', 'SnowboundService', 'Authentication', 'EcmService'
    , 'Object.ModelService', 'Case.InfoService',
    function ($scope, $stateParams, $sce, $log, $q
        , TicketService, ConfigService, LookupService, SnowboundService, Authentication, EcmService
        , ObjectModelService, CaseInfoService) {

        //$scope.$emit('req-component-config', 'viewer');
        //$scope.config = null;
        //$scope.$on('component-config', applyConfig);
        //function applyConfig(e, componentId, config) {
        //    if (componentId == 'viewer') {
        //        $scope.config = config;
        //    }
        //}
        ConfigService.getComponentConfig("cases", "viewer").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });


        $scope.acmTicket = '';
        $scope.userId = '';
        $scope.ecmFileProperties = {};
        $scope.snowboundUrl = '';
        $scope.ecmFile = {};
        $scope.ecmFileEvents = [];
        $scope.ecmFileNotes = [];
        $scope.ecmFileParticipants = [];
        $scope.userList = [];
        $scope.caseInfo = {};
        $scope.assignee = '';

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

        // Obtains authentication token for ArkCase
        var ticketInfo = TicketService.getArkCaseTicket();

        // Obtains the currently logged in user
        //var userInfo = Authentication.queryUserInfo({});
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

                // Obtains the case file metadata which contains the assignee information
                CaseInfoService.getCaseInfo($scope.ecmFile.container.containerObjectId)
                    .then(function (data) {
                        $scope.caseInfo = data;
                        $scope.assignee = ObjectModelService.getAssignee(data);
                    });

                // Opens the selected document in the snowbound viewer
                openSnowboundViewer();
            });

    }
]);