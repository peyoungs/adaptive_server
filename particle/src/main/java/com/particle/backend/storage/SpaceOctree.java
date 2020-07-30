package com.particle.backend.storage;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.particle.backend.runner.Actor;

public class SpaceOctree {
	
	public List<Actor> actors = new ArrayList<>();
	
	SpaceOctree parent;
	SpaceOctree[] children;
	BoundingBox space;
	
	public SpaceOctree(SpaceOctree parent, BoundingBox space, float minSpace)
    {
    	this.parent = parent;
    	this.space = space;
    	
    	if (space.max.subtract(space.min).x > minSpace) {
    		BoundingBox[] boxes = subdividedOctants(space);
    		
    		children = new SpaceOctree[8];
    		for (int i = 0; i < 8; i++)
    		{
    			children[i] = new SpaceOctree(this, boxes[i], minSpace);
    		}
    	}
    }
	
	public void add(Actor a) 
	{
		SpaceOctree o = getNode(a.getPosition());
		if (o != null)
			o.actors.add(a);
		else
			actors.add(a);
	}
	
	public List<Actor> getNeighbors(Vector3f point) {
		SpaceOctree t = getNode(point);
		if (t != null)
			return t.actors;
		return new ArrayList<>();
	}
	
	SpaceOctree getNode(Vector3f point) {
		SpaceOctree[] act = children;
		
		for (int i = 0; i < 8; i++) {
			SpaceOctree sCur = act[i];
			
			if (sCur.space.contains(point)) {
				if (sCur.children == null) {
					return sCur;
				}
				return sCur.getNode(point);
			}
		}
		
		return null;
	}
	
    private static BoundingBox[] subdividedOctants(BoundingBox space)
    {
        Vector3f dimensions = space.max.subtract(space.min);
        Vector3f half = dimensions.divide(2f);
        Vector3f center = space.min.add(half);

        BoundingBox[] childOctant = new BoundingBox[8];
        childOctant[0] = new BoundingBox(space.min, center);
        childOctant[1] = new BoundingBox(new Vector3f(center.x, space.min.y, space.min.z), new Vector3f(space.max.x, center.y, center.z));
        childOctant[2] = new BoundingBox(new Vector3f(center.x, space.min.y, center.z), new Vector3f(space.max.x, center.y, space.max.z));
        childOctant[3] = new BoundingBox(new Vector3f(space.min.x, space.min.y, center.z), new Vector3f(center.x, center.y, space.max.z));
        childOctant[4] = new BoundingBox(new Vector3f(space.min.x, center.y, space.min.z), new Vector3f(center.x, space.max.y, center.z));
        childOctant[5] = new BoundingBox(new Vector3f(center.x, center.y, space.min.z), new Vector3f(space.max.x, space.max.y, center.z));
        childOctant[6] = new BoundingBox(center, space.max);
        childOctant[7] = new BoundingBox(new Vector3f(space.min.x, center.y, center.z), new Vector3f(center.x, space.max.y, space.max.z));

        return childOctant;
    }
}
