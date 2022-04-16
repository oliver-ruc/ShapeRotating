import Jama.Matrix;

public class Camera {
    /**
     * A 3x3 matrix where the first column vector is in the direction of the camera's forward, second in the direction of the camera's left, and third in the direction of the camera's up.
     */
    Matrix _cameraMatrix = new Matrix(3, 3);

    /**
     * Distance from the camera to the screen which it projects points onto.
     */
    double _cameraD;

    /**
     * The horizontal field of vision of the camera, in radians.
     */
    double _horizontalFOV;

    /**
     * The vertical   field of vision of the camera, in radians.
     */
    double _verticalFOV;

    /**
     * A 3x1 column vector for the position of the camera.
     */
    Matrix _cameraPos;

    /**
     * An allowed error value for floating point calculations.
     */
    private static final double EPSILON = 0.001;

    /**
     * Creates a Camera with the specificed direction, position, fields of vision and position.
     * <p>
     * Automatically creates the up vector of the camera by taking the cross product of the forward vector and the left vector.
     * @param cameraFwd 3x1 column vector in the direction of the camera's forward.
     * @param cameraLft 3x1 column vector in the direction of the camera's left.
     * @param cameraD   the distance of the "screen" from the camera.
     * @param horizontalFOV the horizontal field of vision of the camera, in radians.
     * @param verticalFOV   the vertical   field of vision of the camera, in radians.
     * @param cameraPos     3x1 column vector of the position of the camera.
     * @throws IllegalArgumentException if the dimensions of any of the matrices are incorrect
     * @throws IllegalArgumentException if the magnitude of either of the camera direction vectors are <0.001
     * @throws IllegalArgumentException if d <0.001
     * @throws IllegalArgumentException if cameraFwd and cameraLft are not perpendicular (dot product <0.001)
     */
    public Camera(Matrix cameraFwd, Matrix cameraLft, double cameraD, double horizontalFOV, double verticalFOV, Matrix cameraPos) {
        if (cameraFwd.getRowDimension() != 3 || cameraFwd.getColumnDimension() != 1) {
            throw new IllegalArgumentException("cameraFwd should be a 3x1 column vector, was " + cameraFwd.getRowDimension() + "x" + cameraFwd.getColumnDimension() + ".");
        }
        if (cameraLft.getRowDimension() != 3 || cameraLft.getColumnDimension() != 1) {
            throw new IllegalArgumentException("cameraLft should be a 3x1 column vector, was " + cameraLft.getRowDimension() + "x" + cameraLft.getColumnDimension() + ".");
        }
        if (cameraPos.getRowDimension() != 3 || cameraPos.getColumnDimension() != 1) {
            throw new IllegalArgumentException("cameraPos should be a 3x1 column vector, was " + cameraPos.getRowDimension() + "x" + cameraPos.getColumnDimension() + ".");
        }
        if (cameraFwd.normF() < EPSILON) {
            throw new IllegalArgumentException("cameraFwd should not be a zero vector, but seems to be (norm < 0.001)");
        }
        if (cameraLft.normF() < EPSILON) {
            throw new IllegalArgumentException("cameraLft should not be a zero vector, but seems to be (norm < 0.001)");
        }
        if (cameraD < EPSILON) {
            throw new IllegalArgumentException("cameraD should not be 0 or negative, but seems to be ("+cameraD+"<0.001)");
        }
        if (Math.abs(cameraFwd.transpose().times(cameraLft).get(0,0)) > EPSILON) {
            throw new IllegalArgumentException("cameraFwd and cameraLft should be perpendicular, but seem not to be (dot product > 0.001).");
        }
        Matrix _cameraFwd = cameraFwd.times(1/cameraFwd.normF());
        Matrix _cameraLft = cameraLft.times(1/cameraLft.normF());
        Matrix _cameraUp = Utils.crossProduct(_cameraFwd, _cameraLft);
        _cameraMatrix.setMatrix(0, 2, 0, 0, _cameraFwd);
        _cameraMatrix.setMatrix(0, 2, 1, 1, _cameraLft);
        _cameraMatrix.setMatrix(0, 2, 2, 2, _cameraUp );

        _cameraD = cameraD;
        _horizontalFOV = horizontalFOV;
        _verticalFOV   = verticalFOV;
        _cameraPos = cameraPos.copy();
    }

    /**
     * The camera matrix is a representation of the orientation of the camera, where the first column vector is in the direction of the camera's forward, second in the direction of the camera's left, and third in the direction of the camera's up.
     * @return A 3x3 matrix that is a copy of the camera matrix of the camera.
     */
    public Matrix getCameraMatrix() {
        return _cameraMatrix.copy();
    }

    /**
     * Gets a vector in the direction of the camera's forward.
     * @return a 3x1 column vector
     */
    public Matrix getCameraFwd() {
        return _cameraMatrix.getMatrix(0, 2, 0, 0);
    }

