/**
 * Test script for TaskWizard
 *
 * @author jwu
 */
describe("TaskWizard", function()
{
    it("TaskWizard.Initialize", function() {
        spyOn(TaskWizard.Object,   "initialize");
        spyOn(TaskWizard.Event,    "initialize");
        spyOn(TaskWizard.Page,     "initialize");
        spyOn(TaskWizard.Rule,     "initialize");
        spyOn(TaskWizard.Service,  "initialize");
        spyOn(TaskWizard.Callback, "initialize");
        spyOn(TaskWizard.Event,    "onPostInit");
        TaskWizard.initialize();
        expect(TaskWizard.Object  .initialize).toHaveBeenCalled();
        expect(TaskWizard.Event   .initialize).toHaveBeenCalled();
        expect(TaskWizard.Page    .initialize).toHaveBeenCalled();
        expect(TaskWizard.Rule    .initialize).toHaveBeenCalled();
        expect(TaskWizard.Service .initialize).toHaveBeenCalled();
        expect(TaskWizard.Callback.initialize).toHaveBeenCalled();
        expect(TaskWizard.Event   .onPostInit).toHaveBeenCalled();
    });

});
