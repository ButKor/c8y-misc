
# About

A script to create a Service-User in Cumulocity (without having an actual Service running/hosted in Cumulocity). This is useful to create technical-users that can be used in third party applications to access Cumulocity's API. 

# Prerequisites

The script requires to be installed:
* curl (https://curl.se/)
* tr (part of https://www.gnu.org/software/coreutils/)
* jq (https://jqlang.github.io/jq/download/)

In case your system is missing these, one can also start a container via `docker run -it --rm ghcr.io/reubenmiller/c8y-shell` and run it from there. 

# Run the script

First, configure Service Name, it's desired API permissions and the tenant details in the header of the script. Now make this script executable and start it:

```sh
$ chmod +x create-service-user.sh
$ ./create-service-user.sh
```

Once started, the script will create the service-user and output its users name, password and granted permissions to console. Sample output:

```text
$ ./create-service-user.sh
Script started...

Setting 'remove app if exists' is activated. Checking for existance...
Application already existing (ID = 106526). Removing now...
Application removed

Creating an (empty) application...
Server Response: {"owner":{"self":"https://t12345.eu-latest.cumulocity.com/tenant/tenants/t12345","tenant":{"id":"t12345"}},"requiredRoles":["ROLE_EVENT_READ","ROLE_EVENT_ADMIN","ROLE_ALARM_READ","ROLE_ALARM_ADMIN"],"manifest":{"requiredRoles":[],"roles":[],"billingMode":"RESOURCES","noAppSwitcher":true,"settingsCategory":null},"roles":[],"contextPath":"hello","availability":"PRIVATE","type":"MICROSERVICE","name":"hello","self":"https://t12345.eu-latest.cumulocity.com/application/applications/106527","id":"106527","key":"hello-key"}
Extracted Application ID: 106527

Activating the application...
Server Response: {"self":"https://t12345.eu-latest.cumulocity.com/http://t12345.eu-latest.cumulocity.com/tenant/tenants/t12345/106527","application":{"owner":{"self":"https://t12345.eu-latest.cumulocity.com/tenant/tenants/t12345","tenant":{"id":"t12345"}},"requiredRoles":["ROLE_EVENT_READ","ROLE_EVENT_ADMIN","ROLE_ALARM_READ","ROLE_ALARM_ADMIN"],"manifest":{"requiredRoles":[],"roles":[],"billingMode":"RESOURCES","noAppSwitcher":true,"settingsCategory":null},"roles":[],"contextPath":"hello","availability":"MARKET","type":"MICROSERVICE","name":"hello","self":"https://t12345.eu-latest.cumulocity.com/application/applications/106527","id":"106527","key":"hello-key"}}

Extracting bootstrap user from the application...
Found bootstrap user: servicebootstrap_hello

Getting Service User...
Server response: {"users":[{"password":"{obfuscated}","name":"service_hello","tenant":"t12345"}]}

Login as your new service-user and query its granted permissions ...

Script finished. Created service user:
  Username: service_hello
  Password: {obfuscated}
  Tenant: t12345
  Permissions: ["ROLE_EVENT_READ","ROLE_SYSTEM","ROLE_ALARM_READ","ROLE_ALARM_ADMIN","ROLE_EVENT_ADMIN"]
```