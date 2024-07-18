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

### Access ArkCase from Browser

To access ArkCase from a browser, we need to port forward the service. Follow these steps to achieve that:

1. **Get the Cluster IP of the `core` service:**
   - Use this command to get the Cluster IP: `kubectl get service core -n arkcase` (Note the CLUSTER-IP).

2. **Port forward the service:**
   - Run the following command to port forward the service: `kubectl port-forward service/core 8443:8443 -n arkcase`.

3. **Access ArkCase:**

   - **Using Cluster IP:**
     - Open your web browser and access ArkCase using the Cluster IP and port 8443.
     - Replace `Cluster-IP` with the actual **CLUSTER-IP** obtained from the previous step.

       Example:
       If the Cluster IP is `192.168.1.100`, access ArkCase in your browser using: https://192.168.1.100:8443/arkcase/login

   - **Using Localhost:**
     - Open your web browser and access ArkCase using the following URL: https://localhost:8443/arkcase/login

   - **Using Cluster DNS:**
     - Open your web browser and access ArkCase using the following URL: https://core.default.svc.cluster.local:8443/arkcase

Now, you can choose any of the above approaches to access ArkCase from your browser.

### Logging into ArkCase

Once you see the ArkCase login page, you can log in with the default administrator account.  User `arkcase-admin@dev.arkcase.com`, password `$arkcase-admin$`.
