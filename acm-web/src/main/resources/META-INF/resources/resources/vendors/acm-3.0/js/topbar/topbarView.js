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

            this.$formSearch.on("submit", function() {Topbar.View.QuickSearch.onSubmitFormSearch(this);});
            this.$btnSearch .on("click", function(e) {Topbar.View.QuickSearch.onClickBtnSearch(e, this);});

            this.$formSearch.attr("method", "get");
            var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
            this.setActionFormSearch(term);
        }
        ,onInitialized: function() {
        }

        ,onClickBtnSearch : function(event, ctrl) {
            var term = this.getValueEdtSearch();
            this.setActionFormSearch(term);
            //event.preventDefault();
        }

        ,onSubmitFormSearch : function(ctrl) {
            var term = this.getValueEdtSearch();
            Topbar.Controller.QuickSearch.viewChangedQuickSearchTerm(term);
            return false;
        }

        ,setActionFormSearch: function(term) {
            var url = App.getContextPath() + "/plugin/search"
            if (Acm.isNotEmpty(term)) {
                url += "?q=" + term;
            }
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
                            + '<div class="title">' + Acm.goodValue(ctr.title_t) + '</div>'
                            + '<div class="identifier">' + Acm.goodValue(ctr.name) + ' ('+ Acm.goodValue(ctr.object_type_s) + ')' + '</div>'
                            + '<div class="author">By ' + ctr.author  + ' on '+ Acm.getDateTimeFromDatetime(ctr.last_modified) + '</div>'
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
            Topbar.Service.Asn.retrieveAsnList(App.getUserName());
        }
        ,onClickBtnTest3: function(event, ctrl) {
            Topbar.Service.Asn.retrieveAsnList(App.getUserName());
        }
        ,onClickBtnTest4: function(event, ctrl) {
            sessionStorage.setItem("AcmAsnList", null);
        }


        ,onModelRetrievedAsnList: function(asnList) {
            if (asnList.hasError) {
                Acm.Dialog.error("Failed to retrieve notifications:" + asnList.errorMsg);
            } else {
                Topbar.View.Asn.showAsnList(asnList);
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
        ,getAsnListNew: function() {
            return this._asnListNew;
        }
        ,buildAsnListNew: function(asnList) {
            this._asnListNew = [];
            if (asnList) {
                for (var i = 0; i < asnList.length; i++) {
                    var asn = asnList[i];
                    if (asn.action && Topbar.Model.Asn.ACTION_NEW == asn.action) {
                        this._asnListNew.push(asn);
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
                if (asn.action && Topbar.Model.Asn.ACTION_NEW == asn.action) {
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
                    if (asn.action && "New" == asn.action) {
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
                        $(this).attr("class", Topbar.View.Asn.ASN_DEFAULT_STYLE + " " + action);
                        var $btnAck = $(this).find("input[name='ack']");
                        $btnAck.remove();
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
        ,showAsnList: function(asnList) {
            var visibleAsnList = Acm.Object.isVisible(this.$divAsnList);
            var visibleAsnHeader = Acm.Object.isVisible(this.$divAsnList.prev());
            if (!visibleAsnList) {          //no list is shown, popup new ASNs
                var asnListNew = Topbar.View.Asn.buildAsnListNew(asnList);
                if (0 < asnListNew.length) {
                    this.$divAsnList.empty();
                    this.$divAsnList.prev().hide();
                    this.$divAsnList.next().hide();
                    //this._buildAsnListUiDropdown(asnListNew);
                    this._buildAsnListUiPopup(asnListNew);
                    //this._registerToRemove(asnListNew);
                    this.$sectionAsn.fadeIn();
                }

            } else if (!visibleAsnHeader) { //ASN popup is already shown, update new ASN
                var newMore = Topbar.View.Asn.getAsnListNewMore(asnList);
                if (0 < newMore.length) {
                    this._buildAsnListUiPopup(newMore);
                    this._registerToRemove(newMore);
                }

                var noLonger = Topbar.View.Asn.getAsnListNewNoLonger(asnList);
                for (var j = 0; j < noLonger.length; j++) {
                    var asn = noLonger[j];
                    if (asn && asn.id) {
                        this._removeAsnFromPopup(asn.id);
                        Acm.Timer.removeListener(asn.id);
                    }
                } //for j

                Topbar.View.Asn.buildAsnListNew(asnList);

            } else {        //user is viewing ASN list; do nothing
                var newMore = Topbar.View.Asn.getAsnListNewMore(asnList);
                if (0 < newMore.length) {
                    this._buildAsnListUiDropdown(newMore);
                    this._registerToUpdate(newMore);
                }

                var noLonger = Topbar.View.Asn.getAsnListNewNoLonger(asnList);
                for (var j = 0; j < noLonger.length; j++) {
                    var asn = noLonger[j];
                    if (asn && asn.id) {
                        this._updateAsnFromDropdown(asn.id, asn.action);
                        Acm.Timer.removeListener(asn.id);
                    }
                } //for j

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
        ,UI_TYPE_LOCAL: "local"
        ,ASN_DEFAULT_STYLE: "media list-group-item"
        ,_buildAsnListUiDropdown: function(asnList) {
            this._buildAsnListUi(asnList, this.UI_TYPE_LIST);
        }
        ,_buildAsnListUiPopup: function(asnList) {
            this._buildAsnListUi(asnList, this.UI_TYPE_POPUP);
        }
        ,_buildAsnListUiLocal: function(asnList) {
            this._buildAsnListUi(asnList, this.UI_TYPE_LOCAL);
        }
        ,_getStyle: function(status, action) {
            var style = Topbar.Model.Asn.STATUS_NEW;
        }
        ,_buildAsnListUi: function(asnList, type) {
            var countTotal = Topbar.Model.Asn.getAsnCount(asnList);
            //for (var i = countTotal-1; i >= 0; i--) {
            for (var i = 0; i < countTotal; i++) {
                var asn = asnList[i];

                var canClose = ("local" == type)
                    || (("popup" == type) && ("New" == Acm.goodValue(asn.action)));

                var canMark = ("local" != type);
                var canDelete = ("local" != type);
                var hasResult = Acm.isNotEmpty(asn.data);
                var styleRefined = asn.status;

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
                        + "<input type='button' name='markAsRead' value='Mark'/>"
                        + "<input type='button' name='delete' value='Delete'/>"
                        + "<input type='button' name='result' value='Result'/>"
                    ;

                if ("New" == Acm.goodValue(asn.action)) {
                    msg += "<input type='button' name='ack' value='Ack'/>";
                    msg += "<input type='button' name='close' value='AckClose'/>";
                }
                msg += "</div>";

                $(msg).hide().prependTo(this.$divAsnList)
                    .css('display','block')
                    //.slideDown()
                    //.fadeToggle()
                ;
            } //for i

            this.$divAsnList.find("input[name='markAsRead']").unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnMarkAsRead(e, this);});
            this.$divAsnList.find("input[name='delete']")    .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnDelete(e, this);});
            this.$divAsnList.find("input[name='result']")    .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnResult(e, this);});
            this.$divAsnList.find("input[name='ack']")       .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnAck(e, this);});
            this.$divAsnList.find("input[name='close']")     .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnClose(e, this);});
        }

        ,onClickLnkAsn: function(event, ctrl) {
            this.$divAsnList.empty();
            this.$divAsnList.prev().show();
            this.$divAsnList.next().show();

            var asnList = Topbar.Model.Asn.getAsnList();
            this._buildAsnListUiDropdown(asnList);
            this.$sectionAsn.toggle();

            if (Acm.Object.isVisible(this.$divAsnList)) {
                var asnListNew = Topbar.View.Asn.buildAsnListNew(asnList);
            }
        }
        ,onClickBtnResult: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                alert("onClickBtnMarkAsRead, asnId=" + asnId);
            }
        }
        ,onClickBtnDelete: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._removeAsnFromPopup(asnId);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewDeletedAsn(asnId);
            }
        }
        ,onClickBtnMarkAsRead: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.Controller.Asn.viewChangedAsnStatus(asnId, Topbar.Model.Asn.STATUS_READ);
            }
        }
        ,onClickBtnAck: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._updateAsnFromDropdown(asnId, Topbar.Model.Asn.ACTION_ACK);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
            }
        }
        ,onClickBtnClose: function(event, ctrl) {
            var asnId = Topbar.View.Asn._getClickedAsnId(ctrl);
            if (asnId) {
                Topbar.View.Asn._removeAsnFromPopup(asnId);
                Acm.Timer.removeListener(asnId);
                Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
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

            }
            ,onInitialized: function() {
            }


            ,onModelRetrievedAsnList: function(asnList) {
                if (!asnList.hasError) {
                    Topbar.View.Asn.Counter.updateAsnCount(asnList);
                }
            }
            ,onViewChangedAsnAction: function(asnId, action) {
                var asnList = Topbar.Model.Asn.getAsnList();
                if (Topbar.Model.Asn.validateAsnList(asnList)) {
                    Topbar.View.Asn.Counter.updateAsnCount(asnList);
                }
            }


            ,updateAsnCount: function(asnList) {
                var countTotal = Topbar.Model.Asn.getAsnCount(asnList);
                var countNew = 0;
                for (var i = 0; i < countTotal; i++) {
                    var asn = asnList[i];
                    if ("New" == Acm.goodValue(asn.action)) {
                        countNew++;
                    }
                }
                this.setTextSpanCntWarning(countTotal);
                this.warnSpanCntWarning(0 < countNew);
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

