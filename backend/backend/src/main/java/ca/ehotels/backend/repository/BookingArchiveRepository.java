package ca.ehotels.backend.repository;

import ca.ehotels.backend.model.BookingArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingArchiveRepository extends JpaRepository<BookingArchive, Integer> {
}