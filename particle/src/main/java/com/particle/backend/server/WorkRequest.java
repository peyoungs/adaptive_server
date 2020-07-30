package com.particle.backend.server;

import com.fasterxml.jackson.annotation.JsonProperty;

class WorkRequest {
	@JsonProperty("sessionId")
    public String sessionId;
	@JsonProperty("deltaTime")
    public float deltaTime;
}
