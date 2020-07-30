package com.particle.backend.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.jme3.math.Vector3f;
import com.particle.backend.runner.Actor;
import com.particle.backend.storage.BoundingBox;
import com.particle.backend.storage.SpaceOctree;

public class SpaceOctreeTest {
	
	@Test
	void testBuild()
	{
		SpaceOctree s = new SpaceOctree(null, new BoundingBox(new Vector3f(-50, -50, -50), new Vector3f(50, 50, 50)), 25);
		
		assertNotNull(s.children);
		
		SpaceOctree s1 = s.getNode(new Vector3f(-30, -30, -30));
		SpaceOctree s2 = s.getNode(new Vector3f(30, 30, 30));
		
		assertNotEquals(s1, s2);
		
		SpaceOctree s3 = s.getNode(new Vector3f(-30, -29, -30));
		assertEquals(s1, s3);
	}
	
	@Test
	void testAddGet()
	{
		SpaceOctree s = new SpaceOctree(null, new BoundingBox(new Vector3f(-50, -50, -50), new Vector3f(50, 50, 50)), 25);
		
		Actor a1 = new Actor(0, null, new Vector3f(-30, -30, -30), Vector3f.ZERO);
		Actor a2 = new Actor(1, null, new Vector3f(-31, -30, -30), Vector3f.ZERO);
		Actor a3 = new Actor(1, null, new Vector3f(30, 30, 30), Vector3f.ZERO);
		s.add(a1);
		s.add(a2);
		s.add(a3);
		
		assertEquals(2, s.getNeighbors(new Vector3f(-30, -30, -30)).size());
		assertTrue(s.getNeighbors(new Vector3f(-30, -30, -30)).contains(a1));
		assertTrue(s.getNeighbors(new Vector3f(-30, -31, -30)).contains(a2));
		
		assertEquals(1, s.getNeighbors(new Vector3f(30, 30, 30)).size());
		assertTrue(s.getNeighbors(new Vector3f(30, 30, 30)).contains(a3));
	}
}
