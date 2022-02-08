/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package subtitles;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author vivolab
 */
class SubtitleSetIOTXTFAB implements SubtitleSetIO{
    public static final String NEW_LINE_CHARACTER_TXT = "<l>";
    public static final String NEW_SUBTITLE_CHARACTER_TXT = "<p>";
    private String _fileCoding;
    private boolean _useOffsetTimeFromFile;

    public SubtitleSetIOTXTFAB() {
        _useOffsetTimeFromFile = false;
    }

    public ArrayList<Subtitle> read(String filename) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {
        String contents = getHeader();

        for (Subtitle sub: vsubtitles){
            contents += sub.toTXTFAB() + "\r\n\r\n";
        }

        IOUtils.writeFile(filename, contents, _fileCoding);
    }


    private String getHeader() {
        return "# Subtitles file, University of Zaragoza txt format, "
                + Calendar.getInstance().getTime() + "\r\n";
    }

    public void setOffsetTimeFromFile(boolean value) {
        this._useOffsetTimeFromFile = value;
    }

    public String getLastEditionDate() {
        return null;
    }

    public void setEditor(String editorname) {

    }

    public void setVistoBueno(boolean validate) {

    }

    public String getLastVistoBuenoDate() {
        return null;
    }

    public String getEditor() {
        return null;
    }

    public String getEditorVistoBueno() {
        return null;
    }

    @Override
    public void setCoding(String _coding) {
        this._fileCoding = _coding;
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
