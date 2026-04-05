package ca.ehotels.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentingDto {
    private Integer rentingId;
    private String ssn;
    private Integer hotelId;
    private Integer roomNumber;
    private Integer bookingId;
    private String drivingLicenseNumber;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean isPaid;
    private String customerNameSnapshot;
    private String hotelNameSnapshot;
    private String areaSnapshot;
    private BigDecimal roomPriceSnapshot;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private LocalDateTime paidOn;

    public Integer getRentingId() {
        return rentingId;
    }

    public void setRentingId(Integer rentingId) {
        this.rentingId = rentingId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean paid) {
        isPaid = paid;
    }

    public String getCustomerNameSnapshot() {
        return customerNameSnapshot;
    }

    public void setCustomerNameSnapshot(String customerNameSnapshot) {
        this.customerNameSnapshot = customerNameSnapshot;
    }

    public String getHotelNameSnapshot() {
        return hotelNameSnapshot;
    }

    public void setHotelNameSnapshot(String hotelNameSnapshot) {
        this.hotelNameSnapshot = hotelNameSnapshot;
    }

    public String getAreaSnapshot() {
        return areaSnapshot;
    }

    public void setAreaSnapshot(String areaSnapshot) {
        this.areaSnapshot = areaSnapshot;
    }

    public BigDecimal getRoomPriceSnapshot() {
        return roomPriceSnapshot;
    }

    public void setRoomPriceSnapshot(BigDecimal roomPriceSnapshot) {
        this.roomPriceSnapshot = roomPriceSnapshot;
    }

    public LocalDateTime getActualCheckInTime() {
        return actualCheckInTime;
    }

    public void setActualCheckInTime(LocalDateTime actualCheckInTime) {
        this.actualCheckInTime = actualCheckInTime;
    }

    public LocalDateTime getActualCheckOutTime() {
        return actualCheckOutTime;
    }

    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) {
        this.actualCheckOutTime = actualCheckOutTime;
    }

    public LocalDateTime getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDateTime paidOn) {
        this.paidOn = paidOn;
    }
}