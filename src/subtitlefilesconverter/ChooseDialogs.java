/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlefilesconverter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;



/**
 *
 * @author jegarcia
 */
public class ChooseDialogs {
    
    public static String _lastFileDialog;

    
    public static void setDirectoryIfExists(String path) {
        if (new File(path).exists()) _lastFileDialog = path;
    }
    
    public static String getDialogTextFileOpen() {

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File(_lastFileDialog));
        
        fc.setDialogTitle("Abrir fichero de texto");

        
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                return pathname.getAbsolutePath().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Text files .txt";
            }
        });
        
        int returnVal = fc.showOpenDialog(null);

        if (returnVal==0){
            File fchoosed = fc.getSelectedFile();
            setDirectoryIfExists(fchoosed.toString());
            return fchoosed.toString();
        }else{
            return null;
        }
    }
    
    public static String getDialogTextCsvFileOpen() {

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File(_lastFileDialog));
        
        fc.setDialogTitle("Abrir fichero de texto");

        
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                return pathname.getAbsolutePath().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "Text files .csv";
            }
        });
        
        int returnVal = fc.showOpenDialog(null);

        if (returnVal==0){
            File fchoosed = fc.getSelectedFile();
            setDirectoryIfExists(fchoosed.toString());
            return fchoosed.toString();
        }else{
            return null;
        }
    }
    
    public static String getDialogTextFileSave() {

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File(_lastFileDialog));
        
        fc.setDialogTitle("Guardar fichero de texto");

        
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                return pathname.getAbsolutePath().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Text files .txt";
            }
        });
        
        int returnVal = fc.showSaveDialog(null);

        if (returnVal==0){
            File fchoosed = fc.getSelectedFile();
            setDirectoryIfExists(fchoosed.toString());
            return fchoosed.toString();
        }else{
            return null;
        }
    }
    
    public static String getDialogTextCsvFileSave() {

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File(_lastFileDialog));
        
        fc.setDialogTitle("Guardar fichero de texto");

        
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                return pathname.getAbsolutePath().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "Text files .csv";
            }
        });
        
        int returnVal = fc.showSaveDialog(null);

        if (returnVal==0){
            File fchoosed = fc.getSelectedFile();
            setDirectoryIfExists(fchoosed.toString());
            return fchoosed.toString();
        }else{
            return null;
        }
    }
    
          
    public static String getDialogAudioFileOpen() {

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File(_lastFileDialog));
        
        fc.setDialogTitle("Abrir fichero de audio");

        
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                if (pathname.getAbsolutePath().endsWith(".wav")) return true;
                if (pathname.getAbsolutePath().endsWith(".raw")) return true;
                return false;
            }

            @Override
            public String getDescription() {
                return "Audio Files (.wav|.raw)";
            }
        });
        
        int returnVal = fc.showOpenDialog(null);

        if (returnVal==0){
            File fchoosed = fc.getSelectedFile();
            setDirectoryIfExists(fchoosed.toString());
            return fchoosed.toString();
        }else{
            return null;
        }
    }
    
               
    public static String getDialogFolderOpen() {

        
        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new File(_lastFileDialog));
        
        fc.setDialogTitle("Seleccionar carpeta");

        
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) return true;
                else return false;
            }

            @Override
            public String getDescription() {
                return "Elige la carpeta";
            }
        });
        
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = fc.showOpenDialog(null);

        if (returnVal==0){
            File fchoosed = fc.getSelectedFile();
            setDirectoryIfExists(fchoosed.toString());
            return fchoosed.toString();
        }else{
            return null;
        }
    }




    

    
  


}
