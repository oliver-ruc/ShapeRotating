import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;

import Jama.Matrix;

public class Environment {
    ArrayList<Solid> _solids = new ArrayList<Solid>();
    Matrix _rotationMatrix = Matrix.identity(3, 3);

    public Environment(Collection<Solid> solids) {
        _solids.addAll(solids);
    }

    public void addSolid(Solid solid) {
        _solids.add(solid);
    }

    public void rotate(Matrix rotation) {
        _rotationMatrix = rotation.times(_rotationMatrix);
    }

    public void draw(Graphics g, Camera camera, double width, double height) {
        for (Solid solid : _solids) {
            solid.setCenter(_rotationMatrix.times(solid.getCenter()));
            solid.rotate(_rotationMatrix);
            solid.draw(g, camera, width, height);
            solid.rotate(_rotationMatrix.inverse());
            solid.setCenter(_rotationMatrix.inverse().times(solid.getCenter()));
        }
    }
    
    public void update() {
        for (Solid solid : _solids) {
            solid.update();
        }
    }
}
