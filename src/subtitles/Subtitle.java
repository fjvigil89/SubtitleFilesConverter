/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package subtitles;

import java.awt.Color;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jegarcia
 */
public class Subtitle {
    private int _subtitleSTLNumber;

    //-- Subtitle information for any format
    private String  _vlines[];
    private String _timeini, _timeend;  //"secs.milisecs"
    private Color  _fgcolor; //Foreground color
    private ArrayList <Color> _fgcolorlines;
    private Color  _bgcolor; //Background color
    private ArrayList <Color> _bgcolorlines;

    private double _timeinidouble, _timeenddouble; 
    private int    _height_subtitle; //subtitle height(vertical position)

    //-- Subtitle information for STL subtitles
    private final byte TEXTO_NEGRO    =	0x00;
    private final byte TEXTO_ROJO     = 0x01;
    private final byte TEXTO_VERDE    =	0x02;
    private final byte TEXTO_AMARILLO =	0x03;
    private final byte TEXTO_AZUL     =	0x04;
    private final byte TEXTO_MAGENTA  =	0x05;
    private final byte TEXTO_CYAN     =	0x06;
    private final byte TEXTO_BLANCO   =	0x07;

    public static final byte JUSTIFICATION_UNCHANGED = 0x00;
    public static final byte JUSTIFICATION_LEFT      = 0x01;
    public static final byte JUSTIFICATION_CENTRED   = 0x02;
    public static final byte JUSTIFICATION_RIGHT     = 0x03;
    
    private final byte FIN_RECUADRO    = 0x0a;
    private final byte INICIO_RECUADRO = 0x0b;
    private final byte ITALICS_ON      = (byte) 0x80;
    private final byte ITALICS_OFF     = (byte) 0x80;
    private final byte UNDERLINE_ON    = (byte) 0x80;
    private final byte UNDERLINE_OFF   = (byte) 0x80;
    private final byte BOXING_ON       = (byte) 0x80;
    private final byte BOXING_OFF      = (byte) 0x80;


    private final byte ALTURA_NORMAL       =	0x0c;
    private final byte ALTURA_DOBLE        =	0x0d;

    private final byte FONDO_NEGRO	   =	0x10;
    private final byte FONDO_ROJO	   =	0x11;
    private final byte FONDO_VERDE	   =	0x12;
    private final byte FONDO_AMARILLO	   =    0x13;
    private final byte FONDO_AZUL	   =	0x14;
    private final byte FONDO_MAGENTA	   =    0x15;
    private final byte FONDO_CYAN	   =	0x16;
    private final byte FONDO_BLANCO	   =    0x17;
    
    private final String STR_NEGRO	   =	"negro";
    private final String STR_ROJO	   =	"rojo";
    private final String STR_VERDE	   =	"verde";
    private final String STR_AMARILLO	   =    "amarillo";
    private final String STR_AZUL	   =	"azul";
    private final String STR_MAGENTA	   =    "magenta";
    private final String STR_CYAN	   =	"cyan";
    private final String STR_BLANCO	   =    "blanco";

    private final byte FILLER_CHARACTER        = (byte) 0x8F;
    private final byte NEWLINECHARACTER        = (byte) 0x8a;
    private final byte USER_SUBTITLE_STL_CODE  = (byte) 0xFE;
    private final byte EBN_SUBTITLE_STL_CODE   = (byte) 0xFF;
    
    private final byte CS_SUBTITLE_NOT_PART_OF_CUMULATIVE_SET = (byte) 0x00;

    private final byte TILDE_CHARACTER           =    (byte) 0xC2;
    private final byte TILDE_GRAVE_CHARACTER     =    (byte) 0xC1;
    private final byte ENE_CHARACTER             =    (byte) 0xC4;
    private final byte DIERESIS_CHARACTER        =    (byte) 0xC8;
    //private final byte NBSP_CHARACTER          =    (byte) 0x0A;
    private final byte SOMB_CHARACTER            =    (byte) 0xC3;
    private final byte CEDILLA_CHARACTER         =    (byte) 0xCB;
    private final byte GRADO_CHARACTER           =    (byte) 0xB0;    // caracter grado
    private final byte SUP_o_CHARACTER           =    (byte) 0xEB;    // caracter ??
    private final byte SUP_a_CHARACTER           =    (byte) 0xE3;    // caracter ??
    private final byte DOLAR_CHARACTER           =    (byte) 0xA4;    // caracter $

    private final Color DEFAULT_COLOR      =    Color.white;
    private final Color DEFAULT_BKG_COLOR  =    Color.black;
    
    private final byte NEW_BACKGROUND	   =    0x1D; // nuevo fondo, por defecto blanco
    
    private final short _lenHeaderTTIsubtitle    = 16;
    //private final short _lenTextFieldTTIsubtitle = 112;
    private short _lenTextFieldTTIsubtitle = 112;
    private byte _headerTTIsubtitle[];
    private byte _textFieldTTIsubtitle[];
    private int _framespersecond;
    private byte _characterset;
    private boolean _isalturadoble;
    private int _colorfg;
    private int _colorbg;
    private boolean _usersubtitle;
    private int _numberOfBytesByLine;
    private boolean _isSTLsubtitle;
    private int nt;
    private byte _justification_subtitle;
    private boolean _extendedsubtitle;
    





    public Subtitle(){
        _isSTLsubtitle = false;
        _fgcolorlines = new ArrayList();
        _bgcolorlines = new ArrayList();
    }

    //-- Creating a subtitle from XML format Time (timeini = secs.miliseconds)
    public Subtitle(String vlines[], String timeini, String timeend){
      _vlines  = vlines;
      _timeini = formatTime(timeini);
      _timeend = formatTime(timeend);
      _isSTLsubtitle = false;
      _fgcolor   = DEFAULT_COLOR;
      _bgcolor   = DEFAULT_BKG_COLOR;
      _fgcolorlines = new ArrayList();
      _bgcolorlines = new ArrayList();
      for (int i=0; i<Math.max( _vlines.length, 2); i++){
          _fgcolorlines.add(this.DEFAULT_COLOR);
          _bgcolorlines.add(this.DEFAULT_BKG_COLOR);
      }
      _justification_subtitle = this.JUSTIFICATION_CENTRED;
    }
    
    //-- Creating a subtitle from XML format Time (timeini = secs.miliseconds) and adding color
    public Subtitle(String vlines[], String timeini, String timeend, Color foregroundcolor){
      _vlines  = vlines;
      _timeini = formatTime(timeini);
      _timeend = formatTime(timeend);
      _isSTLsubtitle = false;
      _fgcolor  =  foregroundcolor;
      _bgcolor   = DEFAULT_BKG_COLOR;
      _fgcolorlines = new ArrayList();
      _bgcolorlines = new ArrayList();
      for (int i=0; i<Math.max( _vlines.length, 2); i++){
          _fgcolorlines.add(this.DEFAULT_COLOR);
          _bgcolorlines.add(this.DEFAULT_BKG_COLOR);
      }
      _justification_subtitle = this.JUSTIFICATION_CENTRED;
    }
    
    //-- Creating a subtitle from XML format Time (timeini = secs.miliseconds) and adding foreground and background colors (for each line)
    public Subtitle(String vlines[], String timeini, String timeend, ArrayList foregroundcolor, ArrayList backgroundcolor){
      _vlines  = vlines;
      _timeini = formatTime(timeini);
      _timeend = formatTime(timeend);
      _isSTLsubtitle = false;
      _fgcolor  = DEFAULT_COLOR;
      _bgcolor  = DEFAULT_BKG_COLOR;
      _fgcolorlines = foregroundcolor;
      _bgcolorlines = backgroundcolor;
      _justification_subtitle = this.JUSTIFICATION_CENTRED;
    }

    //-- Creating a subtitle from XML UZ format Time (timeini = hh:mm:ss:mmm)
    public Subtitle(String vlines[], String timeini, String timeend, boolean timehours){
      _vlines  = vlines;

      if (!timehours){
          _timeini = formatTime(timeini);
          _timeend = formatTime(timeend);
      }else{
          //from "hh:mm:ss:mmm" to "secs.milisecs"
          _timeini = fromTimeToSeconds(timeini);
          if (timeend.length()>0)  _timeend = fromTimeToSeconds(timeend);
          else                     _timeend = addMinTimeSubtitle(_timeini);
      }

      _isSTLsubtitle = false;

      _fgcolor = DEFAULT_COLOR;
      
      _bgcolor = DEFAULT_BKG_COLOR;
      
      _fgcolorlines = new ArrayList();
      
      _bgcolorlines = new ArrayList();
      
      for (int i=0; i<Math.max( _vlines.length, 2); i++){
          _fgcolorlines.add(this.DEFAULT_COLOR);
          _bgcolorlines.add(this.DEFAULT_BKG_COLOR);
      }
      _justification_subtitle = this.JUSTIFICATION_CENTRED;
    }
    
