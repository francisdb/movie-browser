package eu.somatik.moviebrowser.gui.shelf;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;


import javax.imageio.*;
import java.io.*;
import org.slf4j.LoggerFactory;

public class CrystalCaseFactory {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CrystalCaseFactory.class);

    private static CrystalCaseFactory instance = null;
    
    private BufferedImage cdCase;
    private BufferedImage stitch;
    private BufferedImage reflections;

    private BufferedImage mask;
    
    public static CrystalCaseFactory getInstance() {
        if (instance == null) {
            instance = new CrystalCaseFactory();
        }
        return instance;
    }
    
    private CrystalCaseFactory() {
        try {
            loadPictures();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public BufferedImage createCrystalCase(Image cover) {
        BufferedImage crystal = new BufferedImage(cdCase.getWidth(),
                                                  cdCase.getHeight(),
                                                  BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = crystal.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        g2.drawImage(cover, 19, 3, 240, 227, null);
        g2.drawImage(reflections, 0, 0, null);
        g2.drawImage(stitch, 19, 3, null);
        g2.drawImage(cdCase, 0, 0, null);

        g2.dispose();
        
        return crystal;
    }

    private void loadPictures() throws IOException {
         cdCase = ImageIO.read(getClass().getClassLoader().getResource("cdshelf/cd_case.png"));
            stitch = ImageIO.read(getClass().getClassLoader().getResource("cdshelf/stitch.png"));
            reflections = ImageIO.read(getClass().getClassLoader().getResource("cdshelf/reflections.png"));
            mask = createGradientMask(cdCase.getWidth(), cdCase.getHeight());

    }
    
    public BufferedImage createReflectedPicture(BufferedImage avatar) {
        return createReflectedPicture(avatar, mask);
    }
    
    public BufferedImage createReflectedPicture(BufferedImage avatar,
                                                BufferedImage alphaMask) {
        int avatarWidth = avatar.getWidth();
        int avatarHeight = avatar.getHeight();

        BufferedImage buffer = createReflection(avatar,
                                                avatarWidth, avatarHeight);

        applyAlphaMask(buffer, alphaMask, avatarWidth, avatarHeight);

        return buffer;
    }

    private void applyAlphaMask(BufferedImage buffer,
                                BufferedImage alphaMask,
                                int avatarWidth, int avatarHeight) {

        Graphics2D g2 = buffer.createGraphics();
        g2.setComposite(AlphaComposite.DstOut);
        g2.drawImage(alphaMask, null, 0, avatarHeight);
        g2.dispose();
    }

    private BufferedImage createReflection(BufferedImage avatar,
                                           int avatarWidth,
                                           int avatarHeight) {

        BufferedImage buffer = new BufferedImage(avatarWidth, avatarHeight << 1,
                                                 BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buffer.createGraphics();

        g.drawImage(avatar, null, null);
        g.translate(0, avatarHeight << 1);

        AffineTransform reflectTransform = AffineTransform.getScaleInstance(1.0, -1.0);
        g.drawImage(avatar, reflectTransform, null);
        g.translate(0, -(avatarHeight << 1));
        
        g.dispose();

        return buffer;
    }

    public BufferedImage createGradientMask(int avatarWidth, int avatarHeight) {
        BufferedImage gradient = new BufferedImage(avatarWidth, avatarHeight,
                                                   BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gradient.createGraphics();
        GradientPaint painter = new GradientPaint(0.0f, 0.0f,
                                                  new Color(1.0f, 1.0f, 1.0f, 0.5f),
                                                  0.0f, avatarHeight / 2.0f,
                                                  new Color(1.0f, 1.0f, 1.0f, 1.0f));
        g.setPaint(painter);
        g.fill(new Rectangle2D.Double(0, 0, avatarWidth, avatarHeight));
        
        g.dispose();

        return gradient;
    }
}

