package com.armedia.acm.services.note;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
                                   "/spring/spring-library-note.xml",
                                   "/spring/spring-library-data-source.xml",
                                   "/spring/spring-library-context-holder.xml"
                                  })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class NoteIT
{
    @Autowired
    private NoteDao noteDao;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    @Transactional
    public void saveNote() throws Exception
    {
        Note n = new Note();
        n.setId(123L);
        n.setCreator("testCreator");
        n.setCreated(new Date());
        n.setNote("testNote");
        n.setParentId(234L);
        n.setParentType("COMPLAINT");
        Note saved = noteDao.save(n);
        assertNotNull(saved.getId());
        noteDao.deleteNoteById(saved.getId());

        log.info("Note ID: " + saved.getId());        
    }
 
    
}