    //-- Creating a subtitle from XML UZ format Time (timeini = hh:mm:ss:mmm) and adding color (for each line)
    public Subtitle(String vlines[], String timeini, String timeend, boolean timehours, ArrayList foregroundcolor){
      _vlines  = vlines;

      if (!timehours){
          _timeini = formatTime(timeini);
          _timeend = formatTime(timeend);
      }else{
          //from "hh:mm:ss:mmm" to "secs.milisecs"
          _timeini = fromTimeToSeconds(timeini);
          if (timeend.length()>0)  _timeend = fromTimeToSeconds(timeend);
          else                     _timeend = addMinTimeSubtitle(_timeini);
      }

      _isSTLsubtitle = false;

      _fgcolor = DEFAULT_COLOR;
      
      _bgcolor = DEFAULT_BKG_COLOR;
      
      _fgcolorlines = foregroundcolor;
      
      _bgcolorlines = new ArrayList();
      for (int i=0; i<Math.max( _fgcolorlines.size(), 2); i++){
          _bgcolorlines.add(this.DEFAULT_BKG_COLOR);
      }
      _justification_subtitle = this.JUSTIFICATION_CENTRED;
    }

    //-- Creating a subtitle from STL format (128 bytes), a fps param, and the characterset
    public Subtitle(byte[] blockTTI, int framespersecond, byte characterset, int numberOfBytesByLine, boolean lastextended) throws Exception {
        if (characterset != 0x30){
            throw new Exception ("Unwknown character set (only available 0x30 Latin)");
        }

        _fgcolorlines = new ArrayList();
        
        _bgcolorlines = new ArrayList();
        
        this._isSTLsubtitle   = true;
        this._characterset    = characterset;
        this._framespersecond = framespersecond;
        this._numberOfBytesByLine = numberOfBytesByLine;

        this._headerTTIsubtitle    = new byte[this._lenHeaderTTIsubtitle];
        this._textFieldTTIsubtitle = new byte[this._lenTextFieldTTIsubtitle];

        //-- Copy the header (16 bytes)
        System.arraycopy(blockTTI, 0, this._headerTTIsubtitle, 0, this._lenHeaderTTIsubtitle);
        
        //-- Copy the text information (112 bytes)
        System.arraycopy(blockTTI, this._lenHeaderTTIsubtitle, this._textFieldTTIsubtitle, 0, this._lenTextFieldTTIsubtitle);

        //-- if user subtitle it is not processed
        if (this._headerTTIsubtitle[3] == this.USER_SUBTITLE_STL_CODE){ //default user subtitle
            this._usersubtitle = true;
            return;
        }else{
            this._usersubtitle = false;
        }
        
        //-- verify if extended subtitle
        if (this._headerTTIsubtitle[3] != this.EBN_SUBTITLE_STL_CODE){ 
            this._extendedsubtitle = true;
        }else{
            this._extendedsubtitle = false;
        }
     
        //-- Setting default STL values (altura, colorfg, colorbg)
        setDefaultSTLValues();

        //-- Creating vlines from bytestream
        if (!this._extendedsubtitle && !lastextended) decodeTextSTL();

        //-- Creating _timeini, _timeend from bytestream
        decodeTimeSTL();
        
        //-- Creating _height_subtitle
        decodeHeightSTL();
        
        //-- Getting justification code
        decodeJustificationCodeSTL();
        

        

    }

    public String toString(){
       return "";
    }

    public String toXML(){
        String sub =  "<p begin=\"" + _timeini + "\" end=\"" + _timeend + "\" " + this.getXMLColorString() + ">";
        int len = _vlines.length;
        for (int k=0; k<len; k++){
            String line = _vlines[k];
            sub = sub + line;
            if (k != len-1) sub = sub + SubtitleSetIOXML.NEW_LINE_CHARACTER_XML;
        }
        sub = sub + "</p>";

        return sub;
    }
    
    
    public String toSRT(int number) {
        String sub = String.valueOf(number);
        sub += "\r\n";
        sub += this.fromSecondsToTimeSRT(this._timeini);
        sub += " --> ";
        sub += this.fromSecondsToTimeSRT(this._timeend);
        sub += "\r\n";
        //sub += this.getSRTColorString();
        for (int i=0; i<this._vlines.length; i++){
            
            //sub += this._vlines[i] + "\r\n";
            sub += this.getLineSRTColorString(i) + "\r\n";
        } 
        //if (this._fgcolor!=this.DEFAULT_COLOR){
        //    sub = sub.trim();
        //    sub += "</font>\r\n";
        //}
        
        sub += "\r\n";
        
        return sub;
    }
    

    public String getAllLines() {
        String sub = "";

        int len = _vlines.length;

        for (int k=0; k<len; k++){
            String line = _vlines[k];
            sub = sub + line;
            if (k!=len-1) sub = sub + "\r\n";
        }
        return sub;
    }

    public String getLines(int nlines){
        String sub = "";

        int len = Math.min(_vlines.length,nlines);
        
        for (int k=0; k<len; k++){
            String line = _vlines[k];
            sub = sub + line;
            if (k != len-1) sub = sub + "\r\n";
        }
        if (len==1) sub = sub + "\r\n";
        return sub;
    }

    public int getNLines(){
        return this._vlines.length;
    }

    public String getTimeIni() {
        return this._timeini;
    }

    public String getTimeEnd() {
        return this._timeend;
    }
    
    public float getTimeIniFloat() {
        return Float.parseFloat(this._timeini);
    }

    public float getTimeEndFloat() {
        return Float.parseFloat(this._timeend);
    }

    public String toTXT() {
        String sub =  _timeini + "  " + _timeend + "  ";
        int len = _vlines.length;
        for (int k=0; k<len; k++){
            String line = _vlines[k];
            sub = sub + line;
            if (k != len-1) sub = sub + SubtitleSetIOTXT.NEW_LINE_CHARACTER_TXT + " ";
        }
        sub = sub + SubtitleSetIOTXT.NEW_SUBTITLE_CHARACTER_TXT;

        return sub;
    }
    
    public String toTXTFAB() {
           
        String sub =  _timeini + "  " + _timeend + "  " + this.getDuration(_timeini,_timeend) + "\r\n";
        int len = _vlines.length;
        for (int k=0; k<len; k++){
            String line = _vlines[k];
            sub = sub + line;
            if (k != len-1) sub = sub + "\r\n";
        }

        return sub;
    }

    private String filterXMLCharactersFromLine(String line){
        
        String out = line;
        
        out = out.replaceAll("&", "&amp;");
        out = out.replaceAll("<", "&lt;");
        out = out.replaceAll(">", "&gt;");   
        out = out.replaceAll("\"", "&quot;");
        out = out.replaceAll("'", "&apos;");
 
        return out;
    }
    
    public String toXMLUZ(int min_nlines) {
        String sub =  "<subtitle>\r\n";
        int len = _vlines.length;
        for (int k=0; k<len; k++){
            String line = filterXMLCharactersFromLine(_vlines[k]);
            sub = sub + "<line" + (k+1) + ">" + line + "</line" + (k+1) + ">\r\n";
        }

        for (int k=len; k<min_nlines; k++){
            sub = sub + "<line" + (k+1) + "></line" + (k+1) + ">\r\n";
        }

        sub = sub + "<foreground_color>" + this.getColorNameFromContinuousColor(this._fgcolorlines.get(0)) + "</foreground_color>\r\n";
        sub = sub + "<background_color>" + this.getColorNameFromContinuousColor(this._bgcolorlines.get(0)) + "</background_color>\r\n";
        
        if (this._isSTLsubtitle)
            sub = sub + "<height>" + Integer.toString(this.getHeight()) + "</height>\r\n";
        else
            sub = sub + "<height>-1</height>\r\n";
        
        sub = sub + "<justification>" + this.getJustification().toString() + "</justification>\r\n";
        
        //from "secs.milisecs" to "hh:mm:ss:mmm"
        sub = sub + "<time>" + this.fromSecondsToTime(this._timeini) + "</time>\r\n";
        sub = sub + "</subtitle>";

        return sub;
    }


    public String toXMLUZEmpty(int nlines){
        String sub = "<subtitle>\r\n";
        int len = nlines;
        for (int k=0; k<len; k++){
            sub = sub + "<line" + (k+1) + "></line" + (k+1) + ">\r\n";
        }
        sub = sub + "<foreground_color>" + this.STR_BLANCO + "</foreground_color>\r\n";
        sub = sub + "<background_color>" + this.STR_NEGRO + "</background_color>\r\n";
        sub = sub + "<height>-1</height>\r\n";
        sub = sub + "<justification>" + this.JUSTIFICATION_CENTRED + "</justification>\r\n";
        sub = sub + "<time>" + this.fromSecondsToTime(this._timeend) + "</time>\r\n";
        sub = sub + "</subtitle>";

        return sub;
    }


