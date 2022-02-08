t/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package subtitles;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jegarcia
 */
public class SubtitleSet {

    
    private ArrayList <Subtitle> _vsubtitles;
    SubtitleSetIO _lastreader;
    private String _lastEditionDate;
    private String _lastEditionEditor;
    private String _lastVistoBuenoDate;
    private String _lastVistoBuenoEditor;
    private String _coding;
    private int _framesPerSecond;

    //-- metadatos (stl)
    private String _originalProgramName;
    private String _originalEpisodeName;
    private String _translatedProgramName;
    private String _translatedEpisodeName;
    private String _codRefListaSubtitulado;
    private String _language;
    private String _revision;
    private String _translatorName;
    private String _translatorInfo;
    private String _publisherName;
    private String _editorName;
    private String _editorInfo;
    private String _originCountry;
    private String _startOfProgram;
    
    
    public SubtitleSet(){
        _vsubtitles = new ArrayList();
    }

    public SubtitleSet(String filename) throws Exception{

        read(filename, false);

    }
    
    public SubtitleSet(String filename, String coding) throws Exception{

        this._coding = coding;
        
        read(filename, false);

    }

    public SubtitleSet(String filename, boolean useoffsetTimeFromFile) throws Exception{

        read(filename, useoffsetTimeFromFile);

    }

    public SubtitleSet(ArrayList <Subtitle> vsubtitles){
        this._vsubtitles = vsubtitles;
    }

    public void read(String filename, boolean useoffsetTimeFromfile) throws Exception{

        _vsubtitles = new ArrayList();

        SubtitleSetIO reader;

        if       (filename.matches("^(.*)\\.uz.xml")){
            reader = new SubtitleSetIOXMLUZ();
        }else if (filename.matches("^(.*)\\.uz.nh.xml")){
            reader = new SubtitleSetIOXMLUZ(true);
        }else if (filename.matches("^(.*)\\.cires.xml")){
            reader = new SubtitleSetIOCIRESXML();
        }else if (filename.matches("^(.*)\\.xml")){
            reader = new SubtitleSetIOXML();
        }else if (filename.matches("^(.*)\\.stl")){
            reader = new SubtitleSetIOSTL();
        }else if (filename.matches("^(.*)\\.srt")){
            reader = new SubtitleSetIOSRT();
        }else if (filename.matches("^(.*)\\.fs")){
            reader = new SubtitleSetIOFS();
        }else if (filename.matches("^(.*)\\.rtxt")){
            reader = new SubtitleSetIORows();
        }else{
            reader = null;
            throw new UnsupportedOperationException("Unknown read format");
        }

        if (_coding!=null) reader.setCoding(_coding);
        
        reader.setOffsetTimeFromFile(useoffsetTimeFromfile);

        this._vsubtitles = reader.read(filename);

        this._lastEditionDate= reader.getLastEditionDate();

        this._lastEditionEditor = reader.getEditor();

        this._lastVistoBuenoDate = reader.getLastVistoBuenoDate();

        this._lastVistoBuenoEditor = reader.getEditorVistoBueno();

        this._lastreader = reader;
        
        this._framesPerSecond = reader.getFramesPerSecond();
        
        //-- read metadata
        this._originalProgramName = reader.getOriginalProgramName();
        this._originalEpisodeName = reader.getOriginalEpisodeName();
        this._translatedProgramName = reader.getTranslatedProgramName();
        this._translatedEpisodeName = reader.getTranslatedEpisodeName();
        this._codRefListaSubtitulado = reader.getCodRefListaSubtitulado();
        this._language = reader.getLanguage();
        this._revision = reader.getRevision();
        this._translatorName = reader.getTranslatorName();
        this._translatorInfo = reader.getTranslatorInfo();
        this._publisherName = reader.getPublisherName();
        this._editorName = reader.getEditorName();
        this._editorInfo = reader.getEditorInfo();
        this._originCountry = reader.getOriginCountry();
        this._startOfProgram = reader.getStartOfProgram();

    }

    public void write(String filename, String editorname, boolean validate, String coding) throws Exception{
        
        if (coding!=null) this._coding = coding;
        
        write(filename, editorname, validate);
        
    }
    
