package com.armedia.acm.calendar.service.integration.exchange;

import static com.armedia.acm.calendar.service.integration.exchange.ExchangeCalendarService.PROCESS_USER;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.service.outlook.dao.OutlookDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 26, 2017
 *
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest({CalendarFolder.class, FindItemsResults.class, Appointment.class})
public class CalendarEntityHandlerTest
{

    private static final String UNIQUE_MASTER_ITEM_ID = "UNIQUE_MASTER_ITEM_ID";

    private static final String UNIQUE_ITEM_ID = "UNIQUE_ITEM_ID";

    private static final String ENTITY_TYPE_FOR_QUERY = "CaseFile";

    @Mock
    private ExchangeService mockedService;

    @Mock
    private EntityManager mockedEm;

    @Mock
    private TypedQuery<AcmContainerEntity> mockedQuery;

    @Mock
    private AcmContainerEntity mockedEntity;

    @Mock
    private AuditPropertyEntityAdapter mockedAuditPropertyEntityAdapter;

    @Mock
    private OutlookDao mockedOutlookDao;

    @Mock
    private AcmContainerDao mockedContainerEntityDao;

    @InjectMocks
    private CalendarEntityHandler entityHandler;

    @Mock
    private ConfigurationFileAddedEvent mockedConfigurationEvent;

    @Mock
    private AcmContainerEntity mockedContainerEntity;

    @Mock
    private AcmContainer mockedContainer;

    @Mock
    private CalendarFolder mockedFolder;

    @Mock
    private FindItemsResults<Appointment> mockedFindResults;

    @Mock
    private Appointment mockedAppointment;

    @Mock
    private ItemId mockedItemId;

    @Mock
    private Appointment mockedMasterAppointment;

    @Mock
    private ItemId mockedMasterItemId;

    private List<String> closedStates = Arrays.asList("CLOSED");

    private Integer daysClosed = 3;

