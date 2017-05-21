package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.unizar.tmdad.ucode.domain.Attendee;

@Repository
public interface AttendeeRepository extends MongoRepository<Attendee, BigInteger> {
	
	Attendee findByMail(String mail);
	
	Attendee findByName(String name);
	
	Attendee save(Attendee attendee);
	
	void deleteById(BigInteger id);
	
	void deleteByMail(String mail);
	
	List<Attendee> findAll();

}
