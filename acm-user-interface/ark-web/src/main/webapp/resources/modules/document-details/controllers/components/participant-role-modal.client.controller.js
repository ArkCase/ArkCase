'use strict';

angular.module('document-details').controller('Document.ParticipantRoleController', ['$scope', '$q', '$modalInstance', 'Object.LookupService',
    function ($scope, $q, $modalInstance, ObjectLookupService) {

        var promiseTypes = ObjectLookupService.getParticipantTypes().then(
            function (participantTypes) {
                $scope.participantTypes = participantTypes;
                $scope.participantRole = $scope.participantTypes[0].type;
                return participantTypes;
            }
        );

        $scope.ok = function () {
            $modalInstance.close($scope.participantRole);
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        }
    }
]);