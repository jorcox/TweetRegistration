package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.unizar.tmdad.ucode.domain.TargetedTweet;

@Repository
public interface TweetRepository extends MongoRepository<TargetedTweet, BigInteger> , TweetRepositoryCustom {
	
	//List<TargetedTweet> findByText(String text);
	
	//long countByHash(String hash);

	TargetedTweet save(TargetedTweet tweet);
	
	List<TargetedTweet> findByTargetsContaining(String hackathon);

	void deleteAll();
		
	void delete(BigInteger id);

	long count();
}

