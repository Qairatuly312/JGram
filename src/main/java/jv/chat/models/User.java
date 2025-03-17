package jv.chat.models;

public class User {
    private int id;
    private String username;
    private String password;
    private boolean status;

    public User(int id, String username, String password, boolean status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isOnline() { return status; }

    public void setStatus(boolean status) { this.status = status; }
}
