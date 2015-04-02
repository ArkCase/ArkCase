/**
 * App.View
 *
 * @author jwu
 */
App.View = {
    create : function() {
        if (App.View.MicroData.create)          {App.View.MicroData.create();}
        if (App.View.ErrorBoard.create)          {App.View.ErrorBoard.create();}
        if (App.View.Dirty.create)              {App.View.Dirty.create();}
    }
    ,onInitialized: function() {
        if (App.View.MicroData.onInitialized)   {App.View.MicroData.onInitialized();}
        if (App.View.ErrorBoard.onInitialized)   {App.View.ErrorBoard.onInitialized();}
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

    ,ErrorBoard: {
        create : function() {
            this.$divBoard  = $("#divEbErrorBoard");
            this.$divDetail = $("#divEbErrorDetail");
            this.$btnClose  = $("#btnEbErrorClose");
            this.$btnDetail = $("#btnEbErrorDetail");
            this.$labMsg    = $("#labEbErrorMsg");
            this.$labDetail = $("#labEbErrorDetail");

            this.$btnClose.unbind("click").on("click", function(e) {App.View.ErrorBoard.onClickBtnClose(e, this);});
            this.$btnDetail.unbind("click").on("click", function(e) {App.View.ErrorBoard.onClickBtnDetail(e, this);});
        }
        ,onInitialized: function() {
        }

        ,onClickBtnClose: function(event, ctrl) {
            App.View.ErrorBoard.showDivBoard(false);
        }
        ,onClickBtnDetail: function(event, ctrl) {
            App.View.ErrorBoard.showDivDetail(!App.View.ErrorBoard.isDetailShown());
        }
        ,show: function(msg, detail) {
            App.View.ErrorBoard.setTextLabMsg(msg);

            if (Acm.isNotEmpty(detail)) {
                App.View.ErrorBoard.setTextLabDetail(detail);
                App.View.ErrorBoard.showBtnDetail(true);
            } else {
                App.View.ErrorBoard.showBtnDetail(false);
            }
            App.View.ErrorBoard.showDivDetail(false);

            App.View.ErrorBoard.showDivBoard(true);
        }
//        ,close: function() {
//            App.View.ErrorBoard.showDivBoard(false);
//        }
        ,setTextLabMsg: function(text) {
            Acm.Object.setText(this.$labMsg, text);
        }
        ,setTextLabDetail: function(text) {
            Acm.Object.setText(this.$labDetail, text);
        }
        ,showBtnDetail: function(show) {
            Acm.Object.show(this.$btnDetail, show);
        }
        ,showDivBoard: function(show) {
            if (show) {
                this.$divBoard.slideDown("slow");
            } else {
                this.$divBoard.slideUp("slow");
            }
        }

        ,_isDetailShown: false
        ,isDetailShown: function() {
            return this._isDetailShown;
        }
        ,showDivDetail: function(show) {
            if (show) {
                this.$divDetail.slideDown("slow");
            } else {
                this.$divDetail.slideUp("slow");
            }
            this._isDetailShown = show;
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




