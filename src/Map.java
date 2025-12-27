import java.awt.*;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable{

    ////////////////////// Constructors ///////////////////////
	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
    private int w;
    private int h;
    private int v[][]; // 2D array to hold the map values meaning w x h
	public Map(int w, int h, int v)
    {
        init(w, h, v);
    }
    /**
     * Constructs a square map (size*size).
     * @param size
     */
	public Map(int size) // square map
    {
        if(size<=0)
        {
            throw new IllegalArgumentException("size must be positive");
        }
        this(size,size, 0); // default init value is 0 when the size is the same for width and height
    }
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data)
    {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) { //set width, height, value and fill in values with v
        this.w = w;
        this.h = h;
        this.v = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                this.v[i][j] = v;
            }
        }

	}
	@Override
	public void init(int[][] arr) {
        if(arr==null || arr.length==0)
        {
            throw new IllegalArgumentException("arr can't be null or empty");
        }
        int rowLength = arr[0].length;
        for (int i = 1; i < arr.length; i++) {
            if(arr[i].length != rowLength)
            {
                throw new IllegalArgumentException("arr must be a non-ragged 2D array");
            }
        }
        this.w = arr.length; // get width
        this.h = arr[0].length; // get height from first row
        this.v = new int[h][w]; // make new 2D array
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                this.v[i][j] = arr[i][j]; //fill in values with deep copy
            }
        }

	}

    @Override
	public int[][] getMap() { // return a deep copy of the 2D array
		int[][] ans = new int[h][w];
        for (int i = 0; i < this.h; i++) {
            for (int j = 0; j < this.w; j++) {
                ans[i][j]=this.v[i][j] ;
            }
        }
		return ans;
	}
	@Override
	public int getWidth()
    {
        return this.w;
    }
	@Override
	public int getHeight()
    {
        return this.h;
    }
	@Override
	public int getPixel(int x, int y) {
        checkBounds(x, y);
        return v[y][x];
    }
	@Override
	public int getPixel(Pixel2D p) {
        return getPixel(p.getX(), p.getY());
	}
	@Override
	public void setPixel(int x, int y, int v) {
        checkBounds(x, y);
        this.v[y][x] = v;

    }
	@Override
	public void setPixel(Pixel2D p, int v) {
        setPixel(p.getX(), p.getY(), v);

	}

    @Override
    public boolean isInside(Pixel2D p) {
        int x= p.getX();
        int y= p.getY();
        return inBounds(x, y);
    }

    @Override
    public boolean sameDimensions(Map2D p) {
        return (p!=null && this.getWidth() == p.getWidth() && this.getHeight() == p.getHeight());
    }

    @Override
    public void addMap2D(Map2D p) {
        if(!sameDimensions(p))
        {
            throw new IllegalArgumentException("Maps must have the same dimensions to add");
        }
        for (int i = 0; i < this.h; i++)
        {
            for (int j = 0; j < this.w; j++)
            {
                this.v[i][j] += p.getPixel(j, i); // add corresponding pixels
            }
        }

    }

    @Override
    public void mul(double scalar) {
        for (int i = 0; i < this.h; i++)
        {
            for (int j = 0; j < this.w; j++)
            {
                this.v[i][j] = (int) (this.v[i][j] * scalar); // multiply each pixel by scalar and cast to int
            }

        }
    }

    @Override
    public void rescale(double sx, double sy) {
        //Validation: Ensure scale factors are positive to avoid math/memory errors.
        if (sx <= 0 || sy <= 0) throw new IllegalArgumentException("scale factors must be > 0");

        /* *Calculate New Dimensions:
         * - Math.round: Converts the floating-point result to the nearest integer.
         * - Math.max(1, ...): Guarantees the map is at least 1 pixel wide/high (prevents size 0).
         * - (int): Casts the final result from long to int for array indexing.
         */
        int newW = Math.max(1, (int) Math.round(w * sx));
        int newH = Math.max(1, (int) Math.round(h * sy));

        // 3. Array Allocation: Create a new 2D array for the resized map data.
        int[][] out = new int[newH][newW];

        /* * Resampling Loop:
         * We iterate over every cell in the NEW map and determine which
         * original cell from the OLD map it should "borrow" its value from.
         */
        for (int ny = 0; ny < newH; ny++) {
            for (int nx = 0; nx < newW; nx++) {

                /* * Mapping Inverse Coordinates:
                 * - nx / sx: Finds the corresponding X position in the original map.
                 * - Math.round: Picks the closest original pixel (Nearest Neighbor).
                 * - Math.min(width - 1, ...): Bounds-check to ensure we don't exceed
                 * the original array's last index due to rounding.
                 */
                int ox = Math.min(w - 1, (int) Math.round(nx / sx));
                int oy = Math.min(h - 1, (int) Math.round(ny / sy));

                //Assignment: Copy the value from the old map to the new map.
                out[ny][nx] = v[oy][ox];
            }
        }

        //Update Object State: Replace the old map and dimensions with the new ones.
        this.w = newW;
        this.h = newH;
        this.v = out;
    }

    @Override
    public void drawCircle(Pixel2D center, double rad, int color) {
        // Extract center coordinates and round the radius to the nearest integer
        int cx = center.getX();
        int cy = center.getY();
        int r = (int) Math.round(rad);
        // Validate radius: A circle cannot have a negative radius
        if (r < 0) {
            throw new IllegalArgumentException("Radius must be >= 0");
        }
        // Special case: If radius is 0, just draw a single point at the center
        if (r == 0) {
            setPixel(cx, cy, color);
        }
        // Draw circle using Midpoint Circle Algorithm (Bresenham's)
        if (r > 0) {
            int y = 0; // Start at the top of the circle
            int x = r; // Initial X is the radius
            int err = 1 - x; // Initial decision parameter (error offset)

            // The loop continues until the 1/8th arc (octant) is complete
            while (x >= y)
            {
                /* * Apply 8-way symmetry:
                 * A circle is symmetric across 8 octants. By calculating one point (x, y),
                 * we can determine 7 other mirrored points instantly.
                 */
                setPixel(cx + x, cy + y, color);
                setPixel(cx + y, cy + x, color);
                setPixel(cx - y, cy + x, color);
                setPixel(cx - y, cy - x, color);
                setPixel(cx - x, cy + y, color);
                setPixel(cx - x, cy - y, color);
                setPixel(cx + y, cy - x, color);
                setPixel(cx + x, cy - y, color);

                y++; // Always move one step in the Y direction

                /* * Error Correction:
                 * Decide whether to stay at the current X or move inward to stay on the arc.
                 */
                if (err < 0)
                {
                    // If error is negative, the next pixel is inside the ideal circle boundary
                    err += ((2 * y) + 1);
                }
                else
                {
                    // If error is positive, we are too far out; move X one step inward
                    x--;
                    err += ((2 * (y - x)) + 1);
                }
            }
        }
    }

    // 1. Calculate absolute differences and step directions
    // 2. Initialize error term (err = dx + dy) to manage the decision between X and Y steps
    // 3. Loop until the current coordinates (x0, y0) match the target (x1, y1)
    // 4. In each step:
    //    - If 2*err >= dy, move in the X direction
    //    - If 2*err <= dx, move in the Y direction
    //    - This ensures the line stays as close as possible to the ideal mathematical path.
    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        int x1 = p1.getX(), y1 = p1.getY();
        int x2 = p2.getX(), y2 = p2.getY();
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1; // if x1<x2 then sx=1 else sx=-1
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1; // if y1<y2 then sy=1 else sy=-1
        int err = dx + dy;
        while (true)
        {
            setPixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) // Reached the end point
                break;
            int e2 = 2 * err;
            if (e2 >= dy)
            {
                err += dy;
                x1 += sx;
            }
            if (e2 <= dx)
            {
                err += dx;
                y1 += sy;
            }
        }
    }

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
        if (p1 == p2) { // If both points are the same, draw a single pixel
            setPixel(p1.getX(), p1.getY(), color);
        } else {
            int x1 = Math.min(p1.getX(), p2.getX()); // Left x boundary of both points
            int y1 = Math.min(p1.getY(), p2.getY()); // Top y boundary of both points
            int x2 = Math.max(p1.getX(), p2.getX()); // Right x boundary of both points
            int y2 = Math.max(p1.getY(), p2.getY()); // Bottom y boundary of both points
            for (int x = x1; x <= x2; x++) { // Draw top and bottom edges
                setPixel(x, y1, color);
                setPixel(x, y2, color);
            }
            for (int y = y1; y <= y2; y++) { // Draw left and right edges
                setPixel(x1, y, color);
                setPixel(x2, y, color);
            }
        }
    }

    @Override
    public boolean equals(Object ob)
    {
        boolean ans = true; // assume equal unless proven otherwise
        if(ob instanceof Map) // check if ob is instance of Map
        {
            Map map = (Map)ob;
            if(map.w == this.w && map.h == this.h) // check dimensions
            {
                for (int y = 0; y < this.h; y++)
                {
                    for (int x = 0; x < this.w; x++)
                    {
                        if (this.v[y][x] != map.v[y][x]) // compare each pixel value
                        {
                            return false;
                        }
                    }
                }
            }
            else
            {
                return  false;
            }
        }
        else
        {
            return false;
        }
        return ans;
    }

    @Override
    /**
     * Fills this map with the new color (new_v) starting from p.
     * Uses BFS (Breadth-First Search) algorithm with a Queue to avoid StackOverflow.
     */
        public int fill(Pixel2D xy, int new_v, boolean cyclic) {
            int ans = 0;
            // Initialize dimensions and starting coordinates
            final int H = v.length;
            final int W = v[0].length;
            final int fx = xy.getX(); // starting x coordinate
            final int fy = xy.getY(); // starting y coordinate

            // Initial boundary check: Ensure the starting point is within the map
            if (fy < 0 || fy >= v[0].length || fx < 0 || fx >= v.length)
            {
                return ans;
            }

            // Identify the target color to be replaced
            final int old = v[fy][fx];

            // Optimization: If the target color is already the new color, no work is needed
            if (old == new_v)
            {
                return ans;
            }

            // Setup BFS structures: 'visited' array prevents infinite loops
            // 'q' (Queue) stores pixels that are waiting to have their neighbors checked
            final boolean[][] visited = new boolean[H][W];
            final ArrayDeque<int[]> q = new ArrayDeque<>();

            // Start the process from the initial pixel
            visited[fy][fx] = true;
            q.add(new int[]{fx, fy});

            // Define 4-way connectivity (Right, Left, Down, Up)
            final int[][] directions = {{ 1,  0}, {-1,  0}, { 0,  1}, { 0, -1}};

            // Main BFS Loop: Continue as long as there are connected pixels to process
            while (!q.isEmpty()) {
                int[] cur = q.removeFirst(); // Get the next pixel from the queue
                int x = cur[0];
                int y = cur[1];

                // Update current pixel color and increment the counter
                if (v[y][x] == old) {
                    v[y][x] = new_v;
                    ans++;
                }

                // Check all 4 neighbors
                for (int[] d : directions) // For each direction
                {
                    int nx = x + d[0]; // Neighbor's x coordinate
                    int ny = y + d[1]; // Neighbor's y coordinate

                    // Handle Map Topology: Cyclic vs. Standard Bounded
                    if (cyclic)
                    {
                        // Modulo math ensures that going off-edge wraps around to the opposite side
                        // Added '+ W' handles negative results from nx % W
                        nx = ((nx % W) + W) % W;
                        ny = ((ny % H) + H) % H;
                    } else
                    {
                        // Standard bounds check: Skip the neighbor if it's outside the map
                        if (nx < 0 || nx >= W || ny < 0 || ny >= H)
                            continue;
                    }

                    // If neighbor has the original color and hasn't been visited yet, add to queue
                    if (!visited[ny][nx] && v[ny][nx] == old)
                    {
                        visited[ny][nx] = true; // Mark as visited immediately to avoid duplicate entries
                        q.add(new int[]{nx, ny});
                    }
                }
            }
            // Return total number of pixels that were changed
            return ans;
        }


    @Override
    /**
     * BFS like shortest path computation based on iterative raster implementation.
     * Finds the minimum number of steps between p1 and p2, avoiding obstacles.
     * * https://en.wikipedia.org/wiki/Breadth-first_search
     */
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
        Pixel2D[] ans = null; // Result array

        // Basic validation: Ensure start and end points exist
        if (p1 == null || p2 == null)
        {
            return null;
        }

        final int H = v.length;
        final int W = v[0].length;
        final int sx = p1.getX(); // start x coordinate
        final int sy = p1.getY(); // start y coordinate
        final int ex = p2.getX(); // end x coordinate
        final int ey = p2.getY(); // end y coordinate

        // Boundary checks: Ensure coordinates are within the map dimensions
        if (sx < 0 || sx >= W || sy < 0 || sy >= H)
        {
            return null;
        }
        if (ex < 0 || ex >= W || ey < 0 || ey >= H)
        {
            return null;
        }

        // Obstacle check: Path is impossible if start or end is an obstacle
        if (v[sy][sx] == obsColor || v[ey][ex] == obsColor)
        {
            return null;
        }

        // Edge case: Start and end are the same point
        if (sx == ex && sy == ey)
        {
            return new Pixel2D[]{p1}; // Return array with single point
        }

        // Setup BFS Data Structures:
        // 'visited' prevents re-processing cells (infinite loops)
        // 'parentX/Y' store the coordinates of the previous cell to reconstruct the path later
        final boolean[][] visited = new boolean[H][W];
        final int[][] parentX = new int[H][W];
        final int[][] parentY = new int[H][W];

        // Initialize parents with -1 (meaning "no parent yet")
        for (int y = 0; y < H; y++) {
            java.util.Arrays.fill(parentX[y], -1); // No parent initialized
            java.util.Arrays.fill(parentY[y], -1); // No parent initialized
        }

        // Define 8-way movement (Horizontal, Vertical, and Diagonal)
        final int[][] directions = {{ 1,  0}, {-1,  0}, { 0,  1}, { 0, -1}, { 1,  1}, { 1, -1}, {-1,  1}, {-1, -1}};

        // Queue for BFS: First-In-First-Out (FIFO) ensures shortest path in an unweighted grid
        final java.util.ArrayDeque<int[]> q = new java.util.ArrayDeque<>();
        visited[sy][sx] = true;
        q.addLast(new int[]{sx, sy});

        boolean found = false; // Flag to indicate if the end point was reached

        // Main BFS Loop: Expand outward from start point
        while (!q.isEmpty()) { // While there are cells to process
            int[] cur = q.removeFirst(); // Get the next cell from the queue
            int x = cur[0], y = cur[1];

            // Goal reached: Stop searching and start path reconstruction
            if (x == ex && y == ey) {
                found = true;
                break;
            }

            // Explore all 8 neighboring directions
            for (int[] d : directions) // For each direction: horizontal, vertical, diagonal
            {
                int nx = x + d[0];
                int ny = y + d[1];

                // Coordinate Wrapping (Cyclic): If off-edge, wrap to the opposite side
                if (cyclic) {
                    nx = (nx % W + W) % W; // Handle negative results from modulo and wrap around
                    ny = (ny % H + H) % H; // Handle negative results from modulo and wrap around
                } else {
                    // Standard bounds check for non-cyclic maps
                    if (nx < 0 || nx >= W || ny < 0 || ny >= H)
                    {
                        continue;
                    }
                }

                // Valid movement check: Not visited and not an obstacle
                if (!visited[ny][nx] && v[ny][nx] != obsColor)
                {
                    visited[ny][nx] = true;
                    // Save 'current' as the 'parent' of 'neighbor' to remember the path
                    parentX[ny][nx] = x;
                    parentY[ny][nx] = y;
                    q.addLast(new int[]{nx, ny}); // Add neighbor to the queue for further exploration
                }
            }
        }

        // Path Reconstruction: If target was found, trace back using the parent arrays
        if (!found) return null;

        java.util.ArrayList<Pixel2D> path = new java.util.ArrayList<>(); // To store the path from start to end
        int cx = ex, cy = ey; // Start tracing from the end point

        while (true) // Loop until we reach the start point
        {
            path.add(new Index2D(cx, cy)); // Add current point to path
            if (cx == sx && cy == sy) // Reached the starting point
            {
                break;
            }

            // Move to the parent of the current cell
            int px = parentX[cy][cx];
            int py = parentY[cy][cx];

            if (px == -1 && py == -1) // No parent found (should not happen if 'found' is true)
            {
                return null; // Safety check
            }
            cx = px; // Update current x to parent x
            cy = py; // Update current y to parent y
        }

        // Finalize result: Reverse the list (End->Start to Start->End) and convert to array
        java.util.Collections.reverse(path);
        ans = path.toArray(new Pixel2D[0]); // Convert list to array

        return ans;
    }
    @Override
