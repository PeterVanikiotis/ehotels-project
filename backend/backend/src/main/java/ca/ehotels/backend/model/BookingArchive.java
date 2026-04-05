package ca.ehotels.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_archive")
public class BookingArchive {

    @Id
    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "driving_license_number", nullable = false)
    private String drivingLicenseNumber;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @Column(name = "start_day", nullable = false)
    private LocalDate startDay;

    @Column(name = "end_day", nullable = false)
    private LocalDate endDay;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "customer_name_snapshot", nullable = false)
    private String customerNameSnapshot;

    @Column(name = "hotel_name_snapshot", nullable = false)
    private String hotelNameSnapshot;

    @Column(name = "area_snapshot", nullable = false)
    private String areaSnapshot;

    @Column(name = "room_price_snapshot", nullable = false)
    private BigDecimal roomPriceSnapshot;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    public BookingArchive() {}

    public static BookingArchive fromBooking(BookingDto booking) {
        BookingArchive archive = new BookingArchive();
        archive.bookingId = booking.getBookingId();
        archive.drivingLicenseNumber = booking.getDrivingLicenseNumber();
        archive.hotelId = booking.getHotelId();
        archive.roomNumber = booking.getRoomNumber();
        archive.startDay = booking.getStartDay();
        archive.endDay = booking.getEndDay();
        archive.checkInTime = booking.getCheckInTime();
        archive.checkOutTime = booking.getCheckOutTime();
        archive.customerNameSnapshot = booking.getCustomerNameSnapshot();
        archive.hotelNameSnapshot = booking.getHotelNameSnapshot();
        archive.areaSnapshot = booking.getAreaSnapshot();
        archive.roomPriceSnapshot = booking.getRoomPriceSnapshot();
        archive.createdAt = booking.getCreatedAt();
        archive.archivedAt = LocalDateTime.now();
        return archive;
    }

    public Integer getBookingId() { return bookingId; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public Integer getHotelId() { return hotelId; }
    public Integer getRoomNumber() { return roomNumber; }
    public LocalDate getStartDay() { return startDay; }
    public LocalDate getEndDay() { return endDay; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public String getCustomerNameSnapshot() { return customerNameSnapshot; }
    public String getHotelNameSnapshot() { return hotelNameSnapshot; }
    public String getAreaSnapshot() { return areaSnapshot; }
    public BigDecimal getRoomPriceSnapshot() { return roomPriceSnapshot; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
}