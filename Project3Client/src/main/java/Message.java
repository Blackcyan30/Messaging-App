// import java.io.Serializable;

// public class Message implements Serializable {
//     static final long serialVersionUID = 42L;

//     private String content;
//     private String source;
//     private String destination;

//     public Message(String source, String destination, String content) {
//         this.source = source;
//         this.destination = destination;
//         this.content = content;
//     }

//     public String getContent() {
//         return content;
//     }

//     public void setContent(String content) {
//         this.content = content;
//     }

//     public String getSource() {
//         return source;
//     }

//     public void setSource(String source) {
//         this.source = source;
//     }

//     public String getDestination() {
//         return destination;
//     }

//     public void setDestination(String destination) {
//         this.destination = destination;
//     }
// }

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 42L;

    private String sender;
    private String destination;
    private String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.destination = "";  // Can be set to a default or controlled by setters
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getContent() {
        return content;
    }
}
