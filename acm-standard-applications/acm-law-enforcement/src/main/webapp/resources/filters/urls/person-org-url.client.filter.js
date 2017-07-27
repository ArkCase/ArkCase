'use strict';

angular.module('filters').filter('urlHyperLink', function () {
    return function (link) {
        var result;
        var startingUrl = "http://";
        var httpsStartingUrl = "https://";
        if (_.startsWith(link,startingUrl) || _.startsWith(link,httpsStartingUrl)) {
            result = link;
        }
        else {
            result = startingUrl + link;
        }
        return result;
    }
});
