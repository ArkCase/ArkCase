/**
 * Profile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Profile.Service = {
    create : function() {
        if (this.Info.create) {Profile.Service.Info.create();}
    }
    ,initialize: function() {
        if (this.Info.initialize) {Profile.Service.Info.initialize();}
    }

    ,Info: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,retrieveProfileInfo: function() {
            var info = {};
            Profile.Model.Info.setProfileInfo(info);
            Profile.Controller.Info.onModelChangedProfileInfo(info);
        }

        ,updateProfileInfo: function(info) {
            Profile.Controller.Info.onModelChangedProfileInfoSaved(info);
        }






        ,API_TYPEAHEAD_SUGGESTION_BEGIN_      : "/api/latest/plugin/search/quickSearch?q=*"
        ,API_TYPEAHEAD_SUGGESTION_END         : "*&start=0&n=16"

        ,getTypeAheadUrl: function(query) {
            var url = App.getContextPath() + this.API_TYPEAHEAD_SUGGESTION_BEGIN_
                + query
                + this.API_TYPEAHEAD_SUGGESTION_END;
            return url;
        }

        ,_validateSuggestionData: function(data) {
            if (Acm.isEmpty(data.responseHeader) || Acm.isEmpty(data.response)) {
                return false;
            }
            if (Acm.isEmpty(data.response.numFound) || Acm.isEmpty(data.response.start) || Acm.isEmpty(data.response.docs)) {
                return false;
            }
            return true;
        }
        ,retrieveSuggestion: function(query, process){
            $.ajax({
                url: Profile.Service.Suggestion.getTypeAheadUrl(query)
                ,cache: false
                ,success: function(data){
                    if (Profile.Service.Suggestion._validateSuggestionData(data)) {
                        var docs = data.response.docs;
                        Profile.Model.Suggestion.buildSuggestion(query, docs);
                        Profile.Controller.Suggestion.onModelChangeSuggestion(process);
                    }
                }
            });
        }
    }


};

