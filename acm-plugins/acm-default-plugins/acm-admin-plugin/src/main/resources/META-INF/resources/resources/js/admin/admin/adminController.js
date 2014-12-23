/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Controller = Admin.Controller || {
    create: function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES                 : "admin-model-retrieved-correspondence-templates"              //param : templatesList

    ,MODEL_UPDATED_ACCESS_CONTROL                             : "access-control-updated"                                      //param : accessControlList

    ,modelRetrievedCorrespondenceTemplates : function(templatesList) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES, templatesList);
    }

    ,modelUpdatedAccessControl : function(accessControlList){
        Acm.Dispatcher.fireEvent(this.MODEL_UPDATED_ACCESS_CONTROL, accessControlList);
    }
}