import javax.swing.JPanel;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import java.util.ArrayList;

public class Bezier extends JPanel implements MouseListener{
    int panel_width = 800;
    int panel_height = 600;

    double[][] anchors;
    ArrayList<double[]> anchors_list;

    int curve_steps = 8000;
    int anchor_count = 4;

    public Bezier() {
        anchors_list = new ArrayList<double[]>();

        addMouseListener(this);
    }

    public Dimension getPreferredSize() {
        return new Dimension(panel_width, panel_height);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);

        double point_radius = 6;
        for (double[] point : anchors_list) {
            g.fillOval(
            (int)(point[0] - point_radius/2),
            (int)(point[1] - point_radius/2),
            (int)point_radius,
            (int)point_radius
            );
        }

        if (anchors != null) {
            double[] pP0 = anchors[0].clone();

            double time_increment = 1.0/curve_steps;
            for (double t = 0; t <= 1; t += time_increment) {
                double[] pP1 = lerp(anchors, t);

                g.drawLine((int)pP0[0], (int)pP0[1], (int)pP1[0], (int)pP1[1]);

                pP0 = pP1;
            }
        }

    }

    /*
    turns out, the bezier algorithm is a recursive function
    the base case is just lerping btw to points
    higher order bezier curves are lerping btw the all sets of consecutive points and running again

    Example:
    anchors: A, B, C, D
    final point: P
    Base case: P = lerp(A, B, t) = (1-t)A + tB

    Complex case:
    E = lerp(A, B, t)
    D = lerp(B, C, t)
    F = lerp(C, D, t)

    G = lerp(E, D, t)
    H = lerp(D, F, t)

    P = lerp(G, H, t)

    NOTE: this calculates the position of P at time t
    */
    public double[] lerp(double[][] points, double t) {
        if (points.length == 2) {
            return new double[] {
                (1 - t) * points[0][0] + t * points[1][0],
                (1 - t) * points[0][1] + t * points[1][1]
            };
        } else {
            double[][] newPoints = new double[points.length - 1][2];

            for (int i = 0; i < points.length - 1; i++) {
                newPoints[i] = lerp(new double[][] {points[i], points[i+1]}, t);
            }
            return lerp(newPoints, t);
        }
    }

    //create points for the anchors based on mouse clicks
    public void mouseClicked(MouseEvent e) {
        if (anchors_list.size() < anchor_count) {
            anchors_list.add(new double[] {e.getX(), e.getY()});
        }

        //once we have sufficient points, make it a list
        //can't use ArrayList.toArray(new Object[0]) b/c the ArrayList is made of arrays
        if (anchors_list.size() >= anchor_count && anchors == null) {
            anchors = new double[anchor_count][2];
            for (int i = 0; i < anchor_count; i++) {
                anchors[i] = anchors_list.get(i);
            }
        }
        repaint();
    }


    //These are all to make to make warnings shut up or neccessary for an implementation
    public void mousePressed(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    private static final long serialVersionUID = 1;
}
