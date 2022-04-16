import java.awt.Point;

import Jama.Matrix;

public class Utils {
    public static Matrix crossProduct(Matrix A, Matrix B) {
        return new Matrix(new double[][] {
            {A.get(1,0)*B.get(2,0) - A.get(2,0)*B.get(1,0)},
            {A.get(2,0)*B.get(0,0) - A.get(0,0)*B.get(2,0)},
            {A.get(0,0)*B.get(1,0) - A.get(1,0)*B.get(0,0)}
        });
    }

    public static String matrixToString(Matrix A) {
        String output = "{";
        for (int i = 0; i < A.getRowDimension(); i++) {
            output += "\n\t{";
            for (int j = 0; j < A.getColumnDimension(); j++) {
                if (A.get(i, j) >= 0) {
                    output += " ";
                }
                output += A.get(i,j);
                output += (j != A.getColumnDimension()-1 ? "," : "") + "\t";
            }
            output += "}" + (i != A.getRowDimension()-1 ? "," : "");
        }
        output += "\n}";
        return output;
    }

    public static void multiplyRow(Matrix A, int n_row, double factor) {
        A.setMatrix(n_row, n_row, 0, A.getColumnDimension()-1, A.getMatrix(n_row, n_row, 0, A.getColumnDimension()-1).times(factor));
    }

    public static Point convertToPoint(Matrix A) {
        if (A.getRowDimension() != 2 || A.getColumnDimension() != 1) {
            throw new IllegalArgumentException("Expected 2x1 Matrix, received " + A.getRowDimension() + "x" + A.getColumnDimension() + " matrix.");
        }
        return new Point((int)A.get(0,0), (int)A.get(1,0));
    }

    public static Point[] convertToPoints(Matrix A) {
        if (A.getRowDimension() != 2) {
            throw new IllegalArgumentException("Expected a 2 row matrix, received a " + A.getRowDimension() + " row matrix.");
        }
        Point[] points = new Point[A.getColumnDimension()];
        for (int j = 0; j < A.getColumnDimension(); j++) {
            points[j] = convertToPoint(A.getMatrix(0, 1, j, j));
        }
        return points;
    }

    public static Matrix centerRelativeToComponentRelative(Matrix centerRelative) {

        Matrix uComponentSpace = centerRelative.copy();

        Matrix adjustX = new Matrix(2, uComponentSpace.getColumnDimension(), 0.5);
        Utils.multiplyRow(adjustX, 1, 0); // set other row to 0
        uComponentSpace.plusEquals(adjustX);

        Matrix adjustY = new Matrix(2, uComponentSpace.getColumnDimension(), 0.5);
        Utils.multiplyRow(adjustY, 0, 0); // set other row to 0
        uComponentSpace.plusEquals(adjustY);
        
        adjustY = new Matrix(2, uComponentSpace.getColumnDimension(), 1);
        Utils.multiplyRow(adjustY, 0, 0); // set other row to 0
        uComponentSpace = adjustY.minus(uComponentSpace);
        Utils.multiplyRow(uComponentSpace, 0, -1);

        return uComponentSpace;
    }

    public static double min(double[] nums) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < min) {
                min = nums[i];   
            }
        }
        return min;
    }

    public static int indexOfMin(double[] nums) {
        return indexOf(nums, min(nums));
    }

    public static int indexOfMin(double[] nums, double epsilon) {
        return indexOf(nums, min(nums), epsilon);
    }

    public static double max(double[] nums) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > max) {
                max = nums[i];   
            }
        }
        return max;
    }

    public static int indexOfMax(double[] nums) {
        return indexOf(nums, max(nums));
    }

    public static int indexOfMax(double[] nums, double epsilon) {
        return indexOf(nums, max(nums), epsilon);
    }

    public static int indexOf(double[] nums, double entry, double epsilon) {
        for (int i = 0; i < nums.length; i++) {
            if (Math.abs(nums[i]-entry) < epsilon) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(double[] nums, double entry) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == entry) {
                return i;
            }
        }
        return -1;
    }

    public static Triangle[] meshInit(Triangle[] initMesh, Matrix initCenter, double initScale) {
        Triangle[] _mesh = initMesh;
        for (int i = 0; i < _mesh.length; i++) {
            _mesh[i].displace(initCenter.times(-1));
            _mesh[i].setMatrix(_mesh[i].getMatrix().times(1/initScale));
        }
        return _mesh;
    }
}