    /**
     * Gets a vector in the direction of the camera's left.
     * @return a 3x1 column vector.
     */
    public Matrix getCameraLft() {
        return _cameraMatrix.getMatrix(0, 2, 1, 1);
    }

    /**
     * Gets a vector in the direction of the camera's up.
     * @return a 3x1 column vector.
     */
    public Matrix getCameraUp() {
        return _cameraMatrix.getMatrix(0, 2, 2, 2);
    }

    /**
     * Sets the position of the camera to a copy of the supplied column vector.
     * @param cameraPos a 3x1 column vector.
     * @throws IllegalArgumentException If the supplied cameraPos is not 3x1.
     */
    public void setCameraPos(Matrix cameraPos) {
        if (cameraPos.getRowDimension() != 3 || cameraPos.getColumnDimension() != 1) {
            throw new IllegalArgumentException("Supplied cameraPos vector should be 3x1, but was " + cameraPos.getRowDimension() + "x" + cameraPos.getColumnDimension() + ".");
        }
        _cameraPos = cameraPos.copy();
    }

    /**
     * Gets a copy of the positon vector of the camera.
     * @return A 3x1 column vector.
     */
    public Matrix getCameraPos() {
        return _cameraPos.copy();
    }

    /**
     * Sets the camera matrix of the camera matrix to a copy of the given camera matrix without checking the magnitude, perpendicularity, or orientation of the vectors in the camera Matrix.
     * @param cameraMatrix a 3x3 matrix where is first column is a vector in the direction of the camera's forward, second in the direction of the camera's left, and third in the direction of the camera's up.
     * @throws IllegalArgumentException If the supplied camera matrix is not 3x3.
     * @see Camera#setCameraMatrix(Matrix cameraMatrix)
     */
    public void setCameraMatrixWithoutChecks(Matrix cameraMatrix) {
        if (cameraMatrix.getRowDimension() != 3 || cameraMatrix.getColumnDimension() != 3) {
            throw new IllegalArgumentException("cameraMatrix should be a 3x3 matrix, but was " + cameraMatrix.getRowDimension() + "x" + cameraMatrix.getColumnDimension() + ".");
        }
        _cameraMatrix = cameraMatrix.copy();
    }

    /**
     * Sets the camera matrix of the camera, where is first column is a vector in the direction of the camera's forward, second in the direction of the camera's left, and third in the direction of the camera's up.
     * <p>
     * This method also checks the magnitude (not necessarily 1, but >= 0.001), perpendicularity, and orientation of the vectors. To set the camera matrix without these checks, use {@link Camera#setCameraMatrixWithoutChecks(Matrix cameraMatrix)}.
     * @param cameraMatrix a 3x3 matrix.
     * @throws IllegalArgumentException If the supplied cameraMatrix is not 3x3.
     * @throws IllegalArgumentException If the vectors are not perpendicular.
     * @throws IllegalArgumentException If the norm of any of the vectors is < 0.001.
     * @throws IllegalArgumentException If the orientation of the vectors is wrong (i.e. one of the vectors is in the wrong direction when used with the other two).
     * @see Camera#setCameraMatrixWithoutChecks(Matrix cameraMatrix)
     */
    public void setCameraMatrix(Matrix cameraMatrix) {
        if (cameraMatrix.getRowDimension() != 3 || cameraMatrix.getColumnDimension() != 3) {
            throw new IllegalArgumentException("cameraMatrix should be a 3x3 matrix, was " + cameraMatrix.getRowDimension() + "x" + cameraMatrix.getColumnDimension() + ".");
        }

        Matrix cameraFwd = cameraMatrix.getMatrix(0, 2, 0, 0);
        Matrix cameraLft = cameraMatrix.getMatrix(0, 2, 1, 1);
        Matrix cameraUp  = cameraMatrix.getMatrix(0, 2, 2, 2);

        if (cameraFwd.transpose().times(cameraLft).get(0,0) > EPSILON) {
            throw new IllegalArgumentException("The forward and left vectors should be perpendicular, but seem not to be (dot product > 0.001).");
        }
        if (cameraFwd.transpose().times(cameraUp ).get(0,0) > EPSILON) {
            throw new IllegalArgumentException("The forward and up vectors should be perpendicular, but seem not to be (dot product > 0.001).");
        }
        if (cameraLft.transpose().times(cameraUp ).get(0,0) > EPSILON) {
            throw new IllegalArgumentException("The left and up vectors should be perpendicular, but seem not to be (dot product > 0.001).");
        }

        if (cameraFwd.normF() < EPSILON) {
            throw new IllegalArgumentException("The forward vector should not be a zero vector, but seems to be (norm < 0.001)");
        }
        if (cameraLft.normF() < EPSILON) {
            throw new IllegalArgumentException("The left    vector should not be a zero vector, but seems to be (norm < 0.001)");
        }
        if (cameraUp .normF() < EPSILON) {
            throw new IllegalArgumentException("The up      vector should not be a zero vector, but seems to be (norm < 0.001)");
        }

        Matrix prospectiveUp = Utils.crossProduct(cameraFwd, cameraLft);
        if (Math.abs(
            prospectiveUp.transpose().times(cameraUp).times(1/(prospectiveUp.normF()*cameraUp.normF())).get(0, 0) // finds the cosine of the angle between the angles and checks it to be about 1 (same direction)
        - 1) > EPSILON) {
            throw new IllegalArgumentException("The orientation of the 3 vectors does not work (one of the vectors is in the wrong direction).");
        }
        _cameraMatrix.setMatrix(0, 2, 0, 0, cameraFwd.times(1/cameraFwd.normF()));
        _cameraMatrix.setMatrix(0, 2, 1, 1, cameraLft.times(1/cameraLft.normF()));
        _cameraMatrix.setMatrix(0, 2, 2, 2, cameraUp .times(1/cameraUp .normF()));
    }

