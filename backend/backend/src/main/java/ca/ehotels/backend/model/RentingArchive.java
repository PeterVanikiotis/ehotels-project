package ca.ehotels.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "renting_archive")
public class RentingArchive {

    @Id
    @Column(name = "renting_id")
    private Integer rentingId;

    @Column(name = "ssn", nullable = false)
    private String ssn;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "driving_license_number", nullable = false)
    private String drivingLicenseNumber;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "actual_check_in_time")
    private LocalDateTime actualCheckInTime;

    @Column(name = "actual_check_out_time")
    private LocalDateTime actualCheckOutTime;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid;

    @Column(name = "paid_on")
    private LocalDateTime paidOn;

    @Column(name = "customer_name_snapshot", nullable = false)
    private String customerNameSnapshot;

    @Column(name = "hotel_name_snapshot", nullable = false)
    private String hotelNameSnapshot;

    @Column(name = "area_snapshot", nullable = false)
    private String areaSnapshot;

    @Column(name = "room_price_snapshot", nullable = false)
    private BigDecimal roomPriceSnapshot;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    public RentingArchive() {}

    public static RentingArchive fromRenting(RentingDto renting) {
        RentingArchive archive = new RentingArchive();
        archive.rentingId = renting.getRentingId();
        archive.ssn = renting.getSsn();
        archive.hotelId = renting.getHotelId();
        archive.roomNumber = renting.getRoomNumber();
        archive.bookingId = renting.getBookingId();
        archive.drivingLicenseNumber = renting.getDrivingLicenseNumber();
        archive.startDatetime = renting.getStartDatetime();
        archive.endDatetime = renting.getEndDatetime();
        archive.actualCheckInTime = renting.getActualCheckInTime();
        archive.actualCheckOutTime = renting.getActualCheckOutTime();
        archive.isPaid = renting.getIsPaid();
        archive.paidOn = renting.getPaidOn();
        archive.customerNameSnapshot = renting.getCustomerNameSnapshot();
        archive.hotelNameSnapshot = renting.getHotelNameSnapshot();
        archive.areaSnapshot = renting.getAreaSnapshot();
        archive.roomPriceSnapshot = renting.getRoomPriceSnapshot();
        archive.archivedAt = LocalDateTime.now();
        return archive;
    }

    public Integer getRentingId() { return rentingId; }
}