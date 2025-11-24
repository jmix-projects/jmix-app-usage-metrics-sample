package com.company.businessmetrics.repository;

import com.company.businessmetrics.entity.LoginEvent;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginEventRepository extends JmixDataRepository<LoginEvent, UUID> {
}