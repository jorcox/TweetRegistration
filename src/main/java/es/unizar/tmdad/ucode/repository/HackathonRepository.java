package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.unizar.tmdad.ucode.domain.Hackathon;

public interface HackathonRepository  extends MongoRepository<Hackathon, BigInteger> , HackathonRepositoryCustom{
	
	Hackathon save(Hackathon hack);

	void deleteAll();
		
	void delete(BigInteger id);

	long count();
	
	

}
