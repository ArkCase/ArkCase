/**
 * Test script for ACM.Dispatcher
 *
 * @author jwu
 */
describe("ACM.Dispatcher", function()
{
    beforeEach(function() {
    });

    it("Create/remove event listeners", function() {
        expect(ACM.Dispatcher.numOfListeners("someEvent")).toBe(0);
        expect(ACM.Dispatcher.isListening   ("someEvent", onSomeEventHandler)).toBe(false);

        ACM.Dispatcher.addEventListener     ("someEvent", onSomeEventHandler);
        expect(ACM.Dispatcher.numOfListeners("someEvent")).toBe(1);
        expect(ACM.Dispatcher.isListening   ("someEvent", onSomeEventHandler)).toBe(true);

        ACM.Dispatcher.addEventListener     ("anotherEvent", onAnotherEventHandler);
        expect(ACM.Dispatcher.numOfListeners("anotherEvent")).toBe(1);
        expect(ACM.Dispatcher.isListening   ("anotherEvent", onAnotherEventHandler)).toBe(true);

        ACM.Dispatcher.removeEventListener  ("someEvent", onSomeEventHandler);
        expect(ACM.Dispatcher.numOfListeners("someEvent")).toBe(0);
        expect(ACM.Dispatcher.isListening   ("someEvent", onSomeEventHandler)).toBe(false);
    });

    var onSomeEventHandler    = function(event, data) {};
    var onAnotherEventHandler = function(event, data) {};

});
