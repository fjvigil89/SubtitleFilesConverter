/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package console;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import subtitles.Subtitle;
import subtitles.SubtitleSet;
/**
 *
 * @author fjvigil
 */
public class subtitleConsole {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner in = new Scanner(System.in);
        //String path="AT-20181010.srt";
        //String path="D:\\AT-20.stl";
        //String format=".srt";
        System.out.println("Primer parámetro es la dirección del fichero \n");
        System.out.println("Segundo parámetro es la extensión a convertir (.stl || .srt ) \n");
        String path=args[0];
        String format=args[1];        
        /*
        System.out.println("Entre el Path del fichero");
        path = in.nextLine();
        
        System.out.println("Entre el formato de salida");
        format += in.nextLine();
        */
        
        System.out.println(convert(path, format));
        
        
    }
    
    private static String convert(String path, String format) //.srt .stl
    {       
        try {            
            if (!path.equals("")) 
            {            
                File f = new File(path.toString());

                String so = System.getProperty("os.name");
                String separador = System.getProperty("file.separator");
                
                //File[] files = f.listFiles();
                //Arrays.sort(files);
                ArrayList<File> subtitleFiles= new ArrayList<File>(Arrays.asList(f));

                if (subtitleFiles.size() == 0) {
                    return "No hay archivos en la ruta de entrada seleccionada.";
                }

                for (int i=0; i<subtitleFiles.size(); i++)
                {

                    String file = subtitleFiles.get(i).getPath();
                    SubtitleSet ss;
                    try {
                        // cargamos los subtitulos a convertir
                        ss = new SubtitleSet(file);
                    } catch (Exception ex) {                        
                        String text = "Error al leer el archivo: " +  file + "\n";
                        System.err.println(text);
                        continue;
                    }
                    // guardamos los subtitulos en el formato seleccionado
                    String fileOut = file.substring(0,file.length()-4) + format;
                    //-- convertimos los subtitulos al formato stl
                    ArrayList<Subtitle> vsubtitles = ss.getVsubtitles();
                    ArrayList <Subtitle> vsubOut = new ArrayList();
                    for(int j=0; j<vsubtitles.size(); j++){
                        Subtitle sub = vsubtitles.get(j);
                        sub.toSTLFormat(j, 17, 25);
                        vsubOut.add(sub);        
                    }
                    ss = new SubtitleSet(vsubOut);
                    
                    try {
                        ss.write(fileOut, false);
                    } catch (Exception ex) {                        
                        String text = "Error al escribir el archivo: " +  fileOut + "\n";
                        System.err.println(text);
                    }
                    
                    String text = "Archivo generado: " +  fileOut + "\n";
                    return text;
                }
            }        
            
            return "";
        }
        catch(Exception e) {
            System.err.println(e.getLocalizedMessage());        
            return e.getLocalizedMessage();
        }
    }
}
