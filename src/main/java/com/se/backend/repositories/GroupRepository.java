package com.se.backend.repositories;

import com.se.backend.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
    //    List<Group> findAllByUser(User user);
    List<Group> findAllByLeaderId(Long leaderId);

    List<Group> findAllByMembers_IdAndLeaderIdNot(Long memberId, Long leaderId);
}
