'use strict';

angular.module('consultations').controller('Consultations.ParticipantsController', [ '$scope', '$translate', 'Consultation.InfoService', 'ObjectService', function($scope, $translate, ConsultationInfoService, ObjectService) {

    $scope.participantsInit = {
        moduleId: 'consultations',
        componentId: 'participants',
        showReplaceChildrenParticipants: true,
        retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
        validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
        saveObjectInfo: ConsultationInfoService.saveConsultationInfo,
        objectType: ObjectService.ObjectTypes.CONSULTATION,
        participantsTitle: $translate.instant("consultations.comp.participants.title")
    }
} ]);
