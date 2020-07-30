package com.particle.backend.storage;

import com.jme3.math.Vector3f;

public class BoundingBox {
	public Vector3f min;
	public Vector3f max;
	
	public BoundingBox(Vector3f min, Vector3f max)
	{
		this.min = min;
		this.max = max;
	}
	
	public boolean contains(Vector3f p) {
        return p.x >= min.x && p.x <= max.x && p.y >= min.y && p.y <= max.y && p.z >= min.z && p.z <= max.z;
    }
	
	public String toString()
	{
		return min + " " + max;
	}
}
