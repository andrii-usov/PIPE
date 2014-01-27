/*
 * Created on 08-Feb-2004
 */
package pipe.gui;

import pipe.controllers.PetriNetController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;


/**
 * @author Peter Kyme, Michael Camacho
 * Class to handle selection rectangle functionality
 */
public class SelectionManager 
        extends javax.swing.JComponent
        implements java.awt.event.MouseListener, 
                   java.awt.event.MouseWheelListener,
                   java.awt.event.MouseMotionListener {

   private Point startPoint;
   private final Rectangle selectionRectangle = new Rectangle(-1,-1);
   private boolean isSelecting;
   private static final Color SELECTION_COLOR = new Color(0, 0, 255, 24);
   private static final Color SELECTION_COLOR_OUTLINE = new Color(0, 0, 100);
   private final PetriNetTab petriNetTab;
   private boolean enabled = true;
   private final PetriNetController petriNetController;

    /**
     * Zoom as a percentage e.g. 100%
     */
   private int zoom = 100;

   public SelectionManager(PetriNetTab _view, PetriNetController controller) {
      addMouseListener(this);
      addMouseMotionListener(this);
      addMouseWheelListener(this);
      this.petriNetTab = _view;
       this.petriNetController = controller;
   }
   
   public void setZoom(int zoom) {
       this.zoom = zoom;
   }


   public void updateBounds() {
      if (enabled) {
         setBounds(0, 0, petriNetTab.getWidth(), petriNetTab.getHeight());
      }
   }

   
   public void enableSelection() {
      if (!enabled) {
         petriNetTab.add(this);
         enabled = true;
         updateBounds();
      }
   }

   
   public void disableSelection() {
       if (enabled) {
         petriNetTab.remove(this);
         enabled = false;
      }
   }

   
   private void processSelection(MouseEvent e) {
      if (!e.isShiftDown()){
         clearSelection();
      }
      
      // Get all the objects in the current window
//      ArrayList <PetriNetViewComponent> pns = petriNetTab.getPNObjects();
//      for (PetriNetViewComponent pn : pns) {
//         pn.select(selectionRectangle);
//      }

       Rectangle unzoomedRectangle = calculateUnzoomedSelection();
       petriNetController.select(unzoomedRectangle);
   }


    /**
     * uses zoom and the ZoomController to calculate what the
     * unzoomed selection rectangle would be
     */
    private Rectangle calculateUnzoomedSelection() {
        ZoomController zoomController = petriNetTab.getZoomController();
        int x = zoomController.getUnzoomedValue((int) selectionRectangle.getX());
        int y = zoomController.getUnzoomedValue((int) selectionRectangle.getY());
        int height = zoomController.getUnzoomedValue((int) selectionRectangle.getHeight());
        int width = zoomController.getUnzoomedValue((int) selectionRectangle.getWidth());
        return new Rectangle(x, y, width, height);
    }

   
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(SELECTION_COLOR);
      g2d.fill(selectionRectangle);
      g2d.setPaint(SELECTION_COLOR_OUTLINE);
      g2d.draw(selectionRectangle);
   }

   public void clearSelection() {
       petriNetController.deselectAll();
   }

   public void translateSelection(int transX, int transY) {

      if (transX == 0 && transY == 0) {
         return;
      }

      // First see if translation will put anything at a negative location
      Point topleft = null;

      // Get all the objects in the current window
//      List<PetriNetViewComponent> pns = petriNetTab.getPNObjects();
//      for (PetriNetViewComponent pn : pns) {
//         if (petriNetController.isSelected(pn.getModel())){
//            Point point = pn.getLocation();
//            if (topleft == null) {
//               topleft = point;
//            } else {
//               if (point.x < topleft.x) {
//                  topleft.x = point.x;
//               }
//               if (point.y < topleft.y) {
//                  topleft.y = point.y;
//               }
//            }
//         }
//      }
//
//      if (topleft != null) {
//         topleft.translate(transX, transY);
//         if (topleft.x < 0){
//            transX -= topleft.x;
//         }
//         if (topleft.y < 0){
//            transY -= topleft.y;
//         }
//         if (transX == 0 && transY == 0){
//            return;
//         }
//      }

       petriNetController.translateSelected(new Point2D.Double(transX, transY));

//      for (PetriNetViewComponent pn : pns) {
//         if (pn.isSelected()) {
//            pn.translate(transX, transY);
//         }
//      }
      petriNetTab.updatePreferredSize();
   }

   
   public ArrayList getSelection() {
      ArrayList selection = new ArrayList();

//      // Get all the objects in the current window
//      ArrayList <PetriNetViewComponent> pns = petriNetTab.getPNObjects();
//      for (PetriNetViewComponent pn : pns) {
//         if (petriNetController.isSelected(pn.getModel())){
////        	 if(pn instanceof ArcView)
////        		 System.out.println("arc found");
//        	 selection.add(pn);
//         }
//      }
      return selection;
   }

   
   @Override
   public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1 && !(e.isControlDown())) {
         isSelecting = true;
         petriNetTab.setLayer(this, Constants.SELECTION_LAYER_OFFSET);
         startPoint = e.getPoint();
         selectionRectangle.setRect(startPoint.getX(), startPoint.getY(), 0, 0);
         // Select anything that intersects with the rectangle.
         processSelection(e);
         repaint();
      } else {
         startPoint = e.getPoint();
      }
   }

   
   @Override
   public void mouseReleased(MouseEvent e) {
      if (isSelecting) {
         // Select anything that intersects with the rectangle.
         processSelection(e);
         isSelecting = false;
         petriNetTab.setLayer(this, Constants.LOWEST_LAYER_OFFSET);
         selectionRectangle.setRect(-1, -1, 0, 0);
         repaint();
      }
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
    */
   @Override
   public void mouseDragged(MouseEvent e) {
      if (isSelecting) {
         selectionRectangle.setSize(
                  (int)Math.abs(e.getX() - startPoint.getX()),
                  (int)Math.abs(e.getY() - startPoint.getY()));
         selectionRectangle.setLocation(
                  (int)Math.min(startPoint.getX(), e.getX()),
                  (int)Math.min(startPoint.getY(), e.getY()));
         // Select anything that intersects with the rectangle.
         processSelection(e);
         repaint();
      } else {   
         petriNetTab.drag(startPoint, e.getPoint());
      }
   }


   @Override
   public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
         if (e.getWheelRotation()> 0) {
//            petriNetTab.zoomIn();
         } else {
//            petriNetTab.zoomOut();
         }
      }
   }   
   
   @Override
   public void mouseClicked(MouseEvent e) {
       // Not needed
   }

   
   @Override
   public void mouseEntered(MouseEvent e) {
       // Not needed
   }

   
   @Override
   public void mouseExited(MouseEvent e) {
       // Not needed
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
    */
   @Override
   public void mouseMoved(MouseEvent e) {
       // Not needed
   }

}