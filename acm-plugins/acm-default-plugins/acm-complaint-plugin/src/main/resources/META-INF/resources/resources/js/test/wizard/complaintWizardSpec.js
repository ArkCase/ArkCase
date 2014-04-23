/**
 * Test script for ComplaintWizard
 *
 * @author jwu
 */
describe("ComplaintWizard", function()
{
    it("ComplaintWizard.Initialize", function() {
        spyOn(ComplaintWizard.Object,   "initialize");
        spyOn(ComplaintWizard.Event,    "initialize");
        spyOn(ComplaintWizard.Page,     "initialize");
        spyOn(ComplaintWizard.Rule,     "initialize");
        spyOn(ComplaintWizard.Service,  "initialize");
        spyOn(ComplaintWizard.Callback, "initialize");
        spyOn(ComplaintWizard.Event,    "onPostInit");
        ComplaintWizard.initialize();
        expect(ComplaintWizard.Object  .initialize).toHaveBeenCalled();
        expect(ComplaintWizard.Event   .initialize).toHaveBeenCalled();
        expect(ComplaintWizard.Page    .initialize).toHaveBeenCalled();
        expect(ComplaintWizard.Rule    .initialize).toHaveBeenCalled();
        expect(ComplaintWizard.Service .initialize).toHaveBeenCalled();
        expect(ComplaintWizard.Callback.initialize).toHaveBeenCalled();
        expect(ComplaintWizard.Event   .onPostInit).toHaveBeenCalled();
    });

});
