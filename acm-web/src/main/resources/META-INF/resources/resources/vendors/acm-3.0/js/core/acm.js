/**
 * Acm serves as namespace for Acm application and also provides some frequent miscellaneous functions
 *
 * @author jwu
 */
var Acm = Acm || {
    prepare: function(context) {
        Acm.Service.setContextPath(context.path);
    }
    ,create : function() {
        Acm.Dialog.create();
        Acm.Dispatcher.create();
        Acm.Ajax.create();
        Acm.Object.create();
        Acm.Validator.create();

        if (Acm.Model.create) {Acm.Model.create();}
    }
    ,onInitialized: function() {
        if (Acm.Model.onInitialized) {Acm.Model.onInitialized();}
    }

    ,__FixMe__getUserFullName: function(user) {
        var fullName;
        if ("albert-acm" == user) {
            fullName = "Albert Analyst";
        } else if ("ann-acm" == user || "Ann-acm" == user) {
            fullName = "Ann Administrator";
        } else if ("charles-acm" == user) {
            fullName = "Charles Call Center";
        } else if ("ian-acm" == user) {
            fullName = "Ian Investigator";
        } else if ("samuel-acm" == user) {
            fullName = "Samuel Supervisor";
        } else if ("sally-acm" == user) {
            fullName = "Sally Supervisor";
        } else {
            fullName = user;
        }
        return fullName;
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
    ,isNotArray: function (arr) {
        return !this.isArray(arr);
    }
	,isArrayEmpty: function (arr) {
        if(!this.isArray(arr)) {
            return true;
        }
	    return arr.length === 0;
	}
    ,isItemInArray: function(item, arr) {
        if(!this.isArray(arr)) {
            return false;
        }
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

    //val can be a simple value or an array.
    //Usage ex)   To get good value of grandParent.parent.node.name
    //   Acm.goodValue([grandParent, "parent", "node", "name"], "N/A");
    ,goodValue: function (val, replacement)  {
        var replacedWith = (undefined === replacement) ? "" : replacement;
        if (Acm.isArray(val)) {
            if (0 >= val.length) {
                return replacedWith;
            }

            var v = replacedWith;
            for (var i = 0; i < val.length; i++) {
                if (0 == i) {
                    v = val[0];
                } else {
                    var k = val[i];
                    v = v[k];
                }

                if (this.isEmpty(v)) {
                    return replacedWith;
                }
            }
            return v;

        } else {
            return this.isEmpty(val) ? replacedWith : val;
        }
    }

    ,parseJson: function (str, replacement)  {
        var replacedWith = (undefined === replacement) ? {} : replacement;
        var json = replacedWith;
        try {
            json = JSON.parse(str);
        } catch (e) {
            json = replacedWith;
        }
        return json;
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

        var decoded = decodeURIComponent(
            decodeURI(param)
                .replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"')
                .replace(/\n/g,"\\n").replace(/\r/g,"\\r")
                .replace(/\+/g, " ")
        );

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

    ,deferredTimer: function(data, interval) {
        var dfd = $.Deferred();
        var t = Acm.goodValue(interval, 200);
        setTimeout(function() {
            dfd.resolve(data);
        }, t);
        return dfd;
    }

    ,copyObjectFunction: function(obj, frFn, toFn) {
        var ext =  $.extend({}, obj);
        obj[toFn] = ext[frFn];
        return obj[toFn];
    }

    ,Promise: {
        resolvePromises: function(promises) {
            var resolver = $.Deferred();
            if (Acm.isArrayEmpty(promises)) {
                resolver.resolve();

            } else {
                $.when.apply(null, promises).then(function(data) {
                        resolver.resolve();
                    }, function(e) {
                        resolver.reject();
                    }
                );
            }
            return resolver;
        }
        ,donePromise: function(data) {
            var resolver = $.Deferred();
            resolver.resolve(data);
            return resolver;
        }
        ,failPromise: function(data) {
            var resolver = $.Deferred();
            resolver.reject(data);
            return resolver;
        }

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
            d = moment(dt).format($.t("common:date.short"))
        }
        return d;
    }
    //Get date and time from format: "2014-04-30T16:51:33.914+0000"
    ,getDateTimeFromDatetime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = moment(dt).format($.t("common:date.full"));
        }
        return d;
    }
    ,getFrevvoDateFromDateTime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = moment(dt).format($.t("common:date.frevvo"))
        }
        return d;
    }
    ,getPentahoDateFromDateTime: function(dt) {
        var d = "";
        if (Acm.isNotEmpty(dt)) {
            d = moment(dt).format($.t("common:date.pentaho"))
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

        ,useTimer: function(name, count, callback) {
            Acm.Timer.startWorker(App.getContextPath() + "/resources/js/acmTimer.js");
            Acm.Timer.registerListener(name, count, callback);
        }
    }

    ,log: function(msg) {
        if (window.console) {
            if ('function' == typeof console.log) {
                console.log(msg);
            }
        }
    }
    
    ,isFrevvoXMLFile: function(name, type) {
    	if (Acm.isNotEmpty(name) && Acm.isNotEmpty(type)) {
    		var groups = name.match(/form_(.*)_(\d*)\.xml/);
    		
    		if (Acm.isArray(groups) && !Acm.isArrayEmpty(groups) && groups.length >= 2) {
    			var formName = groups[1];
    			
    			if (Acm.equals(formName + '_xml', type)) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    ,createKeyValueObject: function(quickSearchSolrResponse) {
    	var retval = {};
    	
    	if (Acm.isNotEmpty(quickSearchSolrResponse) && Acm.isArray(quickSearchSolrResponse)) {
    		for (var i = 0; i < quickSearchSolrResponse.length; i++) {
    			retval[quickSearchSolrResponse[i].object_id_s] = quickSearchSolrResponse[i].name;
    		}
    	}
    	
    	return retval;
    }
    
    ,checkRestriction: function(assignee, group, assignees, groups) {
    	var restrict = true;
    	
    	// We need only one true condition.
        // First check if the assignee is the logged user
        if(!Acm.isEmpty(assignee) && Acm.compare(assignee, App.getUserName())){
        	restrict = false;
        } else {
            // If the user is not assignee, check in the assignees (users that belong to the group)
        	// Skip this check if the group is empty
            if (Acm.isArray(assignees) && !Acm.isEmpty(group)) {
            	for (var i = 0; i < assignees.length; i++) {
            		if(Acm.compare(assignees[i].userId, App.getUserName())){
            			restrict = false;
            			break;
            		}
            	}
            }
            
            // If the user in not assignee or is not in the users that belong to a group, check if it's supervisor of the group
            // Skip this check if the group is empty
            if (restrict) {
	            if (Acm.isArray(groups) && !Acm.isEmpty(group)) {
	            	for (var i = 0; i < groups.length; i++) {
	            		if(Acm.compare(groups[i].object_id_s, group)){
	            			if (!Acm.isEmpty(groups[i].supervisor_id_s) && Acm.compare(groups[i].supervisor_id_s, App.getUserName())) {
	            				restrict = false;
	                			break;
	            			}
	            		}
	            	}
	            }
            }
        }
        
        return restrict;
    }

};



