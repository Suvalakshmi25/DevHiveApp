package com.devhive.DevHive.respository;

import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByUser(User user);

    List<Membership> findByOrganizationId(Long orgId);

    @Query("select m from Membership m where m.user.id = :userId and m.organization.id = :orgId")
    Optional<Membership> findByUserIdAndOrganizationId(@Param("userId") Long userId, @Param("orgId") Long orgId);

    @Transactional
    @Modifying
    @Query("delete from Membership m where m.user.id = :userId and m.organization.id = :orgId")
    void deleteByUserIdAndOrganizationId(@Param("userId") Long userId, @Param("orgId") Long orgId);
}