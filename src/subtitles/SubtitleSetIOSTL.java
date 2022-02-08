/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package subtitles;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author jegarcia
 */
public class SubtitleSetIOSTL implements SubtitleSetIO{
    private int  framesPerSecond;
    private byte TeletextLevel;
    private byte characterEncoding;
    private int  numberOfBytesByLine;
    private byte _headerGSI[];
    private boolean _useOffsetTimeFromFile;
    private String _lastEditionUZDate;
    private String _lastVistoBuenoUZDate;
    private String _editor;
    private String _editorVistoBueno;
    private boolean _vistobuenoflag;
    private final int offsetDateUZ             = 512;
    private final int offsetEditorUZ           = 544;
    private final int offsetVistoBuenoDateUZ   = 576;
    private final int offsetVistoBuenoEditorUZ = 608;
    private final int sizeEditorUZ   = 31;
    private final int sizeDateUZ     = 19;
    private final int _nbytes_header_gsi = 1024;
    private int _nSubtitles;
    
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
    private String _firstInCue;
    
    private final int _originalProgramNameMaxSize = 32;
    private final int _originalEpisodeNameMaxSize = 32;
    private final int _translatedProgramNameMaxSize = 32;
    private final int _translatedEpisodeNameMaxSize = 32;
    private final int _codRefListaSubtituladoMaxSize = 16;
    private final int _languageMaxSize = 2;
    private final int _revisionMaxSize = 2;
    private final int _translatorNameMaxSize = 32;
    private final int _translatorInfoMaxSize = 32;
    private final int _publisherNameMaxSize = 32;
    private final int _editorNameMaxSize = 32;
    private final int _editorInfoMaxSize = 32;
    private final int _originCountryMaxSize = 3;
    private final int _startOfProgramMaxSize = 8;
    private final int _firstInCueMaxSize = 8;
    
    private final int _originalProgramNameOffset = 16;
    private final int _originalEpisodeNameOffset = 48;
    private final int _translatedProgramNameOffset = 80;
    private final int _translatedEpisodeNameOffset = 112;
    private final int _codRefListaSubtituladoOffset = 208;
    private final int _languageOffset = 14;
    private final int _revisionOffset = 236;
    private final int _translatorNameOffset = 144;
    private final int _translatorInfoOffset = 176;
    private final int _publisherNameOffset = 277;
    private final int _editorNameOffset = 309;
    private final int _editorInfoOffset = 341;
    private final int _originCountryOffset = 274;
    private final int _startOfProgramOffset = 256;
    private final int _firstInCueOffset = 264;

    private boolean _w32format;
    private boolean _bottomheightformat;
            
    public SubtitleSetIOSTL(){
        _headerGSI = null;
        _useOffsetTimeFromFile = false;
        
        _w32format          = false;
        _bottomheightformat = false;
    }

    SubtitleSetIOSTL(SubtitleSetIOSTL _lastreader) {
        this._headerGSI = _lastreader._headerGSI;
        _useOffsetTimeFromFile = false;
        
        _w32format          = false;
        _bottomheightformat = false;
    }

    public ArrayList<Subtitle> read(String filename) throws Exception{
        
        DataInputStream dis = openBinaryFile(filename);
        
        readHeaderGSI(dis);
        
        ArrayList <Subtitle> vsub = readSubtitles(dis);
        
        closeBinaryFile(dis);
        
        return vsub;

    }

    public void write(String filename, ArrayList<Subtitle> vsubtitles) throws Exception {
        DataOutputStream dos = openWriteBinaryFile(filename);

        writeHeaderGSI(dos,vsubtitles);

        writeSubtitles(dos, vsubtitles);

        closeWriteBinaryFile(dos);
        
    }

    private DataInputStream openBinaryFile(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);

