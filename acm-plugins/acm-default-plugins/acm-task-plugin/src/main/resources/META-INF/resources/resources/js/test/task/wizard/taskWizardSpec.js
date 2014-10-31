/**
 * Test script for TaskWizard
 *
 * @author jwu
 */
describe("TaskWizard", function()
{
    it("TaskWizard.create", function() {
        spyOn(TaskWizard.Object,   "create");
        spyOn(TaskWizard.Event,    "create");
        spyOn(TaskWizard.Page,     "create");
        spyOn(TaskWizard.Rule,     "create");
        spyOn(TaskWizard.Service,  "create");
        spyOn(TaskWizard.Callback, "create");
        spyOn(TaskWizard.Event,    "onPostInit");
        TaskWizard.create();
        expect(TaskWizard.Object  .create).toHaveBeenCalled();
        expect(TaskWizard.Event   .create).toHaveBeenCalled();
        expect(TaskWizard.Page    .create).toHaveBeenCalled();
        expect(TaskWizard.Rule    .create).toHaveBeenCalled();
        expect(TaskWizard.Service .create).toHaveBeenCalled();
        expect(TaskWizard.Callback.create).toHaveBeenCalled();
        expect(TaskWizard.Event   .onPostInit).toHaveBeenCalled();
    });

});
