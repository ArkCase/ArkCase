/**
 * Acm serves as namespace for Acm application and also provides some frequent miscellaneous functions
 *
 * @author jwu
 */
var Acm = Acm || {

    log: function (msg) {
        if (window.console) {
            if ('function' == typeof console.log) {
                console.log(msg);
            }
        }
    }
    ,isEmpty: function (val) {
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
    ,findIndexInArray: function(arr, attr, value) {
        var found = -1;
        if (Acm.isArray(arr)) {
            for (var i = 0; i < arr.length; i++) {
                if (value == arr[i][attr]) {
                    found = i;
                    break;
                }
            }
        }
        return found;
    }
    ,goodValue: function (val, replacement) {
        var replacedWith = (undefined === replacement) ? "" : replacement;
        return this.isEmpty(val) ? replacedWith : val;
    }

    //Usage ex)   To get good value of grandParent.parent.node.name
    //   Acm.goodObjValue([grandParent, "parent", "node", "name"], "N/A");
    ,goodObjValue: function (arr, replacement) {
        var replacedWith = (undefined === replacement) ? "" : replacement;
        if (Acm.isArray(arr)) {
            if (0 >= arr.length) {
                return replacedWith;
            }

            var v = replacedWith;
            for (var i = 0; i < arr.length; i++) {
                if (0 == i) {
                    v = arr[0];
                } else {
                    var k = arr[i];
                    v = v[k];
                }

                if (this.isEmpty(v)) {
                    return replacedWith;
                }
            }
            return v;

        } else {
            return replacedWith;
        }
    }

    ,servicePromise: function($q, arg) {
        var d = $q.defer();
        var service = arg.service;
        var param = Acm.goodValue(arg.param, {});
        if (arg.result) {
            d.resolve(arg.result);

        } else {
            service(param, function(data) {
                if (arg.callback) {
                    d.resolve(arg.callback(data));
                } else {
                    d.resolve(data);
                }
            });
        }
        return d.promise;
    }

};
