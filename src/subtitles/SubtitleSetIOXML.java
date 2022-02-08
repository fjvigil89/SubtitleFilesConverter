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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author jegarcia
 */
public class SubtitleSetIOXML implements SubtitleSetIO{
    public static final String NEW_LINE_CHARACTER_XML = "<br />";
    public static final String NEW_LINE_CHARACTER = "#LF#";
    private  String _fileCoding;
    private final String _lastEditionDateIdentifier      = "LastEditedDate";
    private final String _lastEditionEditorIdentifier    = "LastEditedEditor";
    private final String _lastVistoBuenoDateIdentifier   = "LastVistoBuenoDate";
    private final String _lastVistoBuenoEditorIdentifier = "LastVistoBuenoEditor";
    private int initTime;
    private int secondsOffset;
    private boolean _useOffsetTimeFromFile;
    private String _lastEditionDate;
    private String _editor;
    private boolean _vistobuenoflag;
    private String _lastVistoBuenoUZDate;
    private String _editorVistoBueno;

    public SubtitleSetIOXML(){
        _useOffsetTimeFromFile = false;
        
        _fileCoding = "UTF8";
    }

    public ArrayList<Subtitle> read(String filename) throws Exception {

        String contents = IOUtils.readFile(filename, _fileCoding);

        contents = preprocess_contents(contents);

        getTimeOffsetFromFilename(filename);

        ArrayList <Subtitle> vsub = parse_xml_subtitles(contents);

        return vsub;


    }

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {

        String contents = getHeader();

        for (Subtitle sub: vsubtitles){
            contents += "                    " + sub.toXML() + "\r\n";
        }

        contents += getTail();

        IOUtils.writeFile(filename, contents, _fileCoding);

    }


    private ArrayList<Subtitle> parse_xml_subtitles(String contents) throws Exception {
        ArrayList <Subtitle> vsub = new ArrayList();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


	//Using factory get an instance of document builder
	DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(contents));


        //parse using builder to get DOM representation of the XML file
	Document dom = db.parse(is);

        //get the root elememt
        Element docEle = dom.getDocumentElement();


        NodeList nle; Element elast;
        //get a the node <LastEditedDate></lastEditedDate>
        this._lastEditionDate = null;
        nle = docEle.getElementsByTagName(_lastEditionDateIdentifier);
        if (nle!=null && nle.getLength() > 0){
            elast = (Element) nle.item(0);
            this._lastEditionDate   =  elast.getTextContent();
        }

        this._editor = null;
        nle = docEle.getElementsByTagName(this._lastEditionEditorIdentifier);
        if (nle!=null && nle.getLength() > 0){
            elast = (Element) nle.item(0);
            this._editor   =  elast.getTextContent();
        }

        this._lastVistoBuenoUZDate = null;
        nle = docEle.getElementsByTagName(this._lastVistoBuenoDateIdentifier);
        if (nle!=null && nle.getLength() > 0){
            elast = (Element) nle.item(0);
            this._lastVistoBuenoUZDate   =  elast.getTextContent();
        }

        this._editorVistoBueno = null;
        nle = docEle.getElementsByTagName(this._lastVistoBuenoEditorIdentifier);
        if (nle!=null && nle.getLength() > 0){
            elast = (Element) nle.item(0);
            this._editorVistoBueno   =  elast.getTextContent();
        }


	//get a nodelist of <error> elements
        NodeList nl = docEle.getElementsByTagName("p");
	if(nl != null && nl.getLength() > 0) {
        for (int i=0; i<nl.getLength(); i++){
                Element el, es;
                NodeList ns;


		el = (Element)nl.item(i);

                String begintime = el.getAttribute("begin");
                String endtime   = el.getAttribute("end");
                String text      = el.getTextContent();

                String vtext[] = text.split(this.NEW_LINE_CHARACTER);
                if (vtext.length==0) continue;


                //-- Si hay una linea sin texto, el subtitulo se descarta
                //-- Esto ocurre en la última línea de los XML por cortesía de FAB ¿ ?
                int k;
                for (k=0; k<vtext.length; k++) if (vtext[k].length()>0) break;
                if (k==vtext.length) break;

                //-- Calculamos el offset inicial
                /*if (this.initTime >= 0){
                if (i==0){
                    this.secondsOffset = getOffsetFromTime(begintime, this.initTime);
                }}*/

                //-- Generamos tiempos con offset
                if (this._useOffsetTimeFromFile){
                if (this.initTime >= 0){
                    begintime = computeOffsetString  (begintime, this.initTime/*this.secondsOffset*/);
                    endtime   = computeOffsetString  (endtime,   this.initTime/*this.secondsOffset*/);
                }}
                //--


                //--
                if (intTime(endtime) <= intTime(begintime)){
                    endtime = computeOffsetString  (begintime, 3);
                }
                //--
            
                Subtitle sub = new Subtitle(vtext, begintime, endtime);

                vsub.add(sub);

        }}



