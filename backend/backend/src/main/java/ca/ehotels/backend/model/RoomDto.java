package ca.ehotels.backend.model;

import java.math.BigDecimal;

public class RoomDto {
    private Integer hotelId;
    private Integer roomNumber;
    private BigDecimal price;
    private Integer roomCapacity;
    private String roomViewType;
    private String damageStatus;
    private Boolean hasTv;
    private Boolean hasAirConditioner;
    private Boolean hasFridge;
    private Boolean roomExtendedStatus;
    private String problemDescription;

    // getters + setters
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }

    public Integer getRoomNumber() { return roomNumber; }
    public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getRoomCapacity() { return roomCapacity; }
    public void setRoomCapacity(Integer roomCapacity) { this.roomCapacity = roomCapacity; }

    public String getRoomViewType() { return roomViewType; }
    public void setRoomViewType(String roomViewType) { this.roomViewType = roomViewType; }

    public String getDamageStatus() { return damageStatus; }
    public void setDamageStatus(String damageStatus) { this.damageStatus = damageStatus; }

    public Boolean getHasTv() { return hasTv; }
    public void setHasTv(Boolean hasTv) { this.hasTv = hasTv; }

    public Boolean getHasAirConditioner() { return hasAirConditioner; }
    public void setHasAirConditioner(Boolean hasAirConditioner) { this.hasAirConditioner = hasAirConditioner; }

    public Boolean getHasFridge() { return hasFridge; }
    public void setHasFridge(Boolean hasFridge) { this.hasFridge = hasFridge; }

    public Boolean getRoomExtendedStatus() { return roomExtendedStatus; }
    public void setRoomExtendedStatus(Boolean roomExtendedStatus) { this.roomExtendedStatus = roomExtendedStatus; }

    public String getProblemDescription() {return problemDescription; }

    public void setProblemDescription(String problemDescription) {this.problemDescription = problemDescription; }
}