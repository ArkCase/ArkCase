package gov.foia.service;

import com.armedia.acm.quartz.scheduler.AcmJobDescriptor;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DueDateReminderJobDescriptor extends AcmJobDescriptor
{
    private DueDateReminder dueDateReminder;

    @Override
    public String getJobName()
    {
        return "dueDateReminderJob";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        dueDateReminder.sendDueDateReminder();
    }

    public DueDateReminder getDueDateReminder()
    {
        return dueDateReminder;
    }

    public void setDueDateReminder(DueDateReminder dueDateReminder)
    {
        this.dueDateReminder = dueDateReminder;
    }
}
