'use strict';

angular.module('services').factory('Websockets.MessageHandler', ['$q', '$rootScope', 'Acm.StoreService'
    , function ($q, $rootScope, Store) {
        var Service = {};

        Service.handleMessage = handleMessage;

        return Service;

        function handleMessage(message) {

            //we need to filter messages for objects that we are not interested
            //until we find out what to do with them
            if (message.className.indexOf('AcmObjectHistory') === -1
                && message.className.indexOf('AcmAssignment') === -1
                && message.className.indexOf('AcmSubscriptionEvent') === -1
            ) {
                //we will remove object from cache
                handleCache(message.objectType, message.objectId, message.action);
                //we will publish message for objecttype with action
                publishMessage(message.objectType, message.objectId, message.action);

                if (message.parentObjectType != null && message.parentObjectId != null) {
                    //we will remove parent object from cache
                    //its update because it has child that is inserted/changed so parent is changed
                    handleCache(message.parentObjectType, message.parentObjectId, 'UPDATE');
                    //we will publish message for parent objecttype with action update
                    publishMessage(message.parentObjectType, message.parentObjectId, 'UPDATE');
                }
            }
        }

        function handleCache(objectType, objectId, action) {
            if (action == 'UPDATE') {
                handleCacheObject(objectType, objectId);
            }
            handleCacheLists(objectType, objectId);
        }

        function publishMessage(objectType, objectId, action) {
            if (action == 'INSERT') {
                var eventName = "object.inserted";
                var data = {
                    objectId: objectId,
                    objectType: objectType
                };
                $rootScope.$bus.publish(eventName, data);
            } else {
                var eventName = "object.changed/" + objectType + "/" + objectId;
                $rootScope.$bus.publish(eventName, objectId);
            }
        }

        function handleCacheLists(objectType, objectId) {
            var cacheListStoreName = getCacheListName(objectType);
            if (cacheListStoreName !== '') {
                //find the case lists and reset them
                var cacheList = new Store.CacheFifo(cacheListStoreName);
                if (cacheList != null) {
                    cacheList.reset();
                }
            }
        }

        function handleCacheObject(objectType, objectId) {

            var cacheInfoStoreName = getCacheInfoName(objectType);

            if (cacheInfoStoreName !== '') {
                //find the objectId in cacheCaseInfo and remove it
                var cacheInfo = new Store.CacheFifo(cacheInfoStoreName);
                var item = cacheInfo.get(objectId);
                if (item != null) {
                    cacheInfo.remove(objectId);
                }
            }

            //handle child items data
            handleChildItemsData(objectType, objectId);

        }

        function removeFromCache(subKey, childCacheName) {
            var cacheChildData = new Store.CacheFifo(childCacheName);
            var keys = cacheChildData.keys();
            var objectKeys = [];
            //we will go through all keys to find keys connected with this objecttype and objectid
            angular.forEach(keys, function (key) {
                if (key != null && key.indexOf(subKey) > -1) {
                    objectKeys.push(key);
                }
            });
            //we will go through all keys that we found and remove them from cache
            angular.forEach(objectKeys, function (key) {
                var item = cacheChildData.get(key);
                if (item != null) {
                    cacheChildData.remove(key);
                }
            });
        }

        function handleChildItemsData(objectType, objectId) {
            //for casefile and complaint we have ChildTaskData, CostSheets and TimeSheets

            if (objectType === 'CASE_FILE' || objectType === 'COMPLAINT') {
                //subKey is objecttype.objectid and some other things like sorting
                var subKey = objectType + "." + objectId;
                removeFromCache(subKey, 'ChildTaskData');
                removeFromCache(subKey, 'CostSheets');
                removeFromCache(subKey, 'TimeSheets');
            }
        }

        function getCacheInfoName(objectType) {

            //all strings are hardcoded
            //we can create const service and use it here and in all places where items are stored in cache

            var cacheInfoStoreName = '';
            if (objectType == 'CASE_FILE') {
                cacheInfoStoreName = 'CaseInfo';
            }
            if (objectType == 'COMPLAINT') {
                cacheInfoStoreName = 'ComplaintInfo';
            }
            if (objectType == 'TASK') {
                cacheInfoStoreName = 'TaskInfo';
            }
            if (objectType == 'COSTSHEET') {
                cacheInfoStoreName = 'CostsheetInfo';
            }
            if (objectType == 'TIMESHEET') {
                cacheInfoStoreName = 'TimesheetInfo';
            }
            return cacheInfoStoreName;
        }

        function getCacheListName(objectType) {
            var cacheListStoreName = '';
            if (objectType == 'CASE_FILE') {
                cacheListStoreName = 'CaseList';
            }
            if (objectType == 'COMPLAINT') {
                cacheListStoreName = 'ComplaintList';
            }
            if (objectType == 'TASK') {
                cacheListStoreName = 'TaskList';
            }
            if (objectType == 'COSTSHEET') {
                cacheListStoreName = 'CostsheetList';
            }
            if (objectType == 'TIMESHEET') {
                cacheListStoreName = 'TimesheetList';
            }
            return cacheListStoreName;
        }
    }
]);
