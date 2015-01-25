/**
 * Topbar.View
 *
 * @author jwu
 */
Topbar.View = {
    create : function() {
        if (Topbar.View.QuickSearch.create) {Topbar.View.QuickSearch.create();}
        if (Topbar.View.Suggestion.create)  {Topbar.View.Suggestion.create();}
        if (Topbar.View.Asn.create)         {Topbar.View.Asn.create();}
    }
    ,onInitialized: function() {
        if (Topbar.View.QuickSearch.onInitialized) {Topbar.View.QuickSearch.onInitialized();}
        if (Topbar.View.Suggestion.onInitialized)  {Topbar.View.Suggestion.onInitialized();}
        if (Topbar.View.Asn.onInitialized)         {Topbar.View.Asn.onInitialized();}
    }

    ,QuickSearch: {
        create: function() {
            this.$formSearch = $("form[role='search']");
            this.$edtSearch = this.$formSearch.find("input.typeahead");
            this.$btnSearch = this.$formSearch.find("button[type='submit']");

            //this.$formSearch.on("submit", function() {Topbar.View.QuickSearch.onSubmitFormSearch(this);});
            this.$btnSearch .on("click", function(e) {Topbar.View.QuickSearch.onClickBtnSearch(e, this);});

            this.$formSearch.attr("method", "get");
//            var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
//            this.setActionFormSearch(term);
            this.setActionFormSearch();
        }
        ,onInitialized: function() {
        }

        ,onClickBtnSearch : function(event, ctrl) {
            var term = this.getValueEdtSearch();
            if (0 < Topbar.Controller.QuickSearch.viewChangedQuickSearchTerm(term)) {
                event.preventDefault();
            } else {
//                this.setActionFormSearch(term);
            }
        }

//        ,onSubmitFormSearch : function(ctrl) {
//            var term = this.getValueEdtSearch();
//            Topbar.Controller.QuickSearch.viewChangedQuickSearchTerm(term);
//            return false;
//        }

        ,setActionFormSearch: function() {
//        ,setActionFormSearch: function(term) {
            var url = App.getContextPath() + "/plugin/search"
//            if (Acm.isNotEmpty(term)) {
//                url += "?q=" + term;
//            }
            this.$formSearch.attr("action", url);
        }
        ,getValueEdtSearch: function() {
            return Acm.Object.getPlaceHolderInput(this.$edtSearch);
        }
        ,setValueEdtSearch: function(val) {
            return Acm.Object.setPlaceHolderInput(this.$edtSearch, val);
        }
    }

    ,Suggestion: {
        create: function() {
            this.$formSearch = $("form[role='search']");
            this.$edtSearch = this.$formSearch.find("input.typeahead");
            //this.useTypeAhead(this.$edtSearch);

            Acm.Dispatcher.addEventListener(Topbar.Controller.Suggestion.MODEL_CHANGED_SUGGESTION, this.onModelChangedSuggestion);
        }
        ,onInitialized: function() {
        }

        ,onModelChangedSuggestion: function(process) {
            process(Topbar.Model.Suggestion.getKeys());
        }

        ,useTypeAhead: function($s) {
            $s.typeahead({
                source: function ( query, process ) {
                    _.debounce(Topbar.Service.Suggestion.retrieveSuggestion( query, process ), 300);
                }
                ,highlighter: function( item ){
                    html = '<div class="ctr">';
                    var ctr = Topbar.Model.Suggestion.getObject(item);
                    if (ctr) {
                        var icon = "";
                        var type = Acm.goodValue(ctr.object_type_s, "UNKNOWN");
                        if (type == "COMPLAINT") {
                            icon = '<i class="i i-notice i-2x"></i>';
                        } else if (type == "CASE") {
                            icon = '<i class="i i-folder i-2x"></i>';
                        } else if (type == "TASK") {
                            icon = '<i class="i i-checkmark i-2x"></i>';
                        } else if (type == "DOCUMENT") {
                            icon = '<i class="i i-file i-2x"></i>';
                        } else {
                            icon = '<i class="i i-circle i-2x"></i>';
                        }

                        html += '<div class="icontype">' + icon + '</div>'
                            + '<div class="title">' + Acm.goodValue(ctr.title_parseable) + '</div>'
                            + '<div class="identifier">' + Acm.goodValue(ctr.name) + ' ('+ Acm.goodValue(ctr.object_type_s) + ')' + '</div>'
                            + '<div class="author">By ' + ctr.author_s  + ' on '+ Acm.getDateTimeFromDatetime(ctr.last_modified_tdt) + '</div>'
                        html += '</div>';
                    }
                    return html;
                }
                , updater: function ( selectedtitle ) {
                    var ctr = Topbar.Model.Suggestion.getObject(selectedtitle);
                    $( "#ctrId" ).val(ctr.object_id_s);
                    return selectedtitle;
                }
                ,hint: true
                ,highlight: true
                ,minLength: 1

            }); //end $s.typeahead
        }
    }

    ,Asn: {
        create: function() {
            this.$ulAsn = $("ul.nav-user");
            this.$divAsnList = $('.list-group', this.$ulAsn);
            this.$lnkAsn = $("ul.nav-user a[data-toggle='dropdown']");
            this.$lnkAsn.on("click", function(e) {Topbar.View.Asn.onClickLnkAsn(e, this);});
            this.$sectionAsn = this.$divAsnList.closest("section.dropdown-menu");

            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_RETRIEVED_ASN_LIST        ,this.onModelRetrievedAsnList);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_SAVED_ASN                 ,this.onModelSavedAsn);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_UPDATED_ASN_ACTION        ,this.onModelUpdatedAsnAction);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_UPDATED_ASN_STATUS        ,this.onModelUpdatedAsnStatus);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_DELETED_ASN               ,this.onModelDeletedAsn);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Flash.MODEL_ADDED_FLASH_MSG         ,this.onModelAddedFlashMsg);


            $("#test1").on("click", function(e) {Topbar.View.Asn.onClickBtnTest1(e, this);});
            $("#test2").on("click", function(e) {Topbar.View.Asn.onClickBtnTest2(e, this);});
            $("#test3").on("click", function(e) {Topbar.View.Asn.onClickBtnTest3(e, this);});
            $("#test4").on("click", function(e) {Topbar.View.Asn.onClickBtnTest4(e, this);});


            if (Topbar.View.Asn.Counter.create)         {Topbar.View.Asn.Counter.create();}

        }
        ,onInitialized: function() {
            if (Topbar.View.Asn.Counter.onInitialized)  {Topbar.View.Asn.Counter.onInitialized();}
        }

        ,onClickBtnTest1: function(event, ctrl) {
            Topbar.Service.Asn.retrieveAsnList(App.getUserName());
        }
        ,onClickBtnTest2: function(event, ctrl) {
//            Topbar.Controller.Asn.viewChangedAsnAction(660, Topbar.Model.Asn.ACTION_ACK);
//            Topbar.Controller.Asn.viewChangedAsnAction(661, Topbar.Model.Asn.ACTION_ACK);
//            Topbar.Controller.Asn.viewChangedAsnAction(662, Topbar.Model.Asn.ACTION_ACK);
//            Topbar.Controller.Asn.viewChangedAsnAction(663, Topbar.Model.Asn.ACTION_ACK);
//            Topbar.Controller.Asn.viewChangedAsnAction(664, Topbar.Model.Asn.ACTION_ACK);
//            Topbar.Controller.Asn.viewChangedAsnAction(665, Topbar.Model.Asn.ACTION_ACK);

              //CaseFile.View.Tree.testTree();

        }
        ,nextTest: 1
        ,onClickBtnTest3: function(event, ctrl) {
            Topbar.Model.Flash.add("Something is done " + Topbar.View.Asn.nextTest++);
        }
        ,onClickBtnTest4: function(event, ctrl) {
            sessionStorage.setItem("AcmAsnList", null);
            Topbar.Model.Flash.reset();
        }

        ,onModelAddedFlashMsg: function(msg) {
            var msgList = Topbar.Model.Flash.getMsgList();
            Topbar.View.Asn.showNewAsn(msgList);
        }
        ,onModelRetrievedAsnList: function(asnList) {
            if (asnList.hasError) {
                //Topbar.Model.Flash.add("Failed to retrieve notifications:" + asnList.errorMsg);
                Topbar.Model.Flash.add("Failed to connect to server");
            } else {
                Topbar.View.Asn.showNewAsn(asnList);
            }
        }
        ,onModelSavedAsn: function(asn) {
            if (asn.hasError) {
                Acm.Dialog.error("Failed to save notification:" + asn.errorMsg);
            }
        }
        ,onModelUpdatedAsnAction: function(asnId, action) {
            if (action.hasError) {
                Acm.Dialog.error("Failed to update notification action:" + action.errorMsg);
            }
        }
        ,onModelUpdatedAsnStatus: function(asnId, status) {
            if (status.hasError) {
                Acm.Dialog.error("Failed to update notification status:" + status.errorMsg);
            }
        }
        ,onModelDeletedAsn: function(asnId) {
            if (asnId.hasError) {
                Acm.Dialog.error("Failed to delete notification:" + asnId.errorMsg);
            }
        }



        ,_asnListNew: []
        ,_asnListOld: []
        ,getAsnListNew: function() {
            return this._asnListNew;
        }
        ,getAsnListOld: function() {
            return this._asnListOld;
        }
        ,isNewStatus: function(status) {
            if (status) {
                if (Topbar.Model.Asn.STATUS_NEW == status) {
                    return true;
                } else if (Topbar.Model.Asn.STATUS_AUTO == status) {
                    return true;
                }
            }
            return false;
        }
        ,buildAsnListNew: function(asnList) {
            this._asnListNew = [];
            if (asnList) {
                for (var i = 0; i < asnList.length; i++) {
                    var asn = asnList[i];
                    if (this.isNewStatus(asn.status)) {
                        this._asnListNew.push(asn);
                    } else {
                        this._asnListOld.push(asn);
                    }
                }
            }
            return this._asnListNew;
        }
        ,getAsnListNewMore: function(asnList) {
            var asnListNewMore = [];
            if (!Acm.isArray(asnList)) {
                return asnListNewMore;
            }

            var asnListNew = this.getAsnListNew();
            for (var i = 0; i < asnList.length; i++) {
                var asn = asnList[i];
                if (this.isNewStatus(asn.status)) {
                    var found = null;
                    for (var j = 0; j < asnListNew.length; j++) {
                        var asnNew = asnListNew[j];
                        if (asn.id == asnNew.id) {
                            found =asn;
                            break;
                        }
                    }
                    if (null == found) {
                        asnListNewMore.push(asn);
                    }
                }
            }

            return asnListNewMore;
        }
        ,getAsnListNewNoLonger: function(asnList) {
            var asnListNewNoLonger = [];
            if (!asnList  || !(asnList instanceof Array)) {
                return asnListNewNoLonger;
            }

            var asnListNew = this.getAsnListNew();
            for (var i = 0; i < asnListNew.length; i++) {
                var asnNew = asnListNew[i];
                var found = null;
                for (var j = 0; j < asnList.length; j++) {
                    var asn = asnList[j];
                    if (this.isNewStatus(asn.status)) {
                        if (asn.id == asnNew.id) {
                            found =asn;
                            break;
                        }
                    }
                }
                if (null == found) {
                    asnListNewNoLonger.push(asnNew);
                }
            }

            return asnListNewNoLonger;
        }


        ,_removeAsnFromPopup: function(asnIdToRemove) {
            //if only one child left, remove whole list
            var $children = this.$divAsnList.children();
            var childCnt = $children.length;
            if (1 >= childCnt) {
                this.closeAsnList();
                return;
            }

            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToRemove == asnId) {
                        $(this).remove();
                    }
                }
            }); //end each
        }
        ,_registerToRemove: function(asnList) {
            for (var i = 0; i < asnList.length; i++) {
                var asn = asnList[i];
                if (asn && asn.id) {
                    Acm.Timer.registerListener(asn.id
                        ,16
                        ,function(asnId) {
                            Topbar.View.Asn._removeAsnFromPopup(asnId);
                            Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                            return false;
                        }
                    );
                }
            }
        }
        ,_updateAsnFromDropdown: function(asnIdToUpdate, action) {
            //if only one child left, remove whole list
            var $children = this.$divAsnList.children();
            var childCnt = $children.length;
            if (1 >= childCnt) {
                this.closeAsnList();
                return;
            }

            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToUpdate == asnId) {
                        var styleRefined = Topbar.Model.Asn.STATUS_NEW;
                        $(this).attr("class", Topbar.View.Asn.ASN_DEFAULT_STYLE + " " + styleRefined);

                        var $btnAck = $(this).find("input[name='ack']");
                        $btnAck.remove();

                        Topbar.Controller.Asn.viewChangedAsnAction(asnId, action);
                    }
                }
            }); //end each
        }
        ,_registerToUpdate: function(asnList) {
            for (var i = 0; i < asnList.length; i++) {
                var asn = asnList[i];
                if (asn && asn.id) {
                    Acm.Timer.registerListener(asn.id
                        ,16
                        ,function(asnId) {
                            Topbar.View.Asn._updateAsnFromDropdown(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                            Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                            return false;
                        }
                    );
                }
            }
        }
        ,showNewAsn: function(asnList) {
            var visibleAsnList = Acm.Object.isVisible(this.$divAsnList);
            var visibleAsnHeader = Acm.Object.isVisible(this.$divAsnList.prev());
            if (!visibleAsnList) {          //no list is shown, popup new ASNs
                var asnListNew = Topbar.View.Asn.buildAsnListNew(asnList);
                if (0 < asnListNew.length) {
                    this.$divAsnList.empty();
                    this.$divAsnList.prev().hide();
                    this.$divAsnList.next().hide();
                    this._buildAsnUi(asnListNew, this.UI_TYPE_POPUP);
                    this.$sectionAsn.fadeIn();
                }

            } else if (!visibleAsnHeader) { //ASN popup is already shown, update new ASN
                var newMore = Topbar.View.Asn.getAsnListNewMore(asnList);
                if (0 < newMore.length) {
                    this._buildAsnUi(newMore, this.UI_TYPE_POPUP);
                }

//                var noLonger = Topbar.View.Asn.getAsnListNewNoLonger(asnList);
//                for (var j = 0; j < noLonger.length; j++) {
//                    var asn = noLonger[j];
//                    if (asn && asn.id) {
//                        this._removeAsnFromPopup(asn.id);
//                        Acm.Timer.removeListener(asn.id);
//                    }
//                } //for j

                Topbar.View.Asn.buildAsnListNew(asnList);

            } else {        //user is viewing ASN list; do nothing
                var newMore = Topbar.View.Asn.getAsnListNewMore(asnList);
                if (0 < newMore.length) {
                    this._buildAsnUi(newMore, this.UI_TYPE_LIST);
                }

//                var noLonger = Topbar.View.Asn.getAsnListNewNoLonger(asnList);
//                for (var j = 0; j < noLonger.length; j++) {
//                    var asn = noLonger[j];
//                    if (asn && asn.id) {
//                        this._updateAsnFromDropdown(asn.id, Acm.goodValue(asn.action, Topbar.Model.Asn.ACTION_EXPIRED));
//                        Acm.Timer.removeListener(asn.id);
//                    }
//                } //for j

                Topbar.View.Asn.buildAsnListNew(asnList);
            }
        }

        ,closeAsnList: function() {
            this.$sectionAsn.fadeOut();
//            this.$divAsnList.empty();
//            this.$divAsnList.prev().hide();
//            this.$divAsnList.next().hide();
        }
        ,UI_TYPE_LIST:  "dropdown"
        ,UI_TYPE_POPUP: "popup"

        ,ASN_DEFAULT_STYLE: "media list-group-item"
        ,_getStyle: function(status, uiType) {
            var style = status;
            return style;
        }

        ,_removeAsnFromUi: function(asnIdToRemove) {
            //if only one child left, remove whole list
            var $children = this.$divAsnList.children();
            var childCnt = $children.length;
            if (1 >= childCnt) {
                this.closeAsnList();
                return;
            }

            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToRemove == asnId) {
                        $(this).remove();
                    }
                }
            }); //end each
        }
        ,_hideAckButtonFromUi: function(asnIdToUpdate, action) {
            var $children = this.$divAsnList.children();
            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToUpdate == asnId) {
                        var styleRefined = Topbar.View.Asn._getStyle(Topbar.Model.Asn.STATUS_UNMARKED, Topbar.View.Asn.UI_TYPE_LIST);
                        $(this).attr("class", Topbar.View.Asn.ASN_DEFAULT_STYLE + " " + styleRefined);

                        var $btnAck = $(this).find("input[name='ack']");
                        $btnAck.remove();

                        Topbar.Controller.Asn.viewChangedAsnAction(asnId, action);
                    }
                }
            }); //end each
        }
        ,_hideStopButtonFromUi: function(asnIdToUpdate, action) {
            var $children = this.$divAsnList.children();
            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToUpdate == asnId) {
                        var styleRefined = Topbar.View.Asn._getStyle(Topbar.Model.Asn.STATUS_UNMARKED, Topbar.View.Asn.UI_TYPE_LIST);
                        $(this).attr("class", Topbar.View.Asn.ASN_DEFAULT_STYLE + " " + styleRefined);

                        var $btnStopAuto = $(this).find("input[name='stopAuto']");
                        $btnStopAuto.prev().remove();
                        $btnStopAuto.remove();

                        Topbar.Controller.Asn.viewChangedAsnAction(asnId, action);
                    }
                }
            }); //end each
        }
        ,_autoCountDown: function(asnIdToCountDown) {
            var countDown = 0;
            var $children = this.$divAsnList.children();
            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToCountDown == asnId) {
                        var $btnStopAuto = $(this).find("input[name='stopAuto'], input[name='closeAuto']");
                        var $spanCountDown = $btnStopAuto.prev().find("span");
                        countDown = parseInt(Acm.goodValue($spanCountDown.text(), "0"));
                        countDown--;
                        $spanCountDown.text(countDown);
                    }
                }
            }); //end each
            return countDown;
        }
        ,_buildAsnUi: function(asnList, uiType) {
            var countTotal = Topbar.Model.Asn.getAsnCount(asnList);
            //for (var i = countTotal-1; i >= 0; i--) {
            for (var i = 0; i < countTotal; i++) {
                var asn = asnList[i];
                if (asn && asn.id) {

                    var isFlash = Acm.isNotEmpty(asn.flash);
                    var isAuto = (Topbar.Model.Asn.STATUS_AUTO == Acm.goodValue(asn.status));

                    var canMark = false;
                    var canDelete = false;
                    var canAck = false;
                    var canClose = false;
                    var canCloseFlash = false;
                    var canStopAuto = false;
                    var canCloseAuto = false;
                    if (isFlash) {
                        canCloseFlash = true;
                    } else if (isAuto) {
                        canMark = true;
                        canDelete = true;
                        canStopAuto = (this.UI_TYPE_LIST == uiType);
                        canCloseAuto = (this.UI_TYPE_POPUP == uiType);
                    } else {
                        canMark = true;
                        canDelete = true;
                        canAck = (this.UI_TYPE_LIST == uiType) && (Topbar.Model.Asn.STATUS_NEW == Acm.goodValue(asn.status));
                        canClose = (this.UI_TYPE_POPUP == uiType) && (Topbar.Model.Asn.STATUS_NEW == Acm.goodValue(asn.status));
                    }
                    var canGo = Acm.isNotEmpty(asn.data);
                    var styleRefined = this._getStyle(asn.status, uiType);

                    var msg = "<div class='" + this.ASN_DEFAULT_STYLE + " "
                            + styleRefined
                            + "><a href=''#'><span class='pull-left thumb-sm text-center'>"
                            + "<i class='fa fa-file fa-2x text-success'></i></span>"
                            + "<span class='media-body block m-b-none'>"
                            + Acm.goodValue(asn.note)
                            + "<br><small class='text-muted'>"
                            + Acm.goodValue(asn.created)
                            + "</small></span></a><input type='hidden' name='asnId' value='"
                            + Acm.goodValue(asn.id)
                            + "' />"
                        ;
                    if (canMark) {
                        msg += "<input type='button' name='mark' value='Mark'/>";
                    }
                    if (canDelete) {
                        msg += "<input type='button' name='delete' value='Delete'/>";
                    }
                    if (canGo) {
                        msg += "<input type='button' name='go' value='Go'/>";
                    }
                    if (canAck) {
                        msg += "<input type='button' name='ack' value='Ack'/>";
                    }
                    if (canClose) {
                        msg += "<input type='button' name='close' value='Close'/>";
                    }
                    if (canCloseFlash) {
                        msg += "<input type='button' name='closeFlash' value='Close'/>";
                    }
                    if (canStopAuto) {
                        msg += "<small class='text-muted'><span>5</span> sec</small>";
                        msg += "<input type='button' name='stopAuto' value='Stop'/>";
                    }
                    if (canCloseAuto) {
                        msg += "<small class='text-muted'><span>5</span> sec</small>";
                        msg += "<input type='button' name='closeAuto' value='Close'/>";
                    }
                    msg += "</div>";

                    $(msg).hide().prependTo(this.$divAsnList)
                        .css('display','block')
                        //.slideDown()
                        //.fadeToggle()
                    ;


                    //register timer handlers
                    if (canAck) {
                        Acm.Timer.registerListener(asn.id
                            ,16
                            ,function(asnId) {
                                Topbar.View.Asn._hideAckButtonFromUi(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                return false;
                            }
                        );
                    }
                    if (canClose) {
                        Acm.Timer.registerListener(asn.id
                            ,16
                            ,function(asnId) {
                                Topbar.View.Asn._removeAsnFromUi(asnId);
                                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                return false;
                            }
                        );
                    }
                    if (canCloseFlash) {
                        Acm.Timer.registerListener(asn.id
                            ,16
                            ,function(asnId) {
                                Topbar.View.Asn._removeAsnFromUi(asnId);
                                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                return false;
                            }
                        );
                    }
                    if (canStopAuto) {
                        Acm.Timer.registerListener(asn.id
                            ,1
                            ,function(asnId) {
                                var countDown = Topbar.View.Asn._autoCountDown(asnId);
                                if (0 < countDown) {
                                    return true;
                                } else {
                                    Topbar.View.Asn._hideStopButtonFromUi(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                    Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                    Topbar.View.Asn._goToResult(asnId);
                                    return false;
                                }
                            }
                        );
                    }
                    if (canCloseAuto) {
                        Acm.Timer.registerListener(asn.id
                            ,1
                            ,function(asnId) {
                                var countDown = Topbar.View.Asn._autoCountDown(asnId);
                                if (0 < countDown) {
                                    return true;
                                } else {
                                    Topbar.View.Asn._removeAsnFromUi(asnId);
                                    Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                                    Topbar.View.Asn._goToResult(asnId);
                                    return false;
                                }
                            }
                        );
                    }
                } // if asn.id
            } //for i

            this.$divAsnList.find("input[name='mark']")       .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnMark(e, this);});
            this.$divAsnList.find("input[name='delete']")     .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnDelete(e, this);});
            this.$divAsnList.find("input[name='go']")         .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnGo(e, this);});
            this.$divAsnList.find("input[name='ack']")        .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnAck(e, this);});
            this.$divAsnList.find("input[name='close']")      .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnClose(e, this);});
            this.$divAsnList.find("input[name='closeFlash']") .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnCloseFlash(e, this);});
            this.$divAsnList.find("input[name='stopAuto']")   .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnStopAuto(e, this);});
            this.$divAsnList.find("input[name='closeAuto']")  .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnCloseAuto(e, this);});
        }

        ,onClickLnkAsn1: function(event, ctrl) {
            this.$divAsnList.empty();
            this.$divAsnList.prev().show();
            this.$divAsnList.next().show();

            if (!Acm.Object.isVisible(this.$divAsnList)) {
                var asnList = Topbar.Model.Asn.getAsnList();
                Topbar.View.Asn.buildAsnListNew(asnList);
                var asnListNew = Topbar.View.Asn.getAsnListNew();
                var asnListOld = Topbar.View.Asn.getAsnListOld();

                Topbar.View.Asn._buildAsnUi(asnListOld, Topbar.View.Asn.UI_TYPE_LIST);
                Topbar.View.Asn.showNewAsn(asnListNew);
                this.$sectionAsn.toggle();


            } else {
                this.$sectionAsn.toggle();
            }
        }
        ,onClickLnkAsn: function(event, ctrl) {
            this.$divAsnList.empty();
            this.$divAsnList.prev().show();
            this.$divAsnList.next().show();

            var asnList = Topbar.Model.Asn.getAsnList();
            this._buildAsnUi(asnList, Topbar.View.Asn.UI_TYPE_LIST);
            this.$sectionAsn.toggle();

            if (Acm.Object.isVisible(this.$divAsnList)) {
                var asnListNew = Topbar.View.Asn.buildAsnListNew(asnList);
            }
        }

        ,onClickBtnGo: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._goToResult(asnId);
            }
        }
        ,_goToResult: function(asnId) {
            if (asnId) {
                var url = App.getContextPath() + url;
                var asnList = Topbar.Model.Asn.getAsnList();
                if (Topbar.Model.Asn.validateAsnList(asnList)) {
                    var asn = Topbar.Model.Asn.findAsn(asnId, asnList);
                    if (Topbar.Model.Asn.validateAsn(asn)) {
                        try {
                            var asnData = JSON.parse(asn.data);
                            if (asnData && asnData.name) {
                                var treeInfo = {};
                                treeInfo.name   = asnData.name;
                                treeInfo.start  = Acm.goodValue(asnData.start, 0);
                                treeInfo.n      = Acm.goodValue(asnData.n, 50);
                                treeInfo.sort   = Acm.goodValue(asnData.sort, null);
                                treeInfo.filter = Acm.goodValue(asnData.filter, null);
                                treeInfo.q      = Acm.goodValue(asnData.q, null);
                                treeInfo.key    = Acm.goodValue(asnData.key, null);
                                treeInfo.objId  = Acm.goodValue(asnData.objId, 0);

                                var treeInfoData = new Acm.Model.SessionData("AcmTreeInfo");
                                treeInfoData.set(treeInfo);

                                var url = App.getContextPath() + asnData.name;
                                var href = window.location.href;
                                if (0 < href.indexOf(url)) {
                                    var rc = Topbar.Controller.Asn.viewSetAsnData(asnData);
                                    if (0 < rc) {
                                        return;
                                    }
                                }

                                url = asnData.name;
                                App.gotoPage(url);
                            }
                        } catch(e) {
                            console.log(e.message);
                        }
                    } //validateAsn(asn)
                } //validateAsnList(asnList)
            }
        }
        ,onClickBtnDelete: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._removeAsnFromUi(asnId);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewDeletedAsn(asnId);
            }
        }
        ,onClickBtnMark: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.Controller.Asn.viewChangedAsnStatus(asnId, Topbar.Model.Asn.STATUS_MARKED);
            }
        }
        ,onClickBtnAck: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._hideAckButtonFromUi(asnId, Topbar.Model.Asn.ACTION_ACK);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
            }
        }
        ,onClickBtnClose: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._removeAsnFromUi(asnId);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
            }
        }
        ,onClickBtnCloseFlash: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._removeAsnFromUi(asnId);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
            }

        }
        ,onClickBtnStopAuto: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._hideStopButtonFromUi(asnId, Topbar.Model.Asn.ACTION_STOPPED);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_STOPPED);
            }
        }
        ,onClickBtnCloseAuto: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._removeAsnFromUi(asnId);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_STOPPED);
            }
        }

        ,_getClickedAsnId: function(ctrl) {
            var $ctrl = $(ctrl);
            var $msg = $ctrl.closest("div.list-group-item");
            var $hidAsnId = $msg.find("input[name='asnId']");
            var asnId = $hidAsnId.val();
            return asnId;
        }


        ,Counter: {
            create: function() {
                this.$ulAsn = $("ul.nav-user");
                this.$spanCntWarning = $('a .count', this.$ulAsn);
                this.$spanCntTotal = $('header .count', this.$ulAsn);
                this.$divAsnList = $('.list-group', this.$ulAsn);

                Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_RETRIEVED_ASN_LIST        ,this.onModelRetrievedAsnList);
                Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_CHANGED_ASN_ACTION         ,this.onViewChangedAsnAction);
                Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_CHANGED_ASN_STATUS         ,this.onViewChangedAsnStatus);

            }
            ,onInitialized: function() {
            }


            ,onModelRetrievedAsnList: function(asnList) {
                if (!asnList.hasError) {
                    Topbar.View.Asn.Counter.updateCounter();
                }
            }
            ,onViewChangedAsnAction: function(asnId, action) {
                Topbar.View.Asn.Counter.updateCounter();
            }
            ,onViewChangedAsnStatus: function(asnId, status) {
                Topbar.View.Asn.Counter.updateCounter();
            }

            ,_lastCountTotal: 0
            ,_lastCountNew: 0
            ,_updateRequestCount: 0
            ,updateCounter: function() {
                if (0 >= Topbar.View.Asn.Counter._updateRequestCount) {
                    Acm.Timer.registerListener("AsnCounter"
                        ,3
                        ,function(name) {
                            Topbar.View.Asn.Counter.doUpdateCounter();
                            Topbar.View.Asn.Counter._updateRequestCount = 0;
                            return false;
                        }
                    );
                }
                Topbar.View.Asn.Counter._updateRequestCount++;
            }
            ,doUpdateCounter: function() {
                var asnList = Topbar.Model.Asn.getAsnList();
                if (Topbar.Model.Asn.validateAsnList(asnList)) {
                    var countTotal = Topbar.Model.Asn.getAsnCount(asnList);
                    var countNew = 0;
                    for (var i = 0; i < countTotal; i++) {
                        var asn = asnList[i];
                        if (Topbar.View.Asn.isNewStatus(Acm.goodValue(asn.status))) {
                            countNew++;
                        }
                    }
                    if (this._lastCountTotal != countTotal || this._lastCountNew != countNew) {
                        Topbar.View.Asn.Counter.setTextSpanCntWarning(countTotal);
                        Topbar.View.Asn.Counter.warnSpanCntWarning(0 < countNew);
                        this._lastCountTotal = countTotal;
                        this._lastCountNew = countNew;
                    }
                }
            }
            ,setTextSpanCntWarning: function(text) {
                this.$spanCntWarning.fadeOut().fadeIn().text(text);
            }
            ,warnSpanCntWarning: function(warn) {
                if (warn) {
                    this.$spanCntWarning.addClass("bg-danger");
                } else {
                    this.$spanCntWarning.removeClass("bg-danger");
                }
            }
            ,setTextSpanCntTotal: function(text) {
                this.$spanCntTotal.text(text);
            }
        }
    } //Asn
};

