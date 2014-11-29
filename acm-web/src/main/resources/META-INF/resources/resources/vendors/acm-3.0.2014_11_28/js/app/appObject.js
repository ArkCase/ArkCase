/**
 * App.Object
 *
 * common function for screen object management
 * Argument $s must be a valid jQuery selector
 *
 * @author jwu
 */
App.Object = {
    create : function() {
        var items = $(document).items();
        this._contextPath = items.properties("contextPath").itemValue();
        this._userName = items.properties("userName").itemValue();


        if (App.Object.Dirty.create) {App.Object.Dirty.create();}
    }

    ,Dirty: {
        create : function() {
            $(window).bind("beforeunload", function(event) {
                if (App.Object.Dirty.isDirty()) {
                    return "Warning: There unsaved data. Leaving this page may cause data lost.";
                }
            });
        }

        ,_items: []
        ,isDirty: function() {
            return 0 < this._items.length;
        }
        ,getFirst: function() {
            if (0 < this._items.length) {
                return this._items[0];
            } else {
                return null;
            }

        }
        ,declare: function(item) {
            this._items.push(item);
        }
        ,clear: function(item) {
            for (var i = this._items.length - 1; 0 <= i; i--) {
                if (this._items[i] == item) {
                    this._items.splice(i, 1);
                }
            }
        }
    }

    ,_contextPath: ""
    ,getContextPath: function() {
        return this._contextPath;
    }
    ,_userName: ""
    ,getUserName: function() {
        return this._userName;
    }

    //Expect data to be JSON array: [{userId:"xxx" fullName:"xxx" ...},{...} ]
    ,getApprovers: function() {
        var data = sessionStorage.getItem("AcmApprovers");
        var item = ("null" === data)? null : JSON.parse(data);
        return item;
    }
    ,setApprovers: function(data) {
        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
        sessionStorage.setItem("AcmApprovers", item);
    }
    ,getComplaintTypes: function() {
        var data = sessionStorage.getItem("AcmComplaintTypes");
        var item = ("null" === data)? null : JSON.parse(data);
        return item;
    }
    ,setComplaintTypes: function(data) {
        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
        sessionStorage.setItem("AcmComplaintTypes", item);
    }
    ,getPriorities: function() {
        var data = sessionStorage.getItem("AcmPriorities");
        var item = ("null" === data)? null : JSON.parse(data);
        return item;
    }
    ,setPriorities: function(data) {
        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
        sessionStorage.setItem("AcmPriorities", item);
    }

    ,reset: function() {
        App.Object.setApprovers(null);
        App.Object.setComplaintTypes(null);
        App.Object.setPriorities(null);
    }

};




