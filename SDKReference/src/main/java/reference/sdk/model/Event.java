package reference.sdk.model;

public class Event {

    private Source source;
    private String type;
    private String text;
    private final String time;

    public Event(Source source, String type, String text, String time) {
        this.type = type;
        this.text = text;
        this.source = source;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getTime() {
        return time;
    }
}
