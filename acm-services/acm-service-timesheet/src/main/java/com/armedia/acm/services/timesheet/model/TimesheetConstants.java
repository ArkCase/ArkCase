package com.armedia.acm.services.timesheet.model;

public interface TimesheetConstants {

	/**
	 * Object type
	 */
	public final static String OBJECT_TYPE = "TIMESHEET";
	
	/**
	 * Statuses
	 */
	public final static String DRAFT = "DRAFT";
	public final static String IN_APPROVAL = "IN_APPROVAL";
	public final static String APPROVED = "APPROVED";
	
	/**
	 * Event type (root name)
	 */
	public final static String EVENT_TYPE = "com.armedia.acm." + OBJECT_TYPE.toLowerCase();
	
	public final static String ROOT_FOLDER_KEY = "root.folder";
    public final static String SEARCH_TREE_SORT = "search.tree.sort";

}
