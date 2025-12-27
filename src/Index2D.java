

public class Index2D implements Pixel2D
{
    private int X;
    private int Y;

    public Index2D(int w, int h)
    {
            this.X = w;
            this.Y = h;
    }
    public Index2D(Pixel2D other)
    {
        this.X = other.getX();
        this.Y = other.getY()
        ;
    }
    @Override
    public int getX()
    {
        return this.X;
    }

    @Override
    public int getY() {

        return this.Y;
    }

    @Override
    public double distance2D(Pixel2D p2) ///
    {
        if (p2 == null)
        {
            throw new IllegalArgumentException("p2 can't be null");
        }
        double dx= this.X- p2.getX();
        double dy = this.Y- p2.getY();

        return (Math.sqrt(dx*dx + dy*dy));
    }

    @Override
    public String toString() {

        return "(" + this.X + "," + this.Y + ")";
    }

    @Override
    public boolean equals(Object p) {

        if (p == null)
        {

            return false;
        }
        if(!(p instanceof Pixel2D))
        {
            return false;
        }
        Pixel2D p1 = (Pixel2D)p;
        boolean ans = false;
        if(this.X==p1.getX() && this.Y==p1.getY())
        {
            ans=true;
        }

        return ans;
    }

    /**
     * Intro2CS_2026A
     * This class represents a Graphical User Interface (GUI) for Map2D.
     * The class has save and load functions, and a GUI draw function.
     * You should implement this class, it is recommender to use the StdDraw class, as in:
     * https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
     *
     *
     */
    public static class Ex2_GUI {
        public static void drawMap(Map2D map) {
            //
        }

        /**
         * @param mapFileName
         * @return
         */
        public static Map2D loadMap(String mapFileName) {
            Map2D ans = null;

            return ans;
        }

        /**
         *
         * @param map
         * @param mapFileName
         */
        public static void saveMap(Map2D map, String mapFileName) {


        }
        public static void main(String[] a) {
            String mapFile = "map.txt";
            Map2D map = loadMap(mapFile);
            drawMap(map);
        }
        /// ///////////// Private functions ///////////////
    }
}
