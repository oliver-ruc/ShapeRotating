import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Color;

import Jama.Matrix;

public class Triangle {
    /**
     * A 3x3 matrix where each column vector is a vertex of the triangle.
     */
    private Matrix _pointMatrix = new Matrix(3, 3);

    /**
     * A 3 element array of booleans indicating which edges should be drawn, ordered [0-1, 0-2, 1-2].
     */
    private boolean[] _drawnEdges = new boolean[] {true, true, true};

    /**
     * Constructs a Triangle with the given vertices and which edges to draw.
     * @param points a 3 element array of 3x1 column vectors.
     * @param drawnEdges a 3 element array of booleans to designate which edges should be drawn (defaults to all). Order is [0 to 1, 0 to 2, 1 to 2].
     */
    public Triangle(Matrix[] points, boolean[] drawnEdges) {
        this(points);
        _drawnEdges = drawnEdges;
    }

    /**
     * Constructs a Triangle with the given vertices.
     * @param points a 3 element array of 3x1 column vectors.
     */
    public Triangle(Matrix[] points) {
        if (points.length != 3) {
            throw new IllegalArgumentException("Expected 3 points, recieved " + points.length + " points.");
        }
        for(int j = 0; j < 3; j++) {
            if (points[j].getRowDimension() != 3 || points[j].getColumnDimension() != 1) {
                throw new IllegalArgumentException("Points should be 3x1 column vectors. Intead, point " + j + " was " + points[j].getRowDimension() + "x" + points[j].getColumnDimension());
            }
            _pointMatrix.setMatrix(0, 2, j, j, points[j]);
        }
    }

    /**
     * Constructs a Triangle with a given matrix of points and which edges to draw.
     * @param pointMatrix a 3x3 matrix.
     * @param drawnEdges a 3 boolean array to designate which edges should be drawn (defaults to all). Order is [0 to 1, 0 to 2, 1 to 2].
     */
    public Triangle(Matrix pointMatrix, boolean[] drawnEdges) {
        this(pointMatrix);
        _drawnEdges = drawnEdges;
    }

    /**
     * Constructs a Triangle with a given matrx of points.
     * @param pointMatrix a 3x3 matrix.
     */
    public Triangle(Matrix pointMatrix) {
        if (pointMatrix.getRowDimension() != 3 || pointMatrix.getColumnDimension() != 3){
            throw new IllegalArgumentException("Expected 3x3 matrix, recieved " + pointMatrix.getRowDimension() + "x" + pointMatrix.getColumnDimension() + " matrix.");
        }
        _pointMatrix = pointMatrix.copy();
    }

    /**
     * 
     * @return a 3x3 matrix where each column vector is a vertex of the triangle.
     */
    public Matrix getMatrix() {
        return _pointMatrix.copy();
    }

    /**
     * 
     * @return An array of matrices, where each element is a column vector of one of the points.
     */
    public Matrix[] getPoints() {
        Matrix[] output = new Matrix[3];
        for(int j = 0; j < 3; j++) {
            output[j] = _pointMatrix.getMatrix(0, 2, j, j);
        }
        return output;
    }

    /**
     * 
     * @param index 0-based index for desired point.
     * @return 3x1 column vector with desired point.
     */
    public Matrix getPoint(int index) {
        if (index < 0 || index > 2) {
            throw new IllegalArgumentException("Index should be between 0, 1, or 2, was " + index + ".");
        }
        return _pointMatrix.getMatrix(0, 2, index, index);
    }

    /**
     * 
     * @param pointMatrix a 3x3 matrix where each column vector is a vertex of the triangle.
     */
    public void setMatrix(Matrix pointMatrix) {
        if (pointMatrix.getRowDimension() != 3 || pointMatrix.getColumnDimension() != 3){
            throw new IllegalArgumentException("Expected 3x3 matrix, recieved " + pointMatrix.getRowDimension() + "x" + pointMatrix.getColumnDimension() + " matrix.");
        }
        _pointMatrix = pointMatrix.copy();
    }

    /**
     * 
     * @param points an array of 3x1 column vectors that are the vectices of the triangle.
     */
    public void setPoints(Matrix[] points) {
        if (points.length != 3) {
            throw new IllegalArgumentException("Expected 3 points, recieved " + points.length + " points.");
        }
        for(int j = 0; j < 3; j++) {
            if (points[j].getRowDimension() != 3 || points[j].getColumnDimension() != 1) {
                throw new IllegalArgumentException("Points should be 3x1 column vectors. Intead, point " + j + " was " + points[j].getRowDimension() + "x" + points[j].getColumnDimension());
            }
            _pointMatrix.setMatrix(0, 2, j, j, points[j]);
        }
    }

    /**
     * 
     * @param index 0-based index for desired point.
     * @param point 3x1 column vector with desired point.
     */
    public void setPoint(int index, Matrix point) {
        _pointMatrix.setMatrix(0, 2, index, index, point);
    }

    /**
     * 
     * @return a 3 element boolean array indicating which edges of the triangle should be drawn, ordered [0-1, 0-2, 1-2].
     */
    public boolean[] getDrawnEdges() {
        return _drawnEdges;
    }

