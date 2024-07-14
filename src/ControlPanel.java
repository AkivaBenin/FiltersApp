import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    public JComboBox<String> filterBox;
    private JButton applyButton;
    private JButton saveButton;
    private JButton clearButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton selectImageButton;
    private JButton instructionsButton;
    private ImagePanel imagePanel;

    public ControlPanel(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;

        setLayout(new BorderLayout()); // Use BorderLayout

        JPanel topPanel = new JPanel(new FlowLayout()); // Top panel with select image
        JPanel middlePanel = new JPanel(new FlowLayout()); // Middle panel with apply, save
        JPanel bottomPanel = new JPanel(new FlowLayout()); // Bottom panel with undo, redo, clear filters
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel for instructions button

        selectImageButton = new JButton("Select Image");
        selectImageButton.addActionListener(e -> imagePanel.loadImage());

        filterBox = new JComboBox<>(new String[]{
                "Black-White", "Grayscale", "Posterize", "Tint",
                "Color Shift Right", "Mirror", "Pixelate", "Show Borders",
                "Eliminate Red", "Negative"
        });
        filterBox.setEnabled(false);

        applyButton = new JButton("Apply");
        applyButton.setEnabled(false);
        applyButton.addActionListener(e -> imagePanel.applyFilter((String) filterBox.getSelectedItem()));

        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> imagePanel.saveImage());

        undoButton = new JButton("Undo");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> imagePanel.undoFilter());

        redoButton = new JButton("Redo");
        redoButton.setEnabled(false);
        redoButton.addActionListener(e -> imagePanel.redoFilter());

        clearButton = new JButton("Clear Filters");
        clearButton.setEnabled(false);
        clearButton.addActionListener(e -> imagePanel.clearFilters());

        // Create Instructions button with a "?" icon
        instructionsButton = new JButton("?");
        instructionsButton.setToolTipText("Instructions");
        instructionsButton.addActionListener(e -> imagePanel.showInstructions());

        // Add components to top panel
        topPanel.add(selectImageButton);

        // Add components to middle panel
        middlePanel.add(new JLabel("Filter:"));
        middlePanel.add(filterBox);
        middlePanel.add(applyButton);
        middlePanel.add(saveButton);

        // Add components to bottom panel
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);
        bottomPanel.add(clearButton);

        // Add instructions button to instruction panel
        instructionPanel.add(instructionsButton);

        // Add panels to control panel
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(instructionPanel, BorderLayout.EAST);
    }

    public void enableControls(boolean enabled) {
        filterBox.setEnabled(enabled);
        applyButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        updateUndoRedoButtonState();
        updateClearButtonState();
    }

    public void updateApplyButtonState(int pointCount) {
        applyButton.setEnabled(pointCount == 0 || pointCount == 4);
    }

    public void updateUndoRedoButtonState() {
        undoButton.setEnabled(!imagePanel.undoStack.isEmpty());
        redoButton.setEnabled(!imagePanel.redoStack.isEmpty());
    }

    public void updateClearButtonState() {
        clearButton.setEnabled(!imagePanel.undoStack.isEmpty());
    }
}