        return dis;
    }

    private DataOutputStream openWriteBinaryFile(String filename) throws Exception {
        File f = new File(filename);
        FileOutputStream fis = new FileOutputStream(f);
        DataOutputStream dos = new DataOutputStream(fis);

        return dos;
    }

    //General Subtitle Information Header (1024 bytes)
    private void readHeaderGSI(DataInputStream dis) throws Exception {

        //-- Reading header bytes from file      
        byte headerGSI [] = readBytes(dis, _nbytes_header_gsi);

        //-- Processing GSI header
        processHeaderGSI(headerGSI);

        //-- Save header
        this._headerGSI = headerGSI;

    }


    private ArrayList<Subtitle> readSubtitles(DataInputStream dis) throws Exception{
        ArrayList <Subtitle> vsub = new ArrayList();
        int nbytesblock = 128;
        byte blockTTI[];
        Subtitle extsub = null;
        boolean lastextended = false;
        while ((blockTTI = readBytes(dis, nbytesblock))!=null){
            //-- Processing TTI block
            Subtitle sub = new Subtitle(blockTTI, this.framesPerSecond, this.characterEncoding, this.numberOfBytesByLine, lastextended);
            if (sub.isUsersubtitle()) continue;
            if (sub.isExtendedSubtitle()){
                if (!lastextended){
                    extsub = sub;
                }else{
                    extsub.concatTextSTL(sub);
                }
                lastextended = true;
                continue;
            }
            if(lastextended){
                extsub.concatTextSTL(sub);
                extsub.decodeTextSTL();
                vsub.add(extsub);
                extsub = null;
                lastextended = false;
            }else{ 
                vsub.add(sub);
            }
            
        }

        return vsub;
    }

    private void closeBinaryFile(DataInputStream dis) throws Exception{
        dis.close();
    }

    private byte[] readBytes(DataInputStream dis, int nbytestoread) throws Exception{
        int pending=nbytestoread;
        byte bytes[] = new byte[nbytestoread];
        while (pending > 0){
            int n = dis.read(bytes, nbytestoread-pending, pending);
            if (n==-1) return null;
            pending -= n;
        }

        return bytes;
    }

    private void processHeaderGSI(byte[] headerGSI) throws Exception{
        int l;
        //-- number of frames per second
        getDiskFormatCode(headerGSI);
        
        //-- (Level-2 Teletext, Level-1 Teletext, Open Subtitling, Undefined)
        getDisplayStandardCode(headerGSI);

        //-- Character encoding
        getCharacterEncoding(headerGSI);

        //-- Get the number of bytes by subtitle
        getNumberOfBytesByLine(headerGSI);

        //-- Get the last edition date
        getLastEditionDateFromHeader(headerGSI);

        //-- Get the last editor
        getLastEditionEditorFromHeader(headerGSI);

        //-- Get the last validation date
        getLastValidationDateFromHeader(headerGSI);

        //-- Get the last editor
        getLastValidationEditorFromHeader(headerGSI);
        
        //-- Read metadata
        this._originalProgramName    = readMetadataInfoToHeaderGSI(headerGSI,_originalProgramNameOffset,_originalProgramNameMaxSize);
        this._originalEpisodeName    = readMetadataInfoToHeaderGSI(headerGSI,_originalEpisodeNameOffset,_originalEpisodeNameMaxSize);
        this._translatedProgramName  = readMetadataInfoToHeaderGSI(headerGSI,_translatedProgramNameOffset,_translatedProgramNameMaxSize);
        this._translatedEpisodeName  = readMetadataInfoToHeaderGSI(headerGSI,_translatedEpisodeNameOffset,_translatedEpisodeNameMaxSize);
        this._codRefListaSubtitulado = readMetadataInfoToHeaderGSI(headerGSI,_codRefListaSubtituladoOffset,_codRefListaSubtituladoMaxSize);
        this._language               = readMetadataInfoToHeaderGSI(headerGSI,_languageOffset,_languageMaxSize);
        this._revision               = readMetadataInfoToHeaderGSI(headerGSI,_revisionOffset,_revisionMaxSize);
        this._translatorName         = readMetadataInfoToHeaderGSI(headerGSI,_translatorNameOffset,_translatorNameMaxSize);
        this._translatorInfo         = readMetadataInfoToHeaderGSI(headerGSI,_translatorInfoOffset,_translatorInfoMaxSize);
        this._publisherName          = readMetadataInfoToHeaderGSI(headerGSI,_publisherNameOffset,_publisherNameMaxSize);
        this._editorName             = readMetadataInfoToHeaderGSI(headerGSI,_editorNameOffset,_editorNameMaxSize);
        this._editorInfo             = readMetadataInfoToHeaderGSI(headerGSI,_editorInfoOffset,_editorInfoMaxSize);
        this._originCountry          = readMetadataInfoToHeaderGSI(headerGSI,_originCountryOffset,_originCountryMaxSize);
        this._startOfProgram         = readMetadataInfoToHeaderGSI(headerGSI,_startOfProgramOffset,_startOfProgramMaxSize);
        this._startOfProgram = this._startOfProgram.replaceAll(" ", "");
//--        if (!_startOfProgram.matches("")&&(_startOfProgram.length()==_startOfProgramMaxSize)){
        if ((_startOfProgram.length()==_startOfProgramMaxSize) && Character.isDigit(this._startOfProgram.charAt(0))){
            byte []b = this._startOfProgram.getBytes();
            String hour = _startOfProgram.substring(0, 2);
            String min  = _startOfProgram.substring(2, 4);
            String sec  = _startOfProgram.substring(4, 6);
            String fr   = _startOfProgram.substring(6, 8);
            this._startOfProgram = this._startOfProgram.format("%02d:%02d:%02d:%02d", Integer.parseInt(hour), Integer.parseInt(min), Integer.parseInt(sec), Integer.parseInt(fr));
        }
    }



    private boolean equalsByteArray(byte[] b0, int offset0, byte[] b1, int offset1, int len){
        for (int i=0; i<len; i++){
            if (b0[offset0+i] != b1[offset1+i]){
                return false;
            }
        }
        return true;
    }

    private void getDiskFormatCode(byte[] headerGSI) throws Exception {
        //bytes 3..10
        if       (equalsByteArray("STL25.01".getBytes(), 0, headerGSI, 3, 8)){
            this.framesPerSecond = 25;
        }else if (equalsByteArray("STL30.01".getBytes(), 0, headerGSI, 3, 8)){
            this.framesPerSecond = 30;
        }else{
            throw new Exception ("Error STL format, Unkwnown Disk Format Code");
        }

    }


    private void getDisplayStandardCode(byte[] headerGSI) throws Exception {
        //byte 11
        // Level-2 teletext --> 32h
         TeletextLevel = headerGSI[11];
    }

    private void getCharacterEncoding(byte[] headerGSI) throws Exception {
        //bytes 12..13
        int o = 12;
        if       ((headerGSI[o]==0x30) && (headerGSI[o+1]==0x30)){
        //-- Latin

        }else if ((headerGSI[o]==0x30) && (headerGSI[o+1]==0x31)){
        //-- Latin/Cyrillic

        }else if ((headerGSI[o]==0x30) && (headerGSI[o+1]==0x32)){
        //-- Latin/Arabic

        }else if ((headerGSI[o]==0x30) && (headerGSI[o+1]==0x33)){
        //-- Latin/Greek

        }else if ((headerGSI[o]==0x30) && (headerGSI[o+1]==0x34)){
        //-- Latin/Hebrew

        }else{
        //-- Unknown
            throw new Exception ("Error STL format, Unknown character encoding");
        }

        this.characterEncoding = headerGSI[o+1];
    }

    private void getNumberOfBytesByLine(byte[] headerGSI) throws Exception {
        byte bnum[] = new byte[2];
        bnum[0] = headerGSI[251];
        bnum[1] = headerGSI[252];

        this.numberOfBytesByLine = Integer.parseInt(new String(bnum));

    }

    /*private void writeHeaderGSI(DataOutputStream dos) throws Exception {
        if (this._headerGSI == null){
            generateHeaderGSI();
        }

        appendLastEditionDateHeaderGSI(this._headerGSI);
        appendLastEditionEmptyEditorHeaderGSI(this._headerGSI);

        if (this._editor != null){
        if (this._editor.length()>0){
            appendLastEditionEditorHeaderGSI(this._headerGSI);
         }}

        if (this._vistobuenoflag){
            appendLastValidationDateHeaderGSI(this._headerGSI);  
            appendLastValidationEditorHeaderGSI(this._headerGSI);
        }

        dos.write(this._headerGSI);
    }*/
    
    private void writeHeaderGSI(DataOutputStream dos, ArrayList<Subtitle> vsubt) throws Exception {
        if (this._headerGSI == null){
            generateHeaderGSI();
        }

        appendLastEditionDateHeaderGSI(this._headerGSI);
        appendLastEditionEmptyEditorHeaderGSI(this._headerGSI);

        if (this._editor != null){
        if (this._editor.length()>0){
            appendLastEditionEditorHeaderGSI(this._headerGSI);
         }}

        if (this._vistobuenoflag){
            appendLastValidationDateHeaderGSI(this._headerGSI);  
            appendLastValidationEditorHeaderGSI(this._headerGSI);
        }
        
        //-- añadimos metadatos
        if ((_originalProgramName != null)&&(_originalProgramName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_originalProgramName,_originalProgramNameOffset,_originalProgramNameMaxSize);
        }
        if ((_originalEpisodeName != null)&&(_originalEpisodeName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_originalEpisodeName,_originalEpisodeNameOffset,_originalEpisodeNameMaxSize);
        }
        if ((_translatedProgramName != null)&&(_translatedProgramName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_translatedProgramName,_translatedProgramNameOffset,_translatedProgramNameMaxSize);
        }
        if ((_translatedEpisodeName != null)&&(_translatedEpisodeName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_translatedEpisodeName,_translatedEpisodeNameOffset,_translatedEpisodeNameMaxSize);
        }
        if ((_codRefListaSubtitulado != null)&&(_codRefListaSubtitulado.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_codRefListaSubtitulado,_codRefListaSubtituladoOffset,_codRefListaSubtituladoMaxSize);
        }
        if ((_language != null)&&(_language.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_language,_languageOffset,_languageMaxSize);
        }
        if ((_revision != null)&&(_revision.length()>0)){
            //-- si la longitus es 1, ponemos " X" (espacio es 0x20, valor por defecto de la cabecera)
            if (_revision.length() == 1) _revision = " " + _revision;
            appendMetadataInfoToHeaderGSI(this._headerGSI,_revision,_revisionOffset,_revisionMaxSize);
        }
        if ((_translatorName != null)&&(_translatorName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_translatorName,_translatorNameOffset,_translatorNameMaxSize);
        }
        if ((_translatorInfo != null)&&(_translatorInfo.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_translatorInfo,_translatorInfoOffset,_translatorInfoMaxSize);
        }
        if ((_publisherName != null)&&(_publisherName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_publisherName,_publisherNameOffset,_publisherNameMaxSize);
        }
        if ((_editorName != null)&&(_editorName.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_editorName,_editorNameOffset,_editorNameMaxSize);
        }
        if ((_editorInfo != null)&&(_editorInfo.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_editorInfo,_editorInfoOffset,_editorInfoMaxSize);
        }
        if ((_originCountry != null)&&(_originCountry.length()>0)){
            appendMetadataInfoToHeaderGSI(this._headerGSI,_originCountry,_originCountryOffset,_originCountryMaxSize);
        }
        if ((_startOfProgram != null)&&(_startOfProgram.length()>0)){
            //-- limpiamos () y :
            _startOfProgram = _startOfProgram.replace("(", "");
            _startOfProgram = _startOfProgram.replace(":", "");
            _startOfProgram = _startOfProgram.replace(")", "");
            appendMetadataInfoToHeaderGSI(this._headerGSI,_startOfProgram,_startOfProgramOffset,_startOfProgramMaxSize);
        }
        if (vsubt.size()>0){
            Subtitle first = vsubt.get(0);
            _firstInCue = first.fromSecondsToTimeFrameFormat(first.getTimeIni());
            //-- limpiamos :
            _firstInCue = _firstInCue.replace(":", "");
            appendMetadataInfoToHeaderGSI(this._headerGSI,_firstInCue,_firstInCueOffset,_firstInCueMaxSize);
            
        }
        //--

        dos.write(this._headerGSI);
    }

    private void closeWriteBinaryFile(DataOutputStream dos) throws Exception{
        dos.close();
    }

    private void writeSubtitles(DataOutputStream dos, ArrayList<Subtitle> vsubtitles)  throws Exception {
        
        int num = 0;
        for (Subtitle sub: vsubtitles ){
            //byte [] blockTTI = sub.getTTIblock();
            byte [] blockTTI = sub.getTTIblock(num, this._w32format, this._bottomheightformat);
            dos.write(blockTTI);
            num++;
        }
    }

    private void generateHeaderGSI() {
        
        
        //throw new UnsupportedOperationException("generateHeaderGSI Not yet implemented");
        this._headerGSI = new byte[this._nbytes_header_gsi];
        
        //reset default values
        for (int i=0; i<this._nbytes_header_gsi;i++){
            _headerGSI[i] = 0x20;
        }
        
        //code page number ( 850 multilingual)
        this._headerGSI[0] = '8';
        this._headerGSI[1] = '5';
        this._headerGSI[2] = '0';
        
        String fps = "25"; //-- default value
        if (this.framesPerSecond>0){
            fps = String.valueOf(framesPerSecond);
        }
        
        //Disk Format Code (STL25.01 | STL30.01) for 30 frames per second or 25
        this._headerGSI[3] = 'S';
        this._headerGSI[4] = 'T';
        this._headerGSI[5] = 'L';
        //this._headerGSI[6] = '2';
        //this._headerGSI[7] = '5';
        this._headerGSI[6] = (byte) fps.charAt(0);
        this._headerGSI[7] = (byte) fps.charAt(1);
        this._headerGSI[8] = '.';
        this._headerGSI[9] = '0';
        this._headerGSI[10]= '1';
        
        //Display standard code
        this._headerGSI[11]= '2'; //2 teletext-level 2 (x-26 subtitles)
        
        //Character code table (latin -> '0' '0')
        this._headerGSI[12]= '0';
        this._headerGSI[13]= '0';
        
        //Language Code ( '0' , 'A'
        this._headerGSI[14]= '0';
        this._headerGSI[15]= 'A';
        
        
        //Creation Date (224..229) YYMMDD
        String stringDate = DateUtils.now("yyMMdd");
        char []v = stringDate.toCharArray();
        this._headerGSI[224] = (byte) v[0];
        this._headerGSI[225] = (byte) v[1];
        this._headerGSI[226] = (byte) v[2];
        this._headerGSI[227] = (byte) v[3];
        this._headerGSI[228] = (byte) v[4];
        this._headerGSI[229] = (byte) v[5];
        
     
        
                
        //Revision Date (230..235) YYMMDD
        this._headerGSI[230] = (byte) v[0];
        this._headerGSI[231] = (byte) v[1];
        this._headerGSI[232] = (byte) v[2];
        this._headerGSI[233] = (byte) v[3];
        this._headerGSI[234] = (byte) v[4];
        this._headerGSI[235] = (byte) v[5];
                
        //Revision number (236..237) (default ini 0, 0)
        this._headerGSI[236] = (byte) 0x20;
        this._headerGSI[237] = (byte) '1';
              
        //Total number of TTI blocks (number of subtitles) (238..242)
        String nsubstr = String.valueOf(Math.min(this._nSubtitles, 99999));
        for (int i=0; i<nsubstr.length(); i++){
            this._headerGSI[238+i] = (byte) nsubstr.charAt(i);
        }
        
        //Total number of subtitles (243..247)
        for (int i=0; i<nsubstr.length(); i++){
            this._headerGSI[243+i] = (byte) nsubstr.charAt(i);
        }

        //Total number of subtitle groups (248..250)
        this._headerGSI[248] = '1';
        this._headerGSI[249] = 0x20;
        this._headerGSI[250] = 0x20;
        
        //Maximum number of characters by row (251..252) --> 40
        this._headerGSI[251] = '4'; 
        this._headerGSI[252] = '0';
        
        //Maximum number of rows (253..254) --> 23
        this._headerGSI[253] = '2'; 
        this._headerGSI[254] = '3';
        
        //Validity of Time Code Status (TCS)
        this._headerGSI[255] = '1';
        
        //time code start (default to 0)
        for (int i=0; i<16; i++){
            this._headerGSI[256+i] = '0';
        }
        
        //TND and DSN (Total number of disks and disk sequence number) 
        this._headerGSI[272] = '1';
        this._headerGSI[273] = '1';
        
             
        //Country of origin (ESP)
        this._headerGSI[274] = 'E';
        this._headerGSI[275] = 'S';
        this._headerGSI[276] = 'P';
        
        
    }

    public void setOffsetTimeFromFile(boolean value) {
        this._useOffsetTimeFromFile = value;
    }

    private void getLastEditionDateFromHeader(byte[] headerGSI) {
        //-- From byte 512 to byte 532
        if (headerGSI[this.offsetDateUZ]==0x00){ //--Edited previously
            byte bdate[] = new byte[this.sizeDateUZ];
            System.arraycopy(headerGSI, this.offsetDateUZ+1, bdate, 0, this.sizeDateUZ);
            this._lastEditionUZDate = new String(bdate);
        }else{                  //-- Not edited yet
            this._lastEditionUZDate = null;
        }
    }

    private void getLastEditionEditorFromHeader(byte[] headerGSI) {
        //-- From byte 544 to byte 576
        if (headerGSI[this.offsetEditorUZ]==0x00){ //--Edited previously by known editor
            int nbyteseditor = headerGSI[this.offsetEditorUZ+1];
             nbyteseditor = Math.min(nbyteseditor, this.sizeEditorUZ);
            byte bdate[] = new byte[nbyteseditor];
            System.arraycopy(headerGSI, this.offsetEditorUZ+2, bdate, 0, nbyteseditor);
            this._editor = new String(bdate);
        }else{                  //-- Not edited yet
            this._editor = null;
        }
    }


    private void getLastValidationDateFromHeader(byte[] headerGSI) {
        //-- From byte 576 to byte 595
        if (headerGSI[this.offsetVistoBuenoDateUZ]==0x00){ //--Validated previously
            byte bdate[] = new byte[this.sizeDateUZ];
            System.arraycopy(headerGSI, this.offsetVistoBuenoDateUZ+1, bdate, 0, this.sizeDateUZ);
            this._lastVistoBuenoUZDate = new String(bdate);
        }else{                  //-- Not edited yet
            this._lastVistoBuenoUZDate = null;
        }
    }

    private void getLastValidationEditorFromHeader(byte[] headerGSI) {
        //-- From byte 608 to byte 640
        if (headerGSI[this.offsetVistoBuenoEditorUZ]==0x00){ //--Validated previously by kwnow editor
            int nbyteseditor = headerGSI[this.offsetVistoBuenoEditorUZ+1];
            nbyteseditor = Math.min(nbyteseditor, this.sizeEditorUZ);
            byte bdate[] = new byte[nbyteseditor];
            System.arraycopy(headerGSI, this.offsetVistoBuenoEditorUZ+2, bdate, 0, nbyteseditor);
            this._editorVistoBueno = new String(bdate);
        }else{                  //-- Not edited yet
            this._editorVistoBueno = null;
        }
    }

    private void appendLastEditionDateHeaderGSI(byte[] headerGSI) {

        headerGSI[this.offsetDateUZ] = 0x00;

        String str = Subtitle.getCurrentDate();
        byte bdate[] = str.getBytes();
        System.arraycopy(bdate, 0, headerGSI, this.offsetDateUZ+1, this.sizeDateUZ);

    }

    private void appendLastEditionEditorHeaderGSI(byte[] headerGSI) {

        headerGSI[this.offsetEditorUZ]   = 0x00;

        String str = this._editor;
        byte bdate[] = str.getBytes();
        headerGSI[this.offsetEditorUZ+1] = (byte) bdate.length;
        System.arraycopy(bdate, 0, headerGSI, this.offsetEditorUZ+2, Math.min(bdate.length, this.sizeEditorUZ));
    }

    private void appendLastEditionEmptyEditorHeaderGSI(byte[] headerGSI) {
        headerGSI[this.offsetEditorUZ]   = 0x20;
        headerGSI[this.offsetEditorUZ+1] = 0x00;
        for (int i=0; i<this.sizeEditorUZ-1;i++){
            headerGSI[this.offsetEditorUZ+i+2] = 0x20;
        }
    }

    private void appendLastValidationEmptyEditorHeaderGSI(byte[] headerGSI) {
        headerGSI[this.offsetVistoBuenoEditorUZ]   = 0x20;
        headerGSI[this.offsetVistoBuenoEditorUZ]   = 0x20;
        headerGSI[this.offsetVistoBuenoEditorUZ+1] = 0x00;
        for (int i=0; i<this.sizeEditorUZ-1;i++){
            headerGSI[this.offsetVistoBuenoEditorUZ+i+2] = 0x20;
        }
    }

    private void appendLastValidationDateHeaderGSI(byte[] headerGSI) {
        headerGSI[this.offsetVistoBuenoDateUZ] = 0x00;

        String str = Subtitle.getCurrentDate();
        byte bdate[] = str.getBytes();
        System.arraycopy(bdate, 0, headerGSI, this.offsetVistoBuenoDateUZ+1, this.sizeDateUZ);
    }

    private void appendLastValidationEditorHeaderGSI(byte[] headerGSI) {

        headerGSI[this.offsetVistoBuenoEditorUZ] = 0x00;

        String str = this._editorVistoBueno;
        byte bdate[] = str.getBytes();
        headerGSI[this.offsetVistoBuenoEditorUZ+1] = (byte) bdate.length;
        System.arraycopy(bdate, 0, headerGSI, this.offsetVistoBuenoEditorUZ+2, Math.min(bdate.length, this.sizeEditorUZ));
    }



    public String getLastEditionDate() {
        return this._lastEditionUZDate;
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

    public int getFramesPerSecond() {
       return this.framesPerSecond;
    }
    
    public void setNumberOfSubtitles(int nsubtitles){
        this._nSubtitles = nsubtitles;
    }

    @Override
    public void setCoding(String _coding) {
    
    }

    public void setFramesPerSecond(int _framesPerSecond) {
        this.framesPerSecond = _framesPerSecond;
    }

    public void setSTLMetadataInfo(String originalProgramName, String originalEpisodeName, String translatedProgramName, String translatedEpisodeName, 
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

    private void appendMetadataInfoToHeaderGSI(byte[] headerGSI, String info, int offset, int maxSize) {
        
        byte binfo[] = info.getBytes();
        System.arraycopy(binfo, 0, headerGSI, offset, Math.min(binfo.length, maxSize));
    }
    
    private String readMetadataInfoToHeaderGSI(byte[] headerGSI, int offset, int maxSize) {
        
        byte b[] = new byte[maxSize];
        System.arraycopy(headerGSI, offset, b, 0, maxSize);
        return new String(b);

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

    public void setW32Format(boolean value) {
        this._w32format = value;
    }

    void setBottomHeightFormat(boolean value) {
        this._bottomheightformat = value;
    }
 

}
