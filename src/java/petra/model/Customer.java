package petra.model;

public class Customer {
    private int custId;
    private String custName;
    private String username;
    private String password;
    private String custEmail;
    private String custAddress;
    private String custPhoneNum;

    public Customer() {}
    public int getCustId() { return custId; }
    public void setCustId(int custId) { this.custId = custId; }
    public String getCustName() { return custName; }
    public void setCustName(String custName) { this.custName = custName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCustEmail() { return custEmail; }
    public void setCustEmail(String custEmail) { this.custEmail = custEmail; }
    public String getCustAddress() { return custAddress; }
    public void setCustAddress(String custAddress) { this.custAddress = custAddress; }
    public String getCustPhoneNum() { return custPhoneNum; }
    public void setCustPhoneNum(String custPhoneNum) { this.custPhoneNum = custPhoneNum; }
}
