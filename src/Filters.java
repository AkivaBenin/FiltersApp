import java.awt.*;
import java.awt.image.BufferedImage;

public class Filters {
    public static void applyFilter(BufferedImage image, String filterName) {
        switch (filterName) {
            case "Black-White":
                blackWhite(image);
                break;
            case "Grayscale":
                grayscale(image);
                break;
            case "Posterize":
                posterize(image);
                break;
            case "Tint":
                tint(image, Color.CYAN);
                break;
            case "Color Shift Right":
                colorShiftRight(image);
                break;
            case "Mirror":
                mirror(image);
                break;
            case "Pixelate":
                pixelate(image);
                break;
            case "Show Borders":
                showBorders(image);
                break;
            case "Eliminate Red":
                eliminateColor(image, 'R');
                break;
            case "Negative":
                negative(image);
                break;
        }
    }

    public static void blackWhite(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                if (gray > 127) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    public static void grayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                int newRgb = (gray << 16) | (gray << 8) | gray;
                image.setRGB(x, y, newRgb);
            }
        }
    }

    public static void posterize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int levels = 4; // Number of levels for posterization

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                r = (r / (256 / levels)) * (256 / levels);
                g = (g / (256 / levels)) * (256 / levels);
                b = (b / (256 / levels)) * (256 / levels);

                int newRgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, newRgb);
            }
        }
    }

    public static void tint(BufferedImage image, Color tint) {
        int width = image.getWidth();
        int height = image.getHeight();
        int tintRgb = tint.getRGB() & 0xFFFFFF;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                r = Math.min((r + ((tintRgb >> 16) & 0xFF)) / 2, 255);
                g = Math.min((g + ((tintRgb >> 8) & 0xFF)) / 2, 255);
                b = Math.min((b + (tintRgb & 0xFF)) / 2, 255);

                int newRgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, newRgb);
            }
        }
    }

    public static void colorShiftRight(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int newRgb = (b << 16) | (r << 8) | g;
                image.setRGB(x, y, newRgb);
            }
        }
    }

    public static void mirror(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                int leftRgb = image.getRGB(x, y);
                int rightRgb = image.getRGB(width - 1 - x, y);
                image.setRGB(x, y, rightRgb);
                image.setRGB(width - 1 - x, y, leftRgb);
            }
        }
    }

    public static void pixelate(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pixelSize = 10; // Size of each pixel block

        for (int y = 0; y < height; y += pixelSize) {
            for (int x = 0; x < width; x += pixelSize) {
                int rgb = image.getRGB(x, y);

                for (int dy = 0; dy < pixelSize; dy++) {
                    for (int dx = 0; dx < pixelSize; dx++) {
                        if (x + dx < width && y + dy < height) {
                            image.setRGB(x + dx, y + dy, rgb);
                        }
                    }
                }
            }
        }
    }

    public static void showBorders(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int threshold = 10; // Threshold for edge detection

        BufferedImage edges = new BufferedImage(width, height, image.getType());
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int rgbRight = image.getRGB(x + 1, y);
                int rRight = (rgbRight >> 16) & 0xFF;
                int gRight = (rgbRight >> 8) & 0xFF;
                int bRight = rgbRight & 0xFF;

                int rgbDown = image.getRGB(x, y + 1);
                int rDown = (rgbDown >> 16) & 0xFF;
                int gDown = (rgbDown >> 8) & 0xFF;
                int bDown = rgbDown & 0xFF;

                int edgeColor = Math.abs(r - rRight) + Math.abs(g - gRight) + Math.abs(b - bRight) +
                        Math.abs(r - rDown) + Math.abs(g - gDown) + Math.abs(b - bDown);

                if (edgeColor > threshold) {
                    edges.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    edges.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        Graphics g = image.getGraphics();
        g.drawImage(edges, 0, 0, null);
        g.dispose();
    }

    public static void eliminateColor(BufferedImage image, char color) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (color == 'R') {
                    r = 0;
                } else if (color == 'G') {
                    g = 0;
                } else if (color == 'B') {
                    b = 0;
                }

                int newRgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, newRgb);
            }
        }
    }

    public static void negative(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int newRgb = ((255 - r) << 16) | ((255 - g) << 8) | (255 - b);
                image.setRGB(x, y, newRgb);
            }
        }
    }
}
