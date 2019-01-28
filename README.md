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

## IDE Integration

ArkCase is a Maven project with a standard Maven folder layout.  You can load it into your chosen IDE or editor in whichever way is supported by your editor; if your IDE supports starting and launching a war file, this should work in the normal way.  In this guide we can't provide specific guidance for each editor and IDE.



