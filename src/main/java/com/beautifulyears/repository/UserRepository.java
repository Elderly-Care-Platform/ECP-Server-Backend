package com.beautifulyears.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    public List<User> findAll();
    
    public List<User> findBySocialSignOnId(String socialSignOnId);

    public User findByEmail(String email);

    public User findByPhoneNumber(String phoneNumber);
}

