/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitles;

import java.util.ArrayList;

/**
 *
 * @author jegarcia
 */
public class SubtitleSetIORows implements SubtitleSetIO {
       
    private String _fileCoding;
    
    public SubtitleSetIORows(){
        
    }
    
    public ArrayList <Subtitle> read(String filename) throws Exception{
      String contents = IOUtils.readFile(filename, _fileCoding);

        //System.out.println("Contents: " + contents);
        
        if (contents==null) throw new Exception ("Empty file read IORows subtitles");

        ArrayList <Subtitle> vsub = read_iorows_subtitles(contents);

        return vsub;
    }
    
    

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {
       
        throw new Exception ("write iorows format not supported yet!");

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

    private ArrayList<Subtitle> read_iorows_subtitles(String contents) {
        
        ArrayList <Subtitle> vsub = new ArrayList();
        
        contents = contents.replaceAll("\r\n","\n");
        contents = contents.replaceAll("\n\n", "\n");
        String vlines[] = contents.split("\n");
        
        int k=0;

        while(true){

            String tini = vlines[k++].trim();
            String tend = vlines[k++].trim();
            
            //System.out.println(tini);
            //System.out.println(tend);
            
            ArrayList alinessub = new ArrayList();

            while (k <vlines.length){
                String str = vlines[k++].trim();
                if (str.matches("[0-9:]+")) break;
                if (str.length()>0) alinessub.add(str);   
            }
            
            if (alinessub.size()==0) break;
            
            String vlinessub[] = new String[alinessub.size()]; 
            for (int i=0; i<vlinessub.length; i++){
                vlinessub[i] = (String) alinessub.get(i);
            }


            //convert from number hh:mm:ss:ff to hh:mm:ss:MMM
            tini = convertFromFramesToMiliseconds(tini, 25);
            tend = convertFromFramesToMiliseconds(tend, 25);
           
            //System.out.println(tini);
            //System.out.println(tend);

            Subtitle s = new Subtitle(vlinessub, tini, tend, true);
            //System.out.println("all lines: '" + s.getAllLines() + "'");
            
            vsub.add(s);
            
            if (k==vlines.length) break;
            
            k--;


        }
        
        return vsub;
    }
    
    
    @Override
    public void setCoding(String coding) {
        this._fileCoding = coding;
    }

    public static String convertFromFramesToMiliseconds(String tini, int nframes_per_second) {
         String frames = tini.replaceAll("(\\d\\d):(\\d\\d):(\\d\\d):(\\d\\d)", "$4");
         String times  = tini.replaceAll("(\\d\\d):(\\d\\d):(\\d\\d):(\\d\\d)", "$1:$2:$3");
         
         int iframes = Integer.valueOf(frames);
          
         int miliseconds = Math.round((float)iframes * 1000.0F / (float) nframes_per_second);
         
         return String.format("%s:%03d", times, miliseconds);
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
