'use strict';

/**
 * @ngdoc service
 * @name services:Object.ObjectService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/object/object.client.service.js services/object/object.client.service.js}
 *
 * Basic object service
 */
angular.module('services').factory('ObjectService', [ '$state', '$window', '$log', 'UtilService', 'Object.LookupService', function($state, $window, $log, Util, ObjectLookupService) {
    return {
        ObjectTypes: {
            CASE_FILE: "CASE_FILE",
            COMPLAINT: "COMPLAINT",
            TASK: "TASK",
            ADHOC_TASK: "ADHOC",
            TIMESHEET: "TIMESHEET",
            COSTSHEET: "COSTSHEET",
            DOCUMENT: "DOCUMENT",
            FILE: "FILE",
            DOC_REPO: "DOC_REPO",
            MY_DOC_REPO: "MY_DOC_REPO",
            PERSON: "PERSON",
            ORGANIZATION: "ORGANIZATION",
            FOLDER: "FOLDER",
            TRANSCRIBE: "TRANSCRIBE",
            TRANSCRIBE_ITEM: "TRANSCRIBE_ITEM",
            ASSOCIATED_TAG: "ASSOCIATED_TAG",
            BUSINESS_PROCESS: "BUSINESS_PROCESS",
            NOTIFICATION: "NOTIFICATION",
            CONSULTATION: "CONSULTATION",
            CORRESPONDENCE_TEMPLATE: "correspondenceTemplate"
        },

        RegexTypes : {
            RULE_REGEX: new RegExp(/[`~!#$%^&*|+=?;:',.<>\{\}\[\]\\\/]/),
            LOOKUP_REGEX: new RegExp(/[`~!#$%^&*|+=?;:',<>\\\/]/)
        },

        LockTypes: {
            WRITE: "WRITE",
            READ: "READ",
            SHARED_WRITE: "SHARED_WRITE",
            DELETE: "DELETE"
        }

        ,
        ObjectTypesInEndpoints: {
            CASE_FILE: "casefile",
            COMPLAINT: "complaint",
            TASK: "task",
            DOC_REPO: "documentrepository",
            CONSULTATION: "consultation",
            COSTSHEET: "costsheet",
            TIMESHEET: "timesheet"
        }

        ,
        UploadFileStatus: {
            READY: "READY",
            IN_PROGRESS: "IN_PROGRESS",
            FAILED: "FAILED",
            FINISHED: "FINISHED"
        }

        ,
        gotoUrl: function(objType, objId) {
            $log.warn("WARNING: Object.ObjectService.gotoUrl() is phasing out. Please use Object.ObjectService.showObject() instead");
            this.showObject(objType, objId);
        }

        ,
        gotoState: function(objType, objId) {
            $log.warn("WARNING: Object.ObjectService.gotoState() is phasing out. Please use Object.ObjectService.showObject() instead");
            this.showObject(objType, objId);
        },

        transitionToState: function(objType, objId, state) {
            var params = {
                id: objId,
                type: objType
            };
            $state.transitionTo(state, params, {
                reload: true,
                notify: true
            });
        }

        /**
         * @ngdoc method
         * @name showObject
         * @methodOf services:Object.ObjectService
         *
         * @param {String} objTypeKey ArkCase Object type
         * @param {String} objId ArkCase Object ID
         * @param {Boolean} newTab Optional flag for opening the Object in a new tab
         *
         * @description
         * Go to a page to show an object. If the view page is angular page, use state configuration (Case, Complaint, etc.);
         * else if the view page is non Angular page, use URL in configuration (Document, File, etc.)
         */
        ,
        showObject: function (objTypeKey, objId, newTab) {
            ObjectLookupService.getObjectTypes().then(function(objectTypes) {
                var found = _.find(objectTypes, {
                    key: objTypeKey
                });
                var objType = Util.goodMapValue(found, "type");
                if (Util.goodMapValue(found, "state", false)) {
                    var params = {
                        id: objId,
                        type: objType
                    };
                    if (newTab === true) {
                        $window.open($state.href(found.state, params, {relative: true})
                            , '_blank');
                    } else {
                        $state.transitionTo(found.state, params, {
                            reload: true,
                            notify: true
                        });
                    }
                } else if (Util.goodMapValue(found, "url", false)) {
                    var url = found.url;
                    url = url.replace(":id", objId);
                    url = url.replace(":type", objType);
                    if (Util.goodMapValue(found, "target", false)) {
                        $window.open(url, found.target);
                    } else {
                        $window.location.href = url;
                    }

                } else {
                    $log.warn("No state or url specified in object type lookup");
                }
                return objectTypes;
            });
        }

        /**
         * @ngdoc method
         * @name openObject
         * @methodOf services:Object.ObjectService
         *
         * @param {String} parentType, Lookup parent Type of the file.
         * @param {String} fileName, Lookup name.
         * @param {Number} targetId, target id of the file.
         * @param {Number} parentId,  parent id of the file.
         *
         * @description
         * Go to a page state that show the specified ArkCase File viewer
         * from referenced files from Case, Complain or Task.
         */
        ,
        openObject: function(targetId, parentId, parentType, fileName) {
            var baseUrl = window.location.href.split('!')[0];
            var urlArgs = targetId + "/" + parentId + "/" + parentType + "/" + fileName + "/" + targetId;

            window.open(baseUrl + '!/viewer/' + urlArgs);
        }

    };
} ]);
