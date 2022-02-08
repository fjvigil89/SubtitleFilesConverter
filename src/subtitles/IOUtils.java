/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vivolab
 */
public class IOUtils {
    
    public static String readFile(String filename, String coding) throws Exception{

        InputStreamReader is;
        if (coding!=null) is = new InputStreamReader(new FileInputStream(filename), coding);
        else              is = new InputStreamReader(new FileInputStream(filename));  
        BufferedReader in = new BufferedReader(is);
        String str=in.readLine(), line;
        while ((line = in.readLine()) != null ){
             str = str + "\r\n" + line;
        }
        in.close();
        return str;

    }
    
    

    public static void writeFile(String filename, String contents, String coding) throws Exception{
        OutputStreamWriter os;
        if (coding!=null) os = new OutputStreamWriter(new FileOutputStream(filename), coding);
        else              os = new OutputStreamWriter(new FileOutputStream(filename));
        BufferedWriter out = new BufferedWriter(os);
        out.write(contents);
        out.close();
    }
    
    public static String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }

    
}
