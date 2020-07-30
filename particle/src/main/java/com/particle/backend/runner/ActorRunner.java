package com.particle.backend.runner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.math.Vector3f;
import com.particle.backend.server.RuleParameter;
import com.particle.backend.storage.BoundingBox;
import com.particle.backend.storage.SpaceOctree;

public class ActorRunner {
	Random rand = new Random();
	
	public List<Actor> actors = new ArrayList<>();	
	
	float space;
	float minOctree;
	
	public ActorRunner(RuleParameter params)
	{
		this.space = params.space * params.segments;
		this.minOctree = params.space;
		
        for (int i = 0; i < params.numWorkers; i++) {
        	actors.add(randomSetup(new Actor(i, params)));
        }
	}
	
	/* This version skips the octree. Doesn't scale as well.
	public void update(float deltaTime) {
		for (Actor a: actors) {
			a.update(actors, deltaTime);
			
			handleBorder(a);
		}
	}/*/
	
	public void update(float deltaTime) {
		SpaceOctree s = new SpaceOctree(null, new BoundingBox(
				new Vector3f(-space, -space, -space),  
        		new Vector3f(space, space, space)), minOctree);
		
		for (Actor a: actors) {
			s.add(a);
		}
        
		for (Actor a: actors) {
			a.update(s.getNeighbors(a.position), deltaTime);
			
			handleBorder(a);
		}
	}
	
	Actor handleBorder(Actor a) {
		/*if (a.position.x > space || a.position.x < -space 
		 || a.position.y > space || a.position.y < -space
		 || a.position.z > space || a.position.z < -space) {
			a = randomSetup(a);
		}*/
		
		if (a.position.x > space) {
			a.position.x -= space * 2;
		}
		if (a.position.x < -space) {
			a.position.x += space * 2;
		}
		if (a.position.y > space) {
			a.position.y -= space * 2;
		}
		if (a.position.y < -space) {
			a.position.y += space * 2;
		}
		if (a.position.z > space) {
			a.position.z -= space * 2;
		}
		if (a.position.z < -space) {
			a.position.z += space * 2;
		}
		return a;
	}
    
    public void writeData(OutputStream out, int startNdx) throws IOException {
    	int ndx = 0;
    	ByteBuffer buffer = ByteBuffer.allocate(4 * 4);
    	
        while (ndx < actors.size()) {
        	Actor a = actors.get(ndx);
        	
        	buffer.clear()
        	 .putInt(startNdx + ndx)
        	 .putFloat(a.position.x) //x
        	 .putFloat(a.position.y) //y
        	 .putFloat(a.position.z) //z
        	;
        	out.write(buffer.array(), 0, buffer.limit());
        	ndx++;
        }
    }
    
	Actor randomSetup(Actor a) {
		a.position = new Vector3f(2 * space * rand.nextFloat() - space, 2 * space * rand.nextFloat() - space, 2 * space * rand.nextFloat() - space);
		a.velocity = new Vector3f(2 * rand.nextFloat() - 1, 2 * rand.nextFloat() - 1, 2 * rand.nextFloat() - 1);
		return a;
	}
}
