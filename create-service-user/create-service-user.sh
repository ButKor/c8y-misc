#!/usr/bin/env bash

# configure service details
SERVICE_NAME=hello
SERVICE_ROLES='["ROLE_EVENT_READ","ROLE_EVENT_ADMIN","ROLE_ALARM_READ","ROLE_ALARM_ADMIN"]'

# configure tenant connection
TENANT_BASEURL=https://example.cumulocity.com
TENANT_ID=t1234
TENANT_ADMIN_USER=john.doe@softwareag.com
TENANT_ADMIN_PASS="a-super-secret-pass"

# if TRUE script deletes existing application with given name if already existing (useful for testing & updating roles)
REMOVE_APP_IF_EXISTS=TRUE

echo "Script started..."
echo ""

AUTHORIZATION="Basic $(echo -n "$TENANT_ID/$TENANT_ADMIN_USER:$TENANT_ADMIN_PASS" | base64 | tr -d '\n')"

# 0. Optional step: remove application if it already exists
if [ "$REMOVE_APP_IF_EXISTS" = "TRUE" ]; then
    echo "Setting 'remove app if exists' is activated. Checking for existance..."
    response=$(curl -s -k -X 'GET' -H 'Accept: application/json' \
        -H "Authorization: $AUTHORIZATION" \
        "$TENANT_BASEURL/application/applications?pageSize=2000&type=MICROSERVICE")
    current_app_id=$(echo "$response" |  jq -r '.applications[] | select(.name == "'"$SERVICE_NAME"'") | .id')
    if [ ${#current_app_id} -gt 0 ]; then
        echo "Application already existing (ID = $current_app_id). Removing now..."
        curl -s -k -X 'DELETE' -H 'Accept: application/json'\
            -H "Authorization: $AUTHORIZATION" \
            "$TENANT_BASEURL/application/applications/$current_app_id"
        echo "Application removed"
    else
        echo "No application with current name existing yet. Nothing to remove."
    fi
    echo ""
fi

# 1. Create an Application including the required permissions/roles
echo "Creating an (empty) application..."
response=$(curl -s -k -X 'POST' \
    -d '{"contextPath":"'"$SERVICE_NAME"'","key":"'"$SERVICE_NAME"'-key","name":"'"$SERVICE_NAME"'","requiredRoles":'"$SERVICE_ROLES"',"type":"MICROSERVICE"}' \
    -H 'Accept: application/json' \
    -H "Authorization: $AUTHORIZATION" \
    -H 'Content-Type: application/json' \
    "$TENANT_BASEURL/application/applications")
echo "Server Response: $response"
app_id=$(echo "$response" | jq -r .id)
echo "Extracted Application ID: $app_id"
echo ""

# 2. Activate this application
echo "Activating the application..."
response=$(curl -s -k -X 'POST' -d '{"application":{"id":"'"$app_id"'"}}' -H 'Accept: application/json' -H "Authorization: $AUTHORIZATION" \
    -H 'Content-Type: application/json' "$TENANT_BASEURL/tenant/tenants/$TENANT_ID/applications")
echo "Server Response: $response"
echo ""

# 3. Get Bootstrap User from this application
echo "Extracting bootstrap user from the application..."
response=$(curl -s -k -X 'GET' -H 'Accept: application/json' -H "Authorization: $AUTHORIZATION" "$TENANT_BASEURL/application/applications/$app_id/bootstrapUser")
bootstrap_user=$(echo "$response" | jq -r .name)
bootstrap_pass=$(echo "$response" | jq -r .password)
echo "Found bootstrap user: $bootstrap_user"
echo ""

# 4. Get Service-User from this application (important: do this call using the bootstrap user of step 3!)
echo "Getting Service User..."
AUTHORIZATION_BOOTSTRAP_USER="Basic $(echo -n "$TENANT_ID/$bootstrap_user:$bootstrap_pass" | base64 | tr -d '\n')"
response=$(curl -s -k -X 'GET' -H 'Accept: application/json' -H "Authorization: $AUTHORIZATION_BOOTSTRAP_USER" "$TENANT_BASEURL/application/currentApplication/subscriptions")
echo "Server response: $response"
service_user=$(echo "$response" | jq -r '.users[0].name')
service_pass=$(echo "$response" | jq -r '.users[0].password')
service_tenant=$(echo "$response" | jq -r '.users[0].tenant')
echo ""

# 5. Now login via this user and query user-details and granted permissions
AUTHORIZATION_SERVICE_USER="Basic $(echo -n "$TENANT_ID/$service_user:$service_pass" | base64 | tr -d '\n')"
echo "Login as your new service-user and query its granted permissions ..."
response=$(curl -s -k -X 'GET' -H 'Accept: application/json' \
    -H "Authorization: $AUTHORIZATION_SERVICE_USER" \
    "$TENANT_BASEURL/user/currentUser")
user_id=$(echo "$response" | jq -r .userName)
user_permissions=$(echo "$response" | jq '[.effectiveRoles[].id]' -c)


echo ""
echo "Script finished. Created service user:"
echo "  Username: $user_id"
echo "  Password: $service_pass"
echo "  Tenant: $service_tenant"
echo "  Permissions: $user_permissions"