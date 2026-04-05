package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.RentingArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentingArchiveRepository extends JpaRepository<RentingArchive, Integer> {
}