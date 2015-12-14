'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', '$stateParams', 'UtilService', 'ConfigService'
    , 'Complaint.InfoService', 'ObjectService', 'Object.CorrespondenceService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService'
    , function ($scope, $stateParams, Util, ConfigService
        , ComplaintInfoService, ObjectService, ObjectCorrespondenceService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService) {

        var promiseConfig = ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            $scope.widgets = [];
            _.each(Util.goodMapValue($scope.config, "widgets", []), function (widget) {
                if ("component" == Util.goodValue(widget.type)) {
                    var item = {};
                    var found = _.find(moduleConfig.components, {id: widget.id});
                    //if (found && found.enabled) {
                    if (found) {
                        item.title = found.title;
                        item.id = widget.id;
                        $scope.widgets.push(item);
                    }
                }
            });

            return moduleConfig;
        });


        $scope.widgetData = {};
        ComplaintInfoService.getComplaintInfo($stateParams.id).then(function (complaintInfo) {
            $scope.complaintInfo = complaintInfo;

            $scope.widgetData["details"] = Util.goodMapValue($scope.complaintInfo, "details");

            var location = Util.goodMapValue($scope.complaintInfo, "location.streetAddress")
                    + ", " + Util.goodMapValue($scope.complaintInfo, "location.city")
                    + ", " + Util.goodMapValue($scope.complaintInfo, "location.state")
                    + ", " + Util.goodMapValue($scope.complaintInfo, "location.zip")
                ;
            $scope.widgetData["locations"] = location;

            var personAssociations = $scope.complaintInfo.personAssociations;
            $scope.widgetData["people"] = personAssociations.length;

            var participants = $scope.complaintInfo.participants;
            $scope.widgetData["participants"] = participants.length;

            var references = Util.goodArray($scope.complaintInfo.references);
            $scope.widgetData["references"] = references.length;


            return complaintInfo;
        });

        ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, 0, 10, "", "")
            .then(function (correspondenceData) {
                $scope.widgetData["correspondence"] = Util.goodValue(correspondenceData.totalChildren, 0);
            });

        $scope.widgetData["documents"] = "documents data";

        ObjectNoteService.queryNotes(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id)
            .then(function (notes) {
                $scope.widgetData["notes"] = Util.goodValue(notes.length, 0);
            });

        ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, 0, 10, "", "")
            .then(function (data) {
                $scope.widgetData["tasks"] = Util.goodValue(data.response.numFound, 0);
            });

        ObjectAuditService.queryAudit(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, 0, 10, "", "")
            .then(function (auditData) {
                $scope.widgetData["history"] = Util.goodValue(auditData.totalCount, 0);
            });

        ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id).then(
            function (costsheets) {
                $scope.widgetData["cost"] = Util.goodValue(costsheets.length, 0);
            });

        ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id).then(
            function (timesheets) {
                $scope.widgetData["time"] = Util.goodValue(timesheets.length, 0);
            });

        $scope.widgetData["calendar"] = "calendar data";
    }
]);