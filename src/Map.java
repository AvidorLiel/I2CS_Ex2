import java.io.Serializable;
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
            while (x >= y) {
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
                if (err < 0) {
                    // If error is negative, the next pixel is inside the ideal circle boundary
                    err += ((2 * y) + 1);
                } else {
                    // If error is positive, we are too far out; move X one step inward
                    x--;
                    err += ((2 * (y - x)) + 1);
                }
            }
        }
    }
    // Calculate absolute differences and step directions
    // Initialize error term (err = dx + dy) to manage the decision between X and Y steps
    // Loop until the current coordinates (x0, y0) match the target (x1, y1)
    // In each step:
    //    - If 2*err >= dy, move in the X direction
    //    - If 2*err <= dx, move in the Y direction
    //    - This ensures the line stays as close as possible to the ideal mathematical path
    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
        int x1 = p1.getX(), y1 = p1.getY();
        int x2 = p2.getX(), y2 = p2.getY();
        int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;
        while (true) {
            setPixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) break; // Reached the end point
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x1 += sx;
            }
            if (e2 <= dx) {
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
    public boolean equals(Object ob) {
        boolean ans = true; // assume equal unless proven otherwise
        if(ob instanceof Map) // check if ob is from type Map
        {
            Map m = (Map)ob; // type casting
            if(m.w == this.w && m.h == this.h) // check dimensions of both maps
            {
                //check each value in both maps
                for (int y = 0; y < this.h; y++)
                {
                    for (int x = 0; x < this.w; x++)
                    {
                        if (this.v[y][x] != m.v[y][x]) {
                            return false;
                        }
                    }
                }
            }
            else // if dimensions are not equal
            {
                return  false;
            }
        }
        else // if ob is not from type Map
        {
            return false;
        }
        return ans;
    }
	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v,  boolean cyclic) {
		int ans = -1;

		return ans;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
		Pixel2D[] ans = null;  // the result.

		return ans;
	}
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;  // the result.

        return ans;
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
