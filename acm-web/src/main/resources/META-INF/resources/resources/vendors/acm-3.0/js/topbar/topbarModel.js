/**
 * Topbar.Model
 *
 * @author jwu
 */
Topbar.Model = {
    create : function() {
        if (Topbar.Model.QuickSearch.create) {Topbar.Model.QuickSearch.create();}
        if (Topbar.Model.Suggestion.create)  {Topbar.Model.Suggestion.create();}
        if (Topbar.Model.Asn.create)         {Topbar.Model.Asn.create();}
        if (Topbar.Model.Flash.create)       {Topbar.Model.Flash.create();}
    }
    ,onInitialized: function() {
        if (Topbar.Model.QuickSearch.onInitialized) {Topbar.Model.QuickSearch.onInitialized();}
        if (Topbar.Model.Suggestion.onInitialized)  {Topbar.Model.Suggestion.onInitialized();}
        if (Topbar.Model.Asn.onInitialized)         {Topbar.Model.Asn.onInitialized();}
        if (Topbar.Model.Flash.onInitialized)       {Topbar.Model.Flash.onInitialized();}
    }

    ,QuickSearch: {
        create: function() {
            this._quickSearchTerm = new Acm.Model.SessionData("AcmQuickSearchTerm");

            Acm.Dispatcher.addEventListener(Topbar.Controller.QuickSearch.VIEW_CHANGED_QUICK_SEARCH_TERM, this.onViewChangedQuickSearchTerm);
        }
        ,onInitialized: function() {
        }
        ,getQuickSearchTerm: function() {
            return this._quickSearchTerm.get();
        }
        ,setQuickSearchTerm: function(term) {
            this._quickSearchTerm.set(term);
        }

        ,onViewChangedQuickSearchTerm: function(term) {
            Topbar.Model.QuickSearch.setQuickSearchTerm(term);
        }
    }

    ,Suggestion: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,_ctrObjs: {}
        ,_ctrKeys: []

        ,getKeys: function() {
            return this._ctrKeys;
        }
        ,getObjects: function() {
            return this._ctrObjs;
        }
        ,getObject: function(key) {
            return this._ctrObjs[key];
        }
        ,buildSuggestion: function(query, data) {
            Topbar.Model.Suggestion._ctrObjs = {};
            Topbar.Model.Suggestion._ctrKeys = [];

            _.each( data, function(item, ix, list){
                Topbar.Model.Suggestion._ctrKeys.push( item.name );
                Topbar.Model.Suggestion._ctrObjs[ item.name ] = item;
            });
        }

    }

    ,Asn: {
        create : function() {
            this._asnListData = new Acm.Model.SessionData("AcmAsnList");

            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_CHANGED_ASN_ACTION,this.onViewChangedAsnAction);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_CHANGED_ASN_STATUS,this.onViewChangedAsnStatus);
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_DELETED_ASN       ,this.onViewDeletedAsn);
        }
        ,onInitialized: function() {
            var asnList = Topbar.Model.Asn.getAsnList();
            if (Topbar.Model.Asn.validateAsnList(asnList)) {
                Topbar.Controller.Asn.modelRetrievedAsnList(asnList);
            }

            Acm.Timer.startWorker(App.getContextPath() + "/resources/js/acmTimer.js");
//            Acm.Timer.registerListener("AsnWatch"
//                ,16
//                ,function() {
//                    Topbar.Service.Asn.retrieveAsnList(App.getUserName());
//                    return true;
//                }
//            );
        }

        ,STATUS_AUTO     : "Auto"
        ,STATUS_NEW      : "New"
        ,STATUS_UNMARKED : "Unmarked"
        ,STATUS_MARKED   : "Marked"
        ,STATUS_DELETED  : "Deleted"

        ,ACTION_ACK      : "Ack"
        ,ACTION_EXPIRED  : "Expired"
        ,ACTION_STOPPED  : "Stopped"
        ,ACTION_EXECUTED : "Executed"

        ,_asnListData: null
        ,getAsnList: function() {
            return this._asnListData.get();
        }
        ,setAsnList: function(asnList) {
            this._asnListData.set(asnList);
        }
        ,getAsnCount: function(asnList) {
            return (Acm.isArray(asnList))? asnList.length : 0;
        }
        ,setAsn: function(asn) {
            if (this.validateAsn(asn)) {
                var asnList = this.getAsnList();
                if (!asnList) {
                    asnList = [];
                }

                var found = -1;
                for (var i = 0; i < asnList.length; i++) {
                    if (asn.id == asnList[i].id) {
                        found = i;
                        break;
                    }
                }
                if (0 <= found) {
                    asnList[found] = asn;
                } else {
                    asnList.push(asn);
                }

                this.setAsnList(asnList);
            }
        }
        ,findAsn: function(asnId, asnList) {
            var found = null;
            if (asnList) {
                for (var i = 0; i < asnList.length; i++) {
                    var asn = asnList[i];
                    if (asn && asn.id) {
                        if (asn.id == asnId) {
                            found = asn;
                            break;
                        }
                    }
                }
            }
            return found;
        }

        ,validateAsnList: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }

        ,validateAsn: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.note)) {
                return false;
            }
            return true;
        }


        ,onViewChangedAsnAction: function(asnId, action) {
            if (0 < asnId) {
                Topbar.Service.Asn.updateAsnAction(asnId, action);
            }
        }
        ,onViewChangedAsnStatus: function(asnId, status) {
            if (0 < asnId) {
                Topbar.Service.Asn.updateAsnStatus(asnId, status);
            }
        }
        ,onViewDeletedAsn: function(asnId) {
            if (0 < asnId) {
                Topbar.Service.Asn.deleteAsn(asnId);
            }
        }


    } //Asn

    ,Flash: {
        create : function() {
        }
        ,onInitialized: function() {
            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_CHANGED_ASN_ACTION,this.onViewChangedAsnAction);
        }

        ,onViewChangedAsnAction: function(asnId, action) {
            if (0 > asnId) { //negative id is used for Flash
                Topbar.Model.Flash.removeMsg(asnId);
            }
        }

        ,_msgList: []
        ,_nextId: -1
        ,getMsgList: function() {
            return this._msgList;
        }
        ,reset: function() {
            this._msgList = [];
        }
        ,addMsg: function(msg) {
            var n = {};
            if (0 >= this._msgList.length) {
                this._nextId = -1;
            }
            //we are borrowing ASN data structure so that it can share UI component. Use negative ID to avoid collision with real ASN
            n.id = this._nextId--;
            n.status = Topbar.Model.Asn.STATUS_NEW;
            n.note  = msg;
            n.flash = true;
            this._msgList.push(n);
        }
        ,removeMsg: function(id) {
            for (var i = 0; i < this._msgList.length; i++) {
                if (this._msgList[i].id == id) {
                    this._msgList.splice(i, 1);
                    break;
                }
            }
        }
        ,add: function(msg) {
            this.addMsg(msg);
            Topbar.Controller.Flash.modelAddedFlashMsg(msg);
        }
    }

};

