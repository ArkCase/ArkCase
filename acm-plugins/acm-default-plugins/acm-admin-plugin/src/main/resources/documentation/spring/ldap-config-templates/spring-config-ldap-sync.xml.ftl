<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:task="http://www.springframework.org/schema/task"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
                            http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-3.2.xsd">

    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="location" value="file:${r'${user.home}'}/.arkcase/acm/spring/spring-config-${id}-ldap.properties"/>
    </bean>

    <bean id="${id}_RoleToGroupProperties"
            class="org.springframework.beans.factory.config.PropertiesFactoryBean" >
        <!-- note: must leave "file:" at the start of the file name for spring
             to be able to read the file; otherwise it will try to read from the
             classpath -->
        <property name="location" value="file:${r'${user.home}'}/.arkcase/acm/applicationRoleToUserGroup.properties"/>
        <property name="ignoreResourceNotFound" value="true"/>
    </bean>


    <!-- change the ref to match the bean name of your ldap sync job; and change the 
         cron to the desired cron expression (see JavaDoc for org.springframework.scheduling.support.CronSequenceGenerator).  
         No other changes are needed. -->
    <task:scheduled-tasks scheduler="ldapSyncTaskScheduler">
        <task:scheduled ref="${id}_ldapSyncJob" method="ldapSync" cron="0 0/30 * * * *"/>
    </task:scheduled-tasks>

    <!-- ensure this bean id is unique across all the LDAP sync beans. -->
    <bean id="${id}_ldapSyncJob" class="com.armedia.acm.services.users.service.ldap.LdapSyncService" init-method="ldapSync">
        <!-- directoryName: must be unique across all LDAP sync beans -->
        <property name="directoryName" value='${r"${ldapConfig.directoryName}"}'/>
        <!-- ldapSyncConfig: ref must match an AcmLdapSyncConfig bean, which should be defined below. -->
        <property name="ldapSyncConfig" ref="${id}_sync"/>

        <!-- do not change ldapDao or ldapSyncDatabaseHelper properties. -->
        <property name="ldapDao" ref="springLdapDao"/>
        <property name="ldapSyncDatabaseHelper" ref="userDatabaseHelper"/>
        <property name="auditPropertyEntityAdapter" ref="auditPropertyEntityAdapter"/>
        <property name="syncEnabled" value="true"/>
    </bean>

    <bean id="${id}_sync" class="com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig">
        <!-- only specify authUserDn if your LDAP server requires user authentication (do not specify if you
             are using anonymous authentication -->
        <property name="authUserDn" value='${r"${ldapConfig.authUserDn}"}'/>
        <!-- only specify authUserPassword if you also specify authUserDn -->
        <property name="authUserPassword" value='${r"${ldapConfig.authUserPassword}"}'/>
        <!-- groupSearchBase is the full tree under which groups are found (e.g. ou=groups,dc=armedia,dc=com).  -->
        <property name="groupSearchBase" value='${r"${ldapConfig.groupSearchBase}"}'/>
        <!-- groupSearchFilter is an LDAP filter to restrict which entries under the groupSearchBase are processsed -->
        <property name="groupSearchFilter" value="(|(objectclass=group)(objectclass=groupofnames))"/>
        <!-- ignorePartialResultException: true if your LDAP server is Active Directory, false for other LDAP servers -->
        <property name="ignorePartialResultException" value="true"/>
        <!-- ldapUrl: URL of the ldap instance (e.g. ldap://armedia.com:389) -->
        <property name="ldapUrl" value='${r"${ldapConfig.ldapUrl}"}'/>
        <!-- referral: "follow" if you want to follow LDAP referrals, "ignore" otherwise (search "ldap referral" for more info). -->
        <property name="referral" value="follow"/>
        <!-- mailAttributeName: use "mail"  Most  LDAP servers use "mail". -->
        <property name="mailAttributeName" value="mail"/>

        <!-- userIdAttributeName: use "samAccountName" if your LDAP server is Active Directory.  Most other LDAP
             servers use "uid". -->
        <property name="userIdAttributeName" value='${r"${ldapConfig.userIdAttributeName}"}'/>
        <property name="roleToGroupMap" ref="${id}_RoleToGroupProperties"/>
        <property name="userDomain" value='${r"${ldapConfig.userDomain}"}'/>
    </bean>
</beans>
