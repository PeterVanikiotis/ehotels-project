package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.HotelChain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelChainRepository extends JpaRepository<HotelChain, Long> {
}
