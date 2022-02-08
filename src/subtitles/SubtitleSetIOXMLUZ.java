/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package subtitles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;
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
public class SubtitleSetIOXMLUZ implements SubtitleSetIO{

    private boolean needsHeader;
    private String _fileCoding;
    private boolean _useOffsetTimeFromFile;
    private static final int minNLinesUZ=2;

    public SubtitleSetIOXMLUZ(boolean b) {
        needsHeader = b;
        _useOffsetTimeFromFile = false;
        _fileCoding = "LATIN1";
    }

    public SubtitleSetIOXMLUZ() {
        needsHeader = false;
        _useOffsetTimeFromFile = false;
        _fileCoding = "LATIN1";
    }



    public ArrayList<Subtitle> read(String filename) throws Exception {

        String contents = IOUtils.readFile(filename, _fileCoding);

        contents = preprocess_contents(contents);

        ArrayList <Subtitle> vsub = parse_xml_uz_subtitles(contents);

        return vsub;


    }

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {

        String contents = SubtitleSetIOXMLUZ.toStringUZ(vsubtitles);

        IOUtils.writeFile(filename, contents, _fileCoding);

    }

    public static String toStringUZ(SubtitleSet ss){
        return toStringUZ(ss.getVsubtitles());
    }
    
    public static String toStringUZ(ArrayList <Subtitle> vsubtitles){
        String contents = getHeader() + getTimePacket() + getHeaderMethod();
        int i, len = vsubtitles.size();

        for (i=0; i<len; i++){
            Subtitle sub  = vsubtitles.get(i);
            contents +=  sub.toXMLUZ(minNLinesUZ) + "\r\n";
            if (i!=len-1){
                //-- No es el ultimo subtitulo
                Subtitle sub1 = vsubtitles.get(i+1);
                if (!sub.endTimeEqualsIniTime(sub1)){
                    contents += sub.toXMLUZEmpty(minNLinesUZ) + "\r\n";
                }
            }else{
                //-- Ultimo subtitulo, se añade el paquete de borrado
                contents += sub.toXMLUZEmpty(minNLinesUZ) + "\r\n";
            }
            
        }

        contents += getTail();
        
        return contents;
    }

    public static Vector<String> toVectorStringUZ(SubtitleSet ss) {
        ArrayList <Subtitle> _vsubtitles = ss.getVsubtitles();
        int len = _vsubtitles.size();

        Vector <String> vsub = new Vector();

        for (int i=0; i<len; i++){
            Subtitle sub  = _vsubtitles.get(i);
            String txtsub = sub.toStringUZ(minNLinesUZ);
            vsub.add(txtsub);
            if (i!=len-1){
                //-- No es el ultimo subtitulo
                Subtitle sub1 = _vsubtitles.get(i+1);
                if (!sub.endTimeEqualsIniTime(sub1)){
                    String emptysub = sub.toStringUZEmpty(minNLinesUZ);
                    vsub.add(emptysub);
                }
            }else{
                //-- Ultimo subtitulo, se añade el paquete de borrado
                txtsub = sub.toStringUZEmpty(minNLinesUZ);
                vsub.add(txtsub);
            }

        }


        return vsub;
    }



    private ArrayList<Subtitle> parse_xml_uz_subtitles(String contents) throws Exception {
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

	//get a nodelist of <ide> elements
	NodeList nl = docEle.getElementsByTagName("subtitle");
	if(nl != null && nl.getLength() > 0) {
		for(int i = 0 ; i < nl.getLength()-1;i++) {

                                String line1="",line2="",time="";
                                String time_n="";


				Element el = (Element)nl.item(i);

                                NodeList n1 = el.getElementsByTagName("line1");
                                if (n1!=null && n1.getLength() > 0){
                                    Element e0 = (Element) n1.item(0);
                                    line1 = e0.getTextContent();
                                }
                                NodeList n2 = el.getElementsByTagName("line2");
                                if (n2!=null && n2.getLength() > 0){
                                    Element e0 = (Element) n2.item(0);
                                    line2 = e0.getTextContent();
                                }
                                NodeList n3 = el.getElementsByTagName("time");
                                if (n3!=null && n3.getLength() > 0){
                                    Element e0 = (Element) n3.item(0);
                                    time = e0.getTextContent();
                                }

                                if (i!= nl.getLength()-1){
                                Element el1 = (Element) nl.item(i+1);
                                n3 = el1.getElementsByTagName("time");
                                if (n3!=null && n3.getLength() > 0){
                                    Element e0 = (Element) n3.item(0);
                                    time_n = e0.getTextContent();
                                }
                                }


                                if ((line1.length()==0) && (line2.length()==0)){
                                    //Subtitulo vacio, es un paquete de borrado
                                    continue;
                                }
                                

                                //String all = time + "  -  " + line1 + " ## " + line2;

                                String vtext[] = new String[2];
                                vtext[0] = line1;
                                vtext[1] = line2;
                                
                                Subtitle sub = new Subtitle(vtext, time, time_n, true);

                                vsub.add(sub);
                }
        }

        return vsub;
    }

    private String preprocess_contents(String contents) {
        if (needsHeader){
            contents = getHeader() + getHeaderMethod() + contents + getTail();
        }
        return contents;
    }

    private static String getHeader() {
        return "<subtitlesuz>\r\n";
        
        //"<?xml version=\"1.0\" encoding=\"UTF-8\">\r\n" +
               //"<subtitlesuz>\r\n"    ;
    }

    private static String getTimePacket(){
        return "<time>" + java.util.Calendar.getInstance().getTime() + "</time>\r\n";
    }
    
    private static String getHeaderMethod() {
        return "<method>load_subtitles_from_external_file</method>\r\n";
    }

    private static String getTail() {
        return  "</subtitlesuz>\r\n";
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
