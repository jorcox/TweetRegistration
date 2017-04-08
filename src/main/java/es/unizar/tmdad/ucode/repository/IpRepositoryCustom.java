package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;

import es.unizar.tmdad.ucode.domain.Ip;

public interface IpRepositoryCustom {
	
	Ip findSubnet(BigInteger ip);
	
}
