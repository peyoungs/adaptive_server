package com.particle.backend.runner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jme3.math.Vector3f;
import com.particle.backend.server.RuleParameter;

public class Actor {

	@JsonProperty("index")
	int ndx;
	@JsonProperty("position")
	Vector3f position;
	@JsonProperty("velocity")
	Vector3f velocity;	
	
	//used in testing
	@JsonIgnore
	RuleParameter params;
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	@Override
	public String toString() {
		return "Index " + ndx + " Position: " + position + " Velocity: " + velocity;
	}
	
	public Actor(int ndx, RuleParameter params) {
		this.ndx = ndx;
		this.params = params;
	}
	
	public Actor(int ndx, RuleParameter params, Vector3f pos, Vector3f vel) {
		this.ndx = ndx;
		this.params = params;
		position = pos;
		velocity = vel;
	}
	
	Vector3f limitForce(Vector3f vec) {
		if (vec.length() > params.move.steering) {
			vec = vec.normalize().mult(params.move.steering);
		}
		return vec;
	}
	
	Vector3f seek(Vector3f target) {
		Vector3f desired = target.subtract(position).normalize();
		return limitForce(desired.subtract(velocity));
	}
	
	Vector3f separate(Iterable<Actor> itr) {
		Vector3f steer = Vector3f.ZERO;
		int count = 0;
		for (Actor other : itr) {
			float d = position.distance(other.position);
			
			// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
			if ((d > 0) && (d < params.separation.distance)) {
				Vector3f diff = position.subtract(other.position);
				diff.normalize();
				
				diff.divide(d);
				steer = steer.add(diff);
				count++;
			}
		}
		
		if (count > 0) {
			steer = limitForce(steer.normalize().mult(params.move.velocity).subtract(velocity));
		}
		
		return steer;
	}
	
	Vector3f align(Iterable<Actor> itr) {
		Vector3f sum = Vector3f.ZERO;
		int count = 0;
		for (Actor other : itr) {
			float d = position.distance(other.position);
			if ((d > 0) && (d < params.alignment.distance)) {
				sum = sum.add(other.velocity);
				count++;
			}
		}
		if (count > 0) {			
			sum = sum.normalize().mult(params.move.velocity);
			
			Vector3f steer = sum.subtract(velocity);
			return limitForce(steer);
		} 
		return Vector3f.ZERO;
	}
	
	Vector3f cohesion(Iterable<Actor> itr) {
		Vector3f sum = Vector3f.ZERO;
		int count = 0;
		
		for (Actor other : itr) {
			float d = position.distance(other.position);
			if ((d > 0) && (d < params.cohesion.distance)) {
				sum = sum.add(other.position); // Add position
				count++;
			}
		}
		
		if (count > 0) {
			return seek(sum);
		}
		return Vector3f.ZERO;
	}
	
	/*Vector3f aggregate(Iterable<Actor> itr) {
		Vector3f separate = Vector3f.ZERO;
		Vector3f align = Vector3f.ZERO;
		Vector3f cohere = Vector3f.ZERO;
		
		for (Actor other : itr) {
			float d = position.distance(other.position);
			
			// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
			if ((d > 0) && (d < params.separation.distance)) {
				Vector3f diff = position.subtract(other.position);
				diff.normalize();
				
				diff.divide(d);
				separate = separate.add(diff);
			}
			
			if ((d > 0) && (d < params.cohesion.distance)) {
				align = align.add(other.position); // Add position
			}
			
			if ((d > 0) && (d < params.cohesion.distance)) {
				cohere = cohere.add(other.position); // Add position
			}
		}
		
		if (separate.lengthSquared() > 0) {
			separate = limitForce(separate.normalize().mult(params.move.velocity).subtract(velocity));
		}
		if (align.lengthSquared() > 0) {
			align = align.normalize().mult(params.move.velocity);
			align = limitForce(align.subtract(velocity));	
		}
		if (cohere.lengthSquared() > 0) {
			cohere = seek(cohere);
		}
		
		Vector3f acceleration = Vector3f.ZERO;
		acceleration = acceleration.add(separate.mult(params.separation.weight));
		acceleration = acceleration.add(align.mult(params.alignment.weight));
		acceleration = acceleration.add(cohere.mult(params.cohesion.weight));
		return acceleration;
	}*/
	
	public void update(Iterable<Actor> itr, float deltaTime) {
		Vector3f acceleration = Vector3f.ZERO;
		Vector3f sep = separate(itr);
		Vector3f ali = align(itr);
		Vector3f coh = cohesion(itr);

		acceleration = acceleration.add(sep.mult(params.separation.weight));
		acceleration = acceleration.add(ali.mult(params.alignment.weight));
		acceleration = acceleration.add(coh.mult(params.cohesion.weight));

	    velocity = velocity.add(acceleration).normalize().mult(params.move.velocity);

		position = position.add(velocity);
	}
}
