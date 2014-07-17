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
        Acm.Event.initialize();
        Acm.Service.initialize();
        Acm.Callback.initialize();
        Acm.Rule.initialize();

        Acm.deferred(Acm.Event.onPostInit);
    }

    ,Dialog : {}
    ,Dispatcher : {}
    ,Ajax : {}
    ,Object : {}
    ,Event : {}
    ,Service : {}
    ,Callback : {}
    ,Rule : {}

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
    //  some.com/some/path?var=abc
    ,makeNoneCacheUrl: function(url) {
        var lastChar = url.slice(-1);
        var hasQmark = (-1 !== url.indexOf('?'));

        if (hasQmark) {
            url += '&'
        } else {
//            if ('/' !== lastChar) {
//                url += '/';
//            }
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
        var decoded = decodeURI(param)
            .replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"')
            .replace(/\n/g,"\\n").replace(/\r/g,"\\r")
            .replace(/\+/g, " ");


        var parsed = JSON.parse('{"' + decoded + '"}');

        //todo: make a function to tranvers json object; loop for now
        for (var key in parsed) {
            parsed[key] = parsed[key].replace(/\\r/g, "\r").replace(/\\n/g, "\n");
        }
        return parsed;


        var a1 = param;
        var a1a = param.replace(/%0D/g, '_0D_');
        var a1b = a1a.replace(/%0A/g, '_0A_');
        a1b = a1;
        var a2 = decodeURI(param);
        //var a3 = a2.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"');
        var a3 = a2.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"').replace(/\n/g,"\\n").replace(/\r/g,"\\r").replace(/\+/g, " ");

        var a3a = a3.replace(/\+/g, " ");
        var a3b = a3a.replace(/"_0D_"/g, '%0D');
        var a3c = a3b.replace(/"_0A_"/g, '%0A');
        a3c = a3;
        var a4a = '{"' + a3c + '"}';
        var a5 = JSON.parse(a4a);

        var str = "Visit W3Schools.\nLearn \\nJavaScript.";
        var d1 = str.replace(/\\n/g,"\n");
        var d2 = str.replace(/\n/g,"\n");

        //todo: make a function to tranvers json object; loop for now
        for (var key in a5) {
            var c1 = key;
            var c2 = a5[key];
            var c3 = c2.replace(/\\r/g, "\r");
            var c4 = c3.replace(/\\n/g, "\n");

            a5[key] = a5[key].replace(/\\r/g, "\r").replace(/\\n/g, "\n");
            var z = 1;
        }

        var z = 1;
        return a5;
        //return JSON.parse('{"' + decodeURI(param).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
    }

    ,deferred: function(fn) {
        setTimeout(fn, 200);
    }

    ,keepTrying: function(fn, trials, interval) {
        if (!fn()) {
            if (1 < trials) {
                setTimeout(function(){
                    Acm.keepTrying(fn, trials - 1, interval * 2);
                }, interval);
            };
        }
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

    ,gotoPage: function(url) {
        window.location.href = Acm.getContextPath() + url;
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

    ,reset: function() {
        Acm.Object.setApprovers(null);
        Acm.Object.setComplaintTypes(null);
        Acm.Object.setPriorities(null);
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

