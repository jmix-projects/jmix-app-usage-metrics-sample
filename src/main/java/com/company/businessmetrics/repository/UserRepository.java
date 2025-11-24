package com.company.businessmetrics.repository;

import com.company.businessmetrics.entity.User;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JmixDataRepository<User, UUID> {
 User findUserByUsername(String username);
}