import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    public String content;
    public String sender;
    public String destination;
    public String newClientName;
    public ArrayList<String> userList;
    /**
     * 0 - userName check expecting true or false, statusCode 0
     * 1 - normal message aka personal message to one person with a sender, and a destination, statusCode 1, content
     * 2 - message to all users (Blast) Sender, statusCode 2, content
     * 3 - group chat message
     * 4 - New client has been added newClientName.
     * 5 - Sending and updated arrayList of clients.
     */
    public int statusCode;
    public boolean usernameAccepted;

    /**
     * Standard messaging constructor to be used for most message objects.
     * @param content
     * @param sender
     * @param destination
     * @param statusCode
     */
    Message(String content, String sender, String destination, int statusCode) {
        this.content = content;
        this.sender = sender;
        this.destination = destination;
        this.statusCode = statusCode;
    }

    /**
     * This is to be used when client is connecting for the first time.
     * @param sender
     * @param statusCode
     * @param usernameAccepted
     */
    Message(String sender, int statusCode, boolean usernameAccepted) {
        this.sender = sender;
        this.statusCode = statusCode;
        this.usernameAccepted = usernameAccepted;
    }

    /**
     * This is to be used to send confirmation if userName is accepted or not.
     * @param usernameAccepted
     */
    Message(boolean usernameAccepted, int statusCode) {
        this.usernameAccepted = usernameAccepted;
        this.statusCode = statusCode;
    }

    /**
     * This constructor is only used to send
     * statusCodes over to the client or the server.
     * @param statusCode
     */
    Message(int statusCode) {
        this.statusCode = statusCode;
        this.content = null;
        this.destination = null;
        this.sender = null;
    }

    /**
     * This constructor is only to be used to send a blast
     * that new client has been added to the server.
     * @param content
     * @param statusCode
     */
    Message(String content, int statusCode) {
        this.content = content;
        this.statusCode = statusCode;
    }

    /**
     * This constructor is for sending the updated cliensList
     * blast to all users upon entry/exit of a user.
     * @param userList
     * @param statusCode
     */
    Message(ArrayList<String> userList, int statusCode) {
        this.userList = userList;
        this.statusCode = statusCode;
    }

}