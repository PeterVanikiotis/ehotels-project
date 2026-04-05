package ca.ehotels.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDto {
    private Integer bookingId;
    private String drivingLicenseNumber;
    private Integer hotelId;
    private Integer roomNumber;
    private LocalDate startDay;
    private LocalDate endDay;
    private String customerNameSnapshot;
    private String hotelNameSnapshot;
    private String areaSnapshot;
    private BigDecimal roomPriceSnapshot;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private LocalDateTime createdAt;

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

    public LocalDate getStartDay() {
        return startDay;
    }

    public void setStartDay(LocalDate startDay) {
        this.startDay = startDay;
    }

    public LocalDate getEndDay() {
        return endDay;
    }

    public void setEndDay(LocalDate endDay) {
        this.endDay = endDay;
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

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}