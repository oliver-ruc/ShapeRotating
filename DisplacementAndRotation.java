import Jama.Matrix;

public class DisplacementAndRotation {
    Matrix _displacementMatrix;
    Matrix _rotationMatrix;
    public DisplacementAndRotation() {
        this._displacementMatrix = new Matrix(3,1);
        this._rotationMatrix = Matrix.identity(3, 3);
    }
    public DisplacementAndRotation(Matrix displacement, Matrix rotation) {
        //TODO: add checks
        this._displacementMatrix = displacement;
        this._rotationMatrix = rotation;
    }
    public static DisplacementAndRotation displacement(Matrix displacement) {
        //TODO: add checks (maybe just in constructor)
        return new DisplacementAndRotation(displacement, Matrix.identity(3, 3));
    }
    public static DisplacementAndRotation rotation(Matrix rotation) {
        //TODO: add checks (maybe just in constructor)
        return new DisplacementAndRotation(new Matrix(3,1), rotation);
    }

    public void setRotation(Matrix rotation) {
        //TODO: add checks
        this._rotationMatrix = rotation;
    }
    public Matrix getRotation() {
        return this._rotationMatrix;
    }

    public void setDisplacement(Matrix displacement) {
        //TODO: add checks
        this._displacementMatrix = displacement;
    }
    public Matrix getDisplacement() {
        return this._displacementMatrix;
    }

    public DisplacementAndRotation opposite() {
        return new DisplacementAndRotation(_displacementMatrix.times(-1), _rotationMatrix.inverse());
    }

    public DisplacementAndRotation plus(DisplacementAndRotation other) {
        return new DisplacementAndRotation(_displacementMatrix.plus(other.getDisplacement()), _rotationMatrix.times(other.getRotation()));
    }
    public DisplacementAndRotation minus(DisplacementAndRotation other) {
        return this.plus(other.opposite());
    }
}
