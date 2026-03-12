import java.util.Random;

public class Cookie {
    String image = "🍪";
    int x, y;
    Random random = new Random();

    Cookie(int sizeBoard) {
        this.y = random.nextInt(sizeBoard);
        this.x = random.nextInt(sizeBoard);
    }

    public String getImage() {
        return image;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
