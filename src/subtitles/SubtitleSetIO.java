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
public interface SubtitleSetIO {

    public ArrayList <Subtitle> read(String filename) throws Exception;
    public void                 write(String filename, ArrayList <Subtitle> vsubtitles) throws Exception;
    public void                 setOffsetTimeFromFile(boolean value);
    public void                 setEditor(String editorname);
    public void                 setVistoBueno(boolean validate);
    public String               getLastEditionDate();
    public String               getLastVistoBuenoDate();
    public String               getEditor();
    public String               getEditorVistoBueno();
    public void                 setCoding(String _coding);
    public String               getOriginalProgramName();
    public String               getTranslatedProgramName();
    public String               getOriginalEpisodeName();
    public String               getTranslatedEpisodeName();
    public String               getCodRefListaSubtitulado();
    public String               getLanguage();
    public String               getRevision();
    public String               getTranslatorName();
    public String               getTranslatorInfo();
    public String               getPublisherName();
    public String               getEditorName();
    public String               getEditorInfo();
    public String               getOriginCountry();
    public String               getStartOfProgram();
    public int                  getFramesPerSecond();

}
