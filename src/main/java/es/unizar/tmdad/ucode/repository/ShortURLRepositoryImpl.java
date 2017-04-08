package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import es.unizar.tmdad.ucode.domain.ShortURL;

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepositoryCustom{
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public List<ShortURL> list(BigInteger limit, BigInteger offset) {
		return mongoTemplate.findAll(ShortURL.class).subList(offset.intValue(), limit.intValue());		
	}
	
	@Override
	public void update(ShortURL su) {
		mongoTemplate.save(su);		
	}

}
