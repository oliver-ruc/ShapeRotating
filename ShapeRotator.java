import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
// import java.awt.Point;
// import java.awt.event.MouseAdapter;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
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

    private static final double ROTATE_SPEED = Math.toRadians(135); // radians per second
    private static final double MOVEMENT_SPEED = 3; //units per second
    private Set<Integer> keysPressed = new HashSet<Integer>();
    int frameRate = 60; // not frame rate
    long startTime;

    Solid myShape = new Solid(Meshes.CUBE_MESH, new Matrix(new double[] {0, 1, 0}, 3), 1);

    double xtheta = Math.toRadians(0), ytheta = Math.toRadians(0), ztheta = Math.toRadians(0);
    
    Matrix rotation = Matrix.identity(3, 3);
    Matrix accumulatedRotation = Matrix.identity(3, 3);
    
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
        
    int n = 5;
    int space = n*n*n;
    Solid[] minecraft = new Solid[space];
    
    /**
     * Called by the runtime system whenever the panel needs painting.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        accumulatedRotation = accumulatedRotation.times(rotation);

        
        for(int i = 0; i < minecraft.length; i++) {
            minecraft[i].update();
            minecraft[i].setCenter(rotation.times(minecraft[i].getCenter()));
            minecraft[i].rotate(rotation);
            // minecraft[i].setRotationMatrix(accumulatedRotation);
            minecraft[i].drawFilled(g, camera, getWidth(), getHeight(), Color.getHSBColor(1-1.0f/minecraft.length * i, 1f, 0.5f), Color.getHSBColor(1-1.0f/minecraft.length * i, 1f, 0.5f));
        }
        

        // myShape.setCenter(rotation.times(new Matrix(3,1)));
        // myShape.drawFilled(g, camera, getWidth(), getHeight(), Color.BLACK, Color.CYAN);
        // myShape.update();
        // myShape.rotate(rotation);
        // myShape.drawFilled(g, camera, getWidth(), getHeight(), Color.BLACK, Color.CYAN);
    }

    /**
     * A little driver in case you want a stand-alone application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var panel = new ShapeRotator();
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
                        panel.repaint(0, 0, panel.getWidth(), panel.getHeight());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000/panel.frameRate);
        });
    }

    public ShapeRotator() {
        super();
        startTime = System.currentTimeMillis();
        // SimpleMovement squiggleMovement2 = new SimpleMovement(()->(myShape.getCenter()), (Long currentTime, Long deltaTime) -> (squigglyMovement.apply(currentTime)), startTime, startTime + 10000);
        Matrix startingCenter = myShape.getCenter().copy();
        DisplacementFunction squiggleMovement2 = new DisplacementFunction((Long relativeTime) -> {
            // return RotationMatrices.AboutPositiveX(ROTATE_SPEED * (currentTime-startTime) / 1000).times(startingCenter).minus(startingCenter).times(Math.pow(Math.E, -(currentTime-startTime)/5000.0));
            return RotationMatrices.AboutPositiveX(ROTATE_SPEED * (relativeTime) / 1000).times(startingCenter).minus(startingCenter);
        }, startTime, true);
        RotationFunction squiggleMovement3 = new RotationFunction((Long relativeTime)->( RotationMatrices.AboutPositiveX(ROTATE_SPEED * (relativeTime) / 1000)), startTime, true);
        myShape.addDisplacementFunction(squiggleMovement2);
        // myShape.addDisplacementFunction(new DisplacementFunction((Long currentTime) -> (new Matrix(new double[] {(currentTime - startTime)/1000.0, 0, 0}, 3)), startTime));
        myShape.addRotationFunction(squiggleMovement3);
        rotation = rotation.times(RotationMatrices.AboutPositiveX(xtheta))
                           .times(RotationMatrices.AboutPositiveY(ytheta))
                           .times(RotationMatrices.AboutPositiveZ(ztheta));
        for(int i = 0; i < space; i++) {
            minecraft[i] = new Solid(Meshes.CUBE_MESH, new Matrix(new double[][] {
                {i%n - (n/2.0)+0.5},
                {(i/n)%n - (n/2.0)+0.5},
                {(i/(n*n))%n - (n/2.0)+0.5},
            }), 1);
            // minecraft[i].addRotationFunction(new RotationFunction((Long relativeTime)->(RotationMatrices.AboutPositiveX(ROTATE_SPEED * (relativeTime) / 1000)), startTime, true));
        }
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
        rotation = Matrix.identity(3, 3);
        if (keysPressed.contains(KeyEvent.VK_LEFT)) {
            rotation = RotationMatrices.AboutPositiveX(+ROTATE_SPEED/frameRate).times(rotation);
        }
        if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
            rotation = RotationMatrices.AboutPositiveX(-ROTATE_SPEED/frameRate).times(rotation);
        }
        if (keysPressed.contains(KeyEvent.VK_UP)) {
            rotation = RotationMatrices.AboutPositiveY(+ROTATE_SPEED/frameRate).times(rotation);
        }
        if (keysPressed.contains(KeyEvent.VK_DOWN)) {
            rotation = RotationMatrices.AboutPositiveY(-ROTATE_SPEED/frameRate).times(rotation);
        }
        if (keysPressed.contains(KeyEvent.VK_O)) {
            rotation = RotationMatrices.AboutPositiveZ(+ROTATE_SPEED/frameRate).times(rotation);
        }
        if (keysPressed.contains(KeyEvent.VK_P)) {
            rotation = RotationMatrices.AboutPositiveZ(-ROTATE_SPEED/frameRate).times(rotation);
        }
        if (keysPressed.contains(KeyEvent.VK_W)) {
            camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraUp ().times(+MOVEMENT_SPEED/frameRate)));
        }
        if (keysPressed.contains(KeyEvent.VK_S)) {
            camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraUp ().times(-MOVEMENT_SPEED/frameRate)));
        }
        if (keysPressed.contains(KeyEvent.VK_A)) {
            camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraLft().times(+MOVEMENT_SPEED/frameRate)));
        }
        if (keysPressed.contains(KeyEvent.VK_D)) {
            camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraLft().times(-MOVEMENT_SPEED/frameRate)));
        }
        if (keysPressed.contains(KeyEvent.VK_Z)) {
            camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraFwd().times(+MOVEMENT_SPEED/frameRate)));
        }
        if (keysPressed.contains(KeyEvent.VK_X)) {
            camera.setCameraPos(camera.getCameraPos().plus(camera.getCameraFwd().times(-MOVEMENT_SPEED/frameRate)));
        }
        if (keysPressed.contains(KeyEvent.VK_I)) {
            camera.setCameraMatrix(
                RotationMatrices.Abritary(camera.getCameraLft(), -ROTATE_SPEED/frameRate).times(camera.getCameraMatrix())
            );
        }
        if (keysPressed.contains(KeyEvent.VK_K)) {
            camera.setCameraMatrixWithoutChecks(
                RotationMatrices.Abritary(camera.getCameraLft(), +ROTATE_SPEED/frameRate).times(camera.getCameraMatrix())
            );
        }
        if (keysPressed.contains(KeyEvent.VK_J)) {
            camera.setCameraMatrixWithoutChecks(
                RotationMatrices.Abritary(camera.getCameraUp (), +ROTATE_SPEED/frameRate).times(camera.getCameraMatrix())
            );
        }
        if (keysPressed.contains(KeyEvent.VK_L)) {
            camera.setCameraMatrixWithoutChecks(
                RotationMatrices.Abritary(camera.getCameraUp (), -ROTATE_SPEED/frameRate).times(camera.getCameraMatrix())
            );
        }
        if (keysPressed.contains(KeyEvent.VK_Q)) {
            camera.setCameraMatrixWithoutChecks(
                RotationMatrices.Abritary(camera.getCameraFwd(), -ROTATE_SPEED/frameRate).times(camera.getCameraMatrix())
            );
        }
        if (keysPressed.contains(KeyEvent.VK_E)) {
            camera.setCameraMatrixWithoutChecks(
                RotationMatrices.Abritary(camera.getCameraFwd(), +ROTATE_SPEED/frameRate).times(camera.getCameraMatrix())
            );
        }
    }
}