    /**
     * Draws the edges of the triangle, if in front of the camera and according to _drawnEdges.
     * @param g the Graphics used to draw the triangle.
     * @param camera the Camera from which to draw the triangle from.
     * @param width  the width  of the component.
     * @param height the height of the component.
     */
    public void drawEdges(Graphics g, Camera camera, double width, double height) {
        Matrix trianglePlaneRelative = camera.convertToCenterRelative(_pointMatrix);
        Matrix triangleComponentRelative = Utils.centerRelativeToComponentRelative(trianglePlaneRelative.getMatrix(1, 2, 0, trianglePlaneRelative.getColumnDimension()-1));
        
        Matrix triangleComponentSpace = triangleComponentRelative.copy();
        Utils.multiplyRow(triangleComponentSpace, 0, width );
        Utils.multiplyRow(triangleComponentSpace, 1, height);

        Point[] points = Utils.convertToPoints(triangleComponentSpace.copy());

        if (_drawnEdges[0] && (trianglePlaneRelative.get(0, 0) > 0.5 || trianglePlaneRelative.get(0, 1) > 0.5)) {
            g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
        }
        if (_drawnEdges[1] && (trianglePlaneRelative.get(0, 0) > 0.5 || trianglePlaneRelative.get(0, 2) > 0.5)) {
            g.drawLine(points[0].x, points[0].y, points[2].x, points[2].y);
        }
        if (_drawnEdges[2] && (trianglePlaneRelative.get(0, 1) > 0.5 || trianglePlaneRelative.get(0, 2) > 0.5)) {
            g.drawLine(points[1].x, points[1].y, points[2].x, points[2].y);
        }
    }

    /**
     * Draws the edges of the triangle with the desired color, if in front of the camera and according to _drawnEdges.
     * @param g the Graphics used to draw the triangle.
     * @param camera the Camera from which to draw the triangle from.
     * @param width  the width  of the component.
     * @param height the height of the component.
     * @param edgeColor the desired color of the edges.
     * @see Triangle#drawEdges(Graphics g, Camera camera, double width, double height)
     */
    public void drawEdges(Graphics g, Camera camera, double width, double height, Color edgeColor) {
        Color savedColor = g.getColor();
        g.setColor(edgeColor);
        drawEdges(g, camera, width, height);
        g.setColor(savedColor);
    }

    /**
     * Draws a filled in triangle with the desired fill and edge colors, if in front of the camera and edges according to _drawnEdges.
     * @param g the Graphics used to draw the triangle.
     * @param camera the Camera from which to draw the triangle from.
     * @param width  the width  of the component.
     * @param height the height of the component.
     * @param edgeColor the desired color of the edges.
     * @param fillColor the desired color of the inside of the triangle.
     */
    public void drawFilled(Graphics g, Camera camera, double width, double height, Color edgeColor, Color fillColor) {
        Color savedColor = g.getColor();
        
        Matrix trianglePlaneRelative = camera.convertToCenterRelative(_pointMatrix);
        Matrix triangleComponentRelative = Utils.centerRelativeToComponentRelative(trianglePlaneRelative.getMatrix(1, 2, 0, trianglePlaneRelative.getColumnDimension()-1));
        
        Matrix triangleComponentSpace = triangleComponentRelative.copy();
        Utils.multiplyRow(triangleComponentSpace, 0, width );
        Utils.multiplyRow(triangleComponentSpace, 1, height);

        Point[] points = Utils.convertToPoints(triangleComponentSpace.copy());

        if (trianglePlaneRelative.get(0, 0) > 0.5 && trianglePlaneRelative.get(0, 1) > 0.5 && trianglePlaneRelative.get(0, 2) > 0.5) {
            g.setColor(fillColor);
            Polygon ePolygon = new Polygon(new int[] {points[0].x, points[1].x, points[2].x}, new int[] {points[0].y, points[1].y, points[2].y}, 3);
            g.fillPolygon(ePolygon);
        }

        g.setColor(edgeColor);
        if (_drawnEdges[0] && (trianglePlaneRelative.get(0, 0) > 0.5 || trianglePlaneRelative.get(0, 1) > 0.5)) {
            g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
        }
        if (_drawnEdges[1] && (trianglePlaneRelative.get(0, 0) > 0.5 || trianglePlaneRelative.get(0, 2) > 0.5)) {
            g.drawLine(points[0].x, points[0].y, points[2].x, points[2].y);
        }
        if (_drawnEdges[2] && (trianglePlaneRelative.get(0, 1) > 0.5 || trianglePlaneRelative.get(0, 2) > 0.5)) {
            g.drawLine(points[1].x, points[1].y, points[2].x, points[2].y);
        }

        g.setColor(savedColor);
    }

    /**
     * Displaces the triangle by a given vector.
     * @param displacement a 3x1 column vector.
     */
    public void displace(Matrix displacement) {
        if (displacement.getRowDimension() != 3 || displacement.getColumnDimension() != 1){
            throw new IllegalArgumentException("Expected 3x1 matrix, recieved " + displacement.getRowDimension() + "x" + displacement.getColumnDimension() + " matrix.");
        }
        for(int j = 0; j < 3; j++) {
            _pointMatrix.setMatrix(0, 2, j, j, _pointMatrix.getMatrix(0, 2, j, j).plus(displacement));
        }
    }

    /**
     * Finds the distance from a point to the centroid of the triangle.
     * @param relativePoint a 3x1 column vector from which to find the distance.
     * @return the distance from relativePoint to the centroid of the triangle.
     */
    public double getDistanceFromCentroid(Matrix relativePoint) {
        return computeCentroid().minus(relativePoint).normF();
    }

    /**
     * Finds the centroid of the triangle.
     * @return a 3x1 column vector.
     */
    public Matrix computeCentroid() {
        Matrix averagePoint = new Matrix(3, 1);
        for(int j = 0; j < 3; j++) {
            averagePoint.plusEquals(_pointMatrix.getMatrix(0, 2, j, j));
        }
        return averagePoint.times(1.0/3);
    }
}