    public String toStringUZ(int min_nlines) {
        String all = this.fromSecondsToTime(this._timeini) + "  -  ";
        for (int k=0; k<this._vlines.length-1; k++){
            all += this._vlines[k] + " ## ";
        }
        all += this._vlines[this._vlines.length-1];

        for (int k=this._vlines.length; k<min_nlines; k++){
            all += " ## ";
        }

        return all;
    }

    public String toStringUZEmpty(int nlines) {
        String all = this.fromSecondsToTime(this._timeend) + "  -  ";
        for (int k=0; k<nlines-1; k++){
            all += " ## ";
        }
        return all;
    }

    public boolean endTimeEqualsIniTime(Subtitle sub1) {

        String endTime = this.getTimeEnd();
        String iniTime = sub1.getTimeIni();

        int iendtime = this.getMiliSeconds(endTime);
        int iinitime = this.getMiliSeconds(iniTime);

        if (iinitime-iendtime < 300){ //300 milisegundos
            return true;
        }else{
            return false;
        }
    }

    private String fromTimeToSeconds(String timehour) {
        String vx[] = timehour.split(":");
        int ih,im,is,in,os,on;
        String son;
        int c=0;
        ih = Integer.parseInt(vx[c++]);
        im = Integer.parseInt(vx[c++]);
        is = Integer.parseInt(vx[c++]);
        //in = Integer.parseInt(vx[c++]);
        if (vx.length>3) son = vx[c++];
        else             son = "000";
        
        os = is + im*60 + ih*60*60;
        //on = in;

        String out = os + "." + son;

        out = formatTime(out);

        return out;
    }

    public String fromSecondsToTime(String timeseconds) {
        String vx[] = timeseconds.split("\\.");
        int ih,im,is,in,os,on;
        int c=0;
        os = Integer.parseInt(vx[c++]);
       
        is = os % 60;
        im = os / 60;
        ih = im / 60;
        im = im % 60;

        ih = ih%24;

        String sih = Integer.toString(ih); if (sih.length()==1) sih = "0" + sih;
        String sim = Integer.toString(im); if (sim.length()==1) sim = "0" + sim;
        String sis = Integer.toString(is); if (sis.length()==1) sis = "0" + sis;
        String sin = vx[1];



        String out = sih + ":" + sim + ":" + sis + ":" + sin;

        return out;
    }
    
    private String fromSecondsToTimeSRT(String timeseconds) {
        String vx[] = timeseconds.split("\\.");
        int ih,im,is,in,os,on;
        int c=0;
        os = Integer.parseInt(vx[c++]);
       
        is = os % 60;
        im = os / 60;
        ih = im / 60;
        im = im % 60;

        ih = ih%24;

        String sih = Integer.toString(ih); if (sih.length()==1) sih = "0" + sih;
        String sim = Integer.toString(im); if (sim.length()==1) sim = "0" + sim;
        String sis = Integer.toString(is); if (sis.length()==1) sis = "0" + sis;
        String sin = vx[1];



        String out = sih + ":" + sim + ":" + sis + "," + sin;

        return out;
    }

    private int getMiliSeconds(String time) {
        String vx[] = time.split("\\.");
        int sec  = Integer.parseInt(vx[0]);
        int milisec = Integer.parseInt(vx[1]);

        return 1000*sec + milisec;
    }

    private String addMinTimeSubtitle(String time) {
        String vx[] = time.split("\\.");
        int os,on;
        String son;
        int c=0;
        os = Integer.parseInt(vx[c++]) + 3;
        son = vx[c++];
        String out = os + "." + son;
        return out;
    }

    public boolean isAfterSubtitle(Subtitle slast) {
        int t0 = getMiliSeconds(this.getTimeIni());
        int t1 = getMiliSeconds(slast.getTimeIni());
        return (t0 > t1);
    }
    
    public boolean isBeforeSubtitle(Subtitle slast) {
        int t0 = getMiliSeconds(this.getTimeIni());
        int t1 = getMiliSeconds(slast.getTimeIni());
        return (t0 < t1);
    }

    private String formatTime(String time) {
        String vx[] = time.split("\\.");
        String milisecs = vx[1];

        if (vx[1].length()==1) milisecs =  milisecs + "00";
        if (vx[1].length()==2) milisecs =  milisecs + "0";

        String out = vx[0] + "." + milisecs;
        return out;
    }


    private void setDefaultSTLValues() {
        this._isalturadoble = false;
        this._colorbg = -1;
        this._colorfg = -1;
        _bgcolor   = DEFAULT_BKG_COLOR;
        _fgcolor   = DEFAULT_COLOR;
    }

