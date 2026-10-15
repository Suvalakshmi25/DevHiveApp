package com.devhive.DevHive.respository;

import com.devhive.DevHive.entity.Notification;
import com.devhive.DevHive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}