    public void write(String filename, String editorname, boolean validate) throws Exception{

        SubtitleSetIO writer;

        if       (filename.matches("^(.*)\\.uz.xml")){
            writer = new SubtitleSetIOXMLUZ();
        }else if (filename.matches("^(.*)\\.cires.xml")){
            writer = new SubtitleSetIOCIRESXML();
        }else if (filename.matches("^(.*)\\.xml")){
            writer = new SubtitleSetIOXML();
        }else if (filename.matches("^(.*)\\.stl")){
            SubtitleSetIOSTL wstl;
            if (this._lastreader == null){
                wstl = new SubtitleSetIOSTL();
            }else if (this._lastreader.getClass().equals(SubtitleSetIOSTL.class)){
                wstl = new SubtitleSetIOSTL( (SubtitleSetIOSTL) this._lastreader);
            }else{
                wstl = new SubtitleSetIOSTL();  
            }
            
            wstl.setNumberOfSubtitles(this._vsubtitles.size());
            
            if (this._framesPerSecond>0){
                wstl.setFramesPerSecond(this._framesPerSecond);
            }
            
            writer = wstl;
            
        }else if (filename.matches("^(.*)\\.fab.txt")){
            writer = new SubtitleSetIOTXTFAB();
        }else if (filename.matches("^(.*)\\.txt")){
            writer = new SubtitleSetIOTXT();
        }else if (filename.matches("^(.*)\\.srt")){
            writer = new SubtitleSetIOSRT();
        }else if (filename.matches("^(.*)\\.fs")){
            writer = new SubtitleSetIOFS();
        }else{
            writer = null;
            throw new UnsupportedOperationException("Unknown write format");
        }

        if (this._coding!=null) writer.setCoding(_coding);
        
        writer.setEditor(editorname);
        
        writer.setVistoBueno(validate);

        writer.write(filename, _vsubtitles);
        
    }
    
    public void write(String filename, boolean validate) throws Exception{

        SubtitleSetIO writer;

        if       (filename.matches("^(.*)\\.uz.xml")){
            writer = new SubtitleSetIOXMLUZ();
        }else if (filename.matches("^(.*)\\.cires.xml")){
            writer = new SubtitleSetIOCIRESXML();
        }else if (filename.matches("^(.*)\\.xml")){
            writer = new SubtitleSetIOXML();
        }else if (filename.matches("^(.*)\\.stl")){
            SubtitleSetIOSTL wstl;
            if (this._lastreader == null){
                wstl = new SubtitleSetIOSTL();
            }else if (this._lastreader.getClass().equals(SubtitleSetIOSTL.class)){
                wstl = new SubtitleSetIOSTL( (SubtitleSetIOSTL) this._lastreader);
            }else{
                wstl = new SubtitleSetIOSTL();  
            }
            
            wstl.setNumberOfSubtitles(this._vsubtitles.size());
            
            if (this._framesPerSecond>0){
                wstl.setFramesPerSecond(this._framesPerSecond);
            }
            
            //-- si existen, añadimos los metadatos
            wstl.setSTLMetadataInfo(this._originalProgramName, this._originalEpisodeName, this._translatedProgramName, this._translatedEpisodeName, 
                                    this._codRefListaSubtitulado, this._language, this._revision, this._translatorName, this._translatorInfo, 
                                    this._publisherName, this._editorName, this._editorInfo, this._originCountry, this._startOfProgram);
            
            if (filename.matches("^(.*)\\.w32.stl")) wstl.setW32Format(true);
            else                                     wstl.setW32Format(false);
            
            if (filename.matches("^(.*)\\.bh.stl")) wstl.setBottomHeightFormat(true);
            else                                    wstl.setBottomHeightFormat(false);
                    
            writer = wstl;
            
        }else if (filename.matches("^(.*)\\.fab.txt")){
            writer = new SubtitleSetIOTXTFAB();
        }else if (filename.matches("^(.*)\\.txt")){
            writer = new SubtitleSetIOTXT();
        }else if (filename.matches("^(.*)\\.srt")){
            writer = new SubtitleSetIOSRT();
        }else if (filename.matches("^(.*)\\.fs")){
            writer = new SubtitleSetIOFS();
        }else{
            writer = null;
            throw new UnsupportedOperationException("Unknown write format");
        }

        if (this._coding!=null) writer.setCoding(_coding);
        
        writer.setVistoBueno(validate);

        writer.write(filename, _vsubtitles);
        
    }

