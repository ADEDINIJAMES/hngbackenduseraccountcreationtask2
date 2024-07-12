package com.tumtech.groupcreationuserhngbackendtsk2.repostory;

import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisations, String> {
    Set<Organisations> findByUsers(Users users);
}
