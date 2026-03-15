package ca.ehotels.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel_chain")
public class HotelChain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "central_office_id")
    private Long centralOfficeId;

    @Column(name = "chain_name")
    private String chainName;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "building_number")
    private String buildingNumber;

    @Column(name = "postal_code")
    private String postalCode;

    public Long getCentralOfficeId() {
        return centralOfficeId;
    }

    public void setCentralOfficeId(Long centralOfficeId) {
        this.centralOfficeId = centralOfficeId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
