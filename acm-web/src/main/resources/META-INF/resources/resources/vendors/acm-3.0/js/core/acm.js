/**
 * Acm serves as namespace for Acm application and also provides some frequent miscellaneous functions
 *
 * @author jwu
 */
var Acm = Acm || {
    create : function() {
        Acm.Dialog.create();
        Acm.Dispatcher.create();
        Acm.Ajax.create();
        Acm.Object.create();
        Acm.Validator.create();
        Acm.Model.create();
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
    ,isArray: function(arr) {
        if (arr) {
            if (arr instanceof Array) {
                return true;
            }
        }
        return false;
    }
	,isArrayEmpty: function (arr) {
	    return arr.length === 0;
	}
    ,isItemInArray: function(item, arr) {
        for (var i = 0; i < arr.length; i++) {
            if (item == arr[i]) {
                return true;
            }
        }
        return false;
    }
    ,compare: function(left, right) {  //equals() name is taken, so use compare()
        if (Acm.isEmpty(left)) {
            return Acm.isEmpty(right);
        }
        return left == right;
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
        var decodedUrlComponents = decodeURIComponent(param);

        var decoded = decodeURI(decodedUrlComponents)
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

    //Get date part from format: "2014-04-30T16:51:33.914+0000"
    ,getDateFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            var year  = dt.substr(0, 4);
            var month = dt.substr(5, 2);
            var day   = dt.substr(8, 2);
            d = month + "/" + day + "/" + year;
        }
        return d;
    }
    //Get date and time from format: "2014-04-30T16:51:33.914+0000"
    ,getDateTimeFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            var year  = dt.substr(0, 4);
            var month = dt.substr(5, 2);
            var day   = dt.substr(8, 2);
            var hour   = dt.substr(11, 2);
            var minute   = dt.substr(14, 2);
            var second   = dt.substr(17, 2);
            d = month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second;
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

    //todo: relocate this to AcmEx.Object.XEditable
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



    ,sleep: function(milliseconds) {
        var start = new Date().getTime();
        for (var i = 0; i < 1e7; i++) {
            if ((new Date().getTime() - start) > milliseconds){
                break;
            }
        }
    }

    /**
     * Deep compare of two objects.
     *
     * Note that this does not detect cyclical objects as it should.
     * Need to implement that when this is used in a more general case. It's currently only used
     * in a place that guarantees no cyclical structures.
     *
     * @param {*} x
     * @param {*} y
     * @return {Boolean} Whether the two objects are equivalent, that is,
     *         every property in x is equal to every property in y recursively. Primitives
     *         must be strictly equal, that is "1" and 1, null an undefined and similar objects
     *         are considered different
     */
    ,equals: function ( x, y ) {
        // If both x and y are null or undefined and exactly the same
        if ( x === y ) {
            return true;
        }

        // If they are not strictly equal, they both need to be Objects
        if ( ! ( x instanceof Object ) || ! ( y instanceof Object ) ) {
            return false;
        }

        // They must have the exact same prototype chain, the closest we can do is
        // test the constructor.
        if ( x.constructor !== y.constructor ) {
            return false;
        }

        for ( var p in x ) {
            // Inherited properties were tested using x.constructor === y.constructor
            if ( x.hasOwnProperty( p ) ) {
                // Allows comparing x[ p ] and y[ p ] when set to undefined
                if ( ! y.hasOwnProperty( p ) ) {
                    return false;
                }

                // If they have the same strict value or identity then they are equal
                if ( x[ p ] === y[ p ] ) {
                    continue;
                }

                // Numbers, Strings, Functions, Booleans must be strictly equal
                if ( typeof( x[ p ] ) !== "object" ) {
                    return false;
                }

                // Objects and Arrays must be tested recursively
                if ( !equals( x[ p ],  y[ p ] ) ) {
                    return false;
                }
            }
        }

        for ( p in y ) {
            // allows x[ p ] to be set to undefined
            if ( y.hasOwnProperty( p ) && ! x.hasOwnProperty( p ) ) {
                return false;
            }
        }
        return true;
    }


    ,Timer: {
        _worker: null
        ,startWorker: function(workerUrl) {
            if (null == this._worker) {
                if(typeof(Worker) === "undefined") {
                    return null;
                }

                this._worker = new Worker(workerUrl);
                this._worker.onmessage = function(event) {
                    //console.log("" + event.data);
                    Acm.Timer.triggerEvent();
                };
            }
            return this._worker;
        }
        ,stopWorker: function() {
            this._worker.terminate();
        }

        ,_listeners: []
        ,_listenerCount: 0
        ,registerListener: function(name, count, callback) {
            var i = this._findListener(name);
            if (0 > i) {    //not found; create new entry
                this._listeners.push({name: name, callback: callback, count: count, countDown: count});
                this._listenerCount++;
            } else {
                var listener = this._listeners[i];
                listener.callback = callback;
                listener.count = count;
            }
        }
        ,removeListener: function(name) {
            var i = this._findListener(name);
            this._removeListener(i);
        }
        ,_removeListener: function(i) {
            if (0 <= i) {
                this._listeners.splice(i, 1);
                this._listenerCount--;
            }
        }
        ,_findListener: function(name) {
            for (var i = 0; i < this._listenerCount; i++) {
                var listener = this._listeners[i];
                if (listener.name == name) {
                    return i;
                }
            }
            return -1;
        }
        ,triggerEvent: function() {
            //console.log("triggerEvent, this._listenerCount=" + this._listenerCount);

            //need to loop backwards because of possible item removed while looping
            for (var i = this._listenerCount - 1; 0 <= i; i--) {
                var listener = this._listeners[i];
                if (0 >= --listener.countDown) {
                    if (listener.callback(listener.name)) {
                        listener.countDown = listener.count;
                    } else {
                        this._removeListener(i);
                    }
                }
            } //for i
        }
    }


};