    @Before
    public void setUp()
    {
        entityHandler.setEntityType("CASE_FILE");
        entityHandler.setEntityTypeForQuery("CaseFile");
        File purgerSettings = new File(getClass().getClassLoader().getResource("calendarPurgersSettings.properties").getFile());
        when(mockedConfigurationEvent.getConfigFile()).thenReturn(purgerSettings);
        entityHandler.onApplicationEvent(mockedConfigurationEvent);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     */
    @Test
    public void testPurgeCalendars_retainIndefinitely()
    {
        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.RETAIN_INDEFINITELY, null);
        // then
        verifyZeroInteractions(mockedService, mockedEm, mockedAuditPropertyEntityAdapter, mockedOutlookDao, mockedContainerEntityDao);

    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     *
     * @throws Exception
     */
    @Test
    public void testPurgeCalendars_closed_appointmentMaster() throws Exception
    {
        // given
        mockStatic(CalendarFolder.class, Appointment.class);
        when(mockedEm.createQuery(anyString(), eq(AcmContainerEntity.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(mockedContainerEntity));
        when(mockedContainerEntity.getContainer()).thenReturn(mockedContainer);
        when(mockedContainer.getCalendarFolderId()).thenReturn("folderId");
        when(CalendarFolder.bind(eq(mockedService), any(FolderId.class))).thenReturn(mockedFolder);
        when(mockedFolder.findAppointments(any(CalendarView.class))).thenReturn(mockedFindResults);
        ArrayList<Appointment> itemList = new ArrayList<>();
        itemList.add(mockedAppointment);
        when(mockedFindResults.getItems()).thenReturn(itemList);
        when(mockedAppointment.getId()).thenReturn(mockedItemId);
        when(mockedItemId.getUniqueId()).thenReturn(UNIQUE_ITEM_ID);
        when(mockedAppointment.getIsRecurring()).thenReturn(true);
        when(Appointment.bindToRecurringMaster(eq(mockedService), any(ItemId.class))).thenReturn(mockedMasterAppointment);
        when(mockedMasterAppointment.getId()).thenReturn(mockedMasterItemId);
        when(mockedMasterItemId.getUniqueId()).thenReturn(UNIQUE_MASTER_ITEM_ID);

        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.CLOSED, null);

        // then
        verify(mockedEm).createQuery(
                String.format("SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL",
                        ENTITY_TYPE_FOR_QUERY),
                AcmContainerEntity.class);
        verify(mockedQuery).setParameter("statuses", closedStates);
        verify(mockedQuery).getResultList();
        verify(mockedAuditPropertyEntityAdapter).setUserId(PROCESS_USER);
        verify(mockedOutlookDao).deleteAppointmentItem(mockedService, UNIQUE_MASTER_ITEM_ID, true, DeleteMode.MoveToDeletedItems);
        verify(mockedFolder).delete(DeleteMode.MoveToDeletedItems);
        verify(mockedContainer).setCalendarFolderId(null);
        verify(mockedContainerEntityDao).save(mockedContainer);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     *
     * @throws Exception
     */
    @Test
    public void testPurgeCalendars_closed_appointmentInstance() throws Exception
    {
        // given
        mockStatic(CalendarFolder.class, Appointment.class);
        when(mockedEm.createQuery(anyString(), eq(AcmContainerEntity.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(mockedContainerEntity));
        when(mockedContainerEntity.getContainer()).thenReturn(mockedContainer);
        when(mockedContainer.getCalendarFolderId()).thenReturn("folderId");
        when(CalendarFolder.bind(eq(mockedService), any(FolderId.class))).thenReturn(mockedFolder);
        when(mockedFolder.findAppointments(any(CalendarView.class))).thenReturn(mockedFindResults);
        ArrayList<Appointment> itemList = new ArrayList<>();
        itemList.add(mockedAppointment);
        when(mockedFindResults.getItems()).thenReturn(itemList);
        when(mockedAppointment.getId()).thenReturn(mockedItemId);
        when(mockedItemId.getUniqueId()).thenReturn(UNIQUE_ITEM_ID);
        when(mockedAppointment.getIsRecurring()).thenReturn(false);

        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.CLOSED, null);

        // then
        verify(mockedEm).createQuery(
                String.format("SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL",
                        ENTITY_TYPE_FOR_QUERY),
                AcmContainerEntity.class);
        verify(mockedQuery).setParameter("statuses", closedStates);
        verify(mockedQuery).getResultList();
        verify(mockedAuditPropertyEntityAdapter).setUserId(PROCESS_USER);
        verify(mockedOutlookDao).deleteAppointmentItem(mockedService, UNIQUE_ITEM_ID, false, DeleteMode.MoveToDeletedItems);
        verify(mockedFolder).delete(DeleteMode.MoveToDeletedItems);
        verify(mockedContainer).setCalendarFolderId(null);
        verify(mockedContainerEntityDao).save(mockedContainer);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     *
     * @throws Exception
     */
    @Test
    public void testPurgeCalendars_closedXDays_appointmentMaster() throws Exception
    {
        // given
        mockStatic(CalendarFolder.class, Appointment.class);
        when(mockedEm.createQuery(anyString(), eq(AcmContainerEntity.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(mockedContainerEntity));
        when(mockedContainerEntity.getContainer()).thenReturn(mockedContainer);
        when(mockedContainer.getCalendarFolderId()).thenReturn("folderId");
        when(CalendarFolder.bind(eq(mockedService), any(FolderId.class))).thenReturn(mockedFolder);
        when(mockedFolder.findAppointments(any(CalendarView.class))).thenReturn(mockedFindResults);
        ArrayList<Appointment> itemList = new ArrayList<>();
        itemList.add(mockedAppointment);
        when(mockedFindResults.getItems()).thenReturn(itemList);
        when(mockedAppointment.getId()).thenReturn(mockedItemId);
        when(mockedItemId.getUniqueId()).thenReturn(UNIQUE_ITEM_ID);
        when(mockedAppointment.getIsRecurring()).thenReturn(true);
        when(Appointment.bindToRecurringMaster(eq(mockedService), any(ItemId.class))).thenReturn(mockedMasterAppointment);
        when(mockedMasterAppointment.getId()).thenReturn(mockedMasterItemId);
        when(mockedMasterItemId.getUniqueId()).thenReturn(UNIQUE_MASTER_ITEM_ID);

        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.CLOSED_X_DAYS, daysClosed);

        // then
        verify(mockedEm).createQuery(String.format(
                "SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL AND obj.modified <= :modified",
                ENTITY_TYPE_FOR_QUERY), AcmContainerEntity.class);
        verify(mockedQuery).setParameter("statuses", closedStates);
        verify(mockedQuery).setParameter("modified", calculateModifiedDate(daysClosed));
        verify(mockedQuery).getResultList();
        verify(mockedAuditPropertyEntityAdapter).setUserId(PROCESS_USER);
        verify(mockedOutlookDao).deleteAppointmentItem(mockedService, UNIQUE_MASTER_ITEM_ID, true, DeleteMode.MoveToDeletedItems);
        verify(mockedFolder).delete(DeleteMode.MoveToDeletedItems);
        verify(mockedContainer).setCalendarFolderId(null);
        verify(mockedContainerEntityDao).save(mockedContainer);
    }

    /**
     * Test method for
     * {@link com.armedia.acm.calendar.service.integration.exchange.CalendarEntityHandler#purgeCalendars(microsoft.exchange.webservices.data.core.ExchangeService, com.armedia.acm.calendar.config.service.CalendarConfiguration.PurgeOptions, java.lang.Integer)}.
     *
     * @throws Exception
     */
    @Test
    public void testPurgeCalendars_closedXDays_appointmentInstance() throws Exception
    {
        // given
        mockStatic(CalendarFolder.class, Appointment.class);
        when(mockedEm.createQuery(anyString(), eq(AcmContainerEntity.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(mockedContainerEntity));
        when(mockedContainerEntity.getContainer()).thenReturn(mockedContainer);
        when(mockedContainer.getCalendarFolderId()).thenReturn("folderId");
        when(CalendarFolder.bind(eq(mockedService), any(FolderId.class))).thenReturn(mockedFolder);
        when(mockedFolder.findAppointments(any(CalendarView.class))).thenReturn(mockedFindResults);
        ArrayList<Appointment> itemList = new ArrayList<>();
        itemList.add(mockedAppointment);
        when(mockedFindResults.getItems()).thenReturn(itemList);
        when(mockedAppointment.getId()).thenReturn(mockedItemId);
        when(mockedItemId.getUniqueId()).thenReturn(UNIQUE_ITEM_ID);
        when(mockedAppointment.getIsRecurring()).thenReturn(false);

        // when
        entityHandler.purgeCalendars(mockedService, PurgeOptions.CLOSED_X_DAYS, daysClosed);

        // then
        verify(mockedEm).createQuery(String.format(
                "SELECT obj FROM %s obj WHERE obj.status IN :statuses AND obj.container.calendarFolderId IS NOT NULL AND obj.modified <= :modified",
                ENTITY_TYPE_FOR_QUERY), AcmContainerEntity.class);
        verify(mockedQuery).setParameter("statuses", closedStates);
        verify(mockedQuery).setParameter("modified", calculateModifiedDate(daysClosed));
        verify(mockedQuery).getResultList();
        verify(mockedAuditPropertyEntityAdapter).setUserId(PROCESS_USER);
        verify(mockedOutlookDao).deleteAppointmentItem(mockedService, UNIQUE_ITEM_ID, false, DeleteMode.MoveToDeletedItems);
        verify(mockedFolder).delete(DeleteMode.MoveToDeletedItems);
        verify(mockedContainer).setCalendarFolderId(null);
        verify(mockedContainerEntityDao).save(mockedContainer);
    }

    /**
     * @param daysClosed
     * @return
     */
    private Date calculateModifiedDate(Integer daysClosed)
    {
        LocalDate now = LocalDate.now().minusDays(daysClosed);
        return Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
