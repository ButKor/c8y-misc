package reference.sdk;

public class C8yApiException extends Exception {

    private String details;

    public C8yApiException (String message){
        super(message);
    }

    public C8yApiException (String message, String details){
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public String describe(){
        StringBuilder sb = new StringBuilder();
        sb.append("Message = " + getMessage());
        if(getDetails() != null){
            sb.append("; Details = " + getDetails());
        }
        return sb.toString();
    }
}
