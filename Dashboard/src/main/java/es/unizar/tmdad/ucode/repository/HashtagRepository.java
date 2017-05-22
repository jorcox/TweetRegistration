package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.unizar.tmdad.ucode.domain.Hashtag;

@Repository
public interface HashtagRepository extends MongoRepository<Hashtag, BigInteger> {
	
	Hashtag findByName(String name);
	
	Hashtag save(Hashtag hashtag);
	
	void deleteByName(String name);
	
	List<Hashtag> findAll();

}