    /**
     * Converts point(s) to a space relative to the camera, using the camera's forward, left, and up vectors as basis vectors.
     * @param points a 3xn matrix.
     * @return A 3xn matrix in the standard basis with the points converted to the camera's space.
     * @throws IllegalArgumentException If the points are not in 3-space (points is not 3xn).
     */
    public Matrix convertToCameraSpace(Matrix points) {
        if (points.getRowDimension() != 3) {
            throw new IllegalArgumentException("Points should be in 3-space, but was in " + points.getRowDimension() + "-space.");
        }
        Matrix _relPoints = points.copy();
        for (int j = 0; j < _relPoints.getColumnDimension(); j++) {
            _relPoints.setMatrix(0, 2, j, j, _relPoints.getMatrix(0, 2, j, j).minus(_cameraPos));
        }
        return _cameraMatrix.transpose().times(_relPoints);
    }

    /**
     * Converts the given points in standard-basis 3-space to the "plane space" of the camera (where it intersects the camera's plane, with the origin being in the direction of cameraFwd).
     * <p>
     * The first entry in a column tells whether a point is in front of (1), in the plane of (0), or behind the camera (-1).
     * If the point is behind the camera, the plane coordinates will be reflected across the origin.
     * @param points a 3xn matrix in the standard basis to convert to plane space.
     * @return A 3xn matrix with the first entry of every column as described above and the remaining two in plane coordinates.
     * @throws IllegalArgumentException if points is not 3xn (not in 3-space).
     */
    public Matrix convertToPlaneSpace(Matrix points) {
        if (points.getRowDimension() != 3) {
            throw new IllegalArgumentException("Points should be in 3-space, but was in " + points.getRowDimension() + "-space.");
        }
        Matrix cameraRelative = convertToCameraSpace(points);
        for(int j = 0; j < points.getColumnDimension(); j++) {
            Matrix column = cameraRelative.getMatrix(0, 2, j, j);
            if (column.get(0,0) == 0) { // unsure as to whether this should use EPSILON
                cameraRelative.setMatrix(0, 2, j, j, new Matrix(3,1));
            } else {
                cameraRelative.setMatrix(0, 2, j, j, column.times(_cameraD/Math.abs(column.get(0,0))));
            }
            cameraRelative.set(0, j, cameraRelative.get(0, j)/_cameraD);
        }
        return cameraRelative;
    }

    /**
     * Turns points from standard-basis 3-space to points on the camera's screen, using the camera's horizontal and vertical FOVs to make it relative.
     * <p>
     * As with convertToPlaneSpace, the first entry gives whether the point is in from of (1), on the plane of (0), or behind the camera.
     * The remaining two entries give the x and y coordinates relative to the camera's FOV.
     * <p>
     * The origin is in the center, x=-0.5 and x=0.5 are the left and right sides of the screen, repsectively, and y=-0.5 and y=0.5 are the bottom and top sides of the screen, respectively.
     * @param points 3xn matrix in standard-basis to be converted.
     * @return A 3xn matrix as described above.
     * @throws IllegalArgumentException If the points are not in 3-space.
     * @see Camera#convertToPlaneSpace(Matrix points)
     */
    public Matrix convertToCenterRelative(Matrix points) {
        if (points.getRowDimension() != 3) {
            throw new IllegalArgumentException("Points should be in 3-space, but was in " + points.getRowDimension() + "-space.");
        }
        double screenHeight = 2 * _cameraD * Math.tan(_verticalFOV   / 2);
        double screenWidth  = 2 * _cameraD * Math.tan(_horizontalFOV / 2);
        Matrix planePoints = convertToPlaneSpace(points);
        Utils.multiplyRow(planePoints, 1, -1/screenWidth ); // -1 to change from left to right
        Utils.multiplyRow(planePoints, 2,  1/screenHeight);

        return planePoints;
    }
}
