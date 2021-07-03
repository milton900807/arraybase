package clinic.gene.shell.genomics;

public class LSC{
    private String l = null;
    private String r = null;
    private String c = null;
    private  int center_start;
    private String core;

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }


    public String getFullSequence ( ) {
        return getL() + getC() + getR();
    }

    public int getCenter_start() {
        return center_start;
    }

    public void setCenter_start(int center_start) {
        this.center_start = center_start;
    }

    public String getCore() {
        return core;
    }

    public void setCore(String core) {
        this.core = core;
    }
}
