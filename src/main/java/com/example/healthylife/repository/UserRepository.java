package com.example.healthylife.repository;

import com.example.healthylife.entity.UserEntity;
import com.example.healthylife.mapping.UserInfoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
   Optional<UserEntity> findByUserId(String userId);
  //  Optional<UserInfoMapping> findByUserId(String userId); //유저 아이디만 가져오기!
}
