'use strict';

angular.module('time-tracking').controller(
    'TimeTracking.NewTimesheetController',
    [
        '$scope',
        '$q',
        '$stateParams',
        '$translate',
        '$modalInstance',
        'TimeTracking.InfoService',
        'Object.LookupService',
        'MessageService',
        '$timeout',
        'UtilService',
        'Util.DateService',
        '$modal',
        'ConfigService',
        'ObjectService',
        'modalParams',
        'Person.InfoService',
        'Object.ModelService',
        'Object.ParticipantService',
        'Profile.UserInfoService',
        'Helper.UiGridService',
        'Admin.TimesheetConfigurationService',
        function ($scope, $q, $stateParams, $translate, $modalInstance, TimeTrackingInfoService, ObjectLookupService, MessageService, $timeout, Util, UtilDateService, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService,
                  UserInfoService, HelperUiGridService, TimesheetConfigurationService) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.isEdit = $scope.modalParams.isEdit;
            var participantTypeApprover = 'approver';
            var participantTypeOwningGroup = "owning group";

            $scope.timesheetProperties = {
                "time.plugin.useApprovalWorkflow": "true"
            };

            ConfigService.getModuleConfig("time-tracking").then(function (moduleConfig) {
                $scope.config = moduleConfig;

                $scope.timesConfig = _.find(moduleConfig.components, {
                    id: "times"
                });

                UserInfoService.getUserInfo().then(function (infoData) {
                    if (!$scope.isEdit) {
                        //new timesheet with predefined values
                        $scope.isTypeSelected = false;
                        $scope.isApproverAdded = false;
                        $scope.timesheet = {
                            className: $scope.config.className,
                            status: 'DRAFT',
                            times: [],
                            participants: []
                        };
                        $scope.timesheet.user = infoData;
                        $scope.approverName = "";
                        $scope.groupName = "";

                        $scope.selectedDate = new Date();
                        $scope.selectedWeek = updateChoosedWeekText($scope.selectedDate);

                        $scope.timesForms = [{
                            totalWeekHours: 0,
                            totalCost: 0,
                            dayHours: [0, 0, 0, 0, 0, 0, 0]
                        }];

                        if(!Util.isEmpty($scope.modalParams.timeType) && !Util.isEmpty($scope.modalParams.timeNumber) && !Util.isEmpty($scope.modalParams.timeId)) {
                            $scope.timesForms[0].objectId = $scope.modalParams.timeId;
                            $scope.timesForms[0].type = $scope.modalParams.timeType;
                            $scope.timesForms[0].code = $scope.modalParams.timeNumber;
                            $scope.isTypeSelected = true;
                        }
                    }
                });



                $scope.newTimeObjectPicker = _.find(moduleConfig.components, {
                    id: "newTimeObjectPicker"
                });

                $scope.otherChargeCodePicker = _.find(moduleConfig.components, {
                    id: "otherChargeCodePicker"
                });

                $scope.userSearchConfig = _.find(moduleConfig.components, {
                    id: "userSearch"
                });

                $scope.participantsConfig = _.find(moduleConfig.components, {
                    id: "person"
                });

                return moduleConfig;
            });

            var timesheetConfig = TimesheetConfigurationService.getConfig();

            TimesheetConfigurationService.getProperties().then(function(response) {
                if (!Util.isEmpty(response.data)) {
                    $scope.timesheetProperties = response.data;
                }
            });

            ObjectLookupService.getTimesheetTypes().then(function (timesheetTypes) {
                $scope.timesheetTypes = timesheetTypes;
            });

            ObjectLookupService.getTimesheetChargeRoles().then(function (timesheetChargeRoles) {
                $scope.timesheetChargeRoles = timesheetChargeRoles;
            });
            ObjectLookupService.getTimesheetStatuses().then(function (timesheetStatuses) {
                $scope.timesheetStatuses = timesheetStatuses;

                    for(var i = $scope.timesheetStatuses.length - 1; i >= 0; i--) {
                        if($scope.timesheetStatuses[i].key !== "DRAFT" && $scope.timesheetStatuses[i].key !== "FINAL") {
                            $scope.timesheetStatuses.splice(i, 1);
                        }
                    }
            });

            // ----------------------------- total ----------------------------------------------------------

            $scope.updateTotalWeekHours = function (timesForm) {
                timesForm.totalWeekHours = 0;
                _.forEach(timesForm.dayHours, function (hour) {
                    if (!Util.isEmpty(hour) && hour != 0) {
                        timesForm.totalWeekHours += parseFloat(hour);
                        if (Util.isEmpty(hour))
                            timesForm.totalWeekHours = 0;
                    }
                });

                $scope.updateTotalCost(timesForm);
            };

            $scope.updateTotalCost = function (timesForm) {
                $q.when(timesheetConfig).then(function (response){
                    $scope.timesheetConfiguration = response.data;

                    if (!Util.isEmpty(timesForm.chargeRole)) {
                        timesForm.totalCost = fillTotalCost(timesForm.totalWeekHours, timesForm.chargeRole);
                    }
                });
            };

            // ------------------------------------------ edit ----------------------------------------------
            if ($scope.isEdit) {
                $scope.isTypeSelected = true;
                $scope.objectInfo = angular.copy($scope.modalParams.timesheet);
                var tmpTimesheet = angular.copy($scope.modalParams.timesheet);
                updateIsApproverAdded($scope.objectInfo.participants);

                if (tmpTimesheet.participants != undefined) {
                    if (!Util.isArrayEmpty(tmpTimesheet.participants)) {
                        _.forEach(tmpTimesheet.participants, function (participant) {
                            if(participant.participantType == participantTypeApprover){
                                UserInfoService.getUserInfoById(participant.participantLdapId).then(function(userInfo) {
                                    $scope.approverName = userInfo.fullName;
                                });
                            }
                            if(participant.participantType == participantTypeOwningGroup){
                                $scope.groupName = participant.participantLdapId;
                            }
                        });
                    }
                }

                if (tmpTimesheet.times != undefined) {
                    if (!Util.isArrayEmpty(tmpTimesheet.times)) {
                        $scope.timesForms = [];

                        var arrayOfArrays = [];
                        //put every AcmTime with same code and chargeRole in same array
                        var times = tmpTimesheet.times.slice(0);
                        for (var i = 0; i < times.length;) {
                            var pom = [];
                            for (var j = i + 1; j < times.length; j++) {
                                if (times[i].code == times[j].code && times[i].chargeRole == times[j].chargeRole && times[i].type == times[j].type) {
                                    pom.push(times[j]);
                                }
                            }
                            pom.push(times[i]);

                            _.forEach(pom, function (groupedTime) {
                                _.forEach(times, function (time, i) {
                                    if (Util.compare(groupedTime, time)) {
                                        times.splice(i, 1);
                                    }
                                });
                            });
                            pom.sort(function (a, b) {
                                return a.date > b.date;
                            });
                            arrayOfArrays.push(pom);
                        }

                        _.forEach(arrayOfArrays, function (timeArray) {
                            var dayHours = [];
                            var startDate = moment(tmpTimesheet.startDate);
                            var endDate = moment(tmpTimesheet.endDate);
                            while (startDate <= endDate) {
                                var hasTimeForDate = false;
                                _.forEach(timeArray, function (time) {
                                    if (startDate.format("YYYY-MM-DD") == moment(time.date).format("YYYY-MM-DD")) {
                                        dayHours.push(time.value);
                                        hasTimeForDate = true;
                                    }
                                });
                                if (!hasTimeForDate) dayHours.push(0);
                                startDate = addOneDay(startDate);
                            }

                            var timesForm = {
                                type: timeArray[0].type,
                                code: timeArray[0].code,
                                chargeRole: timeArray[0].chargeRole,
                                dayHours: dayHours,
                            };
                            $scope.updateTotalWeekHours(timesForm);
                            $scope.timesForms.push(timesForm);
                        });

                    } else {
                        $scope.timesForms = [{}];
                    }
                }

                $scope.timesheet = {
                    id: $scope.objectInfo.id,
                    user: $scope.objectInfo.user,
                    status: $scope.objectInfo.status,
                    title: $scope.objectInfo.title,
                    startDate: $scope.objectInfo.startDate,
                    endDate: $scope.objectInfo.endDate,
                    details: $scope.objectInfo.details,
                    times: tmpTimesheet.times,
                    participants: tmpTimesheet.participants
                };

                $scope.selectedDate = new Date($scope.objectInfo.startDate);
                $scope.selectedWeek = updateChoosedWeekText($scope.selectedDate);
            }

            //------------------------------------- selectedWeek ----------------------------------------------
            $scope.isDateBeforeTodaysDate = false;

            $scope.prevWeek = function () {
                var utcDate = moment.utc(UtilDateService.dateToIso($scope.selectedDate)).format();
                $scope.selectedDate = moment(utcDate).subtract(7, 'day').toDate();
                $scope.selectedWeek = updateChoosedWeekText($scope.selectedDate);
                $scope.isDateBeforeTodaysDate = moment(moment($scope.selectedDate).format("DD MMMM YYYY"), "DD MMMM YYYY").toDate() < moment(moment(new Date()).format("DD MMMM YYYY"), "DD MMMM YYYY").toDate();
            };

            $scope.nextWeek = function () {
                var utcDate = moment.utc(UtilDateService.dateToIso($scope.selectedDate)).format();
                $scope.selectedDate = moment(utcDate).add(7, 'day').toDate();
                $scope.selectedWeek = updateChoosedWeekText($scope.selectedDate);
                $scope.isDateBeforeTodaysDate = moment(moment($scope.selectedDate).format("DD MMMM YYYY"), "DD MMMM YYYY").toDate() < moment(moment(new Date()).format("DD MMMM YYYY"), "DD MMMM YYYY").toDate();
            };

            function addOneDay(date) {
                return date.clone().add(1, 'day');
            }

            function setWeekDatesInCalendar(startOfWeek, endOfWeek) {
                $scope.fullWeekDates = [];
                $scope.formatedWeekDates = [];

                var day = startOfWeek.clone();

                while (day <= endOfWeek) {
                    $scope.fullWeekDates.push(day.toDate());
                    $scope.formatedWeekDates.push(day.format("ddd DD"));
                    day = addOneDay(day);
                }
            }

            function updateChoosedWeekText(selectedDate) {
                var correctedChoosedPeriod = UtilDateService.convertToCurrentTime(selectedDate);
                var utcDate = moment.utc(UtilDateService.dateToIso(correctedChoosedPeriod)).format();
                var startOfWeek = moment(utcDate).startOf('week');
                var endOfWeek = moment(utcDate).endOf('week');
                $scope.timesheet.startDate = startOfWeek.toDate();
                $scope.timesheet.endDate = endOfWeek.toDate();

                if (!Util.isEmpty($scope.timesheet)) {
                    $scope.timesheet.title = $scope.timesheet.user.fullName + " - " + " " + startOfWeek.format("M/D/YYYY") + "-" + endOfWeek.format("M/D/YYYY");
                }

                setWeekDatesInCalendar(startOfWeek, endOfWeek);

                return startOfWeek.format("MMMM") + " " + startOfWeek.format("DD") + " - " + endOfWeek.format("MMMM") + " " + endOfWeek.format("DD") + " " + startOfWeek.format("YYYY");
            }

            // -----------------------------------------------------------------------------------------------
            $scope.updateIsTypeSelected = function (time) {
                if (time.type == undefined) {
                    $scope.isTypeSelected = false;
                } else {
                    $scope.isTypeSelected = true;
                }
                time.code = "";
            };

            $scope.chooseChargeCode = function (time) {
                if (time.type == "OTHER") {
                    //opening modal for choosing LEAP, Training, Conference
                    pickOtherChargeCode(time);
                } else {
                    pickCaseOrComplaint(time);
                }

            };

            function pickCaseOrComplaint(time) {

                var params = {};
                params.header = $translate.instant("timeTracking.comp.newTimesheet.objectPicker.title");

                if (time.type == ObjectService.ObjectTypes.CASE_FILE) {
                    params.filter = 'fq="object_type_s": CASE_FILE';
                } else if (time.type == ObjectService.ObjectTypes.COMPLAINT) {
                    params.filter = 'fq="object_type_s": COMPLAINT';
                }

                params.config = $scope.newTimeObjectPicker;

                var modalInstance = $modal.open({
                    templateUrl: "modules/cost-tracking/views/components/cost-tracking-object-picker-search-modal.client.view.html",
                    controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = params.header;
                        $scope.filter = params.filter;
                        $scope.extraFilter = params.extraFilter;
                        $scope.config = params.config;
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (selected) {
                    if (!Util.isEmpty(selected)) {
                        time.code = selected.name;
                        time.objectId = selected.object_id_s;
                    }
                });

            };

            function pickOtherChargeCode(time) {

                var gridHelper = new HelperUiGridService.Grid({
                    scope: $scope
                });
                gridHelper.setColumnDefs($scope.otherChargeCodePicker);
                gridHelper.setBasicOptions($scope.otherChargeCodePicker);
                gridHelper.disableGridScrolling($scope.otherChargeCodePicker);

                var params = {};
                params.config = $scope.otherChargeCodePicker;

                var modalInstance = $modal.open({
                    templateUrl: "modules/time-tracking/views/components/time-tracking-other-charge-code-picker-modal.client.view.html",
                    controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                        $scope.gridOptions = {
                            enableRowSelection: true,
                            enableRowHeaderSelection: false,
                            multiSelect: false,
                            onRegisterApi: function (gridApi) {
                                $scope.myGridApi = gridApi;
                                $scope.myGridApi.selection.on.rowSelectionChanged($scope, function (row) {
                                    $scope.selectedItem = row.entity;
                                });
                            }
                        };

                        $scope.gridOptions.data = params.config.otherTypeChargeCodes;
                        $scope.gridOptions.data.totalItems = params.config.otherTypeChargeCodes.length;
                        $scope.onClickOk = function () {
                            $modalInstance.close($scope.selectedItem);
                        };
                        $scope.onClickCancel = function () {
                            $modalInstance.dismiss('cancel');
                        }
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (selected) {
                    if (!Util.isEmpty(selected)) {
                        time.code = selected.name;
                    }
                });
            };


            // -------------------------- time ----------------------------------------

            $scope.addTimeRow = function () {
                $timeout(function () {
                    if (!_.isEmpty($scope.timesForms[0])) {
                        $scope.timesForms.push({
                            totalWeekHours: 0,
                            totalCost: 0,
                            dayHours: [0, 0, 0, 0, 0, 0, 0]
                        });
                    }
                }, 0);
            };

            $scope.removeTimeRow = function (time) {
                $timeout(function () {
                    _.remove($scope.timesForms, function (object) {
                        return object === time;
                    });
                }, 0);
            };

            function fillTotalCost(hour, chargeRole) {
                var hourRate = 0;
                if ($scope.timesheetConfiguration !== undefined) {
                    for(var i=0; i<$scope.timesheetConfiguration.chargeRoleItems.length; i++){
                         if($scope.timesheetConfiguration.chargeRoleItems[i].chargeRole === chargeRole && $scope.timesheetConfiguration.chargeRoleItems[i].active === "ACTIVE"){
                             hourRate = $scope.timesheetConfiguration.chargeRoleItems[i].rate;
                             break;
                         }
                    }
                }
                return hour * hourRate;
            }

            function fillTimes(timesheet) {
                _.forEach($scope.timesForms, function (timesForm) {
                    _.forEach(timesForm.dayHours, function (hour, i) {
                        if (hour != 0 && !Util.isEmpty(hour)) {
                            var time = {
                                className: $scope.timesConfig.className,
                                date: $scope.fullWeekDates[i],
                                type: timesForm.type,
                                code: timesForm.code,
                                objectId: timesForm.objectId,
                                chargeRole: timesForm.chargeRole,
                                totalCost: fillTotalCost(hour, timesForm.chargeRole),
                                value: hour
                            };
                            if (!$scope.isEdit) {
                                timesheet.times.push(time);
                            }else {
                                var alreadyExists = false;
                                _.forEach(timesheet.times, function (timesheetTime) {
                                    if(moment(timesheetTime.date).format("YYYY-MM-DD") == moment(time.date).format("YYYY-MM-DD")
                                        && timesheetTime.type == time.type && timesheetTime.code == time.code
                                        && timesheetTime.chargeRole == time.chargeRole){
                                        if(timesheetTime.value != time.value){
                                            timesheetTime.value = time.value;
                                            timesheetTime.totalCost = time.totalCost;
                                        }
                                        alreadyExists = true;
                                    }
                                });
                                if(!alreadyExists) {
                                    if(Util.isEmpty(time.objectId)){
                                        time.objectId = time.code.split("_")[1];
                                    }
                                    timesheet.times.push(time);
                                }
                            }
                        }
                    });
                });
            }

            // ---------------------------            approver         --------------------------------------
            $scope.userOrGroupSearch = function() {
                var params = {};
                params.header = $translate.instant("timeTracking.comp.newTimesheet.userSearch.title");
                params.filter = "fq=\"object_type_s\":(GROUP OR USER)&fq=\"status_lcs\":(ACTIVE OR VALID)";
                params.extraFilter = "&fq=\"name\": ";
                params.config = $scope.userSearchConfig;
                params.secondGrid = 'true';

                var modalInstance = $modal.open({
                    templateUrl: "directives/core-participants/participants-user-group-search.client.view.html",
                    controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = params.header;
                        $scope.filter = params.filter;
                        $scope.config = params.config;
                        $scope.secondGrid = params.secondGrid;
                        $scope.extraFilter = params.extraFilter;
                    } ],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function(selection) {
                    if (selection) {
                        var selectedObjectType = selection.masterSelectedItem.object_type_s;
                        if (selectedObjectType === 'USER') { // Selected user
                            var selectedUser = selection.masterSelectedItem;
                            var selectedGroup = selection.detailSelectedItems;

                            $scope.approverName = selectedUser.name;
                            addParticipantInTimesheet(participantTypeApprover, selectedUser.object_id_s);

                            if (selectedGroup) {
                                $scope.groupName = selectedGroup.name;
                                addParticipantInTimesheet(participantTypeOwningGroup, selectedGroup.object_id_s);
                            }
                            updateIsApproverAdded($scope.timesheet.participants);

                            return;
                        } else if (selectedObjectType === 'GROUP') { // Selected group
                            var selectedUser = selection.detailSelectedItems;
                            var selectedGroup = selection.masterSelectedItem;

                            $scope.groupName = selectedGroup.name;
                            addParticipantInTimesheet(participantTypeOwningGroup, selectedGroup.object_id_s);

                            if (selectedUser) {
                                $scope.approverName = selectedUser.name;
                                addParticipantInTimesheet(participantTypeApprover, selectedUser.object_id_s);
                            }
                            updateIsApproverAdded($scope.timesheet.participants);

                            return;
                        }
                    }

                }, function() {
                    // Cancel button was clicked.
                    return [];
                });

            };

            function addParticipantInTimesheet(participantType, participantLdapId){
                var newParticipant = {};
                newParticipant.className = $scope.participantsConfig.className;
                newParticipant.participantType = participantType;
                newParticipant.participantLdapId = participantLdapId;

                if (ObjectParticipantService.validateParticipants([newParticipant], true)) {
                    var participantExists = false;
                    _.forEach($scope.timesheet.participants, function (participant) {
                        if(participant.participantType == participantType){
                            participantExists = true;
                            participant.participantLdapId = newParticipant.participantLdapId;
                            participant.replaceChildrenParticipant = true;
                            return false;
                        }
                    });
                    if(!participantExists){
                        $scope.timesheet.participants.push(newParticipant);
                    }
                }
            }

            function updateIsApproverAdded(participants){
                var approver = _.find(participants, function (participant) {
                    return participant.participantType == participantTypeApprover;
                });
                $scope.isApproverAdded = !Util.isEmpty(approver);
            }

            //-----------------------------------------------------------------------------------------------

            $scope.save = function (submissionName) {
                $q.when(timesheetConfig).then(function (response){
                    $scope.timesheetConfiguration = response.data;

                    if (!$scope.isEdit) {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        fillTimes($scope.timesheet);
                        if($scope.timesheet.status === "FINAL") {
                            submissionName = "SaveFinal";
                        }
                        TimeTrackingInfoService.saveNewTimesheetInfo(clearNotFilledElements(_.cloneDeep($scope.timesheet)), submissionName).then(function (objectInfo) {
                            var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.TIMESHEET);
                            var timesheetUpdatedMessage = $translate.instant('{{objectType}} {{timesheetTitle}} was created.', {
                                objectType: objectTypeString,
                                timesheetTitle: objectInfo.title
                            });
                            MessageService.info(timesheetUpdatedMessage);
                            $modalInstance.close(objectInfo);
                            $scope.loading = false;
                            $scope.loadingIcon = "fa fa-floppy-o";
                        }, function (error) {
                            $scope.loading = false;
                            $scope.loadingIcon = "fa fa-floppy-o";
                            if (error.data && error.data.message) {
                                $scope.error = error.data.message;
                            } else {
                                MessageService.error(error);
                            }
                        });
                    } else {
                        // Updates the ArkCase database when the user changes a timesheet attribute
                        // from the form accessed by clicking 'Edit' and then 'update timesheet button
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        fillTimes($scope.objectInfo);
                        checkForChanges($scope.objectInfo);
                        if (TimeTrackingInfoService.validateTimesheet($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            if($scope.objectInfo.status === "FINAL") {
                                submissionName = "SaveFinal";
                            }
                            promiseSaveInfo = TimeTrackingInfoService.saveTimesheetInfo(objectInfo, submissionName);
                            promiseSaveInfo.then(function (timesheetInfo) {
                                $scope.$emit("report-object-updated", timesheetInfo);
                                var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.TIMESHEET);
                                var objectAction = $translate.instant('common.objectAction.updated');
                                var timesheetUpdatedMessage = $translate.instant('{{objectType}} {{timesheetTitle}} {{action}}.', {
                                    objectType: objectTypeString,
                                    timesheetTitle: timesheetInfo.title,
                                    action: objectAction
                                });
                                MessageService.info(timesheetUpdatedMessage);
                                $modalInstance.close(timesheetInfo);
                                $scope.loading = false;
                                $scope.loadingIcon = "fa fa-floppy-o";
                            }, function (error) {
                                $scope.$emit("report-object-update-failed", error);
                                $scope.loading = false;
                                $scope.loadingIcon = "fa fa-floppy-o";
                                if (error.data && error.data.message) {
                                    $scope.error = error.data.message;
                                } else {
                                    MessageService.error(error);
                                }
                            });
                        }
                        return promiseSaveInfo;
                    }
                });
            };

            function checkForChanges(objectInfo) {
                if (moment(objectInfo.startDate).format("YYYY-MM-DD") != moment($scope.timesheet.startDate).format("YYYY-MM-DD")) {
                    objectInfo.startDate = $scope.timesheet.startDate;
                }
                if (moment(objectInfo.endDate).format("YYYY-MM-DD") != moment($scope.timesheet.endDate).format("YYYY-MM-DD")) {
                    objectInfo.endDate = $scope.timesheet.endDate;
                }
                if (objectInfo.details != $scope.timesheet.details) {
                    objectInfo.details = $scope.timesheet.details;
                }
                if (objectInfo.status != $scope.timesheet.status) {
                    objectInfo.status = $scope.timesheet.status;
                }

                var addedApprover =  _.find($scope.timesheet.participants, function (participant) {
                    return participant.participantType == participantTypeApprover;
                });

                if(!Util.isEmpty(addedApprover)){
                    var hasApprover = false;
                    _.forEach(objectInfo.participants, function (participant, idx) {
                        if((participant.participantType == participantTypeApprover)){
                            hasApprover = true;
                            objectInfo.participants.splice(idx, 1, addedApprover);
                            return false;
                        }
                    });
                    if(!hasApprover){
                        objectInfo.participants.push(addedApprover);
                    }
                }

                var addedOwningGroup =  _.find($scope.timesheet.participants, function (participant) {
                    return participant.participantType == participantTypeOwningGroup;
                });

                if(!Util.isEmpty(addedOwningGroup)){
                    var hasOwningGroup = false;
                    _.forEach(objectInfo.participants, function (participant, idx) {
                        if((participant.participantType == participantTypeOwningGroup)){
                            hasOwningGroup = true;
                            objectInfo.participants.splice(idx, 1, addedOwningGroup);
                            return false;
                        }
                    });
                    if(!hasOwningGroup){
                        objectInfo.participants.push(addedOwningGroup);
                    }
                }

                return objectInfo;
            }

            function clearNotFilledElements(timesheet) {
                if (Util.isEmpty(timesheet.details)) {
                    timesheet.details = null;
                }

                return timesheet;
            }

            $scope.sendForApproval = function () {
                var submissionName = "Submit";
                $scope.save(submissionName);
            };

            $scope.cancelModal = function () {
                $modalInstance.dismiss();
            };

        }]);