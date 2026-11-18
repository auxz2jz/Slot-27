package printcompare;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;
class EngineTest {
    @Test void rejectsInvalidDpi(){assertThrows(IllegalArgumentException.class,()->Engine.validateDpi(0));assertDoesNotThrow(()->Engine.validateDpi(500));}
    @Test void rejectsBlankImage(){var image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);assertThrows(IllegalArgumentException.class,()->Engine.template(image,500,false));}
    @Test void inversionPreservesDimensions(){var image=new BufferedImage(64,64,BufferedImage.TYPE_INT_RGB);image.setRGB(0,0,0xff000000);var flipped=Engine.prepare(image,true);assertEquals(64,flipped.getWidth());assertEquals(255,flipped.getRGB(0,0)&255);}
    @Test void labelsUnknownDpiInconclusive(){assertTrue(new Engine.Result(100,1).interpretation(true).startsWith("Inconclusive"));}
    @Test void neverInterpretsLowScoreAsExclusion(){assertTrue(new Engine.Result(0,1).interpretation(false).contains("cannot be excluded"));}
}
