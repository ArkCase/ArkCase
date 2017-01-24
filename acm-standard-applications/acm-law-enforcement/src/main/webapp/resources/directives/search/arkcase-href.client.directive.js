/**
 * Created by sasko.tanaskoski
 */

'use strict';

angular.module('directives').directive('arkcaseHref', ['UtilService', 'ObjectService', 'Object.LookupService'
    , function(Util, ObjectService, ObjectLookupService) {
        var defaults = {
            isParent: false
        };
        return {
            restrict: 'A',
            scope: {
                objectData: '=',
                isParent: '=',
                url: '='
            },
            link: function(scope, element, attrs) {
                scope.$watch('objectData', function(newValue, oldValue) {
                    buildUrl(newValue);
                });
                
                function buildUrl(objectData) {
                    if (scope.url == undefined) {
                        var objectType = Util.goodMapValue(objectData, "object_type_s");
                        var objectId = Util.goodMapValue(objectData, "object_id_s");
                        if (scope.isParent) {
                            var parentReference = Util.goodMapValue(objectData, "parent_ref_s", "-");
                            objectType  = parentReference.substring(parentReference.indexOf('-') + 1);
                            objectId = parentReference.substring(0, parentReference.indexOf('-'));
                        }
                        if (objectType == ObjectService.ObjectTypes.TASK) {
                            objectType = (Util.goodMapValue(objectData, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        }
                        ObjectLookupService.getObjectTypes().then(
                            function (objectTypes) {
                                var objectUrl = '';
                                var foundObjectType = _.find(objectTypes, {key: objectType});
                                if (Util.goodMapValue(foundObjectType, "url", false)) {
                                    objectUrl = foundObjectType.url;
                                    objectUrl = objectUrl.replace(":id", objectId);
                                }
                                element.attr('href', objectUrl);
                            }
                        );
                    } else {
                        element.attr('href', scope.url);
                    }
                };
                
            }
        }
}]);