package ca.ehotels.backend.model;

public class HotelDto {
    private Integer hotelId;
    private String hotelName;
    private String streetName;
    private String streetNumber;
    private String postalCode;
    private String area;
    private String city;
    private String provinceState;
    private String country;
    private Integer numberOfRooms;
    private Integer rating;

    // Getters and Setters
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }
    public String getStreetNumber() { return streetNumber; }
    public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProvinceState() { return provinceState; }
    public void setProvinceState(String provinceState) { this.provinceState = provinceState; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Integer getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}