/**
 * CaseFile is namespace component for CaseFile
 *
 * @author jwu
 */
var CaseFile = CaseFile || {
    initialize: function() {
        CaseFile.cachePage = new Acm.CacheFifo(2);
        CaseFile.cacheCaseFile = new Acm.CacheFifo(3);
        CaseFile.cacheCaseEvents = new Acm.CacheFifo(3);

        CaseFile.Object.initialize();
        CaseFile.Event.initialize();
        CaseFile.Page.initialize();
        CaseFile.Rule.initialize();
        CaseFile.Service.initialize();
        CaseFile.Callback.initialize();

        Acm.deferred(CaseFile.Event.onPostInit);
    }


    ,cachePage: null
    ,cacheCaseFile: null
    ,cacheCaseEvents: null

    ,_caseFileId: 0
    ,getCaseFileId : function() {
        return this._caseFileId;
    }
    ,setCaseFileId : function(id) {
        this._caseFileId = id;
    }
    ,getCaseFile: function() {
        if (0 >= this._caseFileId) {
            return null;
        }
        return this.cacheCaseFile.get(this._caseFileId);
    }

    ,PERSON_SUBTABLE_TITLE_DEVICES:       "Communication Devices"
    ,PERSON_SUBTABLE_TITLE_ORGANIZATIONS: "Organizations"
    ,PERSON_SUBTABLE_TITLE_LOCATIONS:     "Locations"
    ,PERSON_SUBTABLE_TITLE_ALIASES:       "Aliases"
    ,_personTypes : ["Witness", "Subject", "Spouse"]
    ,getPersonTypes : function() {
        return this._personTypes;
    }

    ,_personTitles : ['Mr.', 'Mrs.', 'Ms.', 'Dr.']
    ,getPersonTitles : function() {
        return this._personTitles;
    }

    ,_deviceTypes : ['Phone', 'Email']
    ,getDeviceTypes : function() {
        return this._deviceTypes;
    }

    ,_organizationTypes : ['org', 'gov', 'com']
    ,getOrganizationTypes : function() {
        return this._organizationTypes;
    }

    ,_locationTypes : ['Home', 'Office', 'Hotel']
    ,getLocationTypes : function() {
        return this._locationTypes;
    }

    ,_aliasTypes : ['Nick Name', 'Other Name']
    ,getAliasTypes : function() {
        return this._aliasTypes;
    }
};


