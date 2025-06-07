package Test2;

public class GeneralUser extends User {
    public GeneralUser(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    @Override
    public String getRole() {
        return "general";
    }
}
