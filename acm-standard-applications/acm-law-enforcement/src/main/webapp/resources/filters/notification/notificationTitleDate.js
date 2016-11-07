'use strict';

angular.module('filters').filter('notificationTitleDate', ['$filter', function ($filter) {
    return function (inTitle) {
        var matchDate = inTitle.match(/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{1,3}[+-][0-9]{2}/g);
        if (matchDate == null) {
            return inTitle;
        }
        else {
            var dateStr = matchDate[0];
            var dateStrFormat = $filter('date')(dateStr.replace(" ", "T") + "00", "MM/dd/yyyy HH:mm:ss");
            var outTitle = inTitle.replace(dateStr, dateStrFormat);
            return outTitle;
        }
    };
}])
;