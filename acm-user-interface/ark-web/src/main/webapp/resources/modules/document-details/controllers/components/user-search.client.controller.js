'use strict';

angular.module('document-details').controller('Document.UserSearchController', ['$scope', '$q', '$modalInstance', 'Object.LookupService', 'ConfigService', '$scopeParticipant',
    function ($scope, $q, $modalInstance, ObjectLookupService, ConfigService, $scopeParticipant) {

        $scope.participantType = {};
        var promiseTypes = ObjectLookupService.getParticipantTypes().then(
            function (participantTypes) {
                $scope.participantTypes = participantTypes;
                $scope.participantType = $scope.participantTypes[0].type;
                return participantTypes;
            }
        );

        $scope.$watchCollection('participantType', function (newValue, oldValue) {
            $scopeParticipant.participantType = $scope.participantType;
        });


        ConfigService.getModuleConfig("document-details").then(function (moduleConfig) {
            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});
            $scope.filter = $scope.userSearchConfig.userFacetFilter;
            $scope.modalInstance = $modalInstance;
        });

        $q.all([promiseTypes]).then(function (data) {
        });
    }
]);