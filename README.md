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

