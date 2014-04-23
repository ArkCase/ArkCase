/**
 * Test script for Complaint
 *
 * @author jwu
 */
describe("Complaint", function()
{
    it("Complaint.Initialize", function() {
        spyOn(Complaint.Object,   "initialize");
        spyOn(Complaint.Event,    "initialize");
        spyOn(Complaint.Page,     "initialize");
        spyOn(Complaint.Rule,     "initialize");
        spyOn(Complaint.Service,  "initialize");
        spyOn(Complaint.Callback, "initialize");
        spyOn(Complaint.Event,    "onPostInit");
        Complaint.initialize();
        expect(Complaint.Object  .initialize).toHaveBeenCalled();
        expect(Complaint.Event   .initialize).toHaveBeenCalled();
        expect(Complaint.Page    .initialize).toHaveBeenCalled();
        expect(Complaint.Rule    .initialize).toHaveBeenCalled();
        expect(Complaint.Service .initialize).toHaveBeenCalled();
        expect(Complaint.Callback.initialize).toHaveBeenCalled();
        expect(Complaint.Event   .onPostInit).toHaveBeenCalled();
    });

});
