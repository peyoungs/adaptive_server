package com.particle.backend.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.jme3.math.Vector3f;
import com.particle.backend.runner.Actor;
import com.particle.backend.server.RuleParameter;
import com.particle.backend.server.RuleParameter.AlignmentRuleParameters;
import com.particle.backend.server.RuleParameter.CohesionRuleParameters;
import com.particle.backend.server.RuleParameter.MoveRuleParameters;
import com.particle.backend.server.RuleParameter.SeparationRuleParameters;

class ActorTest {
	
	@Test
	void testUpdate() {
		List<Actor> actors;
		Actor a = defaultActor();
		
		//Base velocity
		a.params.move.velocity = 5;
		a.params.space = 500;
		a.params.segments = 2;
		actors = defaultActorsPos(new Vector3f[] {
				new Vector3f(1, 1, 1), new Vector3f(-1, -1, -1), new Vector3f(1, 0, 0)});
		a.update(actors, 435f); 
		//Round because the floats don't round cleanly 
		assertEquals(2175, Math.round(a.velocity.length()));
	}
	
	@Test
	void testAdhere() {
		List<Actor> actors;
		Actor a = defaultActor();
		Vector3f result;
		
		//default
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 1, 0);
		a.params.alignment.distance = 10;
		actors = defaultActorsPos(new Vector3f[] {
				new Vector3f(1, 1, 1), new Vector3f(-1, -1, -1), new Vector3f(1, 0, 0)});
		actors = setVelocities(actors, new Vector3f[] {
				new Vector3f(2, 0, -1), new Vector3f(1, -1, 1), new Vector3f(0, 1, 0)});
		result = a.align(actors);
		assertEquals(new Vector3f(1f, -1f, 0), result);
		
		//ignore distance
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 0, 0);
		a.params.alignment.distance = 10;
		actors = defaultActorsPos(new Vector3f[] {
				new Vector3f(1, 1, 1), new Vector3f(-1, -1, -1), new Vector3f(11, 0, 0)});
		actors = setVelocities(actors, new Vector3f[] {
				new Vector3f(1, 1, 1), new Vector3f(-1, -1, -1), new Vector3f(11, 0, 0)});
		result = a.align(actors);
		assertEquals(new Vector3f(0f, 0f, 0), result);
		
		//limiting
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 0, 0);
		a.params.alignment.distance = 10;
		a.params.move.steering = 1f;
		actors = defaultActorsPos(new Vector3f[] {
			new Vector3f(-5, 0, -2), new Vector3f(5, -2, 2), new Vector3f(0, 2, 0)});
		actors = setVelocities(actors, new Vector3f[] {
				new Vector3f(-5, 0, -2), new Vector3f(5, -2, 2), new Vector3f(0, 3, 0)});
		result = a.align(actors);
		assertEquals(new Vector3f(0f, 1f, 0), result);
	}
	
	@Test
	void testCohere() {
		Actor a = defaultActor();
		Vector3f result;
		
		//default
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 1, 0);
		a.params.cohesion.distance = 10;
		result = a.cohesion(defaultActorsPos(new Vector3f[] {
				new Vector3f(2, 0, -1), new Vector3f(1, -1, 1), new Vector3f(0, 1, 0)}));
		assertEquals(new Vector3f(1f, -1f, 0), result);
		
		//ignore distance
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 0, 0);
		a.params.cohesion.distance = 10;
		result = a.cohesion(defaultActorsPos(new Vector3f[] {
				new Vector3f(1, 1, 1), new Vector3f(-1, -1, -1), new Vector3f(11, 0, 0)}));
		assertEquals(new Vector3f(0f, 0f, 0), result);
		
		//limiting
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 1, 0);
		a.params.cohesion.distance = 10;
		a.params.move.steering = 1f;
		result = a.cohesion(defaultActorsPos(new Vector3f[] {
				new Vector3f(-5, 0, -2), new Vector3f(5, -2, 2), new Vector3f(0, 2, 0)}));
		assertEquals(new Vector3f(0f, -1f, 0), result);
	}
	
	@Test
	void testSeparate() {
		Actor a = defaultActor();
		Vector3f result;
		
		//default
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 1, 0);
		a.params.separation.distance = 10;
		result = a.separate(defaultActorsPos(new Vector3f[] {
				new Vector3f(2, 1, -1), new Vector3f(1, 0, 1), new Vector3f(0, 3, 0)}));
		assertEquals(new Vector3f(-0.6f, -1.8f, 0), result);
		
		//ignore distance
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 0, 0);
		a.params.separation.distance = 10;
		result = a.separate(defaultActorsPos(new Vector3f[] {
				new Vector3f(1, 1, 1), new Vector3f(-1, -1, -1), new Vector3f(11, 0, 0)}));
		assertEquals(new Vector3f(0f, 0f, 0), result);
		
		//limiting
		a.position = new Vector3f(0, 0, 0);
		a.velocity = new Vector3f(0, 0, 0);
		a.params.separation.distance = 10;
		a.params.move.steering = 2f;
		result = a.separate(defaultActorsPos(new Vector3f[] {
				new Vector3f(2, 1, -1), new Vector3f(1, 0, 1), new Vector3f(0, 3, 0)}));
		assertEquals(new Vector3f(-0.6f, -0.8f, 0), result);
	}

	@Test
	void testLimit() {
		Actor a = defaultActor();
		Vector3f result;
		
		a.params.move.steering = 0.5f;
		result = a.limitForce(new Vector3f(0, 1, 0));
		assertEquals(new Vector3f(0, 0.5f, 0), result);
		
		a.params.move.steering = 5f;
		result = a.limitForce(new Vector3f(0, 0, 0));
		assertEquals(new Vector3f(0, 0, 0), result);
		
		a.params.move.steering = 2f;
		result = a.limitForce(new Vector3f(3, 4, 0));
		assertEquals(new Vector3f(1.2f, 1.6f, 0), result);
	}
	
	List<Actor> defaultActorsPos(Vector3f[] pos) {
		List<Actor> actors = new ArrayList<>();
		for (Vector3f p: pos) {
			Actor a = defaultActor();
			a.position = p;
			actors.add(a);
		}
		return actors;
	}
	
	List<Actor> setVelocities(List<Actor> actors, Vector3f[] vels) {
		for (int i = 0; i < vels.length; i++) {
			actors.get(i).velocity = vels[i];
		}
		return actors;
	}
	
	
	Actor defaultActor() {
		RuleParameter params = new RuleParameter();
		params.alignment = new AlignmentRuleParameters();
		params.alignment.distance = 100;
		params.alignment.weight = 1;
		params.cohesion = new CohesionRuleParameters();
		params.cohesion.distance = 100;
		params.cohesion.weight = 1;
		params.move = new MoveRuleParameters();
		params.move.velocity = 1;
		params.move.steering = 100f;
		params.separation = new SeparationRuleParameters();
		params.separation.distance = 100;
		params.separation.weight = 1;
		return new Actor(0, params, Vector3f.ZERO, Vector3f.ZERO);
	}
}
