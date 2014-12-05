/**
 * Test script for Topbar.Model
 *
 * @author jwu
 */
describe("Topbar.Model.Asn", function()
{
    beforeEach(function() {
        Topbar.Model.Asn.create();
    });

    it("Topbar.Model.Asn: getAsnList/setAsnList", function() {
        Topbar.Model.Asn.setAsnList([{"id":1, "note":"some notification"}, {"id":2, "note":"other notification"}]);
        expect(Topbar.Model.Asn.getAsnList()).toEqual([{"id":1, "note":"some notification"}, {"id":2, "note":"other notification"}]);

        Topbar.Model.Asn.setAsnList(null);
        expect(Topbar.Model.Asn.getAsnList()).toEqual(null);

        Topbar.Model.Asn.setAsnList([]);
        expect(Topbar.Model.Asn.getAsnList()).toEqual([]);
    });

    //Jasmine bug? It does not take 'findAsn', but 'find Asn' is OK in the description
    it("Topbar.Model.Asn: find Asn", function() {
        var asnList = [{"id":1, "note":"some notification"}, {"id":2, "note":"other notification"}];

        expect(Topbar.Model.Asn.findAsn(1, asnList)).toEqual({"id":1, "note":"some notification"});
        expect(Topbar.Model.Asn.findAsn(2, asnList)).toEqual({"id":2, "note":"other notification"});
        expect(Topbar.Model.Asn.findAsn(3, asnList)).toEqual(null);
        expect(Topbar.Model.Asn.findAsn(1, null)).toEqual(null);
    });


    it("Topbar.Model.Asn: AsnListNew tests", function() {
        var asnList = [
            {"id": 1
                , "note":"notification1"
                , "action":"New"
            }
            ,{"id": 2
                , "note":"notification2"
                , "action":"Ack"
            }
            ,{"id": 3
                , "note":"notification3"
                , "action":"New"
            }
        ];
        var asnListNew = [
            {"id": 1
                , "note":"notification1"
                , "action":"New"
            }
            ,{"id": 3
                , "note":"notification3"
                , "action":"New"
            }
        ];
        Topbar.Model.Asn.setAsnList(asnList);
        expect(Topbar.Model.Asn.buildAsnListNew(asnList)).toEqual(asnListNew);

        //
        // change obj 1 action; add obj 4
        //
        asnList = [
            {"id": 1
                , "note":"updated notification1"
                , "action":"Expired"
            }
            ,{"id": 2
                , "note":"updated notification2"
                , "action":"Ack"
            }
            ,{"id": 3
                , "note":"updated notification3"
                , "action":"New"
            }
            ,{"id": 4
                , "note":"updated notification4"
                , "action":"New"
            }
        ];
        var asnListNewMore = [
            {"id": 4
                , "note":"updated notification4"
                , "action":"New"
            }
        ];
        var asnListNewNoLonger = [
            {"id": 1
                , "note":"notification1"
                , "action":"New"
            }
        ];
        Topbar.Model.Asn.setAsnList(asnList);
        expect(Topbar.Model.Asn.getAsnListNewMore(asnList)).toEqual(asnListNewMore);
        expect(Topbar.Model.Asn.getAsnListNewNoLonger(asnList)).toEqual(asnListNewNoLonger);
        asnListNew = [
            {"id": 3
                , "note":"updated notification3"
                , "action":"New"
            }
            ,{"id": 4
                , "note":"updated notification4"
                , "action":"New"
            }
        ];
        expect(Topbar.Model.Asn.buildAsnListNew(asnList)).toEqual(asnListNew);

        //
        // add obj 5
        //
        asnList = [
            {"id": 1
                , "note":"updated notification1"
                , "action":"Expired"
            }
            ,{"id": 2
                , "note":"updated notification2"
                , "action":"Ack"
            }
            ,{"id": 3
                , "note":"updated notification3"
                , "action":"New"
            }
            ,{"id": 4
                , "note":"updated notification4"
                , "action":"New"
            }
            ,{"id": 5
                , "note":"updated notification5"
                , "action":"New"
            }
        ];
        asnListNewMore = [
            {"id": 5
                , "note":"updated notification5"
                , "action":"New"
            }
        ];
        asnListNewNoLonger = [
        ];
        Topbar.Model.Asn.setAsnList(asnList);
        expect(Topbar.Model.Asn.getAsnListNewMore(asnList)).toEqual(asnListNewMore);
        expect(Topbar.Model.Asn.getAsnListNewNoLonger(asnList)).toEqual(asnListNewNoLonger);
        asnListNew = [
            {"id": 3
                , "note":"updated notification3"
                , "action":"New"
            }
            ,{"id": 4
                , "note":"updated notification4"
                , "action":"New"
            }
            ,{"id": 5
                , "note":"updated notification5"
                , "action":"New"
            }
        ];
        expect(Topbar.Model.Asn.buildAsnListNew(asnList)).toEqual(asnListNew);


        //
        // asnList has no change, expect nothing to chnage for asnListNew
        //
        Topbar.Model.Asn.setAsnList(asnList);
        expect(Topbar.Model.Asn.getAsnListNewMore(asnList)).toEqual([]);
        expect(Topbar.Model.Asn.getAsnListNewNoLonger(asnList)).toEqual([]);


        //
        // remove obj 3,4; obj 5 changes action
        //
        asnList = [
            {"id": 1
                , "note":"updated notification1"
                , "action":"Expired"
            }
            ,{"id": 2
                , "note":"updated notification2"
                , "action":"Ack"
            }
            ,{"id": 5
                , "note":"updated notification5"
                , "action":"Ack"
            }
        ];
        asnListNewMore = [
        ];
        asnListNewNoLonger = [
            {"id": 3
                , "note":"updated notification3"
                , "action":"New"
            }
            ,{"id": 4
                , "note":"updated notification4"
                , "action":"New"
            }
            ,{"id": 5
                , "note":"updated notification5"
                , "action":"New"
            }
        ];
        Topbar.Model.Asn.setAsnList(asnList);
        expect(Topbar.Model.Asn.getAsnListNewMore(asnList)).toEqual(asnListNewMore);
        expect(Topbar.Model.Asn.getAsnListNewNoLonger(asnList)).toEqual(asnListNewNoLonger);
    });

});