    /*private boolean isInicioRecuadro(byte b){
        
        if ((b==this.INICIO_RECUADRO)||(b==this.ITALICS_ON)||
            (b==this.ITALICS_OFF)||(b==this.UNDERLINE_ON)||
            (b==this.UNDERLINE_OFF)||(b==this.BOXING_ON)||
            (b==this.BOXING_OFF)){
            return true;
        }else{
            return false;
        }

    }*/
    
    
    public void decodeTextSTL() throws Exception {
        int i,k;

        int lastline=-1;
        
        _fgcolor = null;
        _bgcolor = null;
        
        //-- Found initial INICIO_RECUADRO
        for (i=0; i<this._lenTextFieldTTIsubtitle; i++){
            byte b = this._textFieldTTIsubtitle[i];
            byte b1 = 0x00;
            if (i<this._lenTextFieldTTIsubtitle-1)
                b1 = this._textFieldTTIsubtitle[i+1];
            
            //System.out.println(Integer.toHexString(b));
            if (b==this.ALTURA_DOBLE) this._isalturadoble = true;
            
            //------ Se incluye el color antes de inicio de recuadro
            //------ ??El standard permite esto? no estamos seguros...
            /*if ((b<= 0x07) && (b>=0x00)){
                this._colorfg = b;
                this._fgcolor = this.getContinuousColorFromSTLForegroundColor(_colorfg);
                lastline = 0;
                continue;
            }*/
            
            if (((b<= 0x07) && (b>=0x00))||((b<= 0x17) && (b>=0x10))){
              
                if ((b<= 0x17) && (b>=0x10)){
                    this._colorbg = b & 0xFF; //-- (cast unsigned int)
                    this._bgcolor = this.getContinuousColorFromSTLBackgroundColor(_colorbg);
                    lastline = 0;
                    continue;  
                }
                if ((b<= 0x07) && (b>=0x00)&&(b1==this.NEW_BACKGROUND)&&(_bgcolor == null)){
                    this._colorbg = b & 0xFF; //-- (cast unsigned int)
                    this._bgcolor = this.getContinuousColorFromSTLForegroundColor(_colorbg);
                    lastline = 0;
                    continue;
                }
                if ((b<= 0x07) && (b>=0x00)){
                    this._colorfg = b;
                    this._fgcolor = this.getContinuousColorFromSTLForegroundColor(_colorfg);
                    lastline = 0;
                    continue;
                }
            }
            
            //if (b==this.INICIO_RECUADRO) break;
            if ((b==this.INICIO_RECUADRO)||(b==this.ITALICS_ON)||
            (b==this.ITALICS_OFF)||(b==this.UNDERLINE_ON)||
            (b==this.UNDERLINE_OFF)||(b==this.BOXING_ON)||
            (b==this.BOXING_OFF)||((b>= 0x20) && (b<=0x7F))) break;
            
            /*if ((b<= 0x17) && (b>=0x10)){
                //this._colorbg = b;
                this._colorbg = b & 0xFF; //-- (cast unsigned int)
                this._bgcolor = this.getContinuousColorFromSTLBackgroundColor(_colorbg);
                lastline = 0;
                continue;
            }*/
            
            if ((b==this.NEW_BACKGROUND)&&(_bgcolor == null)){
                this._colorbg = this.FONDO_BLANCO & 0xFF; //-- (cast unsigned int)
                this._bgcolor = this.getContinuousColorFromSTLBackgroundColor(_colorbg);
            }
            //--------
            //if (b==this.INICIO_RECUADRO) break;
        }

        //if (i==this._lenTextFieldTTIsubtitle){
        if (i==this._lenTextFieldTTIsubtitle){
            //throw new Exception ("Start of Box 0x0B not found in subtitle number '" + this._headerTTIsubtitle[0] + this._headerTTIsubtitle[1] + "'");
            this._usersubtitle = true;
            return;
        }

        //-- Processing text
        ArrayList <String> alines = new ArrayList();
        byte out[] = new byte[this._lenTextFieldTTIsubtitle];
        int nout=0;
        int nbytes=0;
        boolean foundnewline = false;
        boolean initiatedBox = false;

        String text = new String(this._textFieldTTIsubtitle);

        for (; i< this._lenTextFieldTTIsubtitle; i++){
            byte b = this._textFieldTTIsubtitle[i];
            byte b1 = 0x00;
            if (i<this._lenTextFieldTTIsubtitle-1)
                b1 = this._textFieldTTIsubtitle[i+1];
            
            if (initiatedBox) nbytes++;

            //-- Cuando se alcanza el fin de recuadro o el numero maximo de bytes, se crea
            //-- la linea
            if ( ((b==this.FIN_RECUADRO) &&(initiatedBox))
                              ||
                (nbytes > this._numberOfBytesByLine) )
            {
                byte odef[] = new byte[nout];
                System.arraycopy(out, 0, odef, 0, nout);
                String line = new String(odef);
                alines.add(line);
                if(_fgcolor == null) _fgcolor = DEFAULT_COLOR;
                if(_bgcolor == null) _bgcolor = DEFAULT_BKG_COLOR;
                _fgcolorlines.add(this._fgcolor);
                _bgcolorlines.add(this._bgcolor);
                
                _fgcolor = Color.white; //descomentado, ahora si para cada linea no se especifica color se pone en blanco
                foundnewline = false;
                initiatedBox = false;
                nout = 0;
                nbytes = 0;
                continue;
            }
            if (b==this.NEWLINECHARACTER){
                foundnewline = true;
                //-- si no se ha creado la linea antes, la creamos
                if (alines.size() == 0){
                    byte odef[] = new byte[nout];
                    System.arraycopy(out, 0, odef, 0, nout);
                    String line = new String(odef);
                    alines.add(line);
                    if(_fgcolor == null) _fgcolor = DEFAULT_COLOR;
                    if(_bgcolor == null) _bgcolor = DEFAULT_BKG_COLOR;
                    _fgcolorlines.add(this._fgcolor);
                    _bgcolorlines.add(this._bgcolor);

                    _fgcolor = Color.white; //nuevo, ahora si para cada linea no se especifica color se pone en blanco
                    initiatedBox = false;
                    nout = 0;
                    nbytes = 0;
                }
                continue;
            }
            if (b==this.INICIO_RECUADRO){
                if ((alines.size() > 0) && (!foundnewline)) {
                    //throw new Exception ("End of line 0x8A not found!");
                    this._usersubtitle = true;
                    k=0;
                    this._vlines = new String[alines.size()];
                    for (String line : alines){
                        this._vlines[k++] = line;
                    }
                    return;
                }
                if (initiatedBox) nbytes++;
                else              nbytes=1;
                
                initiatedBox = true;
                continue;

            }
            //-- si es caracter imprimible (excepto espacio), iniciamos recuadro
            if ((b> 0x20) && (b<=0x7F) && !initiatedBox){
                if ((alines.size() > 0) && (!foundnewline)) {
                    //throw new Exception ("End of line 0x8A not found!");
                    this._usersubtitle = true;
                    k=0;
                    this._vlines = new String[alines.size()];
                    for (String line : alines){
                        this._vlines[k++] = line;
                    }
                    return;
                }
                
                nbytes=1;
                initiatedBox = true;
            }
            if (b==this.ALTURA_DOBLE){
                this._isalturadoble = true;
                continue;
            }
            if (b==this.ALTURA_NORMAL){
                this._isalturadoble = false;
                continue;
            }
            /*if ((b<= 0x07) && (b>=0x00)){
                int nline = alines.size();
               
                //if (nline!=lastline){
                    this._colorfg = b;
                    this._fgcolor = this.getContinuousColorFromSTLForegroundColor(_colorfg);
                    lastline = nline;
                //}   
                continue;
            }
            if ((b<= 0x17) && (b>=0x10)){
                int nline = alines.size();
                
                //if (nline!=lastline) {
                    //this._colorbg = b;
                    this._colorbg = b & 0xFF; //-- (cast unsigned int)
                    this._bgcolor = this.getContinuousColorFromSTLBackgroundColor(_colorbg);
                    lastline = nline;
                //}
                continue;
            }*/
            
            
            if (((b<= 0x07) && (b>=0x00))||((b<= 0x17) && (b>=0x10))){
              
                if ((b<= 0x17) && (b>=0x10)){
                    int nline = alines.size();
                    this._colorbg = b & 0xFF; //-- (cast unsigned int)
                    this._bgcolor = this.getContinuousColorFromSTLBackgroundColor(_colorbg);
                    lastline = nline;
                    continue;  
                }
                if ((b<= 0x07) && (b>=0x00)&&(b1==this.NEW_BACKGROUND)&&(_bgcolor == null)){
                    int nline = alines.size();
                    this._colorbg = b & 0xFF; //-- (cast unsigned int)
                    this._bgcolor = this.getContinuousColorFromSTLForegroundColor(_colorbg);
                    lastline = nline;
                    continue;
                }
                if ((b<= 0x07) && (b>=0x00)){
                    int nline = alines.size();
                    this._colorfg = b;
                    this._fgcolor = this.getContinuousColorFromSTLForegroundColor(_colorfg);
                    lastline = nline;
                    continue;
                }
             
            }
            
            
            
            if ((b==this.NEW_BACKGROUND)&&(_bgcolor == null)){
                this._colorbg = this.FONDO_BLANCO & 0xFF; //-- (cast unsigned int)
                this._bgcolor = this.getContinuousColorFromSTLBackgroundColor(_colorbg);
            }

            if ((b< 0x20) && (b>=0x00)){
                continue;
            } //Unknown characters!

            if (!initiatedBox) continue;    //Start of box not found yet!

            if (b==this.FILLER_CHARACTER){
                //continue;
                //-- final del subtitulo, a??adimos el texto encontrado a una linea y salimos
                byte odef[] = new byte[nout];
                System.arraycopy(out, 0, odef, 0, nout);
                String line = new String(odef);
                alines.add(line);
                if(_fgcolor == null) _fgcolor = DEFAULT_COLOR;
                if(_bgcolor == null) _bgcolor = DEFAULT_BKG_COLOR;
                _fgcolorlines.add(this._fgcolor);
                _bgcolorlines.add(this._bgcolor);
                
                _fgcolor = Color.white; //descomentado, ahora si para cada linea no se especifica color se pone en blanco
                foundnewline = false;
                initiatedBox = false;
                nout = 0;
                nbytes = 0;
                
                break;
            }

            //tildes! ?? ?? ?? ?? ?? ?? ?? ?? ?? ??
            if (b== this.TILDE_CHARACTER){
                byte ob[];
                byte b2 = this._textFieldTTIsubtitle[++i];
                switch (b2){
                    case 'a':
                        ob = "??".getBytes();
                        break;
                    case 'e':
                        ob = "??".getBytes();
                        break;
                    case 'i':
                        ob = "??".getBytes();
                        break;
                    case 'o':
                        ob = "??".getBytes();
                        break;
                    case 'u':
                        ob = "??".getBytes();
                        break;
                    case 'A':
                        ob = "??".getBytes();
                        break;
                    case 'E':
                        ob = "??".getBytes();
                        break;
                    case 'I':
                        ob = "??".getBytes();
                        break;
                    case 'O':
                        ob = "??".getBytes();
                        break;
                    case 'U':
                        ob = "??".getBytes();
                        break;
                    default:
                        //Unkwnown!
                        ob = " ".getBytes();
                }
                
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }

            //tildes! ?? ?? ?? ?? ?? ??
            }else if (b== this.TILDE_GRAVE_CHARACTER){
                byte ob[];
                byte b2 = this._textFieldTTIsubtitle[++i];
                switch (b2){
                    case 'a':
                        ob = "??".getBytes();
                        break;
                    case 'e':
                        ob = "??".getBytes();
                        break;
                    case 'o':
                        ob = "??".getBytes();
                        break;
                    case 'A':
                        ob = "??".getBytes();
                        break;
                    case 'E':
                        ob = "??".getBytes();
                        break;
                    case 'O':
                        ob = "??".getBytes();
                        break;
                    default:
                        //Unkwnown!
                        ob = " ".getBytes();
                }
                
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }
                
            ////?? ?? ?? ?? ?? ?? ?? ?? ?? ??
            }else if (b== this.DIERESIS_CHARACTER){
                byte ob[];
                byte b2 = this._textFieldTTIsubtitle[++i];
                switch (b2){
                    case 'a':
                        ob = "??".getBytes();
                        break;
                    case 'e':
                        ob = "??".getBytes();
                        break;
                    case 'i':
                        ob = "??".getBytes();
                        break;
                    case 'o':
                        ob = "??".getBytes();
                        break;
                    case 'u':
                        ob = "??".getBytes();
                        break;
                    case 'A':
                        ob = "??".getBytes();
                        break;
                    case 'E':
                        ob = "??".getBytes();
                        break;
                    case 'I':
                        ob = "??".getBytes();
                        break;
                    case 'O':
                        ob = "??".getBytes();
                        break;
                    case 'U':
                        ob = "??".getBytes();
                        break;
                    default:
                        //Unkwnown!
                        ob = " ".getBytes();
                }

                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }
                
