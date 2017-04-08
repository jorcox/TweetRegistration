package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.unizar.tmdad.ucode.domain.Ip;

public interface IpRepository extends MongoRepository<Ip, BigInteger>, IpRepositoryCustom {
	
	Ip save(Ip ip);
	
	void deleteAll();
	
	void delete(BigInteger id);
	
	long count();
	
}
