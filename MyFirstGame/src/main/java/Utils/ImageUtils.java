package Utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class ImageUtils {
    public static BufferedImage[] getAllSubImages(BufferedImage image, int rows, int columns, int nImages) {
        // initializing array to hold subimages
        BufferedImage imgs[] = new BufferedImage[nImages];

        // Equally dividing original image into subimages
        int subimage_Width = image.getWidth() / columns;
        int subimage_Height = image.getHeight() / rows;

        int current = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Creating sub image
                imgs[current] = new BufferedImage(subimage_Width, subimage_Height, image.getType());
                Graphics2D img_creator = imgs[current].createGraphics();

                // coordinates of source image
                int src_first_x = subimage_Width * j;
                int src_first_y = subimage_Height * i;

                // coordinates of sub-image
                int dst_corner_x = subimage_Width * j + subimage_Width;
                int dst_corner_y = subimage_Height * i + subimage_Height;
                
                img_creator.drawImage(image, 0, 0, subimage_Width, subimage_Height, src_first_x, src_first_y, dst_corner_x, dst_corner_y, null);
                current++;
                if (current >= nImages) {
                    break;
                }
            }
            if (current >= nImages) {
                break;
            }
        }


        return imgs;
    }

    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(image, 0, 0, newWidth, newHeight, null);
        return scaledImage;
    }
}