/**
 * Calculates the shortest distance from a start pixel to all other reachable pixels.
 * Returns a Map2D where each pixel value represents its distance from the start.
 */
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;  // Final result object
        final int H = v.length;
        final int W = v[0].length;
        final int sx = start.getX(); // Starting X coordinate
        final int sy = start.getY(); // Starting Y coordinate

        // Initialize distance matrix with -1 (representing unreachable areas)
        int[][] distance = new int[H][W];
        for (int y = 0; y < H; y++)
        {
            java.util.Arrays.fill(distance[y], -1); // Mark all as unreachable initially
        }

        // Initial boundary check: If start is out of bounds, return the empty distance map
        if (sx < 0 || sx >= W || sy < 0 || sy >= H)
        {
            return new Map(distance);
        }

        // Pre-marking obstacles: Copy obstacle positions to the distance map for visibility
        // Also handles the case where the starting point itself is an obstacle
        if (v[sy][sx] == obsColor) { // If start is an obstacle, return map with only obstacles marked
            for (int y = 0; y < H; y++)
            {
                for (int x = 0; x < W; x++)
                {
                    if (v[y][x] == obsColor)
                    {
                        distance[y][x] = obsColor; // Mark obstacles
                    }
                }
            }
            return new Map(distance); // Return the distance map with obstacles marked
        }

        // Fill the distance map with obstacle markers before starting the search
        for (int y = 0; y < H; y++)
        {
            for (int x = 0; x < W; x++)
            {
                if (v[y][x] == obsColor) // If the pixel is an obstacle
                {
                    distance[y][x] = obsColor; // Mark it in the distance map
                }
            }
        }

        // BFS Setup: Use a queue to explore pixels layer by layer (by distance)
        final boolean[][] visited = new boolean[H][W];
        final ArrayDeque<int[]> q = new ArrayDeque<>();
        final int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // 4-way movement

        // Initialize the starting point
        visited[sy][sx] = true;
        distance[sy][sx] = 0; // Distance to self is 0
        q.add(new int[]{sx, sy});

        // Main BFS Loop
        while (!q.isEmpty())
        {
            int[] cur = q.removeFirst(); // Get the next pixel to process
            int x = cur[0], y = cur[1];

            // Check all 4 neighbors (Up, Down, Left, Right)
            for (int[] d : directions)
            {
                int nx = x + d[0], ny = y + d[1];

                // Handle Cyclic Topology: Wrap around edges if cyclic is true
                if (cyclic)
                {
                    // Modulo math to ensure coordinates stay within [0, W-1] and [0, H-1]
                    nx = ((nx % W) + W) % W;
                    ny = ((ny % H) + H) % H;
                }
                else
                {
                    // Standard bounds check: Skip if neighbor is outside the map
                    if (nx < 0 || nx >= W || ny < 0 || ny >= H)
                    {
                        continue;
                    }
                }

                // Skip if the pixel was already visited or is an obstacle
                if (visited[ny][nx])
                {
                    continue;
                }
                if (v[ny][nx] == obsColor)
                {
                    continue;
                }

                // Mark as visited and calculate distance
                visited[ny][nx] = true;
                // The distance to the neighbor is the current pixel's distance + 1 step
                distance[ny][nx] = distance[y][x] + 1;

                // Add neighbor to the queue to process its neighbors in the next layer
                q.add(new int[]{nx, ny});
            }
        }

        // Wrap the resulting distance matrix in a Map object and return
        ans = new Map(distance);
        return ans; // Return the distance map
    }
	////////////////////// Private Methods ///////////////////////
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < w && y >= 0 && y < h;
    }
    private void checkBounds(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("Out of bounds: (" + x + "," + y + ") for " + w + "x" + h);
        }
    }

}
