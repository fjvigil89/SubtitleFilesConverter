/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitles;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vivolab
 */
public class SubtitleSetCompare {

    public static final int COMPARE_WITHOUT_TIMES=0, COMPARE_WITH_TIMES=1;
    
    
    private final int max_len_search = 60;
    private boolean applyOffsetOfFirstEqualSubtitle;
    private float   offsetDiff;
    private float   marginTimeError;
    private List <String>  vverbose;
    
    public int nRefSubtitles;
    public int nTestSubtitles;
    public int nEqualSubtitles;      //-- Numero de subtitulos exactamente iguales
    public int nEqualWordsSubtitles; //-- Numero de subtitulos con las mismas palabras (se diferencian en el corte de linea)
    
    public SubtitleSetCompare(SubtitleSet ssref, SubtitleSet sstest, int compare_mode, List <String> vverbose, float marginTimeError, boolean applyOffsetOfFirstEqualSubtitle) throws Exception {
        
        this.applyOffsetOfFirstEqualSubtitle = applyOffsetOfFirstEqualSubtitle;
        this.offsetDiff = Float.MIN_VALUE;
        this.marginTimeError = marginTimeError;
        this.vverbose = vverbose;
        
        switch (compare_mode){
            case COMPARE_WITHOUT_TIMES:
                SubtitleSetCompareWithoutTimes(ssref, sstest);
                break;
            case COMPARE_WITH_TIMES:
                SubtitleSetCompareWithTimes(ssref, sstest);
                break;
            default:
                throw new Exception ("Unknown compare format");
        }
    }
            
    public SubtitleSetCompare(SubtitleSet ssref, SubtitleSet sstest, int compare_mode) throws Exception {
        
        
        this.applyOffsetOfFirstEqualSubtitle = false;
        this.offsetDiff = 0.0F;
        this.marginTimeError = 0.0F;
        this.vverbose = new ArrayList();
        
        switch (compare_mode){
            case COMPARE_WITHOUT_TIMES:
                SubtitleSetCompareWithoutTimes(ssref, sstest);
                break;
            case COMPARE_WITH_TIMES:
                SubtitleSetCompareWithTimes(ssref, sstest);
                break;
            default:
                throw new Exception ("Unknown compare format");
        }
    }

    private void SubtitleSetCompareWithoutTimes(SubtitleSet ssref, SubtitleSet sstest) throws Exception{
        
        ArrayList <Subtitle> vref  = ssref.getVsubtitles();
        ArrayList <Subtitle> vtest = sstest.getVsubtitles();
        
        this.nRefSubtitles  = vref.size();
        this.nTestSubtitles = vtest.size();
        this.nEqualSubtitles = 0;
        this.nEqualWordsSubtitles = 0;
        
        int t,r,initest = 0;
        boolean next=false;
        for (r=0; r<nRefSubtitles; r++){
            Subtitle sr = vref.get(r);
            if (vverbose.contains("--vcheck")) System.out.println("Ref " + r + " \n " + sr.getAllLines());
            int endsearch = Math.min(initest+max_len_search, this.nTestSubtitles);
            for (t=initest; t<endsearch; t++){
                Subtitle st = vtest.get(t);
                //System.out.println("\tCompare with " + t + " txt: " + st.getAllLines());
                if (sr.equalsWithoutTime(st)){
                    if (vverbose.contains("--vcheck")){ System.out.print("#Equal# ");}
                    this.nEqualSubtitles ++;
                    initest = t+1;
                    next = true;
                }
                if (sr.equalWordsWithoutTime(st)){
                    if (vverbose.contains("--vcheck")){ System.out.println("#Equal Words#"); /*System.out.println("Test " + t + " \n " + st.getAllLines()) ;*/}
                    this.nEqualWordsSubtitles ++;
                    initest = t+1;
                    next = true;
                }
                
                if (next){
                    next=false;
                    break;
                }
                
            } // for t
            
            if (vverbose.contains("--vcheck")) if (t==endsearch) { System.out.println("#NOTFOUND#"); }
            if (vverbose.contains("--vcheck")) System.in.read();
            
        } //for r
    }

    private void SubtitleSetCompareWithTimes(SubtitleSet ssref, SubtitleSet sstest) throws Exception {
                
        ArrayList <Subtitle> vref  = ssref.getVsubtitles();
        ArrayList <Subtitle> vtest = sstest.getVsubtitles();
        
        this.nRefSubtitles  = vref.size();
        this.nTestSubtitles = vtest.size();
        this.nEqualSubtitles = 0;
        this.nEqualWordsSubtitles = 0;
        
        int r,t,initest = 0;
        boolean next=false;
        for (r=0; r<nRefSubtitles; r++){
            Subtitle sr = vref.get(r);
            //System.out.println("Ref " + r + " txt: " + sr.getAllLines());
            int endsearch = Math.min(initest+max_len_search, this.nTestSubtitles);
            for (t=initest; t<endsearch; t++){
                Subtitle st = vtest.get(t);
                if (vverbose.contains("--vcheck")) System.out.println("Ref " + r + " \n " + sr.getAllLines());
                //System.out.println("\tCompare with " + t + " txt: " + st.getAllLines());
                
                if (this.applyOffsetOfFirstEqualSubtitle){
                if (this.offsetDiff == Float.MIN_VALUE){
                if (sr.equalsWithoutTime(st)){
                    this.offsetDiff = sr.getTimeIniFloat() - st.getTimeIniFloat();
                }}}
                
                
                if (sr.equalsWithTime(st, offsetDiff, marginTimeError)){
                    //System.out.println("#Equal#");
                    if (vverbose.contains("--vcheck")){ System.out.print("#Equal# ");}
                    this.nEqualSubtitles ++;
                    initest = t+1;
                    next = true;
                }
                if (sr.equalWordsWithoutTime(st)){
                    //System.out.println("#Equal Words#");
                    if (vverbose.contains("--vcheck")){ System.out.println("#Equal Words#"); /*System.out.println("\nTest " + t + " \n " + st.getAllLines()) ;*/}
                    this.nEqualWordsSubtitles ++;
                    initest = t+1;
                    next = true;
                }
                //System.in.read();
                if (next){
                    next=false;
                    break;
                }
                
            } // for t
                        
            if (vverbose.contains("--vcheck")) if (t==endsearch) { System.out.println("#NOTFOUND#"); }
            if (vverbose.contains("--vcheck")) System.in.read();
            
        } //for r
    }
    
    public void setEnabledOffsetOfFirstEqualSubtitle(boolean val){
        this.applyOffsetOfFirstEqualSubtitle = val;
    }
    
}
