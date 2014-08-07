/**
 * Test script for ComplaintList.Object
 *
 * @author jwu
 */

    //
    //Tree node type - key in following format:
    //prevPage - prevPage
    //c    - [complaintId]
    //ci   - [complaintId].i
    //cii  - [complaintId].ii
    //cip  - [complaintId].ip
    //cipp - [complaintId].ip.[personId]
    //ca   - [complaintId].a
    //cap  - [complaintId].ap
    //caa  - [complaintId].aa
    //car  - [complaintId].ar
    //ct   - [complaintId].t
    //ctu  - [complaintId].tu
    //cta  - [complaintId].ta
    //ctc  - [complaintId].tc
    //cr   - [complaintId].r
    //crc  - [complaintId].rc
    //crs  - [complaintId].rs
    //crt  - [complaintId].rt
    //crd  - [complaintId].rd
    //cp   - [complaintId].p
    //cpa  - [complaintId].pa
    //cpc  - [complaintId].pc
    //cpw  - [complaintId].pw
    //nextPage - nextPage
    //

describe("ComplaintList.Objet", function()
{
    it("ComplaintList.Object.getNodeTypeByKey", function() {
        expect(ComplaintList.Object.getNodeTypeByKey("")).toEqual(null);
        expect(ComplaintList.Object.getNodeTypeByKey("1993")).toEqual("c");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.i")).toEqual("ci");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.ii")).toEqual("cii");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.ip")).toEqual("cip");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.ip.123")).toEqual("cipp");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.a")).toEqual("ca");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.ap")).toEqual("cap");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.aa")).toEqual("caa");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.ar")).toEqual("car");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.t")).toEqual("ct");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.tu")).toEqual("ctu");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.ta")).toEqual("cta");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.tc")).toEqual("ctc");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.r")).toEqual("cr");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.rc")).toEqual("crc");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.rs")).toEqual("crs");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.rt")).toEqual("crt");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.rd")).toEqual("crd");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.p")).toEqual("cp");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.pa")).toEqual("cpa");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.pc")).toEqual("cpc");
        expect(ComplaintList.Object.getNodeTypeByKey("1993.pw")).toEqual("cpw");
        expect(ComplaintList.Object.getNodeTypeByKey("prevPage")).toEqual("prevPage");
        expect(ComplaintList.Object.getNodeTypeByKey("nextPage")).toEqual("nextPage");
    });

    it("ComplaintList.Object.getComplaintIdByKey", function() {
        expect(ComplaintList.Object.getComplaintIdByKey("")).toEqual(0);
        expect(ComplaintList.Object.getComplaintIdByKey("1993")).toEqual(1993);
        expect(ComplaintList.Object.getComplaintIdByKey("1993.i")).toEqual(1993);
        expect(ComplaintList.Object.getComplaintIdByKey("1993.ii")).toEqual(1993);
        expect(ComplaintList.Object.getComplaintIdByKey("1993.ip")).toEqual(1993);
        expect(ComplaintList.Object.getComplaintIdByKey("1993.ip.123")).toEqual(1993);
        expect(ComplaintList.Object.getComplaintIdByKey("prevPage")).toEqual(0);
        expect(ComplaintList.Object.getComplaintIdByKey("nextPage")).toEqual(0);
    });

});
