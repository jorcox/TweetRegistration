package es.unizar.tmdad.ucode.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
//import org.springframework.data.repository.Repository;

import es.unizar.tmdad.ucode.domain.Hackathon;
import es.unizar.tmdad.ucode.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, BigInteger> {
	
	User findByMail(String mail);
	
	User findByProvider(Serializable provider);
	
	User save(User u);
	
	User findByHackathonsContaining(Hackathon hack);
	
	void deleteById(BigInteger id);
	
	void deleteByMail(String mail);
	
	List<User> findAll();
	
}