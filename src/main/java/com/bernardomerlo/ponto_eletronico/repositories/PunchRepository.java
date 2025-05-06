package com.bernardomerlo.ponto_eletronico.repositories;

import com.bernardomerlo.ponto_eletronico.entities.PunchClock;
import com.bernardomerlo.ponto_eletronico.enums.PunchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface PunchRepository extends JpaRepository<PunchClock, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM PunchClock p
        WHERE p.user.id = :userId
        AND p.type = :type
        AND function('date', p.timestamp) = CURRENT_DATE
        """)
    boolean existsTodayByType(@Param("userId") Long userId, @Param("type") PunchType type);
    Optional<ArrayList<PunchClock>> findByUserId(Long userId);

}
