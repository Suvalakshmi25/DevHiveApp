package com.devhive.DevHive.respository;

import com.devhive.DevHive.entity.Membership;
import com.devhive.DevHive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByUser(User user);

    List<Membership> findByOrganizationId(Long orgId);

    Optional<Membership> findByUserIdAndOrganizationId(Long userId, Long orgId);

    void deleteByUserIdAndOrganizationId(Long userId, Long orgId);
}