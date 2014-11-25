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
    }
    ,initialize: function() {
        if (Topbar.Model.QuickSearch.initialize) {Topbar.Model.QuickSearch.initialize();}
        if (Topbar.Model.Suggestion.initialize)  {Topbar.Model.Suggestion.initialize();}
        if (Topbar.Model.Asn.initialize)         {Topbar.Model.Asn.initialize();}
    }

    ,QuickSearch: {
        create: function() {
            this._quickSearchTerm = new Acm.Model.SessionData("AcmQuickSearchTerm");

            Acm.Dispatcher.addEventListener(Topbar.Controller.QuickSearch.VIEW_CHANGED_QUICK_SEARCH_TERM, this.onViewChangedQuickSearchTerm);
        }
        ,initialize: function() {
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
        ,initialize: function() {
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

        }
        ,initialize: function() {
        }

        ,STATUS_READ    : "Read"
        ,STATUS_UNREAD  : "Unread"
        ,STATUS_DELETED : "Deleted"

        ,ACTION_NEW     : "New"
        ,ACTION_ACK     : "Ack"
        ,ACTION_EXPIRED : "Expired"

        ,_asnListData: null
        ,getAsnList: function() {
            return this._asnListData.get();
        }
        ,setAsnList: function(asnList) {
            this._asnListData.set(asnList);
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

        ,_asnListNew: []
        ,getAsnListNew: function() {
            return this._asnListNew;
        }
//    ,setAsnListNew: function(asnListNew) {
//        this._asnListNew = asnListNew;
//    }
        ,buildAsnListNew: function(asnList) {
            this._asnListNew = [];
            if (asnList) {
                for (var i = 0; i < asnList.length; i++) {
                    var asn = asnList[i];
                    if (asn.action && "New" == asn.action) {
                        this._asnListNew.push(asn);
                    }
                }
            }
            return this._asnListNew;
        }
        ,getAsnListNewMore: function(asnList) {
            var asnListNewMore = [];
            if (!asnList  || !(asnList instanceof Array)) {
                return asnListNewMore;
            }

            var asnListNew = this.getAsnListNew();
            for (var i = 0; i < asnList.length; i++) {
                var asn = asnList[i];
                if (asn.action && "New" == asn.action) {
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


        ,ctrlRetrieveAsnList: function(user) {
            Topbar.Service.Asn.retrieveAsnList(user);
        }

        ,onViewChangedAsnAction: function(asnId, action) {
            Topbar.Service.Asn.updateAsnAction(asnId, action);
        }

    } //Asn

};

