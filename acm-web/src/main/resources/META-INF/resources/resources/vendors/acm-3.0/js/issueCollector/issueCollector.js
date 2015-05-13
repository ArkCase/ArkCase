/**
 * IssueCollector serves as namespace for IssueCollector
 *
 * @author manoj.dhungana
 */

var IssueCollector = IssueCollector || {
    create: function () {
        if (IssueCollector.Model.create)              IssueCollector.Model.create();
        if (IssueCollector.View.create)               IssueCollector.View.create();
        if (IssueCollector.Controller.create)         IssueCollector.Controller.create();
    }
    , onInitialized: function () {
        if (IssueCollector.Model.onInitialized)       IssueCollector.Model.onInitialized();
        if (IssueCollector.View.onInitialized)        IssueCollector.View.onInitialized();
        if (IssueCollector.Controller.onInitialized)  IssueCollector.Controller.onInitialized();
    }
}
