/**
 * Acm serves as namespace for Acm application and also provides some frequent miscellaneous functions
 *
 * @author jwu
 */
var Acm = Acm || {
    initialize : function() {
        Acm.Dialog.initialize();
        Acm.Dispatcher.initialize();
        Acm.Ajax.initialize();
        Acm.Object.initialize();
        Acm.Validation.initialize();
    }

    ,Dialog : {}
    ,Dispatcher : {}
    ,Ajax : {}
    ,Object : {}
    ,Validation : {}

    ,getContextPath: function() {
        return Acm.Object.getContextPath();
    }
    ,getUserName: function() {
        return Acm.Object.getUserName();
    }

	,isEmpty: function (val) {
//        if (typeof val == "undefined") {
//            return true;
//        }
	    if (undefined == val) {
	        return true;
	    } else if ("" === val) {
	        return true;
	    } else if (null == val) {
	        return true;
	    } else if ("null" == val) {
	    	return true;
	    }

	    return false;
	}
	,isNotEmpty: function (val) {
	    return !this.isEmpty(val);
	}
	,goodValue: function (val, replacement)  {
	    var replacedWith = (undefined === replacement) ? "" : replacement;
	    return this.isEmpty(val) ? replacedWith : val;
	}

    //append random parameter after a url to avoid undesired cached session variables
    //This function handles input url in following sample cases:
    //  some.com/some/path
    //  some.com/some/path/
    //  some.com/some/path/?var=abc
    ,makeNoneCacheUrl: function(url) {
        var lastChar = url.slice(-1);
        var hasQmark = (-1 !== url.indexOf('?'));

        if (hasQmark) {
            url += '&'
        } else {
            if ('/' !== lastChar) {
                url += '/';
            }
            url += '?';
        }
        url += 'rand=' + Math.floor((Math.random()*10000000000));
        return url;
    }

    ,getUrlParameter : function(param) {
        var url = window.location.search.substring(1);
        var urlVariables = url.split('&');
        for (var i = 0; i < urlVariables.length; i++)
        {
            var paramName = urlVariables[i].split('=');
            if (paramName[0] == param)
            {
                return paramName[1];
            }
        }
    }

    ,getUrlParameter2: function(name){
        var results = new RegExp('[\?&amp;]' + name + '=([^&amp;#]*)').exec(window.location.href);
        return results[1] || 0;
    }

    //convert URL parameters to JSON
    //ex) "abc=foo&def=%5Basf%5D&xyz=5&foo=b%3Dar" to {abc: "foo", def: "[asf]", xyz: "5", foo: "b=ar"}
    ,urlToJson: function(param) {
        return JSON.parse('{"' + decodeURI(param).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
    }

    ,deferred: function(fcn) {
        setTimeout(fcn, 200);
    }

    //datetime format: "2014-04-30T16:51:33.914+0000"
    ,getDateFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = dt.substr(0, 10);
        }
        return d;
    }
    //get today in "yyyy-mm-dd" format
    ,getCurrentDay: function() {
        var d = new Date();
        var month = d.getMonth()+1;
        var day = d.getDate();
        var yyyyMmDd = d.getFullYear()
            + '-' + (10>month ? '0' : '') + month
            + '-' + (10>day   ? '0' : '') + day;
        return yyyyMmDd;
    }


//    ,_foobar_cont: function (){
//        console.log("finished.");
//    }
//    ,sleep: function (millis) {
//        setTimeout(
//            function(){
//                this._foobar_cont();
//            }
//            ,millis);
//    }

//    ,_timer: null
//    ,sleep: function (milliseconds) {
//        //this._timer.start();
//        setTimeout(this._wake, milliseconds);
//    }
//
//    ,_wake: function () {
//        //this._timer.stop;
//    }

    ,sleep: function(milliseconds) {
        var start = new Date().getTime();
        for (var i = 0; i < 1e7; i++) {
            if ((new Date().getTime() - start) > milliseconds){
                break;
            }
        }
    }
};



/**
 * initialize Acm
 */
//
// call it externally, from common/ready.js for example
//
//jQuery(document).ready(
//    function() {
//        Acm.initialize();
//    }
//);

