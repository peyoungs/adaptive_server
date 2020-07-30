package com.particle.backend.server;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonProperty;

class DebugRequest {
	@JsonProperty("sessionId")
	public String sessionId;
	
	@JsonProperty("x")
	public float x;
	
	@JsonProperty("y")
	public float y;
	
	@JsonProperty("z")
	public float z;
}
