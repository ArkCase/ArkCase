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
        Acm.Rule.initialize();

        Acm.deferred(Acm.Event.onPostInit);
    }

//    ,Dialog : {}
//    ,Dispatcher : {}
//    ,Ajax : {}
//    ,Object : {}
//    ,Event : {}
//    ,Rule : {}


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
	,isArrayEmpty: function (arr) {
	    return arr.length === 0;
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
            //d = dt.substr(0, 10);
            var year  = dt.substr(0, 4);
            var month = dt.substr(5, 2);
            var day   = dt.substr(8, 2);
            d = month + "/" + day + "/" + year;
        }
        return d;
    }
    ,getCurrentDay: function() {
        var d = new Date();
        return this.dateToString(d);
    }
    //get day string in "yyyy-mm-dd" format
    //parameter d is java Date() format; for some reason getDate() is 1 based while getMonth() is zero based
    ,dateToString: function(d) {
        if (null == d) {
            return "";
        }
        var month = d.getMonth()+1;
        var day = d.getDate();
        var year = d.getFullYear();
        return this._padZero(month)
            + "/" + this._padZero(day)
            + "/" + year
            ;
//        return year
//            + "-" + this._padZero(month)
//            + "-" + this._padZero(day)
//            ;
    }
    ,getCurrentDayInternal: function() {
        var d = new Date();
        var month = d.getMonth()+1;
        var day = d.getDate();
        var year = d.getFullYear();
        var hour = d.getHours();
        var minute = d.getMinutes();
        var second = d.getSeconds();
        var ms = d.getMilliseconds();
        return year
            + "-" + this._padZero(month)
            + "-" + this._padZero(day)
            + "T" + this._padZero(hour)
            + ":" + this._padZero(minute)
            + ":" + this._padZero(second)
            + "." + this._padZero(ms)
            + "+0000"
            ;
    }

    //parameter d from x-editable date format, both getDate() and getMonth() are zero based
    ,xDateToDatetime: function(d) {
        if (null == d) {
            return "";
        }
        var month = d.getMonth()+1;
        var day = d.getDate()+1;
        var year = d.getFullYear();
        var hour = d.getHours();
        var minute = d.getMinutes();
        var second = d.getSeconds();

        return year
            + "-" + this._padZero(month)
            + "-" + this._padZero(day)
            + "T" + this._padZero(hour)
            + ":" + this._padZero(minute)
            + ":" + this._padZero(second)
            + ".000+0000"
            ;
    }
    ,_padZero: function(i) {
        return (10 > i) ? "0" + i : "" + i;
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

//Untested code, commented for now.
// http://www.w3schools.com/HTML/html5_webworkers.asp
//
//    ,Timer: {
//        startWorker: function() {
//            if(typeof(Worker) !== "undefined") {
//                if(typeof(this._worker) == "undefined") {
//                    this._worker = new Worker("acmTimer.js");
//                }
//                this._worker.onmessage = function(event) {
//                    Console.log("" + event.data);
//                };
//            } else {
//                Console.log("Sorry! No Web Worker support.");
//            }
//        }
//        ,stopWorker: function() {
//            this._worker.terminate();
//        }
//    }
//    ,SessionData: function(name) {
//        this.name = name;
//    }

    ,CacheFifo: function(maxSize) {
        this.maxSize = maxSize;
        this.reset();
    }

};

//data stored in SessionStorage
//Acm.SessionData.prototype = {
//    getName: function() {
//        return this.name;
//    }
//    ,get: function() {
//        var data = sessionStorage.getItem(this.name);
//        var item = ("null" === data)? null : JSON.parse(data);
//        return item;
//    }
//    ,set: function(data) {
//        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
//        sessionStorage.setItem(this.name, item);
//    }
//}

//simple first in first out aging cache
Acm.CacheFifo.prototype = {
    getMaxSize: function() {
        return this.maxSize;
    }
    ,setMaxSize: function(maxSize) {
        this.maxSize = maxSize;
    }
    ,put: function(key, item) {
        var putAt = this.next;
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                putAt = i;
                break;
            }
        }


        this.cache[key] = item;
        this.keys[putAt] = key;


        if (putAt == this.next) {
            this.next = (this.next + 1) % this.maxSize;
            this.size = (this.maxSize > this.size)? (this.size + 1) : this.maxSize;
        }
    }
    ,remove: function(key) {
        var delAt = -1;
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                delAt = i;
                break;
            }
        }

        if (0 <= delAt) {
            var newKeys = [];
            for (var i = 0; i < this.maxSize; i++) {
                newKeys.push(null);
            }

            if (this.size == this.maxSize) {
                var n = 0;
                for (var i = 0; i < this.size; i++) {
                    if (i != delAt) {
                        newKeys[n] = this.keys[(this.next + i + this.maxSize) % this.maxSize];
                        n++;
                    }
                }
            } else {
                var n = 0;
                for (var i = 0; i < this.size; i++) {
                    if (i != delAt) {
                        newKeys[n] = this.keys[i];
                        n++;
                    }
                }
            }
            this.size--;
            this.next = this.size;

            this.keys = newKeys;
            delete this.cache[key];
        } //end if (0 <= delAt) {
    }
    ,get: function(key) {
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                return this.cache[key];
            }
        }
        return null;
    }
    ,reset: function() {
        this.next = 0;
        this.size = 0;
        this.cache = {};
        this.keys = [];
        for (var i = 0; i < this.maxSize; i++) {
            this.keys.push(null);
        }
    }
};


