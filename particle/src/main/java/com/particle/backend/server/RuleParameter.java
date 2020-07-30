package com.particle.backend.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleParameter {
	public static class AlignmentRuleParameters {		
		@JsonProperty("distance")
	    public float distance;
		
		@JsonProperty("weight")
	    public float weight;
	}
	public static class CohesionRuleParameters {
		@JsonProperty("distance")
	    public float distance;
		
		@JsonProperty("weight")
	    public float weight;
	}
	public static class SeparationRuleParameters {
		@JsonProperty("distance")
	    public float distance;
		
		@JsonProperty("weight")
	    public float weight;
	}
	public static class MoveRuleParameters {		
		@JsonProperty("velocity")
	    public float velocity;
		@JsonProperty("steering")
	    public float steering;
	}	
	
	@JsonProperty("sessionId")
	public String sessionId;
	
	@JsonProperty("workers")
	public int numWorkers;
	
	@JsonProperty("space")
	public float space;
	
	@JsonProperty("segments")
	public float segments;

    @JsonProperty("alignment")
    public AlignmentRuleParameters alignment;
    
    @JsonProperty("cohesion")
    public CohesionRuleParameters cohesion;
    
    @JsonProperty("separation")
    public SeparationRuleParameters separation;
    
    @JsonProperty("move")
    public MoveRuleParameters move;
}
