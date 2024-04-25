# ArkCase

ArkCase aims to be the leading open source case management and IT modernization platform. After supporting numerous case management and IT modernization initiatives, the team at Armedia developed a framework to accelerate these initiatives and to reduce the cost of implementation.  That framework matured and is the basis for ArkCase.  We have and will continue to invest in making ArkCase a premier platform for IT modernization.  As a thank you to our customers who embarked on this journey with us and a thank you to all the software engineers that have contributed open source technologies to advance this industry, ArkCase is now open source!

## Architecture

The ArkCase architecture is described here: https://www.arkcase.com/developer-support/architecture/.  

You can visit https://www.arkcase.com for more information on ArkCase in general.

## ArkCase Setup

This section documents how developers can build and run ArkCase.  (For non-developers, and anyone who just wants to run ArkCase, please read the above section; you don't need to follow the rest of this wiki).

### Prerequisites

- [Helm 3.12.1](https://helm.sh/docs/intro/install/) for managing Kubernetes applications.(Versions above 3.13 are not yet supported)
- A functioning [Kubernetes cluster](https://kubernetes.io/docs/setup/).
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) for interacting with your Kubernetes cluster.
- [Docker](https://docs.docker.com/get-docker/) for containerization.

### Install ArkCase with helm chart

To install ArkCase using helm chart we need to add the helm chart to our local repository.
* Run the following command to add arkcase to repository and to update it. 
```bash
helm repo add arkcase https://arkcase.github.io/ark_helm_charts/
helm repo update
```
* Create the namespace to deploy arkcase
```bash
kubectl create namespace arkcase
```
* Now that the repository is installed and updated we can proceed to deploy the ArkCase CE.
```bash
helm install arkcase arkcase/app -n arkcase
```
* Watch Pods Until Running.
```bash
watch kubectl get pods -n arkcase
```

### Access ArkCase from browser
To access ArkCase from browser we need to port forward the service. For that to be done, get the cluster IP of the `core` service.
* Use this command to get the cluster IP: `kubectl get service core` (Note the CLUSTER-IP).
* Port forward the service: `kubectl port-forward service/core 8443:8443`
* Access ArkCase through `Cluster-IP:8443` in your browser.
     - Open your web browser and access ArkCase using the Cluster IP and port 8443.
     - Replace `Cluster-IP` with the actual **CLUSTER-IP** obtained from the previous step.

   Example:
   If the Cluster IP is `192.168.1.100`, access ArkCase in your browser using: https://192.168.1.100:8443

### Logging into ArkCase

Once you see the ArkCase login page, you can log in with the default administrator account.  User `arkcase-admin@arkcase.org`, password `@rKc@3e`.


## Installing Ubuntu on Your Laptop (Optional)

**Note:** This section is provided for users who wish to install Ubuntu on their laptop specifically to set up a development environment for Kubernetes. If you already have an OS capable of running Kubernetes, or if you already have Ubuntu installed on your laptop, you can skip this step.

### Disclaimer

**Important:** Proceeding with this installation process will reformat your laptop's hard drive, resulting in the loss of all existing data. We do not take responsibility for any data loss incurred. Ensure you have backed up all essential files before continuing.

### Prerequisites

- [Balena Etcher](https://balena.io/etcher) for creating bootable drives.


### Install Ubuntu

1. **Download Ubuntu 22.04 LTS:**
   - Obtain the Ubuntu 22.04 LTS ISO file [here](http://ubuntu.com/download/desktop).

   **Note:** It is crucial to select Ubuntu 22.04 LTS specifically for compatibility with the provided setup steps.
   
2. **Create Bootable USB:**
   - Plug in your USB drive.
   - Open Balena Etcher.
   - Click `Flash from file` and select the downloaded ISO file.
   - Choose your USB drive as the target.
   - Click `Flash` to start the process.
   - Safely eject the USB drive once the process is complete.

### Setting Up Kubernetes on a Development Laptop (Ubuntu)

**Note:** If you already have a Kubernetes cluster, you can skip this step.

This is one way to install Kubernetes on your Ubuntu laptop for development purposes, follow these steps:

1. **Clone Repositories:**
   - Clone the following repositories:
     ```bash
     git clone https//github.com/ArkCase/ark_k8s_init.git
     git clone https://github.com/ArkCase/artifacts-dev.git
     ```

2. **Execute Init Script:**
   - Navigate to the `ark_k8s_init` directory.
   - Run the initialization script:
     ```bash
     cd path/to/ark_k8s_init
     ./ubuntu-k8s-init
     ```

3. **Verify Essential Commands:**
   - Ensure the availability of essential commands:
     - `helm`
     - `kubectl`
     - `docker`

4. **Add User to Docker Group:**
   - Run the following command to add your user to the docker group:
     ```bash
     sudo usermod -aG docker $USER
     ```

5. **Add Cluster Configuration:**
   - Create a directory named `.kube` under the home directory:
     ```bash
     mkdir ~/.kube
     ```
   - Copy the cluster configuration to your user’s `.kube` directory:
     ```bash
     sudo cat /etc/Kubernetes/admin.conf > ~/.kube/config
     ```

6. **Verify Cluster Pods:**
   - Finally, verify the cluster pods' status using:
     ```bash
     sudo kubectl get pods -A
     ```
     Running this command should return all the cluster pods that are currently running.
     
**Additional Note:** The `ubuntu-k8s-init` script was developed by Armedia for our local developers and has been tested only on Ubuntu 22.04 LTS. If you are using a different Ubuntu version or another operating system, please refer to `ark_k8s_init` as a guide and install Kubernetes in a way supported by your OS (using Calico for networking, if applicable).