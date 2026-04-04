package ca.ehotels.backend.model;

public class AvailableRoomsPerAreaDto {
    private String area;
    private Integer availableRooms;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }
}