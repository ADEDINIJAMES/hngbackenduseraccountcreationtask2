package com.tumtech.groupcreationuserhngbackendtsk2.repostory;

import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisations, String> {
}
