/**
 * Test script for Complaint.Object
 *
 * @author jwu
 */

describe("Complaint.Object", function()
{
    //
    //tabId             - nodeType - key
    //------------------------------------------
    //tabBlank          - prevPage - prevPage
    //tabBlank          - p        - [pageId]
    //tabMain           - pc       - [pageId].[complaintId]
    //tabDetail         - pci      - [pageId].[complaintId].i
    //tabDetail         - pcid     - [pageId].[complaintId].id
    //tabInitiator      - pcii     - [pageId].[complaintId].ii
    //tabPeople         - pcip     - [pageId].[complaintId].ip
    //tabPeople         - pcipc    - [pageId].[complaintId].ip.[personId]
    //tabNotes          - pcin     - [pageId].[complaintId].in
    //tabPending        - pca      - [pageId].[complaintId].a
    //tabPending        - pcap     - [pageId].[complaintId].ap
    //tabPending        - pcapc    - [pageId].[complaintId].ap.[docId]
    //tabApproved       - pcaa     - [pageId].[complaintId].aa
    //tabApproved       - pcaac    - [pageId].[complaintId].aa.[docId]
    //tabRejected       - pcar     - [pageId].[complaintId].ar
    //tabRejected       - pcarc    - [pageId].[complaintId].ar.[docId]
    //tabUnassigned     - pct      - [pageId].[complaintId].t
    //tabUnassigned     - pctu     - [pageId].[complaintId].tu
    //tabUnassigned     - pctuc    - [pageId].[complaintId].tu.[taskId]
    //tabAssigned       - pcta     - [pageId].[complaintId].ta
    //tabAssigned       - pctac    - [pageId].[complaintId].ta.[taskId]
    //tabCompleted      - pctc     - [pageId].[complaintId].tc
    //tabCompleted      - pctcc    - [pageId].[complaintId].tc.[taskId]
    //tabRefComplaints  - pcr      - [pageId].[complaintId].r
    //tabRefComplaints  - pcrc     - [pageId].[complaintId].rc
    //tabRefCases       - pcrs     - [pageId].[complaintId].rs
    //tabRefTasks       - pcrt     - [pageId].[complaintId].rt
    //tabRefDocuments   - pcrd     - [pageId].[complaintId].rd
    //tabApprovers      - pcp      - [pageId].[complaintId].p
    //tabApprovers      - pcpa     - [pageId].[complaintId].pa
    //tabCollaborators  - pcpc     - [pageId].[complaintId].pc
    //tabWatchers       - pcpw     - [pageId].[complaintId].pw
    //tabBlank          - nextPage - nextPage
    //

    it("Complaint.Object.getTabIdByKey", function() {
        expect(Complaint.Object._getTabIdByKey(null)).toEqual("tabBlank");
        expect(Complaint.Object._getTabIdByKey("")).toEqual("tabBlank");
        expect(Complaint.Object._getTabIdByKey("prevPage")).toEqual("tabBlank");
        expect(Complaint.Object._getTabIdByKey("nextPage")).toEqual("tabBlank");
        expect(Complaint.Object._getTabIdByKey("2")).toEqual("tabBlank");
        expect(Complaint.Object._getTabIdByKey("2.1993")).toEqual("tabMain");
        expect(Complaint.Object._getTabIdByKey("2.1993.i")).toEqual("tabDetail");
        expect(Complaint.Object._getTabIdByKey("2.1993.id")).toEqual("tabDetail");
        expect(Complaint.Object._getTabIdByKey("2.1993.ii")).toEqual("tabInitiator");
        expect(Complaint.Object._getTabIdByKey("2.1993.ip")).toEqual("tabPeople");
        expect(Complaint.Object._getTabIdByKey("2.1993.ip.123")).toEqual("tabPeople");
        expect(Complaint.Object._getTabIdByKey("2.1993.in")).toEqual("tabNotes");
        expect(Complaint.Object._getTabIdByKey("2.1993.a")).toEqual("tabPending");
        expect(Complaint.Object._getTabIdByKey("2.1993.ap")).toEqual("tabPending");
        expect(Complaint.Object._getTabIdByKey("2.1993.aa")).toEqual("tabApproved");
        expect(Complaint.Object._getTabIdByKey("2.1993.ar")).toEqual("tabRejected");
        expect(Complaint.Object._getTabIdByKey("2.1993.t")).toEqual("tabUnassigned");
        expect(Complaint.Object._getTabIdByKey("2.1993.tu")).toEqual("tabUnassigned");
        expect(Complaint.Object._getTabIdByKey("2.1993.tu.111")).toEqual("tabUnassigned");
        expect(Complaint.Object._getTabIdByKey("2.1993.ta")).toEqual("tabAssigned");
        expect(Complaint.Object._getTabIdByKey("2.1993.tc")).toEqual("tabCompleted");
        expect(Complaint.Object._getTabIdByKey("2.1993.r")).toEqual("tabRefComplaints");
        expect(Complaint.Object._getTabIdByKey("2.1993.rc")).toEqual("tabRefComplaints");
        expect(Complaint.Object._getTabIdByKey("2.1993.rs")).toEqual("tabRefCases");
        expect(Complaint.Object._getTabIdByKey("2.1993.rt")).toEqual("tabRefTasks");
        expect(Complaint.Object._getTabIdByKey("2.1993.rd")).toEqual("tabRefDocuments");
        expect(Complaint.Object._getTabIdByKey("2.1993.p")).toEqual("tabApprovers");
        expect(Complaint.Object._getTabIdByKey("2.1993.pa")).toEqual("tabApprovers");
        expect(Complaint.Object._getTabIdByKey("2.1993.pc")).toEqual("tabCollaborators");
        expect(Complaint.Object._getTabIdByKey("2.1993.pw")).toEqual("tabWatchers");
    });

    it("Complaint.Object.getNodeTypeByKey", function() {
        expect(Complaint.Object.getNodeTypeByKey(null)).toEqual(null);
        expect(Complaint.Object.getNodeTypeByKey("")).toEqual(null);
        expect(Complaint.Object.getNodeTypeByKey("prevPage")).toEqual("prevPage");
        expect(Complaint.Object.getNodeTypeByKey("nextPage")).toEqual("nextPage");
        expect(Complaint.Object.getNodeTypeByKey("2")).toEqual("p");
        expect(Complaint.Object.getNodeTypeByKey("2.1993")).toEqual("pc");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.i")).toEqual("pci");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.id")).toEqual("pcid");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.ii")).toEqual("pcii");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.ip")).toEqual("pcip");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.ip.123")).toEqual("pcipc");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.in")).toEqual("pcin");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.a")).toEqual("pca");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.ap")).toEqual("pcap");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.aa")).toEqual("pcaa");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.ar")).toEqual("pcar");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.t")).toEqual("pct");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.tu")).toEqual("pctu");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.tu.111")).toEqual("pctuc");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.ta")).toEqual("pcta");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.tc")).toEqual("pctc");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.r")).toEqual("pcr");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.rc")).toEqual("pcrc");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.rs")).toEqual("pcrs");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.rt")).toEqual("pcrt");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.rd")).toEqual("pcrd");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.p")).toEqual("pcp");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.pa")).toEqual("pcpa");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.pc")).toEqual("pcpc");
        expect(Complaint.Object.getNodeTypeByKey("2.1993.pw")).toEqual("pcpw");
    });

    it("Complaint.Object.getPageIdByKey", function() {
        expect(Complaint.Object.getPageIdByKey(null)).toEqual(-1);
        expect(Complaint.Object.getPageIdByKey("")).toEqual(-1);
        expect(Complaint.Object.getPageIdByKey("prevPage")).toEqual(-1);
        expect(Complaint.Object.getPageIdByKey("nextPage")).toEqual(-1);
        expect(Complaint.Object.getPageIdByKey("2")).toEqual(2);
        expect(Complaint.Object.getPageIdByKey("2.1993")).toEqual(2);
        expect(Complaint.Object.getPageIdByKey("2.1993.i")).toEqual(2);
        expect(Complaint.Object.getPageIdByKey("2.1993.ii")).toEqual(2);
        expect(Complaint.Object.getPageIdByKey("2.1993.ip")).toEqual(2);
        expect(Complaint.Object.getPageIdByKey("2.1993.ip.123")).toEqual(2);
    });

    it("Complaint.Object.getComplaintIdByKey", function() {
        expect(Complaint.Object.getComplaintIdByKey(null)).toEqual(0);
        expect(Complaint.Object.getComplaintIdByKey("")).toEqual(0);
        expect(Complaint.Object.getComplaintIdByKey("prevPage")).toEqual(0);
        expect(Complaint.Object.getComplaintIdByKey("nextPage")).toEqual(0);
        expect(Complaint.Object.getComplaintIdByKey("2")).toEqual(0);
        expect(Complaint.Object.getComplaintIdByKey("2.1993")).toEqual(1993);
        expect(Complaint.Object.getComplaintIdByKey("2.1993.i")).toEqual(1993);
        expect(Complaint.Object.getComplaintIdByKey("2.1993.ii")).toEqual(1993);
        expect(Complaint.Object.getComplaintIdByKey("2.1993.ip")).toEqual(1993);
        expect(Complaint.Object.getComplaintIdByKey("2.1993.ip.123")).toEqual(1993);
    });

    it("Complaint.Object.getChildIdByKey", function() {
        expect(Complaint.Object.getChildIdByKey(null)).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("prevPage")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("nextPage")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("2")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("2.1993")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("2.1993.i")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("2.1993.ii")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("2.1993.ip")).toEqual(0);
        expect(Complaint.Object.getChildIdByKey("2.1993.ip.123")).toEqual(123);
    });

});
