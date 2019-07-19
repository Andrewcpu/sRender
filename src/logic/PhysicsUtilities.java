package logic;

import javafx.geometry.Point3D;
import render.Camera;
import world.blocks.Cube;

class PhysicsUtilities {

    public static Point3D getMovement(Cube cube, Point3D target, Camera camera){
        
        
        double minDistance = Double.MAX_VALUE;
        int face = -1;
        
        
        int f = 0;


        ErrorManagement.log(ErrorLocation.PHYSICS, "Beginning to search for closest face.");

        for(int[] nodes : cube.getSides())
        {
            Point3D avgNode = CoordinateUtilities.averagePoint(new Point3D[]{cube.getNodes()[nodes[0]], cube.getNodes()[nodes[1]], cube.getNodes()[nodes[2]], cube.getNodes()[nodes[3]]});
            double d = avgNode.distance(target);
            if(face == -1 || d < minDistance)
            {
                minDistance = d;
                face = f;
                ErrorManagement.log(ErrorLocation.PHYSICS, "New closest found: " + f);
            }
            f++;
        }

        ErrorManagement.log(ErrorLocation.PHYSICS, "Exiting face loop.");

        Direction faceDirection = null;
        
        switch (face)
        {
            case 0:
                faceDirection = Direction.NORTH;
                break;
            case 1:
                faceDirection = Direction.WEST;
                break;
            case 2:
                faceDirection = Direction.EAST;
                break; 
            case 3:
                faceDirection = Direction.SOUTH;
                break;
            case 4:
                faceDirection = Direction.UP;
                break;
            case 5:
                faceDirection = Direction.DOWN;
                break;
        }
        if(faceDirection == null){
            ErrorManagement.error(ErrorLocation.PHYSICS, "Could not identify closest face relative to point.");
            return new Point3D(0,0,0);
        }

        ErrorManagement.log(ErrorLocation.PHYSICS, "Closest Face: " + faceDirection.toString());

        Point3D slope = CoordinateUtilities.directionToMovement(faceDirection).multiply(-1);

        while(cube.getPhysicsBody().getBounds().intersects(camera.getPhysicsBody().moveTarget(target))){
            target = target.add(slope);
        }

        return target;
    }

}
