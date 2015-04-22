/**
 * App.View
 *
 * @author jwu
 */
App.View = {
    create : function() {
        if (App.View.MicroData.create)          {App.View.MicroData.create();}
        if (App.View.MessageBoard.create)          {App.View.MessageBoard.create();}
        if (App.View.Dirty.create)              {App.View.Dirty.create();}
    }
    ,onInitialized: function() {
        if (App.View.MicroData.onInitialized)   {App.View.MicroData.onInitialized();}
        if (App.View.MessageBoard.onInitialized)   {App.View.MessageBoard.onInitialized();}
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

    ,MessageBoard: {
        create : function() {
            this.$sectionContent = $("#content");                   //this is where all the module data is displayed (tree,tables,topbar etc.)
            this.$divBoard  = $("#ui-messageBoardContainer");
            this.$divDetail = $("#divMessageDetail");
            this.$btnClose  = $("#btnMessageClose");
            this.$btnDetail = $("#btnMessageDetail");
            this.$labMsg    = $("#labMessage");
            this.$labDetail = $("#ui-labelMessageDetail");
            this.$divModalMsgBoard = $(".modal-header");

            this.$btnClose.unbind("click").on("click", function(e) {App.View.MessageBoard.onClickBtnClose(e, this);});
            //this.$btnDetail.unbind("click").on("click", function(e) {App.View.MessageBoard.onClickBtnDetail(e, this);});
            this.$divModalMsgBoard.unbind("click").on("click", "a", function(e) {App.View.MessageBoard.onClickBtnDetail(e, this);});
            App.View.MessageBoard.resizeMessageBoard();
        }
        ,onInitialized: function() {

        }

        ,onClickBtnClose: function(event, ctrl) {
            //clear the lists to
            //prepare for future errors
            this.$divModalMsgBoard.find("ul li").remove();
            App.View.MessageBoard.showDivBoard(false);
            App.View.MessageBoard.showDivDetail(false);

        }
        ,onClickBtnDetail: function(event, ctrl) {
            var detail = $(event.target).attr('data-msg-detail');
            detail = Acm.isEmpty(detail)?"Detail unavailable":Acm.goodValue(detail);
            App.View.MessageBoard.setTextLabDetail(detail);
            //App.View.MessageBoard.slideDivDetail(!App.View.MessageBoard.isDetailShown());
            App.View.MessageBoard.showDivDetail();
        }
        ,fitToContentSize: function(){
            $(window).resize(function () {
                App.View.MessageBoard.resizeMessageBoard();
            });
        }

        ,useAcmMessageBoard: function(){
            // refer to the base object member
            // by default all jtable methods
            // can be accessed via $.hik.jtable.prototype

            // override using the errorboard instead
            $.hik.jtable.prototype._showError = function(message){
                App.View.MessageBoard.show(message);
            }
        }
        ,useDefaultJtableErrorDialog: function(){
            // refer to the base object member
            // by default all jtable methods
            // can be accessed via $.hik.jtable.prototype

            var jtableErrorDialog = $.hik.jtable.prototype._showError;
            $.hik.jtable.prototype._showError = function(){
                // original method to be used
                jtableErrorDialog.apply( this, arguments);
            }
        }
        ,addMessagesToMessageBoard: function(msg,detail){
            if(Acm.isNotEmpty(msg)){
                if(Acm.isEmpty(detail)){
                    detail='';
                }
                var html="";
                html+= "<li><a href='#'><div class='ui-message' title='Click for Detail...'  data-msg-detail='" + detail + "'>" +  msg +
                //"<a href='#' style='position: absolute;right:50px;'>Detail ...</button></a>" style='color:#000000;font-weight: bold;margin:0px;text-align: left'+
                "</div></a></li>";
                this.$divModalMsgBoard.find("ul").prepend(html);
            }
        }

        ,resizeMessageBoard: function(){
            var width = this.$sectionContent.width();
            this.$divModalMsgBoard.css('width', width);
        }

        ,show: function(msg, detail) {
            App.View.MessageBoard.showDivBoard(false);
            if(Acm.isNotEmpty(msg)){
                App.View.MessageBoard.addMessagesToMessageBoard(msg,detail);
                App.View.MessageBoard.fitToContentSize();
                App.View.MessageBoard.showDivBoard(true);
            }
        }
//        ,close: function() {
//            App.View.MessageBoard.showDivBoard(false);
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
            // show/hide showed better performance
            // when there were multiple errors
            // previously used methods for reference:
            // .slideDown("slow");
            // .slideUp("slow");

            if (show) {
                this.$divBoard.show();
            } else {
                this.$divBoard.hide();
            }
        }

        ,showDivDetail: function(){
            this.$divDetail.show();
        }
        ,_isDetailShown: false
        ,isDetailShown: function() {
            return this._isDetailShown;
        }
        ,slideDivDetail: function(show) {
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




