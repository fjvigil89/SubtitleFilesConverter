/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitles;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author vivolab
 */
public class SubtitleSetIOSRT  implements SubtitleSetIO {
    
    
    private String _fileCoding;
    
    public SubtitleSetIOSRT(){
        
    }
    
    public ArrayList <Subtitle> read(String filename) throws Exception{
      String contents = IOUtils.readFile(filename, _fileCoding);

        //System.out.println("Contents: " + contents);
        
        if (contents==null) throw new Exception ("Empty file read srt subtitles");

        ArrayList <Subtitle> vsub = read_srt_subtitles(contents);

        return vsub;
    }
    
    

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {
        String contents="";
        int k=0;
        for (Subtitle sub: vsubtitles){
            contents += sub.toSRT(k++) + "\r\n";
        }
        
        IOUtils.writeFile(filename, contents, _fileCoding);
    }

    public void setOffsetTimeFromFile(boolean value) {
        
    }

    public void setEditor(String editorname) {
        
    }

    public void setVistoBueno(boolean validate) {
        
    }

    public String getLastEditionDate() {
        return "";
    }

    public String getLastVistoBuenoDate() {
        return "";
    }

    public String getEditor() {
        return "";
    }

    public String getEditorVistoBueno() {
        return "";
    }

    public static ArrayList<Subtitle> read_srt_subtitles(String contents) {
        
        ArrayList <Subtitle> vsub = new ArrayList();
                
        contents = contents.replaceAll("\r\n","\n");
        contents = contents.replaceAll("\n\n", "\n");
        String vlines[] = contents.split("\n");
        
        int k=0;
        
        while ((vlines[k].length()==0) || !"1234567890".contains(vlines[k])) k++;
        
        while(true){
        
            int nsub = Integer.parseInt(vlines[k++]);
            String ltime = vlines[k++];
            String vt[] = ltime.split(" +");
            String tini = vt[0].replaceAll(",", ":");
            String tend = vt[2].replaceAll(",", ":");
            ArrayList <Color> fgColorArray = new ArrayList();
            
            int ini = k, end=-1, nlines=0;
            String nextSub = String.valueOf(nsub+1);
            while (k <vlines.length){
                String str = vlines[k++];
                if (str.trim().length()==0) continue;
                if (str.equals(nextSub)) break;
                nlines++;
            }
            
            if (k<vlines.length) end = k-1;
            else                 end = k;
            
             if (nlines==0){
                if (k==vlines.length) break;
                k--;
                continue;
            }


            //System.out.println("ini: " + ini + " end: " + end);
            
            String vtxt[] = new String[nlines];
            int z=0;
            /*for (int i=ini; i<end; i++){
                String str = vlines[i];
                if (str.trim().length()==0) continue;
                //extraemos el color y el texto de la linea
                Pattern p = Pattern.compile("^<font color=\"#(\\S+)\">(.+[^<]+)</font>$");
                Matcher m = p.matcher(str);
                //while (m.find()){
                if(m.matches()){
                    System.out.println("Color: " + m.group(1));
                    System.out.println("Linea: " + m.group(2));
                    Color color = new Color(Integer.parseInt(m.group(1), 16));
                    vtxt[z++] = m.group(2);
                    fgColorArray.add(color);
                }else{
                    fgColorArray.add(Color.white);
                    vtxt[z++] = vlines[i];
                }
            }*/
            
            for (int i=ini; i<end; i++){
                String str = vlines[i];
                if (str.trim().length()==0) continue;
                //extraemos el color y el texto de la linea
                Pattern p = Pattern.compile(".*?<font color=\"#(\\S+)\">.+");
                Matcher m = p.matcher(str);
                if(m.matches()){
                    Color color = new Color(Integer.parseInt(m.group(1), 16));
                    fgColorArray.add(color);
                }else{
                    fgColorArray.add(Color.white);
                }
                String txt = vlines[i].replaceAll("<font color=\"#(\\S+)\">","");
                txt = txt.replaceAll("</font>","");
                vtxt[z++] = txt;
            }

            Subtitle s = new Subtitle(vtxt, tini, tend, true, fgColorArray);
            
            vsub.add(s);

            if (k==vlines.length) break;

            k--;
            //System.out.println(nsub);
            
            //JOptionPane.showMessageDialog(null, "NextSub: " + nextSub);
            
        }
        
        return vsub;
    }
    
    /*
    public static ArrayList<Subtitle> read_srt_subtitles(String contents) {
        
        ArrayList <Subtitle> vsub = new ArrayList();
        
        contents = contents.replaceAll("\r\n","\n");
        contents = contents.replaceAll("\n\n", "\n");
        String vlines[] = contents.split("\n");
        
        int k=0;
        
        while ((vlines[k].length()==0) || !"1234567890".contains(vlines[k])) k++;
        
        while(true){
        
            int nsub = Integer.parseInt(vlines[k++]);
            String ltime = vlines[k++];
            String vt[] = ltime.split(" +");
            String tini = vt[0].replaceAll(",", ":");
            String tend = vt[2].replaceAll(",", ":");
            
            int ini = k, end=-1, nlines=0;
            String nextSub = String.valueOf(nsub+1);
            while (k <vlines.length){
                String str = vlines[k++];
                if (str.trim().length()==0) continue;
                if (str.equals(nextSub)) break;
                nlines++;
            }
            
            if (k<vlines.length) end = k-1;
            else                 end = k;
            
             if (nlines==0){
                if (k==vlines.length) break;
                k--;
                continue;
            }


            //System.out.println("ini: " + ini + " end: " + end);
            
            String vtxt[] = new String[nlines];
            int z=0;
            for (int i=ini; i<end; i++){
                String str = vlines[i];
                if (str.trim().length()==0) continue;
                vtxt[z++] = vlines[i];
            }

            Subtitle s = new Subtitle(vtxt, tini, tend, true);
            //System.out.println("all lines: '" + s.getAllLines() + "'");
            
            vsub.add(s);
            

            if (k==vlines.length) break;

            k--;
            //System.out.println(nsub);
            
            //JOptionPane.showMessageDialog(null, "NextSub: " + nextSub);
            
        }
        
        return vsub;
    } 
     */
    
    
    @Override
    public void setCoding(String coding) {
        this._fileCoding = coding;
    }
    
    public String getOriginalProgramName(){   
        return "";
    }
    
    public String getTranslatedProgramName(){      
        return "";
    }
    
    public String getOriginalEpisodeName(){      
        return "";
    }
    
    public String getTranslatedEpisodeName(){     
        return "";
    }
      
    public String getCodRefListaSubtitulado(){      
        return "";
    }
    
    public String getLanguage(){     
        return "";
    }
    
    public String getRevision(){
        return "";
    }
    
    public String getTranslatorName(){      
        return "";
    }
    
    public String getTranslatorInfo(){
        return "";
    }
    
    public String getPublisherName(){   
        return "";
    }
    public String getEditorName(){    
        return "";
    }
    public String getEditorInfo(){     
        return "";
    }
    
    public String getOriginCountry(){    
        return "";
    }
    
    public String getStartOfProgram(){ 
        return "";
    }
    
    public int getFramesPerSecond() {
       return 0;
    }
    
}
