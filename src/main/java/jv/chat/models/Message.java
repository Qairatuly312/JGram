package jv.chat.models;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String message;
    private LocalDateTime timestamp;

    public Message(int id, int senderId, int receiverId, String message, LocalDateTime timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getContent() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
