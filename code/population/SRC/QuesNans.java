/*
 * Decompiled with CFR 0_110.
 */
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class QuesNans
extends Applet {
    static final int MAXLINES = 250;
    static final int BW = 8;
    static final int TABSIZE = 40;
    static final int TABHSIZE = 20;
    static final int NONE = 0;
    static final int QUESTION = 1;
    static final int ANSWER = 2;
    static final int END = 3;
    static final Color coverGrey = new Color(230, 230, 230);
    Font font;
    Font fontCopy;
    String[] question = new String[250];
    String[] answer = new String[250];
    StringBuffer buildLine = new StringBuffer("");
    InputStream is;
    Image bufImage;
    Image initImage;
    Graphics bufGraphics;
    Graphics initGraphics;
    Rectangle tabrect;
    boolean tabGrabbed = false;
    int ix;
    int ix2;
    int lineHeight;
    int hlineHeight;
    int questNumLines;
    int ansNumLines;
    int questHeight;
    int ansHeight;
    int ansStart;
    int totalWidth;
    int totalHeight;
    int tabmin;
    int tabmax;
    int tabx;
    int taby;
    int x_delta;
    int height;
    Label keyLabel;
    TextField keyField;
    Panel keyPanel;
    String keyWord;
    int x_key;
    boolean keyRequested;

    public void init() {
        String controlFile;
        this.font = new Font("TimesRoman", 0, 14);
        this.fontCopy = new Font("TimesRoman", 0, 9);
        this.ix = 0;
        while (this.ix < 250) {
            this.question[this.ix] = null;
            this.answer[this.ix] = null;
            ++this.ix;
        }
        controlFile = this.getParameter("ctrlfile");
        if (controlFile == null) {
            this.question[0] = "ERROR: No 'ctrlfile' parameter provided in applet";
            this.questNumLines = 1;
        } else {
            try {
                URL url = new URL(this.getDocumentBase(), controlFile);
                this.is = url.openStream();
            }
            catch (MalformedURLException v0) {
                this.question[0] = "ERROR: [01] Can not find or open file '" + controlFile + "'";
                this.questNumLines = 1;
            }
            catch (IOException v1) {
                this.question[0] = "ERROR: [02] Can not find or open file '" + controlFile + "'";
                this.questNumLines = 1;
            }
        }
        if (this.question[0] == null) {
            String line;
            int mode = 0;
            while ((line = this.getLine()) != null) {
                if (line.startsWith("QUESTION")) {
                    this.ix = 0;
                    mode = 1;
                    continue;
                }
                if (line.startsWith("ANSWER")) {
                    this.questNumLines = this.ix;
                    this.ix = 0;
                    mode = 2;
                    continue;
                }
                if (line.startsWith("END")) {
                    this.ansNumLines = this.ix;
                    this.ix = 0;
                    mode = 3;
                    continue;
                }
                if (line.startsWith("KEY:")) {
                    this.keyWord = line.substring(5);
                    this.keyWord.trim();
                    continue;
                }
                if (mode == 1) {
                    this.question[this.ix] = line;
                    ++this.ix;
                    continue;
                }
                if (mode != 2) continue;
                this.answer[this.ix] = line;
                ++this.ix;
            }
        }
        try {
            this.is.close();
        }
        catch (IOException v2) {}
        Graphics g = this.getGraphics();
        g.setFont(this.font);
        FontMetrics fm = g.getFontMetrics();
        this.lineHeight = 17;
        this.hlineHeight = this.lineHeight / 2;
        this.questHeight = this.questNumLines * this.lineHeight + this.hlineHeight;
        this.ansHeight = this.ansNumLines * this.lineHeight + this.hlineHeight;
        if (this.ansHeight < 40) {
            this.ansHeight = 40;
        }
        this.totalHeight = this.questHeight + this.ansHeight + 24;
        Dimension dim = this.size();
        this.totalWidth = dim.width;
        this.ansStart = this.questHeight + 8;
        this.taby = this.ansStart + 8 + this.ansHeight / 2;
        this.initImage = this.createImage(this.totalWidth + 1, this.totalHeight + 1);
        this.initGraphics = this.initImage.getGraphics();
        this.bufImage = this.createImage(this.totalWidth + 1, this.totalHeight + 1);
        this.bufGraphics = this.bufImage.getGraphics();
        if (dim.height != this.totalHeight + 1) {
            System.out.println("WARNING: Set height of " + controlFile + " applet to " + (this.totalHeight + 1) + " not " + dim.height);
        }
        this.resize(this.totalWidth + 1, this.totalHeight + 1);
        this.initGraphics.setFont(this.font);
        this.initGraphics.setColor(Color.white);
        this.initGraphics.fillRect(0, 0, this.totalWidth + 1, this.totalHeight + 1);
        this.initGraphics.setColor(Color.blue);
        this.ix = 0;
        this.height = this.lineHeight + 8;
        while (this.ix < this.questNumLines) {
            this.initGraphics.drawString(this.question[this.ix], 16, this.height);
            ++this.ix;
            this.height += this.lineHeight;
        }
        this.ix = 0;
        this.height = this.ansStart + this.lineHeight + 8;
        while (this.ix < this.ansNumLines) {
            this.initGraphics.drawString(this.answer[this.ix], 16, this.height);
            ++this.ix;
            this.height += this.lineHeight;
        }
        this.ix = 0;
        while (this.ix < 8) {
            this.initGraphics.drawRoundRect(this.ix, this.ix, this.totalWidth - this.ix * 2, this.totalHeight - this.ix * 2, 16, 16);
            ++this.ix;
        }
        this.initGraphics.drawRect(7, this.ansStart - 1, this.totalWidth - 16, 8);
        this.initGraphics.setColor(Color.white);
        this.initGraphics.setFont(this.fontCopy);
        this.initGraphics.drawString("Copyright (c) 1998 Arcytech. All Rights Reserved", 50, this.totalHeight);
        this.tabmax = this.totalWidth + 1 - 16;
        this.tabrect = new Rectangle(this.tabx, this.taby - 20, 40, 40);
        if (this.keyWord != null) {
            this.keyField.setEchoCharacter('*');
            this.keyPanel.resize(176, 22);
            this.keyPanel.move(100, this.taby - 11);
            this.keyPanel.setBackground(coverGrey);
            this.keyPanel.setLayout(new FlowLayout(0, 0, 0));
            this.keyPanel.add(this.keyLabel);
            this.keyPanel.add(this.keyField);
            this.setLayout(new BorderLayout());
            this.add("Center", this.keyPanel);
            this.keyPanel.hide();
        }
    }

    public String getLine() {
        int EOF = -1;
        int EOL = 10;
        int CR = 13;
        char[] buffer = new char[1024];
        String str = "";
        int nextInt = 123;
        int ix = 0;
        try {
            while ((nextInt = this.is.read()) != EOF && nextInt != EOL) {
                if (nextInt == CR) continue;
                buffer[ix++] = (char)nextInt;
            }
            str = new String(buffer, 0, ix);
        }
        catch (IOException v0) {
            System.err.println("ERROR: IO Exception from read() caught ");
        }
        return nextInt == EOF ? null : str;
    }

    public boolean mouseDown(Event e, int x, int y) {
        if (this.tabrect.inside(x, y)) {
            this.tabGrabbed = true;
            this.x_delta = x - this.tabx;
        }
        return true;
    }

    public boolean mouseUp(Event e, int x, int y) {
        this.tabGrabbed = false;
        return true;
    }

    public boolean mouseDrag(Event e, int x, int y) {
        if (!this.tabGrabbed) {
            return true;
        }
        if (this.keyWord != null) {
            this.tabx = x - this.x_delta;
            if (this.tabx >= this.x_key) {
                if (!this.keyRequested) {
                    this.keyPanel.show();
                    this.keyRequested = true;
                }
                this.tabx = this.x_key;
            }
        } else {
            this.tabx = x - this.x_delta;
        }
        if (this.tabx >= this.tabmax) {
            this.tabx = this.tabmax;
        } else if (this.tabx <= this.tabmin) {
            this.tabx = this.tabmin;
        }
        this.tabrect = new Rectangle(this.tabx, this.taby - 20, 40, 40);
        this.repaint();
        return true;
    }

    public boolean action(Event e, Object o) {
        if (e.target instanceof TextField) {
            if (this.keyWord.equals(o)) {
                this.keyWord = null;
                this.keyPanel.hide();
            }
            return true;
        }
        return false;
    }

    public void update(Graphics g) {
        this.paint(g);
    }

    public void paint(Graphics g) {
        this.bufGraphics.drawImage(this.initImage, 0, 0, this);
        this.bufGraphics.setColor(coverGrey);
        this.bufGraphics.fillRoundRect(this.tabx - 8, this.ansStart, this.totalWidth - this.tabx + 8, this.totalHeight - this.ansStart, 16, 16);
        this.bufGraphics.setColor(Color.blue);
        this.ix = 0;
        while (this.ix < 8) {
            this.bufGraphics.drawRoundRect(this.tabx - 8 + this.ix, this.ansStart + this.ix, this.totalWidth - this.tabx - this.ix * 2 + 8, this.totalHeight - this.ix * 2 - this.ansStart, 16, 16);
            ++this.ix;
        }
        this.bufGraphics.setColor(Color.lightGray);
        Polygon new_tab = new Polygon();
        this.bufGraphics.fillRect(this.tabx, this.taby - 20, 40, 40);
        this.bufGraphics.setColor(Color.white);
        int xref = this.tabx + 5;
        int yref = this.taby - 20 + 5;
        this.ix = 0;
        this.ix2 = 0;
        while (this.ix < 16) {
            this.bufGraphics.draw3DRect(xref + this.ix2, yref + this.ix, 3, 30 - this.ix2, false);
            this.ix += 2;
            this.ix2 += 4;
        }
        this.bufGraphics.setColor(Color.blue);
        this.bufGraphics.fillRect(this.totalWidth - 8 + 1, this.taby - 20, 8, 40);
        g.drawImage(this.bufImage, 0, 0, this);
    }

    public QuesNans() {
        this.tabx = this.tabmin = 8;
        this.keyLabel = new Label("Enter key:");
        this.keyField = new TextField("", 12);
        this.keyPanel = new Panel();
        this.x_key = 40;
        this.keyRequested = false;
    }
}
