package es.unizar.tmdad.ucode.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.unizar.tmdad.ucode.domain.ShortURL;

public interface ShortURLRepository extends MongoRepository<ShortURL, String>, ShortURLRepositoryCustom{
	
	ShortURL findByHash(String id);

	List<ShortURL> findByTarget(String target);
	
	List<ShortURL> findByOwner(String owner);

	ShortURL save(ShortURL su);

	void delete(String id);

	long count();
	
}


