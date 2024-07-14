import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javax.imageio.ImageIO;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private BufferedImage originalImage;
    private ControlPanel controlPanel;
    private Point[] selectedPoints;
    private int pointCount;
    private boolean drawBorder;
    private int imageX, imageY, drawWidth, drawHeight;
    Stack<BufferedImage> undoStack;
    Stack<BufferedImage> redoStack;

    public ImagePanel(JFrame frame) {
        setLayout(new BorderLayout());

        controlPanel = new ControlPanel(this);
        add(controlPanel, BorderLayout.SOUTH);

        selectedPoints = new Point[4];
        pointCount = 0;
        drawBorder = false;
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (pointCount > 0) {
                        pointCount--;
                        selectedPoints[pointCount] = null;
                        drawBorder = false;
                        repaint();
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (pointCount == 4) {
                        // Reset the points
                        pointCount = 0;
                        drawBorder = false;
                        selectedPoints = new Point[4];
                    } else if (isClickInsideImage(e.getPoint())) {
                        selectedPoints[pointCount] = e.getPoint();
                        pointCount++;
                        if (pointCount == 4) {
                            drawBorder = true;
                        }
                    }
                    repaint();
                }
                controlPanel.updateApplyButtonState(pointCount);
            }
        });
    }

    private boolean isClickInsideImage(Point point) {
        return point.x >= imageX && point.x <= (imageX + drawWidth) &&
                point.y >= imageY && point.y <= (imageY + drawHeight);
    }

    public void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedImage newImage = ImageIO.read(file);
                setImage(newImage);
                originalImage = copyImage(newImage);
                resetStates();
                controlPanel.enableControls(true);
                repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setImage(BufferedImage newImage) {
        image = new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawImage(newImage, 0, 0, null);
        g.dispose();
    }

    private void resetStates() {
        undoStack.clear();
        redoStack.clear();
        pointCount = 0;
        drawBorder = false;
        selectedPoints = new Point[4];
        controlPanel.updateClearButtonState();
    }

    public void applyFilter(String filterName) {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Please load an image first.");
            return;
        }

        undoStack.push(copyImage(image));
        redoStack.clear();

        if (pointCount == 4) {
            Rectangle selection = getSelectionRectangle();
            int imgX1 = (selection.x - imageX) * image.getWidth() / drawWidth;
            int imgY1 = (selection.y - imageY) * image.getHeight() / drawHeight;
            int imgX2 = (selection.width * image.getWidth()) / drawWidth;
            int imgY2 = (selection.height * image.getHeight()) / drawHeight;

            BufferedImage subImage = image.getSubimage(imgX1, imgY1, imgX2, imgY2);
            Filters.applyFilter(subImage, filterName);
            Graphics g = image.getGraphics();
            g.drawImage(subImage, imgX1, imgY1, null);
            g.dispose();
        } else {
            Filters.applyFilter(image, filterName);
        }
        pointCount = 0;
        drawBorder = false;
        repaint();
        controlPanel.updateUndoRedoButtonState();
        controlPanel.updateClearButtonState();
    }

    public void undoFilter() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyImage(image));
            image = undoStack.pop();
            repaint();
            controlPanel.updateUndoRedoButtonState();
            controlPanel.updateClearButtonState();
        }
    }

    public void redoFilter() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyImage(image));
            image = redoStack.pop();
            repaint();
            controlPanel.updateUndoRedoButtonState();
            controlPanel.updateClearButtonState();
        }
    }

    public void clearFilters() {
        if (originalImage != null) {
            image = copyImage(originalImage);
            resetStates();
            repaint();
            controlPanel.updateUndoRedoButtonState();
            controlPanel.updateClearButtonState();
        }
    }

    public void saveImage() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Please load an image first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG files", "jpg", "jpeg"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getAbsolutePath().endsWith(".jpg") && !file.getAbsolutePath().endsWith(".jpeg")) {
                    file = new File(file.getAbsolutePath() + ".jpg");
                }
                ImageIO.write(image, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Rectangle getSelectionRectangle() {
        int minX = Math.min(Math.min(selectedPoints[0].x, selectedPoints[1].x), Math.min(selectedPoints[2].x, selectedPoints[3].x));
        int minY = Math.min(Math.min(selectedPoints[0].y, selectedPoints[1].y), Math.min(selectedPoints[2].y, selectedPoints[3].y));
        int maxX = Math.max(Math.max(selectedPoints[0].x, selectedPoints[1].x), Math.max(selectedPoints[2].x, selectedPoints[3].x));
        int maxY = Math.max(Math.max(selectedPoints[0].y, selectedPoints[1].y), Math.max(selectedPoints[2].y, selectedPoints[3].y));

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copy.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int width = getWidth();
            int height = getHeight() - controlPanel.getHeight();
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();
            double imgAspect = (double) imgWidth / imgHeight;
            double panelAspect = (double) width / height;

            drawWidth = width;
            drawHeight = height;
            if (panelAspect > imgAspect) {
                drawWidth = (int) (height * imgAspect);
            } else {
                drawHeight = (int) (width / imgAspect);
            }

            imageX = (width - drawWidth) / 2;
            imageY = (height - drawHeight) / 2;

            g.drawImage(image, imageX, imageY, drawWidth, drawHeight, this);

            g.setColor(Color.RED);
            if (drawBorder && pointCount == 4) {
                Rectangle selection = getSelectionRectangle();
                g.drawRect(selection.x, selection.y, selection.width, selection.height);
            } else {
                for (int i = 0; i < pointCount; i++) {
                    g.fillOval(selectedPoints[i].x - 5, selectedPoints[i].y - 5, 10, 10);
                }
            }
        }
    }

    public void showInstructions() {
        String instructions = "<html><body>" +
                "<h2><b>Instructions</b></h2>" +
                "<p><b>Load Image:</b> Click 'Select Image'</p><br>" +
                "<p><b>Apply Filter:</b> Select filter, click 'Apply'</p><br>" +
                "<p><b>Dots:</b> Left-click to add, right-click to remove</p><br>" +
                "<p><b>Reset Dots:</b> Click to reset after 4 dots</p><br>" +
                "<p><b>Undo/Redo:</b> Use 'Undo' and 'Redo' buttons</p><br>" +
                "<p><b>Clear Filters:</b> Click 'Clear Filters'</p><br>" +
                "<p><b>Save Image:</b> Click 'Save'</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, instructions, "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
}
