/**
 * Jun 25, 2015
 * Nitin
 * 10:24:49 AM
 */
package com.beautifulyears.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.DiscussLike;

@Repository
public interface DiscussLikeRepository extends MongoRepository<DiscussLike, String>{

}