    public Subtitle getSubtitle(int n){
        return this._vsubtitles.get(n);
    }


    public void setSubtitle(int n, Subtitle s){
        this._vsubtitles.set(n, s);
    }

    public ArrayList <Subtitle> getVsubtitles(){
        return this._vsubtitles;
    }

    public void setVsubtitles(String texts, String times){
        this._vsubtitles = new ArrayList();
        String vtexts[] = texts.split("\r\n");
        String vtimes[] = times.split("\r\n");

        int numeltxt = vtexts.length;
        int numel    = vtimes.length;
        for (int i=0; i<numel; i++){
            String vlines[] = new String[2];
            if (2*i<numeltxt){
                vlines[0] = vtexts[2*i];
            }else{
                vlines[0] = "";
            }
            if (2*i+1<numeltxt){
                vlines[1] = vtexts[2*i+1];
            }else{
                vlines[1] = "";
            }
            
            String y = vtimes[i];
            String vy[] = y.split("#");
            Subtitle sub = new Subtitle(vlines, vy[0], vy[1]);
            this._vsubtitles.add(sub);
        }
    }


     public static void main(String args[]){
         if (args.length<1){
             System.out.println("\n\n\tUse: java -jar sresults.SubtitleSet  directoryfiles  [extensionfilter]\n\n");
             System.exit(1);
         }
         String extensionfilter;
         if (args.length==2){
             extensionfilter = "^(.*)\\." + args[1] + "$";
         }else{
             extensionfilter = ".*";
         }
         String directorypath = args[0];
         File dir = new File (directorypath);
         String files[] = dir.list();

         for (int i=0; i<files.length; i++){
             String filename = directorypath + "/" + files[i];
             if (!filename.matches(extensionfilter)) continue;
             String filenameout = filename + ".txt";
            try {
                SubtitleSet ss = new SubtitleSet(filename);
                ss.write(filenameout,"",false);
            } catch (Exception ex) {
                System.out.println("Error: " + ex);
                continue;
            }
         }


    }

    public SubtitleSet getAddedSubtitles(SubtitleSet ss) {
        ArrayList <Subtitle> vdiff = new ArrayList();
        ArrayList <Subtitle> vbig  = ss.getVsubtitles();
        Subtitle slast = this._vsubtitles.get(this._vsubtitles.size()-1);

        int posini = 0;
        
        Subtitle siniFile = vbig.get(0);
        if(slast.isBeforeSubtitle(siniFile)){
            //-- detectado cambio de dia de emision dentro del archivo de subtitulos
            
            //-- buscamos el ultimo subtitulo en el array del archivo completo
            int pos = findSubtitle(vbig,slast);
            
            //-- actualizamos la posicion de inicio en la busqueda de nuevos subtitulos
            if (pos > -1) posini = pos;
                    
        }

        for (int i=posini; i<vbig.size(); i++){
            Subtitle snew = vbig.get(i);
            if (snew.isAfterSubtitle(slast)){
                vdiff.add(snew);
            }
        }


        return new SubtitleSet(vdiff);
    }
    
    /*
     * public SubtitleSet getAddedSubtitles(SubtitleSet ss) {
        ArrayList <Subtitle> vdiff = new ArrayList();
        ArrayList <Subtitle> vbig  = ss.getVsubtitles();
        Subtitle slast = this._vsubtitles.get(this._vsubtitles.size()-1);


        for (int i=0; i<vbig.size(); i++){
            Subtitle snew = vbig.get(i);
            if (snew.isAfterSubtitle(slast)){
                vdiff.add(snew);
            }
        }


        return new SubtitleSet(vdiff);
     * 
     */
    
