/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitles;

import java.util.ArrayList;

/**
 *
 * @author vivolab
 */
class SubtitleSetIOFS implements SubtitleSetIO {

    private String _fileCoding;
    
    public SubtitleSetIOFS() {
    }

    @Override
    public ArrayList<Subtitle> read(String filename) throws Exception {
        String contents = IOUtils.readFile(filename, _fileCoding);
        
        String vc[] = contents.split("\n");
        
        ArrayList va = new ArrayList();
        
        for (int i=0; i<vc.length; i++){
            String vlines[] = vc[i].split("<p>");
            for (int j=0; j<vlines.length; j++) vlines[j] = vlines[j].trim();
            Subtitle s = new Subtitle(vlines, "0.0", "0.0");
            va.add(s);
        }
        
        return va;
    }

    @Override
    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {
        int i;
        String out="";
        for (i=0; i<vsubtitles.size(); i++){
            Subtitle sub = vsubtitles.get(i);
            out += sub.toTXTWithSeparator(" <p> ") + "\n";
        }
        
        IOUtils.writeFile(filename, out, null);
    }

    @Override
    public void setOffsetTimeFromFile(boolean value) {
        
    }

    @Override
    public void setEditor(String editorname) {
        
    }

    @Override
    public void setVistoBueno(boolean validate) {
        
    }

    @Override
    public String getLastEditionDate() {
        return "";
    }

    @Override
    public String getLastVistoBuenoDate() {
        return "";
    }

    @Override
    public String getEditor() {
        return "";
    }

    @Override
    public String getEditorVistoBueno() {
        return "";
    }

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
