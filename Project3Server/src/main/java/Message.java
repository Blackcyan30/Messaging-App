import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    public String content;
    public String sender;
    public String destination;
    public int actionCode;

    Message(String content, String sender, String destination) {
        this.content = content;
        this.sender = sender;
        this.destination = destination;
    }
}
