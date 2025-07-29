package timetogeter.context.place.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import timetogeter.context.place.domain.entity.PromisePlace;

import java.util.Optional;

public interface PromisePlaceRepository extends JpaRepository<PromisePlace, String> {
    Optional<PromisePlace> findByPlaceId(int placeId);

    @Query("SELECT p FROM PromisePlace p WHERE p.promiseId = :promiseId")
    Page<PromisePlace> findByPromiseId(@Param("promiseId") String promiseId, PageRequest pageRequest);
}
