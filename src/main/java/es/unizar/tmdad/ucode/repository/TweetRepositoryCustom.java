package es.unizar.tmdad.ucode.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapreduce.GroupByResults;

import es.unizar.tmdad.ucode.domain.Click;
import es.unizar.tmdad.ucode.domain.TargetedTweet;

public interface TweetRepositoryCustom {

	/*long clicksByHash(String hash, Date from, Date to, Float min_latitude,
			Float max_longitude, Float max_latitude, Float min_longitude);*/

	void update(TargetedTweet cl);

	List<TargetedTweet> list(BigInteger limit, BigInteger offset);

	/*GroupByResults<TargetedTweet> getTweetsByCountry(String url, Date from, Date to);

	long clicksByCity(String city, Date from, Date to);

	GroupByResults<TargetedTweet> getClicksByCity(String city, Date from, Date to,
			Float min_latitude, Float max_longitude, Float max_latitude,
			Float min_longitude);*/
}
