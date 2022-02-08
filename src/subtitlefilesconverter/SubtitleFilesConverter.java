/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlefilesconverter;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author aarguedas
 */
public class SubtitleFilesConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            //-- system look and feel
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        } catch (Exception ex) {
         try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex2) {                
            System.out.println(ex);
            System.exit(1);
        }}
        
        //-- new frame
        SubtitleFilesConverterView mf = new SubtitleFilesConverterView();
        
        //-- title
        mf.setTitle("SubtitleFilesConverter"); 
        
        //-- adjust dimension to its contents
        mf.pack();
        
        //-- here's the part where i center the jframe on screen
        mf.setLocationRelativeTo(null);

        //-- showing the frame
        mf.setVisible(true);
    }
}
