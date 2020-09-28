import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private final Picture pic;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Null argument");
        }

        pic = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x == 0 || y == 0 || x == pic.width()-1 || y == pic.height()-1) {
            return 1000;
        }
        if (x >= pic.width() || y >= pic.height()) {
            throw new IllegalArgumentException("x or y are out of range");
        }

        double dX = deltaX(x,y);
        double dY = deltaY(x, y);

        return Math.sqrt(dX + dY);
    }

    private double deltaX(int x, int y) {
        // get encoded rgb and decode to r, g, b separately.
        int rgbRight = pic.getRGB(x+1, y);
        int rgbLeft = pic.getRGB(x-1, y);
        int r = ((rgbRight >> 16) & 0xFF) - ((rgbLeft >> 16) & 0xFF);
        int g = ((rgbRight >>  8) & 0xFF) -  ((rgbLeft >> 8) & 0xFF);
        int b = (rgbRight & 0xFF) - (rgbLeft & 0xFF);

        // return r^2 + g^2 + b^2
        return (Math.pow(r,2) + Math.pow(g,2) + Math.pow(b,2));
    }

    private double deltaY(int x, int y) {
        // get encoded rgb and decode to r, g, b separately.
        int rgbRight = pic.getRGB(x, y+1);
        int rgbLeft = pic.getRGB(x, y-1);
        int r = ((rgbRight >> 16) & 0xFF) - ((rgbLeft >> 16) & 0xFF);
        int g = ((rgbRight >>  8) & 0xFF) -  ((rgbLeft >> 8) & 0xFF);
        int b = (rgbRight & 0xFF) - (rgbLeft & 0xFF);

        // return r^2 + g^2 + b^2
        return (Math.pow(r,2) + Math.pow(g,2) + Math.pow(b,2));
    }

}
