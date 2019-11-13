package com.armedia.acm.acm_services.acm_configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;

import com.armedia.acm.configuration.api.environment.PropertySource;
import com.armedia.acm.configuration.core.ConfigurationContainer;
import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationServiceImpl;
import com.armedia.acm.configuration.util.MergeFlags;
import com.armedia.acm.configuration.util.MergePropertiesUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class CollectionPropertiesConfigurationServiceImplTest
{
    @InjectMocks
    CollectionPropertiesConfigurationServiceImpl collectionPropertiesConfigurationServiceImpl;

    @Mock
    ConfigurationContainer configurationContainer;

    private Map<String, Object> expectedValues;
    private List<PropertySource> propertySourceList;
    private Map<String, Object> mergedConfig;
    private Map<String, Object> arkaseYaml;
    private Map<String, Object> arkaseFoiaYaml;
    private PropertySource arkcase;
    private PropertySource arkcaseFoia;
    private List<Object> rolesToSave;
    private List<Object> privilegesToAdd;

    private static Set<String> fromArray(String... elements)
    {
        return new HashSet<>(Arrays.asList(elements));
    }

    @Before
    public void setup()
    {
        expectedValues = new HashMap<>();
        propertySourceList = new LinkedList<>();
        mergedConfig = new HashMap<>();
        arkaseYaml = new HashMap<>();
        arkaseFoiaYaml = new HashMap<>();
        arkcase = new PropertySource();
        arkcase.setName("arkcase.yaml");
        arkcase.setSource(arkaseYaml);
        arkcaseFoia = new PropertySource();
        arkcaseFoia.setName("arkcaseFoia.yaml");
        arkcaseFoia.setSource(arkaseFoiaYaml);
        rolesToSave = new LinkedList<>();
        privilegesToAdd = new LinkedList<>();
        collectionPropertiesConfigurationServiceImpl.setConfigurationContainer(configurationContainer);
    }

    // @formatter:off
    /**
        arkcase.yaml		arkcase-foia.yaml		    ArkCase memory
        roles:		    	~roles:					      roles:
          - admin			  - new_role				    - officer
          - officer           - ^admin						- new_role
                              - ^not-exsiting
     */
    // @formatter:on
    @Test
    public void mergeListEntriesFromConfiguration()
    {

        arkaseYaml.put("application.roles[0]", "admin");
        arkaseYaml.put("application.roles[1]", "officer");

        arkaseFoiaYaml.put("application.~roles[0]", "^admin");
        arkaseFoiaYaml.put("application.~roles[1]", "new_role");
        arkaseFoiaYaml.put("application.~roles[2]", "^not-exsiting");

        propertySourceList.add(arkcaseFoia);
        propertySourceList.add(arkcase);

        Collections.reverse(propertySourceList);

        for (PropertySource source : propertySourceList)
        {
            Map<String, Object> map = source.getSource();
            MergePropertiesUtil.mergePropertiesFromSources(mergedConfig, map);
        }

        expectedValues = filterAnnotationPropertyMap(mergedConfig, "application.roles",
                "[");

        assertThat(expectedValues.size(), is(2));
        assertThat("Merged keys from rolesToGroups should be:", expectedValues.keySet(),
                everyItem(isIn(fromArray("application.roles[0]", "application.roles[1]"))));
        assertThat("Merged values from rolesToGroups should have:", expectedValues,
                hasEntry("application.roles[1]", "officer"));
        assertThat("Merged values from rolesToGroups should have:", expectedValues,
                hasEntry("application.roles[0]", "new_role"));

    }

    // @formatter:off
    /**
     * arkcase.yaml				arkcase-foia.yaml		    Arkcase memory:
     *   rolesToGroups:			  ~rolesToGroups:			  rolesToGroups:
     *     admin:				    new_role:				    admin
     *       - Administrator	      - Administrator			  - Administrator
     *     officer:					^guest:	""				    officer:
     *       - Administrator		~officer:					  - Officers
     *       - Officers				  - NewGroup				  - NewGroup
     *     guest:				      - ^Administrator		    new_role:
     *       - Guests				 						      - Administrator
     */
    // @formatter:on
    @Test
    public void mergeMapEntriesFromConfiguration()
    {

        arkaseYaml.put("application.rolesToGroups.admin[0]", "Administrator");
        arkaseYaml.put("application.rolesToGroups.officer[0]", "Administrator");
        arkaseYaml.put("application.rolesToGroups.officer[1]", "Officers");
        arkaseYaml.put("application.rolesToGroups.guest[0]", "Guests");

        arkaseFoiaYaml.put("application.~rolesToGroups.new_role[0]", "Administrator");
        arkaseFoiaYaml.put("application.~rolesToGroups.^guest", "");
        arkaseFoiaYaml.put("application.~rolesToGroups.officer[0]", "^Administrator");
        arkaseFoiaYaml.put("application.~rolesToGroups.officer[1]", "NewGroup");

        propertySourceList.add(arkcaseFoia);
        propertySourceList.add(arkcase);

        Collections.reverse(propertySourceList);

        for (PropertySource source : propertySourceList)
        {
            Map<String, Object> map = source.getSource();
            MergePropertiesUtil.mergePropertiesFromSources(mergedConfig, map);
        }

        expectedValues = filterAnnotationPropertyMap(mergedConfig, "application.rolesToGroups",
                ".");

        assertThat(expectedValues.size(), is(4));
        assertThat("Merged keys from rolesToGroups should be:", expectedValues.keySet(),
                everyItem(isIn(fromArray("officer[0]", "admin[0]", "officer[1]", "new_role[0]"))));
        assertThat("Merged values from rolesToGroups should have:", expectedValues,
                hasEntry("officer[0]", "Officers"));
        assertThat("Merged values from rolesToGroups should have:", expectedValues,
                hasEntry("admin[0]", "Administrator"));
        assertThat("Merged values from rolesToGroups should have:", expectedValues,
                hasEntry("officer[1]", "NewGroup"));
        assertThat("Merged values from rolesToGroups should have:", expectedValues,
                hasEntry("new_role[0]", "Administrator"));

    }

    @Test
    public void mergeNotExistingInTheTwoMaps()
    {

        arkaseYaml.put("application.rolesToGroups.admin[0]", "Administrator");
        arkaseYaml.put("application.rolesToGroups.officer[0]", "Administrator");
        arkaseYaml.put("application.rolesToGroups.officer[1]", "Officers");
        arkaseYaml.put("application.rolesToGroups.guest[0]", "Guests");

        arkaseFoiaYaml.put("instance.indentity.identityId", "c65523b7");
        arkaseFoiaYaml.put("instance.indentity.dateCreated", "2019-09-29");
        arkaseFoiaYaml.put("instance.indentity.digest", "fruJ1tH+Z5FmdSnKqLQZlqfDSFOHVbqamJZO");

        propertySourceList.add(arkcaseFoia);
        propertySourceList.add(arkcase);

        Collections.reverse(propertySourceList);

        for (PropertySource source : propertySourceList)
        {
            Map<String, Object> map = source.getSource();
            MergePropertiesUtil.mergePropertiesFromSources(mergedConfig, map);
        }

        assertThat(mergedConfig.size(), is(7));

    }

    @Test
    public void mergeAndRemovePrivilegesFromConfiguration()
    {

        arkaseYaml.put("application.privileges.searchPrivilege", "Search Privileges");
        arkaseYaml.put("application.privileges.acmCaseApprovePrivilege", "Case Approve Privilege");

        arkaseFoiaYaml.put("application.~privileges.searchPrivilege", "Search Privilege");
        arkaseFoiaYaml.put("application.privileges.^acmCaseApprovePrivilege", "");
        arkaseFoiaYaml.put("application.~privileges.foiaFilePrivilege", "FOIA File Privilege");

        propertySourceList.add(arkcaseFoia);
        propertySourceList.add(arkcase);

        Collections.reverse(propertySourceList);

        for (PropertySource source : propertySourceList)
        {
            Map<String, Object> map = source.getSource();
            MergePropertiesUtil.mergePropertiesFromSources(mergedConfig, map);
        }

        assertThat(mergedConfig.size(), is(2));
        assertThat("Merged values from privileges should have:", mergedConfig,
                hasEntry("application.privileges.searchPrivilege", "Search Privilege"));
        assertThat("Merged values from privileges should have:", mergedConfig,
                hasEntry("application.privileges.foiaFilePrivilege", "FOIA File Privilege"));

    }

    @Test
    public void removeListPropertyTest()
    {
        rolesToSave.add("ROLE_ADMINISTRATOR");
        rolesToSave.add("ROLE_CONTRIBUTOR");

        expectedValues = collectionPropertiesConfigurationServiceImpl.updateListEntry(
                "application.~roles", "roles",
                rolesToSave,
                MergeFlags.REMOVE);

        List<String> roles = (List<String>) expectedValues.get("application.~roles");

        assertThat(expectedValues.size(), is(1));
        assertThat("Role should be merged in the runtime map:", expectedValues.keySet(),
                everyItem(isIn(fromArray("application.~roles"))));
        assertThat("Role should be merged in the runtime map:", roles, hasItem("^ROLE_ADMINISTRATOR"));
        assertThat("Role should be merged in the runtime map:", roles, hasItem("^ROLE_CONTRIBUTOR"));
    }

    @Test
    public void addListPropertyTest()
    {
        rolesToSave.add("ROLE_ADMINISTRATOR");
        rolesToSave.add("ROLE_CONTRIBUTOR");

        expectedValues = collectionPropertiesConfigurationServiceImpl.updateListEntry(
                "application.~roles", "roles",
                rolesToSave,
                MergeFlags.MERGE);

        List<String> roles = (List<String>) expectedValues.get("application.~roles");

        assertThat(expectedValues.size(), is(1));
        assertThat("Role should be merged in the runtime map:", expectedValues.keySet(),
                everyItem(isIn(fromArray("application.~roles"))));
        assertThat("Role should be merged in the runtime map:", roles, hasItem("ROLE_ADMINISTRATOR"));
        assertThat("Role should be merged in the runtime map:", roles, hasItem("ROLE_CONTRIBUTOR"));
    }

    @Test
    public void updateMapPropertyTest()
    {

        privilegesToAdd.add(Arrays.asList("acmCaseApprovePrivilege", "caseFileListPrivilege", "caseFileCreatePrivilege",
                "acmCaseModulePrivilege", "acmCategoryManagementPrivilege"));

        Map<String, Object> expectedValues = collectionPropertiesConfigurationServiceImpl.updateMapProperty(
                "application.rolesToPrivileges",
                "ROLE_ADMINISTRATOR",
                privilegesToAdd,
                MergeFlags.MERGE);

        assertThat(expectedValues.size(), is(1));
        assertThat("Privilege should be merged in the runtime map:", expectedValues.keySet(),
                everyItem(isIn(fromArray("application.rolesToPrivileges"))));

        Map<String, Object> rolePrivileges = (Map<String, Object>) expectedValues.get("application.rolesToPrivileges");

        assertThat("Privilege should be merged in the runtime map:", rolePrivileges.keySet(),
                everyItem(isIn(fromArray("~ROLE_ADMINISTRATOR"))));

        List<Object> administratorPrivileges = (List<Object>) rolePrivileges.get("~ROLE_ADMINISTRATOR");
        List<String> privileges = (List<String>) administratorPrivileges.get(0);

        assertThat("Privilege should be merged in the runtime map:", privileges, hasItem("acmCaseApprovePrivilege"));
        assertThat("Privilege should be merged in the runtime map:", privileges, hasItem("caseFileListPrivilege"));
        assertThat("Privilege should be merged in the runtime map:", privileges, hasItem("caseFileCreatePrivilege"));
        assertThat("Privilege should be merged in the runtime map:", privileges, hasItem("acmCaseModulePrivilege"));
        assertThat("Privilege should be merged in the runtime map:", privileges, hasItem("acmCategoryManagementPrivilege"));

    }

    private Map<String, Object> filterAnnotationPropertyMap(Map<String, Object> mergedConfig, String propertyKey, String listMapFlag)
    {
        Function<Map.Entry<String, Object>, Map.Entry<String, Object>> transform = entry -> {

            String newKey = entry.getKey().replace(propertyKey + ".", "");
            return new AbstractMap.SimpleEntry<>(newKey, entry.getValue());

        };

        // filter all the properties from configuration that contains the propertyKey from the annotation
        return mergedConfig.entrySet()
                .stream()
                .filter(s -> s.getKey().startsWith(propertyKey + listMapFlag))
                .map(transform)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
