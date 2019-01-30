# ArkCase
====

ArkCase aims to be the leading open source case management and IT modernization platform. After supporting numerous case management and IT modernization initiatives, the team at Armedia developed a framework to accelerate these initiatives and to reduce the cost of implementation.  That framework matured and is the basis for ArkCase.  We have and will continue to invest in making ArkCase a premier platform for IT modernization.  As a thank you to our customers who embarked on this journey with us and a thank you to all the software engineers that have contributed open source technologies to advance this industry, ArkCase is now open source!

## Developer Setup
This section documents how developers can build and run ArkCase.

### Prerequisites
* Java 8 (AdoptOpenJDK JVM works well).  Note, ArkCase is not tested on Java 9, Java 10, or Java 11.
* Maven 3.5+
* VirtualBox
* Vagrant
* Tomcat 9
* git
* nodejs; for MacOS you must use Node 6.  Node 8 and Node 11 do not work on MacOS.  For Windows and Linux, use Node 8 or above.
* npm 
* yarn

### Install the Vagrant VM
ArkCase uses a set of services including Solr, ActiveMQ, MySQL, Alfresco, and Pentaho.  Install a pre-build Vagrant box with all these services already installed.

First, install all the prerequisites.

Next, install a Vagrant plugin:

```
vagrant plugin install vagrant-hostsupdater
```

Next, create a file named `Vagrantfile` in the empty folder of your choice, with the contents below:
```
Vagrant.configure("2") do |config|
  config.vm.box = "davidocmiller/arkcase-ce-for-devs"
  config.vm.box_version = "0.0.1"
  config.vm.network "private_network", ip: "192.168.56.15"
  config.vm.hostname = "arkcase-ce.local"
  config.hostsupdater.remove_on_suspend = false
end
```

Next, inside this folder, run the command `vagrant up`.  The box file is 6G; the download may take a few minutes.

Windows note: sometimes the `vagrant-hostsupdater` plugin doesn't trigger a UAC prompt to allow non-admin users to edit the hosts file.  If you see an error that the hosts file could not be updated, then edit the Vagrantfile to appear as follows, and manually update your `C:\Windows\system32\drivers\etc\hosts` file to include the line `192.168.56.15 arkcase-ce.local`.

```
# this Vagrantfile is for Windows hosts where the Vagrant hosts-updater plugin is not able to update the hosts file.
Vagrant.configure("2") do |config|
  config.vm.box = "davidocmiller/arkcase-ce-for-devs"
  config.vm.box_version = "0.0.1"
  config.vm.network "private_network", ip: "192.168.56.15"
  #config.vm.hostname = "arkcase-ce.local"
  #config.hostsupdater.remove_on_suspend = false
end
```

After the box is up, the following URLs should work from your browser; although you will have to accept the self-signed ArkCase certificate.

https://arkcase-ce.local/solr

https://arkcase-ce.local/share

https://arkcase-ce.local/pentaho

https://arkcase-ce.local/VirtualViewerJavaHTML5 (expect a 503 error from this URL)

## Clone the repository and build the war file

Clone this repository to a folder of your choice.

`cd` to the root folder; `mvn -DskipITs clean install`.  This will run the unit tests and build the war file.  It should take a few minutes.

## Clone the configuration folder

ArkCase requires a configuration folder which is housed in another GitHub repository: https://github.com/ArkCase/.arkcase.  Clone this repository into your home folder.  *This repository must be cloned to your home folder.* . ArkCase will not work without a .arkcase folder in your home folder.

## Copy TLS keys and certificates from the Vagrant box

You need to copy the self-signed keys, certifications, key stores, and trust stores from the Vagrant box to the configuration folder.

```
cd ~/.arkcase/acm/private
scp vagrant@arkcase-ce.local:/etc/ssl/ca/arkcase-ca.crt .
scp vagrant@arkcase-ce.local:/etc/ssl/private/acm-arkcase.rsa.pem .
scp vagrant@arkcase-ce.local:/etc/ssl/crt/acm-arkcase.crt .
scp vagrant@arkcase-ce.local:/opt/common/arkcase.ks .
scp vagrant@arkcase-ce.local:/opt/common/arkcase.ts .
```

