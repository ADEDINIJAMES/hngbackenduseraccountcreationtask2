package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class OrganisationServiceImplementation {
    private OrganisationRepository organisationRepository;
@Autowired
    public OrganisationServiceImplementation(OrganisationRepository organisationRepositoty){
        this.organisationRepository= organisationRepositoty;
    }
public Organisations createOrganisation (Users users){
    Organisations organisations = new Organisations();
    organisations.setName(users.getFirstName()+"'s"+ " "+"Organisation");
    Set<Users> usersSet = new HashSet<>();
    usersSet.add(users);
    organisations.setUsers(usersSet);
    organisations.setDescription(users.getFirstName()+"'s"+ " "+"Organisation");
    return organisationRepository.save(organisations);
}

}
