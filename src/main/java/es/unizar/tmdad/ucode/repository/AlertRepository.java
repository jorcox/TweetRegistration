package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.unizar.tmdad.ucode.domain.Alert;

public interface AlertRepository extends MongoRepository<Alert, BigInteger> {
	
	Alert save(Alert a);
	
	Alert findByHash(String hash);
	
	Alert findFirstByOrderByDate();
	
	void delete(Alert a);
	
}
