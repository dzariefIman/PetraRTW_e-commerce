package petra.model;

public class Staff {
    private int staffId;
    private String staffName;
    private String username;
    private String password;
    private String staffEmail;
    private String staffAddress;
    private String staffPhoneNum;
    private String profilePicture;

    public Staff() {}
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStaffEmail() { return staffEmail; }
    public void setStaffEmail(String staffEmail) { this.staffEmail = staffEmail; }
    public String getStaffAddress() { return staffAddress; }
    public void setStaffAddress(String staffAddress) { this.staffAddress = staffAddress; }
    public String getStaffPhoneNum() { return staffPhoneNum; }
    public void setStaffPhoneNum(String staffPhoneNum) { this.staffPhoneNum = staffPhoneNum; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}
