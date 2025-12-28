import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Intro2CS_2026A
 * This class represents a Graphical User Interface (GUI) for Map2D.
 * It handles graphical rendering using StdDraw and persistent storage (save/load).
 */
public class GUI {

    // Color palette for pixel values 0-12
    private static final Color[] COLORS = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
            Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA,
            Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
    };

    /**
     * Draws the Map2D onto a graphical window.
     * @param map The map to draw.
     */
    public static void drawMap(Map2D map) {
        if (map == null) return;

        int w = map.getWidth();
        int h = map.getHeight();

        // 1. Setup Canvas: Set smooth rendering and dynamic window size based on map dimensions
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(Math.max(512, w * 8), Math.max(512, h * 8));

        // 2. Setup Scale: Matches the coordinate system to the map size
        StdDraw.setXscale(0, w);
        StdDraw.setYscale(0, h);
        StdDraw.clear(Color.WHITE);

        // 3. Render Pixels
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixelVal = map.getPixel(x, y);
                Color c = COLORS[pixelVal % COLORS.length];

                StdDraw.setPenColor(c);

                /* * 4. Coordinate Inversion:
                 * Inverts Y because StdDraw (0,0) is bottom-left, while array y=0 is TOP.
                 * Square is centered at x+0.5, y+0.5 with radius 0.5.
                 */
                int drawY = (h - 1) - y;
                StdDraw.filledSquare(x + 0.5, drawY + 0.5, 0.5);
            }
        }

        // 5. Draw Grid: Thin lines to separate pixels visually
        StdDraw.setPenColor(new Color(0, 0, 0, 40)); // Light transparent black
        StdDraw.setPenRadius(0.0);
        for (int x = 0; x <= w; x++) StdDraw.line(x, 0, x, h);
        for (int y = 0; y <= h; y++) StdDraw.line(0, y, w, y);

        StdDraw.show();
    }

    /**
     * Loads a map from a text file with safety checks for comments and empty lines.
     * @param mapFileName Path to the file.
     * @return Map2D object or null on error.
     */
    public static Map2D loadMap(String mapFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(mapFileName))) {
            String header;

            // 1. Header Logic: Skip empty lines and comments (starting with #)
            while (true) {
                header = br.readLine();
                if (header == null) throw new IllegalArgumentException("Empty map file");
                header = header.trim();
                if (!header.isEmpty() && !header.startsWith("#")) break;
            }

            // 2. Parse Dimensions: Extract Width and Height
            String[] parts = header.split("\\s+");
            if (parts.length != 2) throw new IllegalArgumentException("Header must contain 'W H'");

            int w = Integer.parseInt(parts[0]);
            int h = Integer.parseInt(parts[1]);
            int[][] grid = new int[h][w];

            // 3. Parse Pixel Data
            for (int y = 0; y < h; y++) {
                String line = br.readLine();
                if (line == null) throw new IllegalArgumentException("Unexpected EOF at row " + y);

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length != w) throw new IllegalArgumentException("Row width mismatch at " + y);

                for (int x = 0; x < w; x++) {
                    grid[y][x] = Integer.parseInt(tokens[x]);
                }
            }
            return new Map(grid);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the Map2D into a text file using a StringBuilder for performance.
     * @param map The map to save.
     * @param mapFileName The target filename.
     */
    public static void saveMap(Map2D map, String mapFileName) {
        if (map == null) return;

        int w = map.getWidth();
        int h = map.getHeight();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mapFileName))) {
            // 1. Write Header
            bw.write(w + " " + h);
            bw.newLine();

            // 2. Write Data: Use StringBuilder for efficient string concatenation
            for (int y = 0; y < h; y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < w; x++) {
                    row.append(map.getPixel(x, y));
                    if (x < w - 1) row.append(" ");
                }
                bw.write(row.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




/// ---- main ----
public static void main(String[] args) {
    // 1. Define color constants for convenient use based on the COLORS palette
    final int BLACK = 0, BLUE = 1, CYAN = 2, DARK_GRAY = 3, GRAY = 4,
            GREEN = 5, LIGHT_GRAY = 6, MAGENTA = 7, ORANGE = 8,
            PINK = 9, RED = 10, WHITE = 11, YELLOW = 12;

    // 2. Initialize a new map (80x80) with a light gray background (6)
    // to verify colors don't default to white
    Map testMap = new Map(80, 80, LIGHT_GRAY);
    int wallColor = BLUE; // Obstacles will be represented in Blue
    int pathColor = RED;  // The calculated path will be marked in Red

    // 3. Create obstacles using various shapes (Testing drawRect, drawCircle, drawLine)
    // Draw a hollow rectangle on the left side
    testMap.drawRect(new Index2D(10, 10), new Index2D(30, 40), wallColor);

    // Draw a large circle in the center
    testMap.drawCircle(new Index2D(40, 40), 15, wallColor);

    // Draw diagonal walls (Testing the drawLine algorithm)
    testMap.drawLine(new Index2D(0, 70), new Index2D(20, 50), wallColor);
    testMap.drawLine(new Index2D(60, 10), new Index2D(79, 30), wallColor);

    // 4. Test the fill function - Fills the center of the previously drawn circle
    testMap.fill(new Index2D(40, 40), wallColor, false);

    // 5. Define start and end points for Shortest Path testing
    // Start at top-left corner, end at bottom-right corner
    Index2D start = new Index2D(5, 75);
    Index2D end = new Index2D(75, 5);

    // Ensure the start and end points are not blocked by obstacles
    testMap.setPixel(start, BLACK);
    testMap.setPixel(end, BLACK);

    // 6. Run the Shortest Path algorithm (BFS)
    // Find a path that avoids all blue obstacles
    Pixel2D[] path = testMap.shortestPath(start, end, wallColor, false);

    if (path != null) {
        // Color the resulting path in red for visual verification
        for (Pixel2D p : path) {
            testMap.setPixel(p, pathColor);
        }
        System.out.println("Shortest path found! Steps: " + path.length);
    } else {
        System.out.println("No path could be found between the points.");
    }

    // 7. Test All Distance map computation (Internal logic check without drawing)
    Map2D distances = testMap.allDistance(start, wallColor, false);

    // 8. Test File I/O system (Save & Load)
    String testFile = "comprehensive_test.txt";
    saveMap(testMap, testFile);

    // Load the file into a separate variable to verify data integrity
    Map2D loadedVersion = loadMap(testFile);

    // 9. GUI Display
    if (loadedVersion != null) {
        System.out.println("Map loaded successfully from file.");
        GUI.drawMap(loadedVersion);
    } else {
        System.err.println("Error: Could not load the map.");
    }
}


/// --- main עם ציור של הפרצוף של פיקאצ'ו ---
//    public static void main(String[] args) {
//        // 1. Create a smaller map (100x100) to easily fit in the window
//        Map pikachuMap = new Map(100, 100, 11);
//
//        // Define color constants based on the COLORS array
//        int YELLOW = 12;
//        int BLACK = 0;
//        int RED = 10;
//        int WHITE = 11;
//
//        // 2. Head base (a smaller yellow circle in the center)
//        pikachuMap.drawCircle(new Index2D(50, 45), 30, YELLOW);
//        pikachuMap.fill(new Index2D(50, 45), YELLOW, false);
//
//
//        // 4. Eyes (small circles with highlights)
//        // Left eye
//        pikachuMap.drawCircle(new Index2D(40, 52), 4, BLACK);
//        pikachuMap.fill(new Index2D(40, 52), BLACK, false);
//        pikachuMap.setPixel(41, 54, WHITE);
//
//        // Right eye
//        pikachuMap.drawCircle(new Index2D(60, 52), 4, BLACK);
//        pikachuMap.fill(new Index2D(60, 52), BLACK, false);
//        pikachuMap.setPixel(59, 54, WHITE);
//
//        // 5. Red cheeks
//        pikachuMap.drawCircle(new Index2D(28, 40), 6, RED);
//        pikachuMap.fill(new Index2D(28, 40), RED, false);
//        pikachuMap.drawCircle(new Index2D(72, 40), 6, RED);
//        pikachuMap.fill(new Index2D(72, 40), RED, false);
//
//        // 6. Nose and small smile
//        pikachuMap.setPixel(50, 48, BLACK); // Nose
//        pikachuMap.drawLine(new Index2D(46, 42), new Index2D(50, 40), BLACK); // Left side of the mouth
//        pikachuMap.drawLine(new Index2D(50, 40), new Index2D(54, 42), BLACK); // Right side of the mouth
//
//        // 7. Save and display
//        saveMap(pikachuMap, "mini_pikachu.txt");
//        Map2D loaded = loadMap("mini_pikachu.txt");
//        drawMap(loaded);
//    }
//

    }

