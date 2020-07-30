package com.particle.backend.runner;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class Constants {
	public static Cache<String, ActorRunner> actorContexts;
			
	static {
		actorContexts = CacheBuilder.newBuilder()
		       .maximumSize(25)
		       .expireAfterAccess(3, TimeUnit.MINUTES)
		       .build();
	}
}
