package com.armedia.acm.service.orbeon.forms.model;

public class ROIFormFooter {

	String acm_ticket;
	String complaint_id;
	
	public ROIFormFooter() {}

	public String getAcm_ticket() {
		return acm_ticket;
	}

	public void setAcm_ticket(String acm_ticket) {
		this.acm_ticket = acm_ticket;
	}

	public String getComplaint_id() {
		return complaint_id;
	}

	public void setComplaint_id(String complaint_id) {
		this.complaint_id = complaint_id;
	}

	@Override
	public String toString() {
		return "ROIFormInternal [acm_ticket=" + acm_ticket + ", complaint_id="
				+ complaint_id + "]";
	};
	
}
