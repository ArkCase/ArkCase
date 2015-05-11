/**
 * IssueCollector.Model
 *
 * @author manoj.dhungana
 */
IssueCollector.Model = {
    create : function() {
        if (IssueCollector.Model.IssueCollector.create)          {IssueCollector.Model.IssueCollector.create();}
        if (IssueCollector.Model.MicroData.create)               {IssueCollector.Model.MicroData.create();}
    }
    ,onInitialized: function() {
        if (IssueCollector.Model.IssueCollector.onInitiliazed)   {IssueCollector.Model.IssueCollector.onInitiliazed();}
        if (IssueCollector.Model.MicroData.onInitiliazed)        {IssueCollector.Model.MicroData.onInitiliazed();}
    }

    ,MicroData: {
        create : function() {
            this.issueCollectorFlag = Acm.Object.MicroData.get("issueCollectorFlag");
        }
    }

    ,IssueCollector: {
        create: function(){

        }
        ,onInitiliazed: function(){
            this.displayIssueCollectorTab(Acm.goodValue(IssueCollector.Model.MicroData.issueCollectorFlag));
        }

        ,displayIssueCollectorTab: function(issueCollectorFlag){
            if(Acm.isNotEmpty(issueCollectorFlag) && "true" == issueCollectorFlag){
                IssueCollector.Service.getIssueCollector(issueCollectorFlag);
            }
        }
    }

};




