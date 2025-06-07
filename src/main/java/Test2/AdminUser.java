package Test2;

public class AdminUser extends User {
    public AdminUser(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    @Override
    public String getRole() {
        return "admin";
    }
}
