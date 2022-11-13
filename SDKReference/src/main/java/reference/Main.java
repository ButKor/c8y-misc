package reference;

import reference.sdk.C8YApi;
import reference.sdk.util.AuthMethod;
import reference.sdk.C8yApiException;

public class Main {
    public static void main(String[] args) {
        new Main().run();

    }

    private void run() {
        try {
            C8YApi api = new C8YApi(AuthMethod.OAUTH);
            String response = api.getInventoryApi().getManagedObjectsByType("myType");
            System.out.println("API Response (Inventory API): \n" + response);
            String eventId = api.getEventApi()
                    .createEvent("157935884", "myEventType", "My test event", "2022-10-24T20:11:39.864Z");
            System.out.println("Created Event with id: " + eventId);
        } catch (C8yApiException e) {
            System.out.println("Exception while executing REST Requests against Cumulocity: " +  e.describe());
        }

    }
}