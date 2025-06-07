package Test2.Java.model;

public abstract class User {
    private String username;
    private String password;
    private String name;
    private String email;

    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public abstract String getRole();

    // Getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public String toDataString() {
        return String.join(",", username, password, getRole(), name, email);
    }

    public static User fromDataString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) return null;

        String username = parts[0];
        String password = parts[1];
        String role = parts[2];
        String name = parts[3];
        String email = parts[4];

        switch (role) {
            case "admin":
                return new AdminUser(username, password, name, email);
            case "technician":
                return new TechnicianUser(username, password, name, email);
            default:
                return new GeneralUser(username, password, name, email);
        }
    }
}
