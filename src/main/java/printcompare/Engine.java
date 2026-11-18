package printcompare;

import com.machinezoo.sourceafis.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Iterator;

final class Engine {
    static final int MAX_BYTES=20*1024*1024, MAX_EDGE=2500, MAX_PIXELS=4_000_000;
    static final double THRESHOLD=40;
    record Result(double score, long elapsedMs) {
        String interpretation(boolean guessedDpi) {
            if (guessedDpi) return "Inconclusive — capture DPI is unverified";
            return score >= THRESHOLD ? "Similarity above reference threshold — review required"
                : "Below reference threshold — same finger cannot be excluded";
        }
    }
    static void validateDpi(int dpi) {
        if(dpi<100 || dpi>2000) throw new IllegalArgumentException("DPI must be between 100 and 2000.");
    }
    static BufferedImage load(Path path) throws IOException {
        if(Files.size(path)>MAX_BYTES) throw new IOException("Image exceeds 20 MB.");
        try(InputStream in=Files.newInputStream(path);
            ImageInputStream stream=ImageIO.createImageInputStream(in)) {
            if(stream==null) throw new IOException("Cannot open image.");
            Iterator<ImageReader> readers=ImageIO.getImageReaders(stream);
            if(!readers.hasNext()) throw new IOException("Unsupported image. Use PNG, JPEG or BMP.");
            ImageReader reader=readers.next();
            try {
                reader.setInput(stream,true,true);
                int w=reader.getWidth(0), h=reader.getHeight(0);
                if(w<32 || h<32 || w>MAX_EDGE || h>MAX_EDGE || (long)w*h>MAX_PIXELS)
                    throw new IOException("Image size must be 32–2500 pixels per side and at most 4 megapixels.");
                BufferedImage image=reader.read(0);
                if(image==null) throw new IOException("Could not decode image.");
                return image;
            } finally {reader.dispose();}
        }
    }
    static BufferedImage prepare(BufferedImage image, boolean invert) {
        int w=image.getWidth(), h=image.getHeight();
        BufferedImage gray=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=gray.createGraphics();
        try {g.setColor(Color.WHITE);g.fillRect(0,0,w,h);g.drawImage(image,0,0,null);} finally {g.dispose();}
        for(int y=0;y<h;y++) for(int x=0;x<w;x++) {
            int rgb=gray.getRGB(x,y);
            int v=(int)Math.round(.299*((rgb>>16)&255)+.587*((rgb>>8)&255)+.114*(rgb&255));
            if(invert) v=255-v;
            gray.setRGB(x,y,0xff000000|(v<<16)|(v<<8)|v);
        }
        return gray;
    }
    static FingerprintTemplate template(BufferedImage source,int dpi,boolean invert) throws IOException {
        validateDpi(dpi);
        double scale=500.0/dpi;
        if(source.getWidth()*scale>MAX_EDGE || source.getHeight()*scale>MAX_EDGE ||
           source.getWidth()*scale*source.getHeight()*scale>MAX_PIXELS)
            throw new IllegalArgumentException("Image is too large at normalized DPI. Crop to fingerprint first.");
        BufferedImage image=prepare(source,invert);
        int min=255,max=0;
        for(int y=0;y<image.getHeight();y++) for(int x=0;x<image.getWidth();x++){
            int v=image.getRGB(x,y)&255; min=Math.min(min,v);max=Math.max(max,v);
        }
        if(max-min<15) throw new IllegalArgumentException("Image has insufficient visible contrast.");
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        if(!ImageIO.write(image,"png",out)) throw new IOException("PNG encoding unavailable.");
        return new FingerprintTemplate(new FingerprintImage(out.toByteArray(),new FingerprintImageOptions().dpi(dpi)));
    }
    static Result compare(BufferedImage a,int dpiA,boolean invertA,BufferedImage b,int dpiB,boolean invertB) throws IOException {
        long start=System.nanoTime();
        FingerprintTemplate first=template(a,dpiA,invertA), second=template(b,dpiB,invertB);
        double score=new FingerprintMatcher(first).match(second);
        if(!Double.isFinite(score)||score<0) throw new IllegalStateException("Invalid engine score.");
        return new Result(score,(System.nanoTime()-start)/1_000_000);
    }
}
