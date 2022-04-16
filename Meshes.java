import Jama.Matrix;

public class Meshes {
    public static final Triangle[] BIPYRAMIDAL_MESH = new Triangle[] {
        new Triangle(new Matrix(new double[][] {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        })),
        new Triangle(new Matrix(new double[][] {
            {-1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        })),
        new Triangle(new Matrix(new double[][] {
            {1, 0, 0},
            {0, -1, 0},
            {0, 0, 1}
        })),
        new Triangle(new Matrix(new double[][] {
            {-1, 0, 0},
            {0, -1, 0},
            {0, 0, 1}
        })),
        new Triangle(new Matrix(new double[][] {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, -1}
        })),
        new Triangle(new Matrix(new double[][] {
            {-1, 0, 0},
            {0, 1, 0},
            {0, 0, -1}
        })),
        new Triangle(new Matrix(new double[][] {
            {1, 0, 0},
            {0, -1, 0},
            {0, 0, -1}
        })),
        new Triangle(new Matrix(new double[][] {
            {-1, 0, 0},
            {0, -1, 0},
            {0, 0, -1}
        }))
    };
    public static final Triangle[] CUBE_MESH = Utils.meshInit(new Triangle[] {
        new Triangle(new Matrix(new double[][] {
            {0, 1, 0},
            {0, 0, 1},
            {0, 0, 0}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {1, 1, 0},
            {1, 0, 1},
            {0, 0, 0}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {0, 1, 0},
            {0, 0, 0},
            {0, 0, 1}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {1, 1, 0},
            {0, 0, 0},
            {1, 0, 1}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {0, 0, 0},
            {1, 1, 0},
            {1, 0, 1}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {1, 0, 1},
            {1, 1, 0},
            {1, 1, 1}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {0, 0, 1},
            {0, 1, 0},
            {1, 1, 1}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {1, 0, 1},
            {1, 1, 1},
            {1, 1, 0}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {0, 0, 1},
            {1, 1, 1},
            {0, 1, 0}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 0}
        }), new boolean[] {true, true, false}),
        new Triangle(new Matrix(new double[][] {
            {1, 1, 1},
            {0, 0, 1},
            {0, 1, 0}
        }), new boolean[] {true, true, false}) 
    }, new Matrix(3, 1, 0.5), 1);
    public static Triangle[] TETRAHEDRON_MESH(Matrix p1, Matrix p2, Matrix p3, Matrix p4) {
        Matrix centroid = p1.plus(p2).plus(p3).plus(p4).times(0.25);
        return Utils.meshInit(new Triangle[] {
            new Triangle(new Matrix[] {p1, p2, p3}),
            new Triangle(new Matrix[] {p1, p2, p4}),
            new Triangle(new Matrix[] {p1, p3, p4}),
            new Triangle(new Matrix[] {p2, p3, p4})
        }, centroid, 1);
    }
    public static Triangle[] TETRAHEDRON_MESH(Matrix[] points) {
        return TETRAHEDRON_MESH(points[0], points[1], points[2], points[3]);
    }
    public static final Triangle[] REGULAR_TETRAHEDRON_MESH = TETRAHEDRON_MESH(
        new Matrix(new double[] { 0.5, 0, -1/Math.sqrt(8)}, 3),
        new Matrix(new double[] {-0.5, 0, -1/Math.sqrt(8)}, 3),
        new Matrix(new double[] {0,  0.5,  1/Math.sqrt(8)}, 3),
        new Matrix(new double[] {0, -0.5,  1/Math.sqrt(8)}, 3)
    );
}
