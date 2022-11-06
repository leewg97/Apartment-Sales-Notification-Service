package com.dev.be.aptbatch.core.repository;

import com.dev.be.aptbatch.core.entity.AptNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AptNotificationRepository extends JpaRepository<AptNotification, Long> {
}
