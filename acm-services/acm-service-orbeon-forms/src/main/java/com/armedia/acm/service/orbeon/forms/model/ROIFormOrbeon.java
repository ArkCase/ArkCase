package com.armedia.acm.service.orbeon.forms.model;

public class ROIFormOrbeon {
	
	private FormHeader header;
	private ROIFormReportDetail detail;
	private ROIFormFooter footer;
	private FormAttachment attachment;
	
	public ROIFormOrbeon() {}

	public FormHeader getHeader() {
		return header;
	}

	public void setHeader(FormHeader header) {
		this.header = header;
	}

	public ROIFormReportDetail getDetail() {
		return detail;
	}

	public void setDetail(ROIFormReportDetail detail) {
		this.detail = detail;
	}

	public ROIFormFooter getFooter() {
		return footer;
	}

	public void setFooter(ROIFormFooter footer) {
		this.footer = footer;
	}

	public FormAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(FormAttachment attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "ROIFormOrbeon [header=" + header + ", detail=" + detail
				+ ", footer=" + footer + ", attachment=" + attachment + "]";
	}

		
}