            ////?? ?? ?? ?? ??
            }else if (b== this.SOMB_CHARACTER){
                byte ob[];
                byte b2 = this._textFieldTTIsubtitle[++i];
                switch (b2){
                    case 'a':
                        ob = "??".getBytes();
                        break;
                    case 'e':
                        ob = "??".getBytes();
                        break;
                    case 'i':
                        ob = "??".getBytes();
                        break;
                    case 'o':
                        ob = "??".getBytes();
                        break;
                    case 'u':
                        ob = "??".getBytes();
                        break;
                    default:
                        //Unkwnown!
                        ob = " ".getBytes();
                }

                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }

            ////?? ??
            }else if (b== this.CEDILLA_CHARACTER){
                byte ob[];
                byte b2 = this._textFieldTTIsubtitle[++i];
                switch (b2){
                    case 'C':
                        ob = "??".getBytes();
                        break;
                    case 'c':
                        ob = "??".getBytes();
                        break;
                    default:
                        //Unkwnown!
                        ob = " ".getBytes();
                }

                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }
                
            //?? ?? ?? ?? ?? ??
            } else if (b==this.ENE_CHARACTER){
                byte ob[];
                b = this._textFieldTTIsubtitle[++i];
                switch (b){
                    case 'n':
                        ob = "??".getBytes();
                        break;
                    case 'N':
                        ob = "??".getBytes();
                        break;
                    case 'A':
                        ob = "??".getBytes();
                        break;
                    case 'O':
                        ob = "??".getBytes();
                        break;
                    case 'a':
                        ob = "??".getBytes();
                        break;    
                    case 'o':
                        ob = "??".getBytes();
                        break;
                    default:
                        //Unkwnown!
                        ob = " ".getBytes();
                }

                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }

            // grados ??
            } else if (b==this.GRADO_CHARACTER){
                byte ob[];
                //b = this._textFieldTTIsubtitle[++i];
                ob = "??".getBytes();
                    
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }  
                
            // '`'
            } else if (b==(byte)0x60){
                byte ob[];
                //b = this._textFieldTTIsubtitle[++i];
                ob = "`".getBytes();
                    
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                } 
                
                
            // '??'
            } else if (b==(byte)0xC7){
                byte ob[];
                //b = this._textFieldTTIsubtitle[++i];
                ob = "??".getBytes();
                    
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                }  
    
            // '??'
            } else if (b==this.SUP_o_CHARACTER){
                byte ob[];
                //b = this._textFieldTTIsubtitle[++i];
                ob = "??".getBytes();
                    
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                } 
                
            // '??'
            } else if (b==this.SUP_a_CHARACTER){
                byte ob[];
                //b = this._textFieldTTIsubtitle[++i];
                ob = "??".getBytes();
                    
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                } 
            
            // '$'
            } else if (b==this.DOLAR_CHARACTER){
                byte ob[];
                //b = this._textFieldTTIsubtitle[++i];
                ob = "$".getBytes();
                    
                for (int x=0; x<ob.length; x++){
                    out[nout++] = ob[x];
                } 
                    
            //Normal characters
            } else {
                out[nout++] = b;
            }



        }

        k=0;
        int nlines = alines.size();
        this._vlines = new String[nlines];
        for (String line : alines){
            this._vlines[k++] = line;
        }

    }

    private void decodeTimeSTL() throws Exception{

        int h0,m0,s0,f0,n0,  h1,m1,s1,f1,n1;


        h0 = this._headerTTIsubtitle[5];
        m0 = this._headerTTIsubtitle[6];
        s0 = this._headerTTIsubtitle[7];
        f0 = this._headerTTIsubtitle[8];
        n0 = (int) Math.round ((double) 1000 *(double) f0 / (double) (this._framespersecond) );

        h1 = this._headerTTIsubtitle[9];
        m1 = this._headerTTIsubtitle[10];
        s1 = this._headerTTIsubtitle[11];
        f1 = this._headerTTIsubtitle[12];
        n1 = (int) Math.round((double) 1000 *(double) f1 / (double) (this._framespersecond) );





        String timehours0 = fromIntegersToTime(h0, m0, s0, n0);
        String timehours1 = fromIntegersToTime(h1, m1, s1, n1);
        
        _timeini = this.fromTimeToSeconds(timehours0);
        _timeend = this.fromTimeToSeconds(timehours1);
    }
    
    private void decodeHeightSTL() {
      _height_subtitle = this._headerTTIsubtitle[13];
    }

    /**
     * @return the _usersubtitle
     */
    public boolean isUsersubtitle() {
        return _usersubtitle;
    }

    private String fromIntegersToTime(int h, int m, int s, int n) {
        return String.format("%02d:%02d:%02d:%03d", h,m,s,n);


    }

     public void updateTextContents(String[] vlinesnew) {
        String vlinesnewok[] = removeEmptyLines(vlinesnew);

        //-- No se produce actualizacion, los contenidos de texto son los mismos
        if (isEqualText(vlinesnewok, this._vlines)) return;

        //-- Se actualizan el campo de texto TTI, para formato STL
        try{
            encodeTextSTL(vlinesnewok, false);
        }catch (Exception e){

        }

        //-- Se actualizan los campos de texto
        this._vlines = vlinesnewok;



    }

    private String [] removeEmptyLines(String []vlines){

        int count=0;
        for (int i=0; i<vlines.length; i++){
            if (vlines[i].length() > 0) count++;
        }

        String [] vlinesnew = new String[count];
        int k=0;
        for (int i=0; i<vlines.length; i++){
            if (vlines[i].length() > 0) vlinesnew[k++] = vlines[i];
        }

        return vlinesnew;

    }

    private boolean isEqualText(String vlines0[], String vlines1[]){
        int n0   = vlines0.length;
        int n1   = vlines1.length;

        if (n0!=n1) return false;

        for (int i=0; i<n0; i++){
            if (!vlines0[i].equals(vlines1[i])) return false;
        }

        return true;

    }

    private void encodeTextSTL(String[] vlines, boolean w32format) {
        resetSTLTextContents();
        for (int i=0; i<vlines.length; i++){
            //addLineTextSTL(vlines[i]);
            addLineTextWithColorInfoSTL(vlines[i],i,w32format);
            if (i!= vlines.length-1) addNewLineCharacters();
        }
    }

    private void encodeSTLHeaders(int subtitleNumber, int height) throws Exception {
  
        this._subtitleSTLNumber = subtitleNumber;
        this._height_subtitle   = height;
        
        this._headerTTIsubtitle = new byte[this._lenHeaderTTIsubtitle];
        
        //--Subtitle Group Number (bytes 0)
        this._headerTTIsubtitle[0] = 0x00;
        
        //--Subtitle Number (bytes 1..2)
        this._headerTTIsubtitle[1] = (byte) (this._subtitleSTLNumber & 0xFF);
        this._headerTTIsubtitle[2] = (byte) ((this._subtitleSTLNumber>>8) & 0xFF);
        
        //--Extension block number (byte 3)
        this._headerTTIsubtitle[3] = (byte) this.EBN_SUBTITLE_STL_CODE;
        
        //-- Cumulative status (byte 4)
        this._headerTTIsubtitle[4] = (byte) this.CS_SUBTITLE_NOT_PART_OF_CUMULATIVE_SET;
        
            
        int hours,min,sec, milisec, nframe, remainder;
        remainder = (int) Math.floor(this._timeinidouble);
        hours = remainder / 3600;
        remainder = remainder % 3600;
        min = remainder / 60;
        sec = remainder % 60;
        //milisec = (int) ((this._timeinidouble - Math.floor(this._timeinidouble))* (double)1000);
        milisec = (int) Math.round((this._timeinidouble - Math.floor(this._timeinidouble))* (double)1000);
        
        //nframe = Math.round ((float)milisec * (float)(_framespersecond-1) / 1000.0F);
        //nframe = Math.round ((float)milisec * (float)_framespersecond / 1000.0F);
        nframe = (int) Math.floor ((float)milisec * (float)_framespersecond / 1000.0F);

        
        //-- Time code in (byte 5..8)
        this._headerTTIsubtitle[5] = (byte) hours;
        this._headerTTIsubtitle[6] = (byte) min;
        this._headerTTIsubtitle[7] = (byte) sec;
        this._headerTTIsubtitle[8] = (byte) nframe;
        
        
        
        remainder = (int) Math.floor(this._timeenddouble);
        hours = remainder / 3600;
        remainder = remainder % 3600;
        min = remainder / 60;
        sec = remainder % 60;
        //milisec = (int) ((this._timeenddouble - Math.floor(this._timeenddouble))* (double)1000);
        milisec = (int) Math.round((this._timeenddouble - Math.floor(this._timeenddouble))* (double)1000);
        //nframe = Math.round ((float)milisec * (float)(_framespersecond-1) / 1000.0F);
        //nframe = Math.round ((float)milisec * (float)_framespersecond / 1000.0F);
        nframe = (int) Math.floor ((float)milisec * (float)_framespersecond / 1000.0F);
        
        
         //-- Time code in (byte 9..12)
        this._headerTTIsubtitle[9] = (byte) hours;
        this._headerTTIsubtitle[10] = (byte) min;
        this._headerTTIsubtitle[11] = (byte) sec;
        this._headerTTIsubtitle[12] = (byte) nframe;

        //-- Vertical position (byte 13)
        this._headerTTIsubtitle[13] = (byte) this._height_subtitle;

        //-- Justification (byte 14)
        this._headerTTIsubtitle[14] = (byte) this._justification_subtitle;
        //this.JUSTIFICATION_CENTRED;
        
        //-- Comment flag (byte 15) 
        this._headerTTIsubtitle[15] = 0x00; 
        
        
    }

    private void resetSTLTextContents() {
        this.nt = 0;
        this._textFieldTTIsubtitle = new byte[this._lenTextFieldTTIsubtitle];
        for (int i=0; i<this._lenTextFieldTTIsubtitle; i++) this._textFieldTTIsubtitle[i] = this.FILLER_CHARACTER;

    }

    private void addLineTextSTL(String strline) {

        //-- Number of bytes per line
        //if (!this._isSTLsubtitle) 
        this._numberOfBytesByLine = 40;

        //-- Checking if enough space available (112 bytes in lentextfieldttisubtitle)!
        if ( (this._lenTextFieldTTIsubtitle - this.nt) < this._numberOfBytesByLine ) return;

        //-- Double heigh character
        if (this._isalturadoble) this._textFieldTTIsubtitle[this.nt++] = this.ALTURA_DOBLE;
        
        //-- Foreground color (if exist)
        if ((this._fgcolor != this.DEFAULT_COLOR)) this._textFieldTTIsubtitle[this.nt++] = getSTLForegroundColorFromContinuousColor(this._fgcolor);

        //-- centering
        int ncar = strline.length();
        int ncontrol = 2;
        if (this._isalturadoble) ncontrol++;
        if (this._fgcolor != this.DEFAULT_COLOR) ncontrol++;
        
        int maxcar = this._numberOfBytesByLine - ncontrol;
        if (ncar > maxcar) ncar = maxcar;
        int startpos = (this._numberOfBytesByLine - (ncar + ncontrol) )/2;

        //-- empty characters 0x20
        int i;
        for (i=0; i<startpos; i++) this._textFieldTTIsubtitle[this.nt++] = 0x20;

        //-- Start of Box
        this._textFieldTTIsubtitle[this.nt++] = this.INICIO_RECUADRO;
        this._textFieldTTIsubtitle[this.nt++] = this.INICIO_RECUADRO;

        //-- Text contents
        byte b[] = getBytesSTLFromString(strline, ncar);
        for (i=0; i<b.length; i++) this._textFieldTTIsubtitle[this.nt++] = b[i];

         //--End of Box
        if ( ncar + ncontrol < this._numberOfBytesByLine)     this._textFieldTTIsubtitle[this.nt++] = this.FIN_RECUADRO;

    }

    private byte[] getBytesSTLFromString(String strline, int ncar) {

        int n=0;
        int maxlen = 2*ncar; //--sobredimensionamos el array btmpout, ya que el maximo numero de bytes por caracter en el formato STL es 2
        byte btmpout[] = new byte[maxlen];

        for (int i=0; i<ncar; i++){
            char c = strline.charAt(i);
            if       (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'a';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'e';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'i';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'o';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'u';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'A';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'E';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'I';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'O';
            }else if (c=='??'){
                btmpout[n++] = this.TILDE_CHARACTER;
                btmpout[n++] = 'U';
            }else if  (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'a';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'e';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'i';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'o';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'u';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'A';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'E';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'I';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'O';
            }else if (c=='??'){
                btmpout[n++] = this.DIERESIS_CHARACTER;
                btmpout[n++] = 'U';
            }else if (c=='??'){
                btmpout[n++] = this.ENE_CHARACTER;
                btmpout[n++] = 'n';
            }else if (c=='??'){
                btmpout[n++] = this.ENE_CHARACTER;
                btmpout[n++] = 'N';
            }else if (c=='??'){
                btmpout[n++] = this.ENE_CHARACTER;
                btmpout[n++] = 'A';
            }else if (c=='??'){
                btmpout[n++] = this.ENE_CHARACTER;
                btmpout[n++] = 'O';
            }else if (c=='??'){
                btmpout[n++] = this.ENE_CHARACTER;
                btmpout[n++] = 'a';
            }else if (c=='??'){
                btmpout[n++] = this.ENE_CHARACTER;
                btmpout[n++] = 'o';
            }else if  (c=='??'){
                btmpout[n++] = this.SOMB_CHARACTER;
                btmpout[n++] = 'a';
            }else if (c=='??'){
                btmpout[n++] = this.SOMB_CHARACTER;
                btmpout[n++] = 'e';
            }else if (c=='??'){
                btmpout[n++] = this.SOMB_CHARACTER;
                btmpout[n++] = 'i';
            }else if (c=='??'){
                btmpout[n++] = this.SOMB_CHARACTER;
                btmpout[n++] = 'o';
            }else if (c=='??'){
                btmpout[n++] = this.SOMB_CHARACTER;
                btmpout[n++] = 'u';
            }else if (c=='??'){ //grado
                btmpout[n++] = this.GRADO_CHARACTER;
            }/*else if (c=='??'){
                //btmpout[n++] = (byte)0x00;
                //btmpout[n++] = (byte)0xBA;
            }else if (c=='??'){
                //btmpout[n++] = (byte)0x00;
                //btmpout[n++] = (byte)0xAA;
            }*/else if (c=='??'){
                btmpout[n++] = this.CEDILLA_CHARACTER;
                btmpout[n++] = 'C';
            }else if (c=='??'){
                btmpout[n++] = this.CEDILLA_CHARACTER;
                btmpout[n++] = 'c';
            }else if (c=='`'){
                btmpout[n++] = (byte)0x60;
            }else if (c=='??'){
                btmpout[n++] = (byte)0xC7;
            }/*else if (c=='\''){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '\'';
            }else if (c=='|'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '|';
            }else if (c=='['){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '[';
            }else if (c=='\/'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '\/';
            }else if (c==']'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = ']';
            }else if (c=='_'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '_';
            }else if (c=='{'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '{';
            }else if (c=='}'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '}';
            }else if (c=='^'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '^';
            }*//*else if (c=='~'){
                btmpout[n++] = this.NBSP_CHARACTER;
                btmpout[n++] = '~';
            }else if (c=='@'){
                
            }else if (c=='#'){
               
            }*/else{
                btmpout[n++] = (byte) c;
            }

            if (n==maxlen) break;

        }

        byte bout[] = new byte[n];
        System.arraycopy(btmpout, 0, bout, 0, n);

        return bout;


    }

    private void addNewLineCharacters() {
        this._textFieldTTIsubtitle[this.nt++] = this.NEWLINECHARACTER;
        this._textFieldTTIsubtitle[this.nt++] = this.NEWLINECHARACTER;
    }

    public byte [] getTTIblock(int num_subtitle, boolean w32format, boolean bottomheight) throws Exception{

        if (!this._isSTLsubtitle){
            //-- Se genera el campo de texto y las cabeceras STL
            encodeTextSTL(this._vlines, false);

            //-- subtitle number=0, height=15. TODO!!!!
            encodeSTLHeaders(num_subtitle,  _height_subtitle);

            this._isSTLsubtitle = true;
        }
        
        if (w32format){
            //-- Se genera el campo de texto y las cabeceras STL
            encodeTextSTL(this._vlines, w32format);
        }
        
        if (bottomheight){

            if (bottomheight && (this.getNLines()==1) && (_height_subtitle > 15))
                _height_subtitle = (_height_subtitle+2)>23 ? 23 : (_height_subtitle+2);
            
            encodeSTLHeaders(num_subtitle,  _height_subtitle);   
        }

        byte [] blockTTI = new byte[this._lenHeaderTTIsubtitle + this._lenTextFieldTTIsubtitle];

        //-- Copy the header (16 bytes)
        System.arraycopy(this._headerTTIsubtitle, 0, blockTTI, 0, this._lenHeaderTTIsubtitle);

        //-- Copy the text information (112 bytes)
        System.arraycopy(this._textFieldTTIsubtitle, 0, blockTTI, this._lenHeaderTTIsubtitle, this._lenTextFieldTTIsubtitle);


        return blockTTI;

    }


    public static String getCurrentDate() {
        Format formatter;
        Date currentDate = Calendar.getInstance().getTime();

        formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return formatter.format(currentDate);

    }

    public void toSTLFormat(int subtitleNumber, int altura, int framesPerSecond) {
        
        //-- Default values
        _isSTLsubtitle = true;
        _isalturadoble = true;
        _usersubtitle = false; //user subtitles are not processed
        _framespersecond = framesPerSecond; //frames per second, for enconding times
        _height_subtitle = altura; //vertical position of the subtitle
        _timeinidouble   = Double.valueOf(_timeini) ;
        _timeenddouble   = Double.valueOf(_timeend) ;
        
        //-- Se actualiza el campo de texto TTI, para formato STL
        try{
            encodeTextSTL(this._vlines, false);
        }catch (Exception e){

        }
        
         //-- Se actualizan las cabeceras TTI, para formato STL
        try{   
            encodeSTLHeaders(subtitleNumber, _height_subtitle);
        }catch (Exception e){

        }

    }

    public void toSTLFormat(int subtitleNumber) {
        toSTLFormat(subtitleNumber, 16, 25);
    }

    public boolean equalsWithoutTime(Subtitle st) {
        
        
        String vlines0[] = this.getVlines();
        String vlines1[] = st.getVlines();
        
        int nlines0 = this.getNLines();
        int nlines1 = st.getNLines();
        
        //System.out.println("nlines0: " + nlines0 + " nlines1: " + nlines1);
        
        if (nlines0 != nlines1) return false;
        
        for (int i=0; i<nlines0; i++){
            String a[] = vlines0[i].split(" +");
            String b[] = vlines1[i].split(" +");
            //System.out.println("aleng: " + a.length + " blen: " + b.length);
            if (a.length!=b.length) return false;
            for (int j=0; j<a.length; j++){
                 //System.out.println("a[" + j + "]: '" +a[j] + "' b[" + j + "]: '" + b[j] + "'");
                 //try { System.in.read(); } catch (IOException ex) {}
                if (a[j].compareTo(b[j])!=0) return false;
            }
        }
        return true;
        
    }

    public boolean equalWordsWithoutTime(Subtitle st) {
        
        
        String vwords0[] = this.getAllWords();
        String vwords1[] = st.getAllWords();
        
       
        
        if (vwords0.length != vwords1.length) return false;
        
        for (int i=0; i<vwords0.length; i++){
             if (!vwords0[i].equals(vwords1[i])){
                 return false;
             }
        }
        return true;
        
    }

    
    public String[] getVlines() {
        return this._vlines;
    }

    
    public String[] getAllWords() {
        
        String out="", vout[];
        
        for (int i=0; i<this._vlines.length; i++){
            String str = _vlines[i].trim();
            out += str + " ";
        }
        
        return out.split(" +");
        
    }

    public String[] getAllWordsWithLineSeparator(String separator){
        String out="", vout[];
        
        for (int i=0; i<this._vlines.length; i++){
            String str = _vlines[i].trim();
            out += str + ( (i!=_vlines.length-1) ? separator: "");
        }
        
        return out.split(" +");
 
    }
    
    public  String toTXTWithSeparator(String separator) {
        String sub =  "";
        int len = _vlines.length;
        for (int k=0; k<len; k++){
            String line = _vlines[k];
            sub = sub + line;
            if (k != len-1) sub = sub + separator;
        }
        return sub;
    }

    public boolean equalsWithTime(Subtitle st, float offsetSt, float diff) {
        
        boolean eqtext = this.equalsWithoutTime(st);
        
        if (eqtext == false) return false;
        
        float diffini = this.getTimeIniFloat()-(st.getTimeIniFloat() + offsetSt);
        float diffend = this.getTimeEndFloat()-(st.getTimeEndFloat() + offsetSt);
        
        if (Math.abs(diffini) > diff) return false;
        if (Math.abs(diffend) > diff) return false;
        
        return true;
    }

    public String getFirstWord() {
        String vwords[] = this.getAllWords();
        return vwords[0];
    }

    private String getColorNameFromContinuousColor(Color fgcolor) {
        if       (fgcolor.equals(Color.black))  return STR_NEGRO;
        else if  (fgcolor.equals(Color.red))    return STR_ROJO;
        else if  (fgcolor.equals(Color.green))  return STR_VERDE;
        else if  (fgcolor.equals(Color.yellow)) return STR_AMARILLO;
        else if  (fgcolor.equals(Color.blue))   return STR_AZUL;
        else if  (fgcolor.equals(Color.magenta))return STR_MAGENTA;
        else if  (fgcolor.equals(Color.cyan) )  return STR_CYAN;
        else if  (fgcolor.equals(Color.white))  return STR_BLANCO;
        else{
            //TODO! get the most similar color
            return STR_BLANCO;
        }
    }
    
    private byte getSTLForegroundColorFromContinuousColor(Color fgcolor) {
        if       (fgcolor.equals(Color.black))  return TEXTO_NEGRO;
        else if  (fgcolor.equals(Color.red))    return TEXTO_ROJO;
        else if  (fgcolor.equals(Color.green))  return TEXTO_VERDE;
        else if  (fgcolor.equals(Color.yellow)) return TEXTO_AMARILLO;
        else if  (fgcolor.equals(Color.blue))   return TEXTO_AZUL;
        else if  (fgcolor.equals(Color.magenta))return TEXTO_MAGENTA;
        else if  (fgcolor.equals(Color.cyan) )  return TEXTO_CYAN;
        else if  (fgcolor.equals(Color.white))  return TEXTO_BLANCO;
        else{
            //TODO! get the most similar color
            return TEXTO_BLANCO;
        }
    }

    private Color getContinuousColorFromSTLForegroundColor(int colorfg) {
        if       (colorfg== TEXTO_NEGRO )   return Color.black;
        else if  (colorfg== TEXTO_ROJO )    return Color.red;
        else if  (colorfg== TEXTO_VERDE)    return Color.green;
        else if  (colorfg== TEXTO_AMARILLO) return Color.yellow;
        else if  (colorfg== TEXTO_AZUL)     return Color.blue;
        else if  (colorfg== TEXTO_MAGENTA)  return Color.magenta;
        else if  (colorfg== TEXTO_CYAN)     return Color.cyan;
        else if  (colorfg== TEXTO_BLANCO)   return Color.white;
        else{
            //TODO! get the most similar color
            return Color.white;
        }
    }
    
     private byte getSTLBackgroundColorFromContinuousColor(Color bgcolor) {
        if       (bgcolor.equals(Color.black))  return FONDO_NEGRO;
        else if  (bgcolor.equals(Color.red))    return FONDO_ROJO;
        else if  (bgcolor.equals(Color.green))  return FONDO_VERDE;
        else if  (bgcolor.equals(Color.yellow)) return FONDO_AMARILLO;
        else if  (bgcolor.equals(Color.blue))   return FONDO_AZUL;
        else if  (bgcolor.equals(Color.magenta))return FONDO_MAGENTA;
        else if  (bgcolor.equals(Color.cyan))   return FONDO_CYAN;
        else if  (bgcolor.equals(Color.white))  return FONDO_BLANCO;
        else{
            //TODO! get the most similar color
            return FONDO_NEGRO;
        }
    }
    
    private Color getContinuousColorFromSTLBackgroundColor(int colorbg) {
      
        if       (colorbg== FONDO_NEGRO )   return Color.black;
        else if  (colorbg== FONDO_ROJO )    return Color.red;
        else if  (colorbg== FONDO_VERDE)    return Color.green;
        else if  (colorbg== FONDO_AMARILLO) return Color.yellow;
        else if  (colorbg== FONDO_AZUL)     return Color.blue;
        else if  (colorbg== FONDO_MAGENTA)  return Color.magenta;
        else if  (colorbg== FONDO_CYAN)     return Color.cyan;
        else if  (colorbg== FONDO_BLANCO)   return Color.white;
        else{
            //TODO! get the most similar color
            return Color.black;
        }
    }
    
    private String getXMLColorString() {
        String out="";
         if (this._fgcolor != this.DEFAULT_COLOR){   
             out += "tts:color=\"#" + getRGBString(Integer.toHexString( _fgcolor.getRGB() & 0x00ffffff)).toUpperCase() + "\" ";
        }
         return out;
    }
    
    private String getSRTColorString() {
        String out="";
         if (this._fgcolor != this.DEFAULT_COLOR){
             out += "<font color=\"#" + getRGBString(Integer.toHexString( _fgcolor.getRGB() & 0x00ffffff)) + "\"> ";
        }
         return out;
    }


    private String getLineSRTColorString(int nline) {
        String out="";
        Color color = this._fgcolorlines.get(nline);
        
        if(color!=Color.white){
            //out += "<font color=\"#" + getRGBString(Integer.toHexString( color.getRGB() & 0x00ffffff)) + "\"> " + this._vlines[nline] + " </font>";
            out += "<font color=\"#" + getRGBString(Integer.toHexString( color.getRGB() & 0x00ffffff)) + "\">" + this._vlines[nline] + "</font>";
        }else{
            out = this._vlines[nline];
        }
        return out;
    }
    
    
    //Returns a string with format "rrggbb", when the input hexString has equal or less than 3 bytes
    private String getRGBString(String hexString){
        int nzeros = 6 - hexString.length();
        String sz = "";
        for (int i=0; i<nzeros; i++) sz+= "0";
        return sz + hexString;
    }

    public Color getForeground() {
        return (this._fgcolor!=null? this._fgcolor: Color.white);
    }

    public int getBackgroundSTL() {
        return this._colorbg;
    }
    public int getForegroundSTL() {
        return this._colorfg;
    }
    
    public int getHeight() {
        return this._height_subtitle;
    }

    public boolean isSTLSubtitle() {
        return this._isSTLsubtitle;
    }
    
    private void addLineTextWithColorInfoSTL(String strline, int nline, boolean w32format) {

        //-- Number of bytes per line
        //if (!this._isSTLsubtitle) 
        this._numberOfBytesByLine = 40;

        //-- Checking if enough space available (112 bytes in lentextfieldttisubtitle)!
        if ( (this._lenTextFieldTTIsubtitle - this.nt) < this._numberOfBytesByLine ) return;

        //-- Double heigh character
        if (this._isalturadoble) this._textFieldTTIsubtitle[this.nt++] = this.ALTURA_DOBLE;
        
        //-- Foreground color (if exist)
        Color fgcolor = null;
        try {
            fgcolor = (Color) this._fgcolorlines.get(nline);
        } catch (Exception e) {
            fgcolor = this.DEFAULT_COLOR;
        }
        
        //-- Foreground color (if exist)
        Color bgcolor = null;
        try {
            bgcolor = (Color) this._bgcolorlines.get(nline);
        } catch (Exception e) {
            bgcolor = this.DEFAULT_BKG_COLOR;
        }
             
        //-- Background color (if exist)
        //-- background color + 1D byte (new background)
        if (bgcolor != this.DEFAULT_BKG_COLOR){
            if (w32format)  this._textFieldTTIsubtitle[this.nt++] = getSTLForegroundColorFromContinuousColor(bgcolor);
            else            this._textFieldTTIsubtitle[this.nt++] = getSTLBackgroundColorFromContinuousColor(bgcolor);
            this._textFieldTTIsubtitle[this.nt++] = this.NEW_BACKGROUND;
        }
        
        //if (fgcolor != this.DEFAULT_COLOR) 
        //-- cuando hay linea de color y luego blanca, se pone el color ya que si no
        //-- se conserva el anterior
        this._textFieldTTIsubtitle[this.nt++] = getSTLForegroundColorFromContinuousColor(fgcolor);
 
        
        //-- centering
        int ncar = strline.length();
        int ncontrol = 2;
        if (this._isalturadoble) ncontrol++;
        if (fgcolor != this.DEFAULT_COLOR) ncontrol++;
        if (this._bgcolor != this.DEFAULT_BKG_COLOR) ncontrol++;
        int maxcar = this._numberOfBytesByLine - ncontrol;
        if (ncar > maxcar) ncar = maxcar;
        int startpos = (this._numberOfBytesByLine - (ncar + ncontrol) )/2;

        //-- empty characters 0x20
        int i;
        for (i=0; i<startpos; i++) this._textFieldTTIsubtitle[this.nt++] = 0x20;

        //-- Start of Box
        this._textFieldTTIsubtitle[this.nt++] = this.INICIO_RECUADRO;
        this._textFieldTTIsubtitle[this.nt++] = this.INICIO_RECUADRO;

        //-- Text contents
        byte b[] = getBytesSTLFromString(strline, ncar);
        for (i=0; i<b.length; i++) this._textFieldTTIsubtitle[this.nt++] = b[i];

         //--End of Box
        //if ( ncar + ncontrol < this._numberOfBytesByLine)     
        this._textFieldTTIsubtitle[this.nt++] = this.FIN_RECUADRO;

    }


    public ArrayList getForegroundArray() {
           
        return this._fgcolorlines;

    }
    
    public ArrayList getBackgroundArray() {
           
        return this._bgcolorlines;

    }
    

    public void setOffsetSeconds(double toffset) throws Exception {
        
        _timeinidouble = Double.valueOf(_timeini) + toffset ;
        _timeenddouble = Double.valueOf(_timeend) + toffset;
        
        _timeini = String.format("%d.%03d", (int) Math.floor(_timeinidouble), (int) ((_timeinidouble-Math.floor(_timeinidouble))*1000.0F));
        _timeend = String.format("%d.%03d", (int) Math.floor(_timeenddouble), (int) ((_timeenddouble-Math.floor(_timeenddouble))*1000.0F));

        if(this.isSTLSubtitle()){
            this.encodeSTLHeaders(_subtitleSTLNumber,_height_subtitle);
        }
    }
    
     public String getDuration(String timeini, String timeend) {
        int t0 = this.getMiliSeconds(timeini);
        int t1 = this.getMiliSeconds(timeend);

        int dur = t1-t0;
        int durs  = dur/1000;
        int durms = dur%1000;

        return String.format("%d.%03d", durs, durms);
    }

     public int getNControl() {
        int ncontrol=0;
        if (!this._isSTLsubtitle) return ncontrol;
        if ((this._colorfg >=0 ) &&(this._colorfg != TEXTO_BLANCO))  ncontrol++;
        if ((this._colorbg >=0 ) &&(this._colorbg != FONDO_NEGRO ))  ncontrol++;


        return ncontrol;
    }

    public int getNPunctuations() {
        String text = this.getAllLines();
        int k=0;
        text = text.replaceAll("\\.\\.\\.", "\\.");
        k += text.split("\\Q"+"."+"\\E", -1).length - 1; 
        k += text.split("\\Q"+"!"+"\\E", -1).length - 1; 
        k += text.split("\\Q"+"?"+"\\E", -1).length - 1; 
        k += text.split("\\Q"+")"+"\\E", -1).length - 1;
        
        return k;
    }

    private void decodeJustificationCodeSTL() {
        this._justification_subtitle = this._headerTTIsubtitle[14];
    }

    public void setJustification(Byte justification) {
        this._justification_subtitle = justification;
    }

    public Byte getJustification() {
        return this._justification_subtitle;
    }
    
    

    public String fromSecondsToTimeFrameFormat(String time) {
        
        Double timeDouble = Double.valueOf(time);
                
        int hours,min,sec, milisec, nframe, remainder;
        remainder = (int) Math.floor(timeDouble);
        hours = remainder / 3600;
        remainder = remainder % 3600;
        min = remainder / 60;
        sec = remainder % 60;
        milisec = (int) Math.round((timeDouble - Math.floor(timeDouble))* (double)1000);
        nframe = (int) Math.floor ((float)milisec * (float)_framespersecond / 1000.0F);
        
        return String.format("%02d",hours)+":"+String.format("%02d",min)+":"+String.format("%02d",sec)+":"+String.format("%02d",nframe);
    } 
    
    public void setSubtitleSTLNumber(int num) throws Exception{
        
        if(this.isSTLSubtitle()){     
            encodeSTLHeaders(num,_height_subtitle);
        }
    }

    boolean isExtendedSubtitle() {
        
        return this._extendedsubtitle;
    }

    public void concatTextSTL(Subtitle sub) {
        
        byte newTextFieldTTI[] = new byte[this._lenTextFieldTTIsubtitle + sub._lenTextFieldTTIsubtitle];           

        System.arraycopy(this._textFieldTTIsubtitle, 0, newTextFieldTTI, 0, this._lenTextFieldTTIsubtitle);
        System.arraycopy(sub._textFieldTTIsubtitle, 0, newTextFieldTTI, this._lenTextFieldTTIsubtitle, sub._lenTextFieldTTIsubtitle);
        
        this._lenTextFieldTTIsubtitle = (short) (this._lenTextFieldTTIsubtitle + sub._lenTextFieldTTIsubtitle);
        this._textFieldTTIsubtitle = newTextFieldTTI;
    }
  
}
