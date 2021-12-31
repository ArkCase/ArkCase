package gov.foia.service.dataupdate;

import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import gov.foia.model.FOIAPerson;

public class CreateAnonymousPersonExecutor implements AcmDataUpdateExecutor
{

    private PersonDao personDao;

    @Override
    public String getUpdateId() {
        return "create-anonymous-person";
    }

    @Override
    public void execute() {
        if(getPersonDao().findAnonymousPerson() == null){
            FOIAPerson anonymousPerson = new FOIAPerson();
            anonymousPerson.setAnonymousFlag(true);
            anonymousPerson.setFamilyName("Anonymous");
            anonymousPerson.setGivenName("Anonymous");
            anonymousPerson.setTitle("Anonymous");
            getPersonDao().save(anonymousPerson);
        }
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
