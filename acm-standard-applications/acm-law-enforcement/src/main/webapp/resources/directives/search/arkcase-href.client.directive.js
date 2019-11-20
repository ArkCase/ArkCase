/**
 * Created by sasko.tanaskoski
 */

'use strict';

/**
 * @ngdoc directive
 * @name global.directive:arkcaseHref
 * @restrict A
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/search/arkcase-href.client.directive.js directives/search/arkcase-href.client.directive.js}
 *
 * The arkcaseHref directive builds href for arkcase object.
 *
 * @param {Object} object-data Arkcase object data
 * @param {Boolean} is-parent If true builds href for parent arkcase object. Default value is false
 * @param {Boolean} is-viewer-link If true builds href to the external or integrated viewer, for example snowbound. Default value is false
 * @param {String} url If url exists in object-data, it can be used as such and will not be built. For example notification object already has url as attribute.
 *
 * @example
 <example>
 <file name="index.html">
 <a arkcase-href object-data='row.entity' is-parent='false' is-viewer-link='true' url='row.entity.notification_link_s'>{{row.entity.name}}</a>
 </file>
 </example>
 */
angular.module('directives').directive('arkcaseHref', [ 'UtilService', 'ObjectService', 'Object.LookupService', function(Util, ObjectService, ObjectLookupService) {
    return {
        restrict: 'A',
        scope: {
            objectData: '=',
            isParent: '=',
            isViewerLink: '=?',
            url: '='
        },
        link: function(scope, element, attrs) {
            scope.$watch('objectData', function(newValue, oldValue) {
                buildUrl(newValue);
            });

            function buildUrl(objectData) {
                if (scope.url == undefined) {
                    var parentReference = Util.goodMapValue(objectData, "parent_ref_s", "-");
                    var objectType = scope.isParent ? parentReference.substring(parentReference.indexOf('-') + 1) : Util.goodMapValue(objectData, "object_type_s");
                    var objectId = scope.isParent ? parentReference.substring(0, parentReference.indexOf('-')) : Util.goodMapValue(objectData, "object_id_s");
                    if (objectData.object_type_s == ObjectService.ObjectTypes.TRANSCRIBE || objectData.object_type_s == ObjectService.ObjectTypes.TRANSCRIBE_ITEM) {
                        objectId = Util.goodMapValue(objectData, "parent_file_id_s");
                        objectType = objectData.object_type_s;
                    }
                    var objectUrlKey = 'url';
                    var pathVariables = {
                        ":id": objectId
                    };

                    if (objectType == ObjectService.ObjectTypes.TASK) {
                        objectType = (Util.goodMapValue(objectData, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                    }
                    if (objectType == ObjectService.ObjectTypes.FILE) {
                        if (scope.isViewerLink) {
                            var containerType = parentReference.substring(parentReference.indexOf('-') + 1);
                            var containerId = parentReference.substring(0, parentReference.indexOf('-'));
                            var name = Util.goodMapValue(objectData, "title_parseable");
                            objectUrlKey = "viewerUrl";
                            pathVariables[":containerId"] = containerId;
                            pathVariables[":containerType"] = containerType;
                            pathVariables[":name"] = encodeURIComponent(name);
                            pathVariables[":selectedIds"] = objectId;
                        }
                    }

                    if (objectData.object_type_s == ObjectService.ObjectTypes.TRANSCRIBE || objectData.object_type_s == ObjectService.ObjectTypes.TRANSCRIBE_ITEM) {
                        var containerType = Util.goodMapValue(objectData, "parent_root_type_s");
                        var containerId = Util.goodMapValue(objectData, "parent_root_id_s");
                        var name = Util.goodMapValue(objectData, "title_parseable");
                        if (objectType == ObjectService.ObjectTypes.TRANSCRIBE_ITEM) {
                            name = Util.goodMapValue(objectData, "parent_name_t");
                            pathVariables[":seconds"] = Util.goodMapValue(objectData, "start_time_s");
                        }
                        objectUrlKey = "viewerUrl";
                        pathVariables[":containerId"] = containerId;
                        pathVariables[":containerType"] = containerType;
                        pathVariables[":name"] = encodeURIComponent(name);
                        pathVariables[":selectedIds"] = objectId;

                    }

                    ObjectLookupService.getObjectTypes().then(function(objectTypes) {
                        var objectUrl = '';
                        var foundObjectType = _.find(objectTypes, {
                            key: objectType
                        });
                        if (Util.goodMapValue(foundObjectType, objectUrlKey, false)) {
                            objectUrl = foundObjectType[objectUrlKey];
                            objectUrl = replacePathVariables(pathVariables, objectUrl);
                        }
                        if (!Util.isEmpty(foundObjectType) && !Util.isEmpty(foundObjectType.target)) {
                            element.attr('target', foundObjectType.target);
                        }
                        element.attr('href', objectUrl);
                    });
                } else {
                    if (objectData.object_type_s == ObjectService.ObjectTypes.NOTIFICATION) {
                        var baseUrl = window.location.href.split('/home.html#!')[0];
                        element.attr('href', baseUrl + scope.url);
                    }
                    else {
                        element.attr('href', scope.url);
                    }
                }
            }

            function replacePathVariables(pathVars, url) {
                _.forEach(pathVars, function(value, key) {
                    url = url.replace(key, value);
                });
                return url;
            }

        }
    }
} ]);