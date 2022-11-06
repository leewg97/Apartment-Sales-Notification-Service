package com.dev.be.aptbatch.core.repository;

import com.dev.be.aptbatch.core.entity.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AptRepository extends JpaRepository<Apt, Long> {
}