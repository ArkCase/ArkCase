/**
 * CaseFile is namespace component for CaseFile
 *
 * @author jwu
 */
var CaseFile = CaseFile || {
    create: function() {
        CaseFile.cachePage = new Acm.Model.CacheFifo(2);
        CaseFile.cacheCaseFile = new Acm.Model.CacheFifo(3);
        CaseFile.cacheCaseEvents = new Acm.Model.CacheFifo(3);

        CaseFile.Object.create();
        CaseFile.Event.create();
        CaseFile.Page.create();
        CaseFile.Rule.create();
        CaseFile.Service.create();
        CaseFile.Callback.create();

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

    ,_personTypes : ['Complaintant','Subject','Witness','Wrongdoer','Other', 'Initiator']
    ,getPersonTypes : function() {
        return this._personTypes;
    }

    ,_personTitles : ['Mr', 'Mrs', 'Ms', 'Miss']
    ,getPersonTitles : function() {
        return this._personTitles;
    }

    ,_deviceTypes : ['Home phone', 'Office phone', 'Cell phone', 'Pager',
        'Email','Instant messenger', 'Social media','Website','Blog']
    ,getDeviceTypes : function() {
        return this._deviceTypes;
    }

    ,_organizationTypes : ['Non-profit','Government','Corporation']
    ,getOrganizationTypes : function() {
        return this._organizationTypes;
    }

    ,_locationTypes : ['Business' , 'Home']
    ,getLocationTypes : function() {
        return this._locationTypes;
    }

    ,_aliasTypes : ['FKA' , 'Married']
    ,getAliasTypes : function() {
        return this._aliasTypes;
    }

};


