import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;

import Jama.Matrix;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A panel maintaining a picture of a Do Not Enter sign.
 */
public class ShapeRotator extends JPanel implements KeyListener {
    private static final long serialVersionUID = 7148504528835036003L;

    private static final double ROTATE_SPEED = Math.toRadians(5); // radians per frame
    private static final double MOVEMENT_SPEED = 0.1; //units per frame
    private Set<Integer> keysPressed = new HashSet<Integer>();

    Solid myShape = new Solid(Meshes.REGULAR_TETRAHEDRON_MESH, new Matrix(3,1), 1);

    double xtheta = Math.toRadians(0), ytheta = Math.toRadians(0), ztheta = Math.toRadians(0);
    
    Matrix rotation = Matrix.identity(3, 3);
    
    Matrix cameraPos = new Matrix(new double[][] {
        {3},
        {0},
        {0}
    });

    Matrix cameraFwd = new Matrix(new double[][] {
        {-1},
        {0},
        {0}
    });

    Matrix cameraLft = new Matrix(new double[][] {
        {0},
        {-1},
        {0}
    });

    double cameraD = 0.5;
    
    Camera camera = new Camera(cameraFwd, cameraLft, cameraD, Math.toRadians(90), Math.toRadians(90), cameraPos);
    
    /**
     * Called by the runtime system whenever the panel needs painting.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);

        // myShape.draw(g, camera, getWidth(), getHeight());

        
        int n = 5;
        int space = n*n*n;
        Solid[] minecraft = new Solid[space];
        for(int i = 0; i < space; i++) {
            minecraft[i] = new Solid(Meshes.CUBE_MESH, new Matrix(new double[][] {
                {i%n - (n/2.0)+0.5},
                {(i/n)%n - (n/2.0)+0.5},
                {(i/(n*n))%n - (n/2.0)+0.5},
            }), 0.25);
        }

        for(int i = 0; i < minecraft.length; i++) {
            g.setColor(Color.getHSBColor(1.0f/minecraft.length * i, 1f, 0.5f));
            minecraft[i].setCenter(rotation.times(minecraft[i].getCenter()));
            minecraft[i].setRotationMatrix(rotation);
            minecraft[i].drawFilled(g, camera, getWidth(), getHeight(), Color.BLACK, Color.getHSBColor(1.0f/minecraft.length * i, 1f, 0.5f));
        }
        
        
        // Solid myShape = new Solid(Meshes.CUBE_MESH);

        // myShape.setRotationMatrix(rotation);
        // myShape.setCenter(rotation.times(new Matrix(3,1)));
        // myShape.drawFilled(g, camera, getWidth(), getHeight(), Color.BLACK, Color.CYAN);
        
    }

    /**
     * A little driver in case you want a stand-alone application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var panel = new ShapeRotator();
            //panel.setBackground(Color.GREEN.darker());
            var frame = new JFrame("A simple graphics program");
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setVisible(true);
            frame.addKeyListener(panel);
            Timer mainLoop = new Timer();
            mainLoop.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    try {
                        panel.updateKeysStuffs();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000/30);
        });
    }

    public ShapeRotator() {
        super();

        rotation = rotation.times(RotationMatrices.AboutPositiveX(xtheta))
                           .times(RotationMatrices.AboutPositiveY(ytheta))
                           .times(RotationMatrices.AboutPositiveZ(ztheta));
    }

    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
        //System.out.println(e.getKeyCode());
    }

    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
        //System.out.println(e.getKeyCode());
    }

    public void keyTyped(KeyEvent e) {
        //System.out.println(e.getKeyCode());
    }

    public void updateKeysStuffs() {
        // System.out.println(keysPressed.size());
        for (Integer keyCode : keysPressed) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    rotation = RotationMatrices.AboutPositiveX( ROTATE_SPEED).times(rotation);
                    break;
                case KeyEvent.VK_RIGHT:
                    rotation = RotationMatrices.AboutPositiveX(-ROTATE_SPEED).times(rotation);
                    break;
                case KeyEvent.VK_UP:
                    rotation = RotationMatrices.AboutPositiveY( ROTATE_SPEED).times(rotation);
                    break;
                case KeyEvent.VK_DOWN:
                    rotation = RotationMatrices.AboutPositiveY(-ROTATE_SPEED).times(rotation);
                    break;
                case KeyEvent.VK_O:
                    rotation = RotationMatrices.AboutPositiveZ( ROTATE_SPEED).times(rotation);
                    break;
                case KeyEvent.VK_P:
                    rotation = RotationMatrices.AboutPositiveZ(-ROTATE_SPEED).times(rotation);
                    break;
                case KeyEvent.VK_W:
                    camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraUp ().times( MOVEMENT_SPEED)));
                    break;
                case KeyEvent.VK_S:
                    camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraUp ().times(-MOVEMENT_SPEED)));
                    break;
                case KeyEvent.VK_A:
                    camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraLft().times( MOVEMENT_SPEED)));
                    break;
                case KeyEvent.VK_D:
                    camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraLft().times(-MOVEMENT_SPEED)));
                    break;
                case KeyEvent.VK_X:
                    camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraFwd().times( MOVEMENT_SPEED)));
                    break;
                case KeyEvent.VK_Z:
                    camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraFwd().times(-MOVEMENT_SPEED)));
                    break;
                case KeyEvent.VK_I:
                    camera.setCameraMatrix(
                        RotationMatrices.Abritary(camera.getCameraLft(), -ROTATE_SPEED).times(camera.getCameraMatrix())
                    );
                    break;
                case KeyEvent.VK_K:
                    camera.setCameraMatrixWithoutChecks(
                        RotationMatrices.Abritary(camera.getCameraLft(), +ROTATE_SPEED).times(camera.getCameraMatrix())
                    );
                    break;
                case KeyEvent.VK_J:
                    camera.setCameraMatrixWithoutChecks(
                        RotationMatrices.Abritary(camera.getCameraUp (), +ROTATE_SPEED).times(camera.getCameraMatrix())
                    );
                    break;
                case KeyEvent.VK_L:
                    camera.setCameraMatrixWithoutChecks(
                        RotationMatrices.Abritary(camera.getCameraUp (), -ROTATE_SPEED).times(camera.getCameraMatrix())
                    );
                    break;
                case KeyEvent.VK_Q:
                    camera.setCameraMatrixWithoutChecks(
                        RotationMatrices.Abritary(camera.getCameraFwd(), -ROTATE_SPEED).times(camera.getCameraMatrix())
                    );
                    break;
                case KeyEvent.VK_E:
                    camera.setCameraMatrixWithoutChecks(
                        RotationMatrices.Abritary(camera.getCameraFwd(),  ROTATE_SPEED).times(camera.getCameraMatrix())
                    );
                    break;
            }
        }
        repaint(0, 0, getWidth(), getHeight());
    }
}