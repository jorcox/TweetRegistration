package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;
import java.util.List;

import es.unizar.tmdad.ucode.domain.ShortURL;

public interface ShortURLRepositoryCustom {
	
	List<ShortURL> list(BigInteger limit, BigInteger offset);
	
	void update(ShortURL su);
	
}
