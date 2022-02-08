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
import java.util.Locale;
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
public class SubtitleSetIOCIRESXML implements SubtitleSetIO{
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

    public SubtitleSetIOCIRESXML(){
        _useOffsetTimeFromFile = false;
        
        _fileCoding = "UTF8";
    }

    public ArrayList<Subtitle> read(String filename) throws Exception {

        String contents = IOUtils.readFile(filename, _fileCoding);

        contents = preprocess_contents(contents);

        getTimeOffsetFromFilename(filename);

        ArrayList <Subtitle> vsub = parse_ciresxml_subtitles(contents);

        return vsub;


    }

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {

        String contents = getHeader();

        for (Subtitle sub: vsubtitles){
            float t0Milis = sub.getTimeIniFloat() * 1000.0F;
            float t1Milis = sub.getTimeEndFloat() * 1000.0F;
            String txt = sub.getAllLines();
            contents += "   <subtitle>\n" + 
                        "     <text>" + txt + "</text>\n" + 
                        "     <t0>" + String.format("%.0f", t0Milis) + "</t0>\n" + 
                        "     <t1>" + String.format("%.0f", t1Milis) + "</t1>\n" +
                        "   </subtitle>\n";          
        }

        contents += getTail();

        IOUtils.writeFile(filename, contents, _fileCoding);

    }


    private ArrayList<Subtitle> parse_ciresxml_subtitles(String contents) throws Exception {
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
/*
<subtitle>
    <text>El Rayo Cósmico</text>
    <t0>1020</t0>
    <t1>2045</t1>
  </subtitle>
*/
	//get a nodelist of <subtitle> elements
        NodeList nl = docEle.getElementsByTagName("subtitle");
	if(nl != null && nl.getLength() > 0) {
        for (int i=0; i<nl.getLength(); i++){
                Element el, es;
                NodeList ns;
                String vtext[]=null, txt, t0=null,t1=null;


		el = (Element)nl.item(i);

                ns = el.getElementsByTagName("text");
                if(ns != null && ns.getLength() > 0) {
                    es = (Element) ns.item(0);
                    txt = es.getTextContent();
                    vtext = txt.split("\n");
                }
                
                ns = el.getElementsByTagName("t0");
                if(ns != null && ns.getLength() > 0) {
                    es = (Element) ns.item(0);
                    t0 = es.getTextContent();
                }
                
                ns = el.getElementsByTagName("t1");
                if(ns != null && ns.getLength() > 0) {
                    es = (Element) ns.item(0);
                    t1 = es.getTextContent();
                }

                if (vtext==null) throw new Exception ("text not found in subtitle number (" + i + "/" + nl.getLength() + ")");
                if (t0==null)    throw new Exception ("t0   not found in subtitle number (" + i + "/" + nl.getLength() + ")");
                if (t1==null)    throw new Exception ("t1   not found in subtitle number (" + i + "/" + nl.getLength() + ")");
            
                float ft0 = Float.valueOf(t0);
                float ft1 = Float.valueOf(t1);
                
                ft0 /= 1000.0F;
                ft1 /= 1000.0F;
                
                Subtitle sub = new Subtitle(vtext, String.format(Locale.US, "%.03f", ft0), String.format(Locale.US, "%.03f", ft1));

                vsub.add(sub);

        }}



        return vsub;
    }

    private String preprocess_contents(String contents) {
        contents = contents.replaceAll(this.NEW_LINE_CHARACTER_XML, this.NEW_LINE_CHARACTER);
        return contents;
    }

    private String getHeader() {
        return "<xmlpacket>\n";
    }

    private String getTail() {
        return  "</xmlpacket>\n";
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
