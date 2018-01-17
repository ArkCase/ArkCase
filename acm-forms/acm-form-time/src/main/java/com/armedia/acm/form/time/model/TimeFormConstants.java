/**
 * 
 */
package com.armedia.acm.form.time.model;

import com.armedia.acm.frevvo.config.FrevvoFormName;

/**
 * @author riste.tutureski
 *
 */
public interface TimeFormConstants
{

    /**
     * Submission name Save - we need to recognize when to save as draft
     */
    public final static String SAVE = "Save";

    /**
     * Submission name Submit - we need to recognize when to send for approval
     */
    public final static String SUBMIT = "Submit";

    /**
     * Days of week
     */
    public final static String SUNDAY = "SUNDAY";
    public final static String MONDAY = "MONDAY";
    public final static String TUESDAY = "TUESDAY";
    public final static String WEDNESDAY = "WEDNESDAY";
    public final static String THURSDAY = "THURSDAY";
    public final static String FRIDAY = "FRIDAY";
    public final static String SATURDAY = "SATURDAY";

    /**
     * Time types
     */
    public final static String CASE_FILE = FrevvoFormName.CASE_FILE.toUpperCase();
    public final static String COMPLAINT = FrevvoFormName.COMPLAINT.toUpperCase();
    public final static String OTHER = "OTHER";

    public final static String APPROVER_PRIVILEGE = "acm-timesheet-approve";
}