    public void modifyVsubtitles(ArrayList vlinessubtitles) {
        ArrayList <Subtitle> vsubtitles = this.getVsubtitles();
        int i, len = vsubtitles.size();
        for (i=0; i<len; i++){
            Subtitle sub = vsubtitles.get(i);
            String vlinesnew[] = (String []) vlinessubtitles.get(i);
            sub.updateTextContents(vlinesnew);
            vsubtitles.set(i, sub);
        }

    }

    public String getLastEditionDate() {
        return this._lastEditionDate;
    }
    public String getLastEditionEditor() {
        return this._lastEditionEditor;
    }
    public String getLastVistoBuenoDate() {
        return this._lastVistoBuenoDate;
    }
    public String getLastVistoBuenoEditor() {
        return this._lastVistoBuenoEditor;
    }

    public void setOffsetSeconds(double tini) throws Exception{
        for (Subtitle sub : this._vsubtitles){
            sub.setOffsetSeconds(tini);
        }
    }

    public void setFramesPerSecond(int framesPerSecond) {
        this._framesPerSecond = framesPerSecond;
    }

    public int getNearestSubtitleFromTiniAndTend(double dt0, double dt1) {  
        double EPS = 1E-1;
        int k=0;
        for (Subtitle sub: this._vsubtitles){
            float diff0 = Math.abs(sub.getTimeIniFloat()-(float)dt0);
            float diff1 = Math.abs(sub.getTimeEndFloat()-(float)dt1);
            if (diff0 < EPS & diff1 < EPS){
                return k;
            }
            k++;
        }
        
        return -1;
    }

    public void setMetadataInfo(String originalProgramName, String originalEpisodeName, String translatedProgramName, String translatedEpisodeName, 
                                String codRefListaSubtitulado, String language, String revision, String translatorName, String translatorInfo,
                                String publisherName, String editorName, String editorInfo, String originCountry, String startOfProgram){
        
        //-- metadatos (stl)
        this._originalProgramName = originalProgramName;
        this._originalEpisodeName = originalEpisodeName;
        this._translatedProgramName = translatedProgramName;
        this._translatedEpisodeName = translatedEpisodeName;
        this._codRefListaSubtitulado = codRefListaSubtitulado;
        this._language = language;
        this._revision = revision;
        this._translatorName = translatorName;
        this._translatorInfo = translatorInfo;
        this._publisherName = publisherName;
        this._editorName = editorName;
        this._editorInfo = editorInfo;
        this._originCountry = originCountry;
        this._startOfProgram = startOfProgram;
    }
    
    public String getOriginalProgramName(){
       
        return this._originalProgramName;
    }
    
    public String getTranslatedProgramName(){
       
        return this._translatedProgramName;
    }
    
    public String getOriginalEpisodeName(){
       
        return this._originalEpisodeName;
    }
    
    public String getTranslatedEpisodeName(){
       
        return this._translatedEpisodeName;
    }
      
    public String getCodRefListaSubtitulado(){
       
        return this._codRefListaSubtitulado;
    }
    
    public String getLanguage(){
       
        return this._language;
    }
    
    public String getRevision(){
       
        return this._revision;
    }
    
    public String getTranslatorName(){
       
        return this._translatorName;
    }
    
    public String getTranslatorInfo(){
       
        return this._translatorInfo;
    }
    
    public String getPublisherName(){
       
        return this._publisherName;
    }
    public String getEditorName(){
       
        return this._editorName;
    }
    public String getEditorInfo(){
       
        return this._editorInfo;
    }
    
    public String getOriginCountry(){
       
        return this._originCountry;
    }
    
    public String getStartOfProgram(){
       
        return this._startOfProgram;
    }
    
    public int getFramesPerSecond() {
       return this._framesPerSecond;
    }

    private int findSubtitle(ArrayList<Subtitle> vbig, Subtitle slast) {
        
        for(int i=0; i<vbig.size(); i++){
            
            Subtitle sub = vbig.get(i);
            if (sub.equalsWithTime(slast, 0, 0)){
                return i;
            }
        }
        
        return -1;
    }

}
