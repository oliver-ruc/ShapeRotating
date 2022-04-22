import Jama.Matrix;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

public class Solid {
    private Triangle[] _mesh;
    private Matrix _center;
    double _scale = 1;
    Matrix _rotationMatrix = Matrix.identity(3, 3);
    ArrayList<DisplacementFunction> _displacementFunctions = new ArrayList<DisplacementFunction>();

    public Solid(Triangle[] mesh, Matrix center, double scale) {
        _mesh = mesh;
        _center = center.copy();
        _scale = scale;
    }

    public Solid(Triangle[] mesh, Matrix center) {
        this(mesh, center, 1);
    }

    public Solid(Triangle[] mesh) {
        this(mesh, new Matrix(3,1));
    }
    
    public void setRotationMatrix(Matrix rotationMatrix) {
        _rotationMatrix = rotationMatrix.copy();
    }

    public void rotate(Matrix rotationMatrix) {
        _rotationMatrix = rotationMatrix.times(_rotationMatrix);
    }

    public void rotateX(double theta) {_rotationMatrix = RotationMatrices.AboutPositiveX(theta).times(_rotationMatrix);}
    public void rotateY(double theta) {_rotationMatrix = RotationMatrices.AboutPositiveY(theta).times(_rotationMatrix);}
    public void rotateZ(double theta) {_rotationMatrix = RotationMatrices.AboutPositiveZ(theta).times(_rotationMatrix);}

    public void draw(Graphics g, Camera camera, double width, double height) {
        
        //System.out.println("In Solid: " + Utils.matrixToString(_rotationMatrix));

        for(int i = 0; i < _mesh.length; i++) {
            Matrix saved = _mesh[i].getMatrix().copy();
            _mesh[i].setMatrix(saved.times(_scale));
            Matrix rotated = _rotationMatrix.times(_mesh[i].getMatrix());
            _mesh[i].setMatrix(rotated);
            _mesh[i].displace(_center);
            _mesh[i].drawEdges(g, camera, width, height);
            _mesh[i].setMatrix(saved);
        }
    }

    public void drawFilled(Graphics g, Camera camera, double width, double height, Color edgeColor, Color fillColor) {

        Matrix[] saved = new Matrix[_mesh.length];

        for(int i = 0; i < _mesh.length; i++) {
            saved[i] = _mesh[i].getMatrix();
            _mesh[i].setMatrix(_mesh[i].getMatrix().times(_scale));
            Matrix rotated = _rotationMatrix.times(_mesh[i].getMatrix());
            _mesh[i].setMatrix(rotated);
            _mesh[i].displace(_center);
        }

        double[] triangleCentroidDistances = new double[_mesh.length];
        for(int i = 0; i < _mesh.length; i++) {
            triangleCentroidDistances[i] = _mesh[i].getDistanceFromCentroid(camera._cameraPos);
            _mesh[i].computeCentroid();
        }
        for(int i = 0; i < _mesh.length; i++) {
            int max_index = Utils.indexOfMax(triangleCentroidDistances, 0.001);
            _mesh[max_index].drawFilled(g, camera, width, height, edgeColor, fillColor);
            triangleCentroidDistances[max_index] = Double.NEGATIVE_INFINITY;
            _mesh[max_index].setMatrix(saved[max_index]);
        }
    }

    public Matrix getCenter() {
        return _center;
    }

    public void setCenter(Matrix center) {
        _center = center.copy();
    }

    public void displace(Matrix displacement) {
        _center.plusEquals(displacement);
    }

    public void addDisplacementFunction(DisplacementFunction displacementFunction) {
        _displacementFunctions.add(displacementFunction);
    }

    public void update() {
        for (DisplacementFunction displacementFunction : _displacementFunctions) {
            _center.plusEquals(displacementFunction.displacementSinceLastCheck(System.currentTimeMillis()));
            System.out.println(Utils.matrixToString(_center.transpose()));
        }
    }
}
