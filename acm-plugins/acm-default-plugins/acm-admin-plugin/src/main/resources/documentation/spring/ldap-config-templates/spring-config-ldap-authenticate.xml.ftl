<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
       http://www.springframework.org/schema/task http://www.springframework.org/schema/task/spring-task-3.2.xsd">

    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="location" value="file:${propertiesFileName}"/>
    </bean>

	<!--
	Authenticates a user id and password against LDAP directory.  To support multiple LDAP configurations, create multiple Spring 
	beans, each with its own LdapAuthenticateService.
    -->
    <bean id="${id}_ldapAuthenticateService" class="com.armedia.acm.services.users.service.ldap.LdapAuthenticateService">
        <!-- ldapAuthenticateConfig: ref must match an AcmLdapAuthenticateConfig bean, which should be defined below. -->
        <property name="ldapAuthenticateConfig" ref="${id}_authenticate"/>

        <!-- do not change ldapDao properties. -->
        <property name="ldapDao" ref="springLdapDao"/>
    </bean>

    <bean id="${id}_authenticate" class="com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig">
        <!-- only specify authUserDn if your LDAP server requires user authentication (do not specify if you
             are using anonymous authentication -->
        <property name="authUserDn" value='${r"${ldapConfig.authUserDn}"}'/>
        <!-- only specify authUserPassword if you also specify authUserDn -->
        <property name="authUserPassword" value='${r"${ldapConfig.authUserPassword}"}'/>
        <!-- groupSearchBase is the full tree under which groups are found (e.g. ou=groups,dc=armedia,dc=com).  -->
        <property name="searchBase" value='${r"${ldapConfig.groupSearchBase}"}'/>
        <!-- groupSearchFilter is an LDAP filter to restrict which entries under the groupSearchBase are processsed -->
        <property name="ignorePartialResultException" value="true"/>
        <!-- ldapUrl: URL of the ldap instance (e.g. ldap://armedia.com:389) -->
        <property name="ldapUrl" value='${r"${ldapConfig.ldapUrl}"}'/>
        <!-- referral: "follow" if you want to follow LDAP referrals, "ignore" otherwise (search "ldap referral" for more info). -->
        <property name="referral" value="follow"/>
        <!-- userIdAttributeName: use "samAccountName" if your LDAP server is Active Directory.  Most other LDAP
             servers use "uid". -->
        <property name="userIdAttributeName" value='${r"${ldapConfig.userIdAttributeName}"}'/>
    </bean>
</beans>
