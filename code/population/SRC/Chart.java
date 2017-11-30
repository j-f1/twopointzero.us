/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  Chart$dataPoint
 */
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Vector;

public class Chart
extends Frame {
    boolean chartOn = false;
    boolean autoScaleY = true;
    static final int maxLines = 3;
    int numLines;
    String xAxisName;
    String yAxisName;
    int xChartOrig;
    int yChartOrig;
    int width;
    int height;
    Vector dataSet;
    Color[] lineColors = new Color[]{Color.red, Color.green, Color.blue};
    String[] labels = new String[3];
    int[] labelOffset = new int[]{100, 250, 400};
    float[] yMax = new float[3];
    float[] yFactor = new float[3];

    Chart(String title) {
        super(title);
    }

    public void dimension(int x, int y, int width, int height) {
        this.xChartOrig = x;
        this.yChartOrig = y;
        this.width = width;
        this.height = height;
        this.reshape(x, y, width, height + 25);
        this.setBackground(Color.white);
        this.reset();
    }

    public void numLines(int number) {
        this.numLines = number;
    }

    public void axisNames(String xAxis, String yAxis) {
        this.xAxisName = xAxis;
        this.yAxisName = yAxis;
    }

    public void label(int number, String name) {
        this.labels[number - 1] = name;
    }

    public void reset() {
        this.dataSet = new Vector(10, 10);
    }

    public void addDataPoint(int x, float[] y) {
        this.dataSet.addElement(new dataPoint(this, x, y));
    }

    public void rescaleY(boolean rescale) {
        this.autoScaleY = rescale;
    }

    public void maxValue(int number, int max) {
        int y0 = this.height - 40;
        int y_max = 40;
        this.yMax[number - 1] = max;
        this.yFactor[number - 1] = (float)(y0 - y_max) / this.yMax[number - 1];
    }

    public boolean handleEvent(Event ev) {
        if (ev.id == 201) {
            this.chartOn = false;
            this.hide();
            return true;
        }
        return super.handleEvent(ev);
    }

    public void paint(Graphics g) {
        int yTmp;
        String strTmp;
        dataPoint d;
        int x0 = 50;
        boolean displayXValue = false;
        int y0 = this.height - 40;
        int x_max = this.width - 10;
        int y_max = 40;
        int xTmp = (x_max - x0) / this.dataSet.size();
        int xSpacing = xTmp >= 25 ? 25 : (xTmp >= 15 ? 15 : (xTmp >= 5 ? 5 : 2));
        g.setColor(Color.black);
        g.drawLine(x0 - 10, y0, x_max, y0);
        int ix = 1;
        while (ix < this.dataSet.size()) {
            xTmp = x0 + ix * xSpacing;
            g.drawLine(xTmp, y0 + 2, xTmp, y0 - 1);
            displayXValue = false;
            d = (dataPoint)this.dataSet.elementAt(ix);
            if (xSpacing >= 20) {
                displayXValue = true;
            } else if (xSpacing >= 15) {
                if (ix % 2 == 0) {
                    displayXValue = true;
                }
            } else if (xSpacing >= 10) {
                if (ix % 3 == 0) {
                    displayXValue = true;
                }
            } else if (xSpacing >= 5) {
                if (ix % 4 == 0 && (d.x <= 96 || d.x % 8 == 0)) {
                    displayXValue = true;
                }
            } else if (ix % 10 == 0 && (d.x <= 100 || d.x % 20 == 0)) {
                displayXValue = true;
            }
            if (displayXValue) {
                g.drawLine(xTmp, y0 + 4, xTmp, y0 - 2);
                strTmp = Integer.toString(d.x);
                g.drawString(strTmp, xTmp + 2, y0 + 13);
            }
            ++ix;
        }
        d = (dataPoint)this.dataSet.elementAt(0);
        strTmp = Integer.toString(d.x);
        g.drawString(strTmp, x0 + 2, y0 + 13);
        g.drawString(this.xAxisName, x0, this.height - 7);
        g.drawLine(x0, y0 + 10, x0, y_max - 20);
        int ySpacing = (y0 - y_max) / 4;
        ix = 1;
        while (ix <= 4) {
            yTmp = y0 - ix * ySpacing;
            g.drawLine(x0 - 3, yTmp, x0 + 3, yTmp);
            strTmp = String.valueOf(25 * ix) + "%";
            g.drawString(strTmp, 10, yTmp + 5);
            ++ix;
        }
        g.drawString(this.yAxisName, x0 + 10, y_max - 5);
        if (this.autoScaleY) {
            ix = 0;
            while (ix < this.dataSet.size()) {
                d = (dataPoint)this.dataSet.elementAt(ix);
                int ix2 = 0;
                while (ix2 < d.y.length) {
                    if (d.y[ix2] > this.yMax[ix2]) {
                        this.yMax[ix2] = d.y[ix2];
                    }
                    ++ix2;
                }
                ++ix;
            }
            ix = 0;
            while (ix < d.y.length) {
                this.yFactor[ix] = (float)(y0 - y_max) / this.yMax[ix];
                ++ix;
            }
        }
        int lineCtr = 0;
        while (lineCtr < this.numLines) {
            g.setColor(this.lineColors[lineCtr]);
            this.drawMarker(g, lineCtr, x0 + this.labelOffset[lineCtr] - 8, this.height - 11);
            strTmp = "Max " + this.labels[lineCtr] + " = " + this.yMax[lineCtr];
            g.drawString(strTmp, x0 + this.labelOffset[lineCtr], this.height - 7);
            int xPrev = x0;
            d = (dataPoint)this.dataSet.elementAt(0);
            int yPrev = (int)((float)y0 - this.yFactor[lineCtr] * d.y[lineCtr]);
            this.drawMarker(g, lineCtr, xPrev, yPrev);
            ix = 1;
            while (ix < this.dataSet.size()) {
                xTmp = x0 + ix * xSpacing;
                d = (dataPoint)this.dataSet.elementAt(ix);
                yTmp = (int)((float)y0 - this.yFactor[lineCtr] * d.y[lineCtr]);
                g.drawLine(xPrev, yPrev, xTmp, yTmp);
                xPrev = xTmp;
                yPrev = yTmp;
                this.drawMarker(g, lineCtr, xPrev, yPrev);
                ++ix;
            }
            ++lineCtr;
        }
    }

    void drawMarker(Graphics g, int type, int x, int y) {
        switch (type) {
            case 0: {
                int[] xArray = new int[]{x - 3, x, x + 3, x - 3};
                int[] yArray = new int[]{y + 2, y - 3, y + 2, y + 2};
                g.fillPolygon(xArray, yArray, 4);
                break;
            }
            case 1: {
                g.fillRect(x - 2, y - 2, 4, 4);
                break;
            }
            case 2: {
                g.fillOval(x - 2, y - 2, 4, 4);
                break;
            }
        }
    }
}
