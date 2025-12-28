import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Intro2CS, 2026A, this is a very
 */
class MapTest {
    /**
     */
    private int[][] _map_3_3 = {{0,1,0}, {1,0,1}, {0,1,0}};
    private Map2D _m0, _m1, _m3_3;
    @BeforeEach
    public void setup() {
        _m3_3 = new Map(_map_3_3);
        _m0 = new Map(1,1,0);
        _m1 = new Map(1,1,0);
    }
    @Test
    @Timeout(value = 1, unit = SECONDS)
    void init() {
        int[][] bigarr = new int [500][500];
        _m1.init(bigarr);
        assertEquals(bigarr.length, _m1.getWidth());
        assertEquals(bigarr[0].length, _m1.getHeight());
        Pixel2D p1 = new Index2D(3,2);
        _m1.fill(p1,1, true);
    }

    @Test
    void testInit() {
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);

        assertEquals(_m0, _m1);
    }
    @Test
    void testEquals() {
        assertEquals(_m0,_m1);
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);
        assertEquals(_m0,_m1);
    }

    @Test
    void equals_SameSize_SameData() {
        // Changed from 3x3 to 4x4 with a different color pattern
        int[][] grid = {
                {12, 11, 12, 11},
                {11, 12, 11, 12},
                {12, 11, 12, 11},
                {11, 12, 11, 12}
        };
        Map a = new Map(grid);
        Map b = new Map(grid);
        assertTrue(a.equals(b), "Maps with identical content must be equal");
        assertEquals(a, b, "equals() should agree with assertEquals");
    }

    @Test
    void equals_SameSize_DifferentData() {
        // Using 3x3 maps with a single pixel difference
        int[][] g1 = {
                {5, 5, 5},
                {5, 5, 5},
                {5, 5, 5}
        };
        int[][] g2 = {
                {5, 5, 5},
                {5, 10, 5}, // Center pixel is different (10 instead of 5)
                {5, 5, 5}
        };
        Map a = new Map(g1);
        Map b = new Map(g2);
        assertFalse(a.equals(b), "Maps with differing cells must not be equal");
        assertNotEquals(a, b);
    }

    @Test
    void equals_DifferentSize() {
        // Changed dimensions from 3x3/4x3 to 10x5 and 10x6
        Map a = new Map(10, 5, 7);
        Map b = new Map(10, 6, 7);
        assertFalse(a.equals(b), "Maps of different sizes must not be equal");
    }

    @Test
    void equals_noNulls() {
        // Changed map size to 5x5 and default color to 12
        Map a = new Map(5, 5, 12);
        assertFalse(a.equals(null), "equals(null) must be false");
        assertFalse(a.equals("not a map"), "equals(non-Map) must be false");
    }

    @Test
    void shortestPath_basic() {
        // Increased map size to 10x10
        Map m = new Map(10, 10, 11); // White background
        Pixel2D s = new Index2D(1, 1);
        Pixel2D t = new Index2D(8, 8);

        // obstacle color is 5 (Green)
        Pixel2D[] path = m.shortestPath(s, t, 5, false);

        assertNotNull(path);
        assertEquals(s.getX(), path[0].getX());
        assertEquals(s.getY(), path[0].getY());
        assertEquals(t.getX(), path[path.length-1].getX());
        assertEquals(t.getY(), path[path.length-1].getY());
    }

    @Test
    void shortestPath_ChecksObstacles() {
        // Creating a horizontal wall that blocks the path
        Map m = new Map(5, 5, 11);
        int obstacleColor = 0; // Black

        // Draw a solid wall at row 2
        for(int x = 0; x < 5; x++) {
            m.setPixel(x, 2, obstacleColor);
        }

        // Start below the wall, Target above the wall
        Pixel2D s = new Index2D(2, 0);
        Pixel2D t = new Index2D(2, 4);

        Pixel2D[] path = m.shortestPath(s, t, obstacleColor, false);
        assertNull(path, "Path should be blocked by the wall");
    }

    @Test
    void allDistance_NullTest() {
        // Testing distances on a 4x4 map from a corner
        Map m = new Map(4, 4, 11);
        int obsColor = 2; // Cyan
        Pixel2D start = new Index2D(0, 0);

        Map2D distMap = m.allDistance(start, obsColor, false);
        assertNotNull(distMap);

        // Verify distance calculations (Manhattan distance for neighbors)
        assertEquals(0, distMap.getPixel(0, 0), "Start must be distance 0");
        assertEquals(1, distMap.getPixel(0, 1));
        assertEquals(1, distMap.getPixel(1, 0));
        assertEquals(2, distMap.getPixel(1, 1));
        assertEquals(3, distMap.getPixel(2, 1));
        assertEquals(6, distMap.getPixel(3, 3), "Opposite corner should be distance 6");
    }

}