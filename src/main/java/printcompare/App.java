package printcompare;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class App extends JFrame {
    private final PrintPanel first=new PrintPanel("Fingerprint A");
    private final PrintPanel second=new PrintPanel("Fingerprint B");
    private final JButton compare=new JButton("Compare fingerprints"), clear=new JButton("Clear both");
    private final JTextArea result=new JTextArea("Select two fingerprint images to begin.");
    private boolean busy;

    public App(){
        super("PrintCompare 0.1.0 — Slot-27");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700,580));
        setSize(1000,740);setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout(12,12));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JTextArea intro=new JTextArea("Compare two full or partial impressions of a finger. Load clear images with dark ridges on a light background. Confirm the actual capture DPI where possible. Processing is local; results require human review.");
        intro.setEditable(false);intro.setLineWrap(true);intro.setWrapStyleWord(true);intro.setOpaque(false);intro.setRows(3);
        root.add(intro,BorderLayout.NORTH);
        JPanel prints=new JPanel(new GridLayout(1,2,12,0));prints.add(first);prints.add(second);root.add(prints,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new BorderLayout(8,8));
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));actions.add(compare);actions.add(clear);bottom.add(actions,BorderLayout.NORTH);
        result.setEditable(false);result.setLineWrap(true);result.setWrapStyleWord(true);result.setRows(6);result.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        bottom.add(new JScrollPane(result),BorderLayout.CENTER);
        root.add(bottom,BorderLayout.SOUTH);setContentPane(root);
        compare.addActionListener(e->runComparison());
        clear.addActionListener(e->{first.reset();second.reset();invalidateResult();});
        invalidateResult();
    }
    private void invalidateResult(){
        if(busy)return;
        compare.setEnabled(first.image!=null && second.image!=null);
        result.setText(compare.isEnabled()?"Ready. Select Compare fingerprints.":"Select two fingerprint images to begin.");
    }
    private void setBusy(boolean value){
        busy=value;
        first.controls(!value);second.controls(!value);clear.setEnabled(!value);
        compare.setEnabled(!value && first.image!=null && second.image!=null);
    }
    private void runComparison(){
        if(busy||first.image==null||second.image==null)return;
        final int da,db;
        try{first.dpi.commitEdit();second.dpi.commitEdit();da=(Integer)first.dpi.getValue();db=(Integer)second.dpi.getValue();}
        catch(Exception ex){result.setText("Enter valid DPI values.");return;}
        BufferedImage a=first.image,b=second.image;
        boolean ia=first.invert.isSelected(),ib=second.invert.isSelected();
        boolean uncertain=first.assumed.isSelected()||second.assumed.isSelected();
        setBusy(true);result.setText("Extracting ridge features and comparing...");
        new SwingWorker<Engine.Result,Void>(){
            protected Engine.Result doInBackground() throws Exception{return Engine.compare(a,da,ia,b,db,ib);}
            protected void done(){
                try{
                    Engine.Result r=get();
                    result.setText(String.format(java.util.Locale.ROOT,
                      "%s%nSimilarity score: %.2f (reference threshold: %.0f)%nProcessing time: %d ms. DPI A: %d; DPI B: %d.%nScore is not a percentage or identity determination. Partial, non-overlapping, unclear or incorrectly scaled prints may give misleading scores.",
                      r.interpretation(uncertain),r.score(),Engine.THRESHOLD,r.elapsedMs(),da,db));
                }catch(Exception ex){
                    Throwable cause=ex.getCause()!=null?ex.getCause():ex;
                    String msg=cause.getMessage();
                    result.setText("Comparison could not be completed. "+(msg==null?"Check image quality and try again.":msg));
                }finally{setBusy(false);}
            }
        }.execute();
    }
    private final class PrintPanel extends JPanel {
        BufferedImage image;
        final JLabel info=new JLabel("No image selected");
        final JButton choose=new JButton("Choose image");
        final JSpinner dpi=new JSpinner(new SpinnerNumberModel(500,100,2000,1));
        final JCheckBox assumed=new JCheckBox("DPI is assumed / unknown",true),invert=new JCheckBox("Invert light-on-dark ridges");
        final JPanel preview=new JPanel(){
            @Override protected void paintComponent(Graphics graphics){
                super.paintComponent(graphics);
                if(image==null)return;
                Graphics2D g=(Graphics2D)graphics.create();
                try{
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    double scale=Math.min((double)getWidth()/image.getWidth(),(double)getHeight()/image.getHeight());
                    int w=(int)(image.getWidth()*scale), h=(int)(image.getHeight()*scale);
                    g.drawImage(image,(getWidth()-w)/2,(getHeight()-h)/2,w,h,null);
                }finally{g.dispose();}
            }
        };
        PrintPanel(String title){
            super(new BorderLayout(7,7));
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),BorderFactory.createEmptyBorder(6,6,6,6)));
            preview.setBackground(new Color(230,230,230));preview.setPreferredSize(new Dimension(300,240));add(preview,BorderLayout.CENTER);
            JPanel fields=new JPanel();fields.setLayout(new BoxLayout(fields,BoxLayout.Y_AXIS));
            fields.add(info);fields.add(choose);
            JPanel resolution=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));resolution.add(new JLabel("Capture DPI: "));resolution.add(dpi);fields.add(resolution);
            fields.add(assumed);fields.add(invert);add(fields,BorderLayout.SOUTH);
            choose.addActionListener(e->load());
            dpi.addChangeListener(e->invalidateResult());
            assumed.addActionListener(e->invalidateResult());invert.addActionListener(e->invalidateResult());
        }
        void controls(boolean enabled){choose.setEnabled(enabled);dpi.setEnabled(enabled);assumed.setEnabled(enabled);invert.setEnabled(enabled);}
        void load(){
            JFileChooser picker=new JFileChooser();picker.setFileFilter(new FileNameExtensionFilter("Fingerprint images (PNG, JPEG, BMP)","png","jpg","jpeg","bmp"));
            if(picker.showOpenDialog(App.this)!=JFileChooser.APPROVE_OPTION)return;
            File file=picker.getSelectedFile();
            setBusy(true);result.setText("Loading image...");
            new SwingWorker<BufferedImage,Void>(){
                protected BufferedImage doInBackground() throws Exception{return Engine.load(file.toPath());}
                protected void done(){
                    try{
                        image=get();dpi.setValue(500);assumed.setSelected(true);invert.setSelected(false);
                        info.setText(image.getWidth()+" × "+image.getHeight()+" pixels");preview.repaint();
                        setBusy(false);invalidateResult();
                    }catch(Exception ex){
                        reset();setBusy(false);
                        Throwable c=ex.getCause()!=null?ex.getCause():ex;
                        result.setText("Image could not be loaded: "+c.getMessage());
                    }
                }
            }.execute();
        }
        void reset(){image=null;info.setText("No image selected");dpi.setValue(500);assumed.setSelected(true);invert.setSelected(false);preview.repaint();}
    }
    public static void main(String[] args){
        ImageIO.setUseCache(false);
        SwingUtilities.invokeLater(()->{
            try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ignored){}
            new App().setVisible(true);
        });
    }
}
