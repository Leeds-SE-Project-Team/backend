package com.se.backend.repositories;


import com.se.backend.models.GroupCollection;
import com.se.backend.models.Group;
import com.se.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupCollectionRepository extends JpaRepository<GroupCollection, Long>, JpaSpecificationExecutor<GroupCollection> {

    List<GroupCollection> findByGroup(Group groupToDelete);

    List<GroupCollection> findAllByUser(User user);

    List<GroupCollection> findAllByGroup(Group group);
}
