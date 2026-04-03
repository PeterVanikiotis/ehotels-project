package ca.ehotels.backend.model;

public class CheckoutRentingRequest {
    private String ssn;
    private Integer rentingId;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Integer getRentingId() {
        return rentingId;
    }

    public void setRentingId(Integer rentingId) {
        this.rentingId = rentingId;
    }
}