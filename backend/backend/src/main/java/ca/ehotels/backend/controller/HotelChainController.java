package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.HotelChain;
import ca.ehotels.backend.repository.HotelChainRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotel-chains")
public class HotelChainController {

    private final HotelChainRepository hotelChainRepository;

    public HotelChainController(HotelChainRepository hotelChainRepository) {
        this.hotelChainRepository = hotelChainRepository;
    }

    @GetMapping
    public List<HotelChain> getAllHotelChains() {
        return hotelChainRepository.findAll();
    }
}
