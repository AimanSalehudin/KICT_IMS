package Test2;

public class TechnicianUser extends User {
    private String specialization;

    public TechnicianUser(String username, String password, String name, String email) {
        super(username, password, name, email);
    }

    @Override
    public String getRole() {
        return "technician";
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    @Override
    public String toDataString() {
        return super.toDataString() + "," + (specialization != null ? specialization : "");
    }
}