        return vsub;
    }

    private String preprocess_contents(String contents) {
        contents = contents.replaceAll(this.NEW_LINE_CHARACTER_XML, this.NEW_LINE_CHARACTER);
        return contents;
    }

    private String getHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
                          "    <tt xml:lang=\"es\" xmlns=\"http://www.w3.org/2006/04/ttaf1\" xmlns:tts=\"http://www.w3.org/2006/10/ttaf1#style\">\r\n" +
                          "    <" + _lastEditionDateIdentifier   + ">" + Subtitle.getCurrentDate() + "</" + _lastEditionDateIdentifier + ">\r\n" +
                          "    <" + _lastEditionEditorIdentifier + ">" + this._editor              + "</" + _lastEditionEditorIdentifier + ">\r\n" +
                          (this._vistobuenoflag ? 
                          "    <" + this._lastVistoBuenoDateIdentifier   +  ">"   + Subtitle.getCurrentDate() +   "</" + this._lastVistoBuenoDateIdentifier   + ">\r\n" +
                          "    <" + this._lastVistoBuenoEditorIdentifier +  ">"   + this._editorVistoBueno     +   "</" + this._lastVistoBuenoEditorIdentifier + ">\r\n"
                          : "") +
                          "    <head>\r\n" +
                          "          <styling>\r\n" +
                          "              <style id=\"c1\" tts:color=\"#FFFFFF\" tts:textAlign=\"center\" />\r\n" +
                          "          </styling>\r\n" +
                          "    </head>\r\n" +
                          "    <body>\r\n" +
                          "          <div xml:lang=\"es\" style=\"c1\">\r\n";
    }

    private String getTail() {
        return "            </div>\r\n" +
               "     </body>\r\n" +
               "</tt>\r\n";

    }

    private void getTimeOffsetFromFilename(String filename) {
        this.initTime = -1;
        filename = filename.replaceAll("\\\\", "_");
        String regex = "^(.+)-(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)-(\\d\\d)-(\\d\\d)-(\\d\\d)(.+)$";
        if (filename.matches(regex)){
            String h = filename.replaceAll(regex, "$5");
            String m   = filename.replaceAll(regex, "$6");
            String s  = filename.replaceAll(regex, "$7");

            this.initTime =  Integer.parseInt(s) + Integer.parseInt(m)*60 + Integer.parseInt(h)*60*60;

        }

    }

    private int getOffsetFromTime(String begintime, int initTime) {
        String vx[] = begintime.split("\\.");
        int ibegintime = Integer.parseInt(vx[0]);
        return (initTime - ibegintime);
    }

    private String computeOffsetString(String time, int secondsOffset) {
        String vx[] = time.split("\\.");
        int itime = Integer.parseInt(vx[0]);
        int outtime = itime + secondsOffset;
        String ovx0 = String.valueOf(outtime);
        String out = ovx0 + "." + vx[1];

        return out;
    }
    
    private int intTime(String time){
        String vx[] = time.split("\\.");
        int secs = Integer.parseInt(vx[0]);
        int milisecs = Integer.parseInt(vx[1]);
        
        return 1000 * secs + milisecs; 
    }

    public void setOffsetTimeFromFile(boolean value) {
        this._useOffsetTimeFromFile = value;
    }

    public String getLastEditionDate() {
        return this._lastEditionDate;
    }

    public void setEditor(String editorname) {
        this._editor = editorname;
        this._editorVistoBueno = editorname;
    }

    public void setVistoBueno(boolean validate) {
        this._vistobuenoflag = validate;
    }

    public String getLastVistoBuenoDate() {
        return this._lastVistoBuenoUZDate;
    }

    public String getEditorVistoBueno() {
        return this._editorVistoBueno;
    }

    public String getEditor() {
       return this._editor;
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
