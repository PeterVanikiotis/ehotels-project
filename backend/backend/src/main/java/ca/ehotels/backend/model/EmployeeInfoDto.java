package ca.ehotels.backend.model;

public class EmployeeInfoDto {
    private String ssn;
    private String employeeName;
    private Integer hotelId;
    private String hotelName;
    private String area;
    private String chainName;

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getChainName() { return chainName; }
    public void setChainName(String chainName) { this.chainName = chainName; }
}