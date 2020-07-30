package com.particle.backend.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.jme3.math.Vector3f;
import com.particle.backend.storage.BoundingBox;

public class BoundingBoxTest {
	
	@Test
	void testContains()
	{
		BoundingBox b = new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(5, 5, 5));
		
		assertTrue(b.contains(new Vector3f(0, 0, 0)));
		assertTrue(b.contains(new Vector3f(3, 1, 1)));
		assertTrue(b.contains(new Vector3f(1, 3, 1)));
		assertTrue(b.contains(new Vector3f(1, 1, 3)));
		assertFalse(b.contains(new Vector3f(6, 6, 6)));
		assertFalse(b.contains(new Vector3f(6, 1, 3)));
		
		BoundingBox b2 = new BoundingBox(new Vector3f(0, -2000, 1000), new Vector3f(1000, -1000, 2000));
		assertTrue(b2.contains(new Vector3f(340.1184f, -1722.8749f, 1302.96f)));
		
		BoundingBox b3 = new BoundingBox(new Vector3f(0.0f, 0.0f, -100.0f), new Vector3f(100.0f, 100.0f, 0.0f));
		assertTrue(b3.contains(new Vector3f(16.40998f, 17.605797f, -70.89978f)));
		
	}
}
