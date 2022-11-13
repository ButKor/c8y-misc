package reference.sdk;

import reference.sdk.apis.InventoryApi;
import reference.sdk.util.AuthMethod;
import reference.sdk.apis.EventApi;

public class C8YApi {

    private final InventoryApi inventoryApi;
    private final EventApi eventApi;

    public static int SUCCESS_CREATE_OBJECT_STATUS = 201;
    public static int SUCCESS_GET_OBJECT_STATUS = 200;

    public C8YApi(AuthMethod authMethod) throws C8yApiException {
        C8YClient client = new C8YClient(authMethod);
        this.inventoryApi = new InventoryApi(client);
        this.eventApi = new EventApi(client);
    }

    public InventoryApi getInventoryApi() {
        return inventoryApi;
    }

    public EventApi getEventApi() {
        return eventApi;
    }
}
