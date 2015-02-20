/**
 * App.View
 *
 * @author jwu
 */
App.View = {
    create : function() {
        if (App.View.MicroData.create)          {App.View.MicroData.create();}
        if (App.View.Dirty.create)              {App.View.Dirty.create();}
    }
    ,onInitialized: function() {
        if (App.View.MicroData.onInitialized)   {App.View.MicroData.onInitialized();}
        if (App.View.Dirty.onInitialized)       {App.View.Dirty.onInitialized();}
    }


    ,gotoPage: function(url) {
        window.location.href = App.getContextPath() + url;
    }

    ,MicroData: {
        create : function() {
            this.contextPath = Acm.Object.MicroData.get("contextPath");
            this.userName    = Acm.Object.MicroData.get("userName");
            this.objectTypes = Acm.Object.MicroData.getJson("objectTypes");
        }
        ,onInitialized: function() {
        }

        ,validateObjectTypes: function(data) {
            if (!Acm.isArray(data)) {
                return false;
            }

            return true;
        }
        ,findObjectType: function(typeName) {
            var ot = null;
            if (this.validateObjectTypes(this.objectTypes)) {
                for (var i = 0; i < this.objectTypes.length; i++) {
                    if (Acm.compare(typeName, this.objectTypes[i].name)) {
                        ot = this.objectTypes[i];
                        break;
                    }
                }
            }
            return ot;
        }
    }

    ,Dirty: {
        create : function() {
            $(window).bind("beforeunload", function(event) {
                if (App.View.Dirty.isDirty()) {
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
        App.View.setApprovers(null);
        App.View.setComplaintTypes(null);
        App.View.setPriorities(null);
    }

};