The password for the `vagrant` user is `vagrant`, as per the Vagrant box guidelines.

## Configure Tomcat

Make sure the Tomcat native connector library is being used.  MacOS: open terminal, issue the command `brew install tomcat-native`, and follow any directions you see at the end.  Windows: download from https://tomcat.apache.org/download-native.cgi.  Linux: Information on building for Linux is available from the same URL (https://tomcat.apache.org/download-native.cgi).

In your Tomcat 9 installation, edit the `conf/server.xml` file, and add the following connector, below the existing connector for port 8080:

```xml
    <Connector port="8843"
           maxThreads="150" SSLEnabled="true" secure="true" scheme="https"
           maxHttpHeaderSize="32768"
           connectionTimeout="40000"
           useBodyEncodingForURI="true"
           address="0.0.0.0">
      <UpgradeProtocol className="org.apache.coyote.http2.Http2Protocol" />
      <SSLHostConfig protocols="TLSv1.2" certificateVerification="none">
        <Certificate certificateFile="${user.home}/.arkcase/acm/private/acm-arkcase.crt"
                    certificateKeyFile="${user.home}/.arkcase/acm/private/acm-arkcase.rsa.pem"
                    certificateChainFile="${user.home}/.arkcase/acm/private/arkcase-ca.crt"
                    type="RSA" />
      </SSLHostConfig>
    </Connector>
```
Also, search for the text `Listener className="org.apache.catalina.core.AprLifecycleListener"`, and make sure to add the `useAprConnector="true"` attribute, so it ends like this:

```xml
<Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" useAprConnector="true"/>
``` 

Create the file `bin/setenv.sh`, mark it executable, and set the contents as the following, *being careful to set the correct path to the Tomcat native library*:

```bash
#!/bin/sh

### MacOS X note: replace {user.home} with the actual path to your home folder, e.g. /Users/dmiller
export JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djavax.net.ssl.keyStorePassword=password -Djavax.net.ssl.trustStorePassword=password -Djavax.net.ssl.keyStore=${user.home}/.arkcase/acm/private/arkcase.ks -Djavax.net.ssl.trustStore=${user.home}/.arkcase/acm/private/arkcase.ts -Dspring.profiles.active=ldap -Xms1024M -Xmx1024M"

export NODE_ENV=development

export CATALINA_OPTS="$CATALINA_OPTS -Djava.library.path=(PATH TO THE TOMCAT NATIVE LIBRARY)
# MacOS Example: export CATALINA_OPTS=/usr/local/opt/tomcat-native/lib"

export CATALINA_PID=$CATALINA_HOME/temp/catalina.pid
```
On MacOS X, you have to replace `file:${user.home}` in the above script, with the actual full path to your home folder.

Now you should be able to start Tomcat: `$TOMCAT_HOME/bin/startup.sh`.  To shutdown Tomcat: `$TOMCAT_HOME/bin/shutdown.sh -force`.

## Trusting the self-signed ArkCase certificate

When you open ArkCase in your browser, you will have to trust the self-signed cert.  The cert is signed by a self-signed ArkCase certificate authority.  Follow the right procedure for your operating system to trust this certificate.

MacOS: A good guide is here, https://www.accuweaver.com/2014/09/19/make-chrome-accept-a-self-signed-certificate-on-osx/

## IDE Integration

ArkCase is a Maven project with a standard Maven folder layout.  You can load it into your chosen IDE or editor in whichever way is supported by your editor; if your IDE supports starting and launching a war file, this should work in the normal way.  In this guide we can't provide specific guidance for each editor and IDE.

## Manual war file deployment

Ideally, as you develop ArkCase you will use automated support from your IDE to launch and deploy, as described above.  However, this section shows how to deploy the war file manually, for situations where you want to try ArkCase without IDE integration.

Rename the file `ArkCase/acm-standard-applications/acm-law-enforcement/target/acm-law-enforcement-3.3.0.war` (which the Maven build created a few sections ago) to `arkcase.war`.  Note, the `3.3.0` in the filename will change depending on which version of ArkCase you are building.

Move the file `arkcase.war` to the Tomcat `webapps` folder, then watch the Tomcat `logs/catalina.out` folder.  After a few minutes, the ArkCase webapp should start.
