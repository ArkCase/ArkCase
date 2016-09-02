<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
             xmlns:beans="http://www.springframework.org/schema/beans"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                            http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-3.2.xsd">

    <beans:bean class="com.armedia.acm.crypto.properties.AcmEncryptablePropertySourcesPlaceholderConfigurer">
        <beans:property name="encryptablePropertyUtils" ref="acmEncryptablePropertyUtils"/>
        <beans:property name="location"
                        value="file:${r'${user.home}'}/.arkcase/acm/spring/spring-config-${id}-ldap.properties"/>
    </beans:bean>

    <beans:bean id="${id}_RoleToGroupProperties"
                class="org.springframework.beans.factory.config.PropertiesFactoryBean">
        <!-- note: must leave "file:" at the start of the file name for spring
             to be able to read the file; otherwise it will try to read from the
             classpath -->
        <beans:property name="location"
                        value="file:${r'${user.home}'}/.arkcase/acm/applicationRoleToUserGroup.properties"/>
        <beans:property name="ignoreResourceNotFound" value="true"/>
    </beans:bean>


    <!-- change the ref to match the bean name of your ldap sync job; and change the
         cron to the desired cron expression (see JavaDoc for org.springframework.scheduling.support.CronSequenceGenerator).
         No other changes are needed. -->
    <task:scheduled-tasks scheduler="ldapSyncTaskScheduler">
        <task:scheduled ref="${id}_ldapSyncJob" method="ldapSync" cron="0 0/30 * * * *"/>
    </task:scheduled-tasks>

    <!-- ensure this bean id is unique across all the LDAP sync beans. -->
    <beans:bean id="${id}_ldapSyncJob" class="com.armedia.acm.services.users.service.ldap.LdapSyncService"
                init-method="ldapSync">
        <!-- directoryName: must be unique across all LDAP sync beans -->
        <beans:property name="directoryName" value='${r"${ldapConfig.directoryName}"}'/>
        <!-- ldapSyncConfig: ref must match an AcmLdapSyncConfig bean, which should be defined below. -->
        <beans:property name="ldapSyncConfig" ref="${id}_sync"/>

        <!-- do not change ldapDao or ldapSyncDatabaseHelper properties. -->
        <beans:property name="ldapDao" ref="springLdapDao"/>
        <beans:property name="ldapSyncDatabaseHelper" ref="userDatabaseHelper"/>
        <beans:property name="auditPropertyEntityAdapter" ref="auditPropertyEntityAdapter"/>
        <beans:property name="syncEnabled" value="true"/>
    </beans:bean>

    <beans:bean id="${id}_sync" class="com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig">
        <!-- only specify authUserDn if your LDAP server requires user authentication (do not specify if you
             are using anonymous authentication -->
        <beans:property name="authUserDn" value='${r"${ldapConfig.authUserDn}"}'/>
        <!-- only specify authUserPassword if you also specify authUserDn -->
        <beans:property name="authUserPassword" value='${r"${ldapConfig.authUserPassword}"}'/>
        <!-- groupSearchBase is the full tree under which groups are found (e.g. ou=groups,dc=armedia,dc=com).  -->
        <beans:property name="groupSearchBase" value='${r"${ldapConfig.groupSearchBase}"}'/>
        <!-- groupSearchFilter is an LDAP filter to restrict which entries under the groupSearchBase are processsed -->
        <beans:property name="groupSearchFilter" value="(|(objectclass=group)(objectclass=groupofnames))"/>
        <!-- filter to retrieve all groups with a name greater than some group name - used to page group search results -->
        <beans:property name="groupSearchPageFilter" value='${r"${ldapConfig.groupSearchPageFilter}"}'/>
        <!-- ignorePartialResultException: true if your LDAP server is Active Directory, false for other LDAP servers -->
        <beans:property name="ignorePartialResultException" value="true"/>
        <!-- ldapUrl: URL of the ldap instance (e.g. ldap://armedia.com:389) -->
        <beans:property name="ldapUrl" value='${r"${ldapConfig.ldapUrl}"}'/>
        <beans:property name="baseDC" value='${r"${ldapConfig.base}"}'/>
        <!-- referral: "follow" if you want to follow LDAP referrals, "ignore" otherwise (search "ldap referral" for more info). -->
        <beans:property name="referral" value="follow"/>
        <!-- mailAttributeName: use "mail"  Most  LDAP servers use "mail". -->
        <beans:property name="mailAttributeName" value="mail"/>

        <beans:property name="allUsersFilter" value='${r"${ldapConfig.allUsersFilter}"}'/>
        <beans:property name="allUsersPageFilter" value='${r"${ldapConfig.allUsersPageFilter}"}'/>
        <beans:property name="allUsersSearchBase" value='${r"${ldapConfig.allUsersSearchBase}"}'/>

        <!-- userIdAttributeName: use "samAccountName" if your LDAP server is Active Directory.  Most other LDAP
             servers use "uid". -->
        <beans:property name="userIdAttributeName" value='${r"${ldapConfig.userIdAttributeName}"}'/>
        <beans:property name="roleToGroupMap" ref="${id}_RoleToGroupProperties"/>
        <beans:property name="userDomain" value='${r"${ldapConfig.userDomain}"}'/>
        <beans:property name="userSearchBase" value='${r"${ldapConfig.userSearchBase}"}'/>
        <beans:property name="userSearchFilter" value='${r"${ldapConfig.userSearchFilter}"}'/>
        <beans:property name="groupSearchFilterForUser" value='${r"${ldapConfig.groupSearchFilterForUser}"}'/>
        <beans:property name="syncPageSize" value='${r"{ldapConfig.syncPageSize}"}'/>
    </beans:bean>

    <!-- NOTE, do NOT activate both Kerberos and LDAP profiles at the same time.  When the kerberos profile
         is enabled, the LDAP authentication is still used as a backup, in case Kerberos auth fails.  That
         is why these beans are active both for Kerberos and LDAP. -->
    <beans:beans profile="ldap,kerberos">
        <beans:bean id="${id}_userSearch"
                    class="org.springframework.security.ldap.search.FilterBasedLdapUserSearch">
            <beans:constructor-arg index="0" value='${r"${ldapConfig.userSearchBase}"}'/>
            <beans:constructor-arg index="1" value='${r"${ldapConfig.userIdAttributeName}={0}"}'/>
            <beans:constructor-arg index="2" ref="${id}_contextSource"/>
        </beans:bean>

        <beans:bean id="${id}_authenticationProvider"
                    class="com.armedia.acm.auth.AcmLdapAuthenticationProvider">
            <beans:constructor-arg>
                <beans:bean
                        class="org.springframework.security.ldap.authentication.BindAuthenticator">
                    <beans:constructor-arg ref="${id}_contextSource"/>
                    <beans:property name="userSearch" ref="${id}_userSearch"/>
                </beans:bean>
            </beans:constructor-arg>
            <beans:constructor-arg>
                <beans:bean
                        class="org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator">
                    <beans:constructor-arg ref="${id}_contextSource"/>
                    <beans:constructor-arg value='${r"${ldapConfig.groupSearchBaseOU}"}'/>
                    <beans:property name="groupSearchFilter" value="member={0}"/>
                    <beans:property name="rolePrefix" value=""/>
                    <beans:property name="searchSubtree" value="true"/>
                    <beans:property name="convertToUpperCase" value="true"/>
                    <beans:property name="ignorePartialResultException" value="true"/>
                </beans:bean>
            </beans:constructor-arg>
            <beans:property name="userDao" ref="userJpaDao"/>
            <beans:property name="ldapSyncService" ref="${id}_ldapSyncJob"/>
        </beans:bean>

        <beans:bean id="${id}_contextSource"
                    class="org.springframework.ldap.core.support.LdapContextSource">
            <beans:property name="url" value='${r"${ldapConfig.ldapUrl}"}'/>
            <beans:property name="base" value='${r"${ldapConfig.base}"}'/>
            <beans:property name="userDn" value='${r"${ldapConfig.authUserDn}"}'/>
            <beans:property name="password" value='${r"${ldapConfig.authUserPassword}"}'/>
            <beans:property name="pooled" value="true"/>
            <!-- AD Specific Setting for avoiding the partial exception error -->
            <beans:property name="referral" value="follow"/>
        </beans:bean>

        <!--
        Authenticates a user id and password against LDAP directory.  To support multiple LDAP configurations, create multiple Spring
        beans, each with its own LdapAuthenticateService.
        -->
        <beans:bean id="${id}_ldapAuthenticateService"
                    class="com.armedia.acm.services.users.service.ldap.LdapAuthenticateService">
            <!-- ldapAuthenticateConfig: ref must match an AcmLdapAuthenticateConfig bean, which should be defined below. -->
            <beans:property name="ldapAuthenticateConfig" ref="${id}_authenticate"/>

            <!-- do not change ldapDao properties. -->
            <beans:property name="ldapDao" ref="springLdapDao"/>
        </beans:bean>

        <beans:bean id="${id}_authenticate" class="com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig">
            <!-- only specify authUserDn if your LDAP server requires user authentication (do not specify if you
                 are using anonymous authentication -->
            <beans:property name="authUserDn" value='${r"${ldapConfig.authUserDn}"}'/>
            <!-- only specify authUserPassword if you also specify authUserDn -->
            <beans:property name="authUserPassword" value='${r"${ldapConfig.authUserPassword}"}'/>
            <!-- groupSearchBase is the full tree under which groups are found (e.g. ou=groups,dc=armedia,dc=com).  -->
            <beans:property name="searchBase" value='${r"${ldapConfig.groupSearchBase}"}'/>
            <!-- groupSearchFilter is an LDAP filter to restrict which entries under the groupSearchBase are processsed -->
            <beans:property name="ignorePartialResultException" value="true"/>
            <!-- ldapUrl: URL of the ldap instance (e.g. ldap://armedia.com:389) -->
            <beans:property name="ldapUrl" value='${r"${ldapConfig.ldapUrl}"}'/>
            <!-- referral: "follow" if you want to follow LDAP referrals, "ignore" otherwise (search "ldap referral" for more info). -->
            <beans:property name="referral" value="follow"/>
            <!-- userIdAttributeName: use "samAccountName" if your LDAP server is Active Directory.  Most other LDAP
                 servers use "uid". -->
            <beans:property name="userIdAttributeName" value='${r"${ldapConfig.userIdAttributeName}"}'/>
        </beans:bean>
    </beans:beans>
</beans:beans>
