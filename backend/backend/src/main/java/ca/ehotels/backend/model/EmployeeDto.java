package ca.ehotels.backend.model;

public class EmployeeDto {

    private String ssn;
    private String firstName;
    private String middleName;
    private String lastName;
    private String streetName;
    private String streetNumber;
    private String postalCode;
    private String role;

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getStreetNumber() { return streetNumber; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}