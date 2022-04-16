import java.lang.Math;

import Jama.Matrix;

public class RotationMatrices {
    public static Matrix AboutPositiveX(double theta) {
        return new Matrix(new double[][] {
            {1, 0, 0},
            {0, Math.cos(theta), -Math.sin(theta)},
            {0, Math.sin(theta),  Math.cos(theta)}
        });
    }
    public static Matrix AboutPositiveY(double theta) {
        return new Matrix(new double[][] {
            {Math.cos(theta), 0, Math.sin(theta)},
            {0, 1, 0},
            {-Math.sin(theta), 0, Math.cos(theta)}
        });
    }
    public static Matrix AboutPositiveZ(double theta) {
        return new Matrix(new double[][] {
            {Math.cos(theta), -Math.sin(theta), 0},
            {Math.sin(theta),  Math.cos(theta), 0},
            {0, 0, 1}
        });
    }
    public static Matrix TwoSpace(double theta) {
        return new Matrix(new double[][] {
            {Math.cos(theta), -Math.sin(theta)},
            {Math.sin(theta),  Math.cos(theta)}
        });
    }
    public static Matrix Abritary(Matrix axis, double theta) {
        double a = axis.get(0, 0),
               b = axis.get(1, 0),
               c = axis.get(2, 0);
        return new Matrix(new double[][] {
            {
                a*a*(1-Math.cos(theta)) + Math.cos(theta),
                a*b*(1-Math.cos(theta)) - c*Math.sin(theta),
                a*c*(1-Math.cos(theta)) + b*Math.sin(theta)
            },
            {
                a*b*(1-Math.cos(theta)) + c*Math.sin(theta),
                b*b*(1-Math.cos(theta)) + Math.cos(theta),
                b*c*(1-Math.cos(theta)) - a*Math.sin(theta)
            },
            {
                a*c*(1-Math.cos(theta)) - b*Math.sin(theta),
                b*c*(1-Math.cos(theta)) + a*Math.sin(theta),
                c*c*(1-Math.cos(theta)) + Math.cos(theta)
            }
        });
    }
    
}
