package io.github.fernandapcaetano.phastfin_backend.statement.domain.repository;

import io.github.fernandapcaetano.phastfin_backend.statement.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrganizationRepository extends JpaRepository<Organization, Long> {
    @Query("""
    SELECT o.id FROM Organization o
    WHERE o.externalId = :externalId
    """)
    Optional<Long> findIdByExternalId(@Param("externalId") UUID organizationExternalId);
}
