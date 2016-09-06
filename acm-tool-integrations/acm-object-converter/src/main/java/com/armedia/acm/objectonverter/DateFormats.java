package com.armedia.acm.objectonverter;

public interface DateFormats {
	
	/**
	 * These date formats for Frevvo are general formats that Frevvo recognize. If we are sending the formats like these to Frevvo,
	 * we can user locale for changing the format.
	 */
	public final String FREVVO_DATE_FORMAT = "yyyy-M-dd";
	public final String FREVVO_DATE_FORMAT_MARSHAL_UNMARSHAL = "yyyy-M-dd";
	
	public final String TASK_NAME_DATE_FORMAT = "yyyyMMdd";
	public final String TIMESHEET_DATE_FORMAT = "M/dd/yyyy";
    public final String WORKFLOW_DATE_FORMAT = "M/dd/yyyy";

	public final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
}
