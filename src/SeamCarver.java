import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture pic;
    static final double BORDER = 1000.0;

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
            return BORDER;
        }
        if (x >= pic.width() || y >= pic.height()) {
            throw new IllegalArgumentException("x or y are out of range");
        }

        double dX = deltaX(x,y);
        double dY = deltaY(x, y);

        return Math.sqrt(dX + dY);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] path = findVerticalSeam();
        transpose();
        return path;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energyMatrix = picToMatrix();
        int[][] edgeTo = new int[pic.height()][pic.width()];
        double[][] distTo = new double[pic.height()][pic.width()];

        int height = pic.height();
        int width = pic.width();

        // initialize matrices
        for (int x = 0; x < width; x++) {
            for (int y=0; y < height; y++) {
                if (y == 0) {
                    edgeTo[y][x] = x;
                    distTo[y][x] = BORDER;
                }
                else {
                    distTo[y][x] = Double.POSITIVE_INFINITY;
                    edgeTo[y][x] = -1;
                }
            }
        }

        // topological order
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height-1; y++) {
                for (int i = -1; i <= 1; i++) {
                    // border, there are only two options to check
                    if (x + i < 0 || x + i == width) {
                        continue;
                    }
                    if (distTo[y][x] + energyMatrix[y+1][x + i] < distTo[y+1][x]) {
                        distTo[y+1][x] = distTo[y][x] + energyMatrix[y+1][x+i];
                        edgeTo[y+1][x] = x + i;
                    }
                }
            }
        }

        // find shortest path
        double min = distTo[height-1][0];
        int minIndex = 0;
        for (int x = 1; x < width; x++) {
            if (distTo[height-1][x] < min) {
                min = distTo[height-1][x];
                minIndex = x;
            }
        }

        // create the path[]
        int[] path = new int[height];
        for (int y = height-1; y >= 0; y--) {
            path[y] = edgeTo[y][minIndex];
        }

        return path;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // removing a horizontal seam will result in a picture with same width, height-1 (one less row)
        Picture newPic = new Picture(pic.width(), pic.height()-1);

        for (int col = 0; col < newPic.width(); col++) {
            int skip = 0;
            for (int row = 0; row < newPic.height(); row++) {
                if (seam[col] == row) {
                    skip = 1;
                }
                newPic.setRGB(col, row, pic.getRGB(col, row+skip));
            }
        }
        pic = newPic;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        // transpose and call removeHorizontalSeam
        transpose();
        removeHorizontalSeam(seam);
        transpose();
    }

    private void transpose() {
        int transposedHeight = pic.width();
        int transposedWidth = pic.height();
        Picture transposed = new Picture(transposedWidth, transposedHeight);

        for (int row = 0; row < transposedHeight; row++) {
            for (int col = 0; col < transposedWidth; col++) {
                transposed.setRGB(col, row, pic.getRGB(row, col));
            }
        }

        pic = transposed;
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

    private double[][] picToMatrix() {
        double[][] energyMatrix = new double[pic.height()][pic.width()];

        for (int row = 0; row < pic.height(); row++) {
            for (int col = 0; col < pic.width(); col++) {
                energyMatrix[row][col] = energy(col, row);
            }
        }
        return energyMatrix;
    }

}
