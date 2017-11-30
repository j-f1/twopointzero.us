/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  Chart
 *  Population$YellowFilter
 *  Table
 */
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class Population
extends Applet
implements Runnable {
    Thread evolveSystemThread;
    boolean evolvingOn = false;
    boolean loadingImages = true;
    float[] points = new float[3];
    public boolean debug = false;
    public boolean clicked = false;
    public boolean exception = false;
    public boolean display_name = false;
    public boolean rotate = false;
    int ix;
    int ixX;
    int ixY;
    int oakIxX;
    int oakIxY;
    int curr_x;
    int curr_y;
    int prev_x;
    int prev_y;
    int pos_x = 10;
    int pos_y = 215;
    int x_size = 607;
    int y_size = 319;
    int oakSeparationX = 100;
    int oakSeparationY = 100;
    int oakNumX;
    int oakNumY;
    int oak_x_min = 5;
    int oak_x_max = 620;
    int oak_y_min = 5;
    int oak_y_max = 305;
    int acornsPerOak = 2200;
    int waterChosen;
    int squirrelStartYear;
    int squirrelStartNumber;
    int hawkStartYear;
    int hawkStartNumber;
    int squirrelBirthRate;
    int hawkBirthRate;
    int oakNumberIx;
    int waterChosenIx;
    int squirrelStartYearIx;
    int squirrelStartNumberIx;
    int hawkStartYearIx;
    int hawkStartNumberIx;
    int squirrelBirthRateIx = 2;
    int hawkBirthRateIx = 2;
    double squirrelBornFactor = 2.2;
    double hawkBornFactor = 1.0;
    double squirrelDeathFactor = 0.6;
    double hawkDeathFactor = 0.6;
    Vector bornSquirrelStack = new Vector(20, 20);
    Vector bornHawkStack = new Vector(24, 20);
    int squirrelsPerHawk = 3;
    boolean sustainPopulations;
    boolean lockedPopulations;
    boolean hawkSecondGrowth = false;
    int yearSecondGrowth = 140;
    int sustainedHawkTarget;
    int lowLockSquirrels;
    int highLockSquirrels;
    int lowLockHawks;
    int highLockHawks;
    int cycleYear;
    int cycleYearCtr;
    boolean cycleUp;
    Chart chart;
    Table table;
    Panel setupPanel;
    boolean setupPanelOn = false;
    public Font bigFont = new Font("Helvetica", 0, 36);
    public Font defaultFont = new Font("Helvetica", 0, 11);
    public Font italicsFont = new Font("Helvetica", 2, 15);
    public Color squirrelGray = new Color(210, 210, 210);
    Image backgroundImage;
    Image bufImage;
    Image chartImage;
    int yearNumber;
    int oakNumber;
    int acornNumber;
    int squirrelNumber;
    int hawkNumber;
    double acornFactor;
    double prevAcornFactor = -1.0;
    int oakStrength;
    boolean oaksTakenCareOff = false;
    Graphics bufGraphics;
    Graphics chartGraphics;
    String msgLoading = "Loading the Graphics for the Population Applet...";
    String msgLoadingOaks = "Loading the Oak Images for the Population Applet...";
    String msgLoadingErr = "Error while loading the Oak Images";
    String msgRunning = "The Population applet is running";
    static final int numOakImages = 51;
    String[] oakImagesGreenNames = new String[51];
    Image[] oakImages;
    Image[] oakImagesGreen = new Image[51];
    Image[] oakImagesYellow = new Image[51];
    ImageFilter filter;
    static final int oakRadius = 25;
    Choice waterAmount;
    List nutrientList;
    boolean[] nutrientsChosen;
    Choice oaksChoiceNumber;
    Choice squirrelChoiceNumber;
    Choice squirrelChoiceYear;
    Choice squirrelChoiceRate;
    Choice hawkChoiceNumber;
    Choice hawkChoiceYear;
    Choice hawkChoiceRate;

    public String getAppletInfo() {
        return new String("Population Applet\nAuthor: Jacobo Bulaevsky \nemail:  jacobo@arcytech.org \nCopyright (c) 2000 Arcytech. All Rights Reserved");
    }

    public void init() {
        try {
            this.resize(this.x_size, this.y_size);
            this.showStatus(this.msgLoading);
            this.bufImage = this.createImage(this.x_size, this.y_size);
            this.bufGraphics = this.bufImage.getGraphics();
            this.repaint();
            MediaTracker tracker = new MediaTracker(this);
            this.backgroundImage = this.getImage(this.getDocumentBase(), "images/population.gif");
            tracker.addImage(this.backgroundImage, 0);
            try {
                tracker.waitForID(0);
            }
            catch (InterruptedException v0) {
                return;
            }
            this.showStatus(this.msgLoadingOaks);
            int i = 0;
            while (i < 51) {
                this.oakImagesGreenNames[i] = "images/oak" + i + ".gif";
                Image image = this.getImage(this.getDocumentBase(), this.oakImagesGreenNames[i]);
                tracker.addImage(image, 1);
                this.oakImagesGreen[i] = image;
                ++i;
            }
            try {
                tracker.waitForID(1);
            }
            catch (InterruptedException v1) {
                return;
            }
            if (tracker.isErrorID(1)) {
                this.showStatus(this.msgLoadingErr);
            }
            this.loadingImages = false;
            this.repaint();
            this.showStatus(this.msgRunning);
            this.setSetupPanel();
            this.table = new Table(" Population Growth Table ");
            this.table.dimension(50, 50, 250, 500);
            this.chart = new Chart(" Population Growth Chart ");
            this.chart.axisNames("Time (years)", "Population");
            this.chart.dimension(100, 200, 600, 240);
            this.chart.numLines(3);
            this.chart.label(1, "Acorns");
            this.chart.label(2, "Squirrels");
            this.chart.label(3, "Hawks");
            this.chart.hide();
            this.resetEvolution();
            this.start();
            this.evolveSystemThread = new Thread(this);
            this.evolveSystemThread.stop();
        }
        catch (Exception e) {
            System.out.println("Exception detected: " + e.getMessage());
            this.exception = true;
            return;
        }
    }

    void resetChoices() {
        this.oaksChoiceNumber.select("0");
        this.waterAmount.select("0");
        this.ix = 0;
        while (this.ix < 7) {
            this.nutrientList.deselect(this.ix);
            ++this.ix;
        }
        this.squirrelChoiceRate.select(2);
        this.hawkChoiceRate.select(2);
        this.squirrelChoiceYear.select("0");
        this.squirrelChoiceNumber.select("0");
        this.hawkChoiceYear.select("0");
        this.hawkChoiceNumber.select("0");
    }

    void cancelChoices() {
        this.oaksChoiceNumber.select(this.oakNumberIx);
        this.waterAmount.select(this.waterChosenIx);
        this.ix = 0;
        while (this.ix < 7) {
            if (this.nutrientsChosen[this.ix]) {
                this.nutrientList.select(this.ix);
            } else {
                this.nutrientList.deselect(this.ix);
            }
            ++this.ix;
        }
        this.squirrelChoiceRate.select(this.squirrelBirthRateIx);
        this.hawkChoiceRate.select(this.hawkBirthRateIx);
        this.squirrelChoiceYear.select(this.squirrelStartYearIx);
        this.squirrelChoiceNumber.select(this.squirrelStartNumberIx);
        this.hawkChoiceYear.select(this.hawkStartYearIx);
        this.hawkChoiceNumber.select(this.hawkStartNumberIx);
    }

    void resetEvolution() {
        this.yearNumber = 0;
        this.acornNumber = 0;
        this.squirrelNumber = 0;
        this.hawkNumber = 0;
        this.bornSquirrelStack.removeAllElements();
        this.bornHawkStack.removeAllElements();
        this.squirrelBirthRate = 3;
        this.hawkBirthRate = 3;
        this.sustainPopulations = false;
        this.lockedPopulations = false;
        this.sustainedHawkTarget = 0;
        this.cycleYear = 10 + this.getRandom(2);
        this.cycleYearCtr = 0;
        this.cycleUp = true;
        this.yearSecondGrowth = 140 + ((int)Math.round(Math.random() * 30.0) - 15);
        this.table.clear();
        this.table.addLine("\tNum of\tNum of\tNum of");
        this.table.addLine("Year\tAcorns\tSquirrels\tHawks");
        this.table.addLine("----------------------------------------------------------");
        this.points[0] = this.acornNumber;
        this.points[1] = this.squirrelNumber;
        this.points[2] = this.hawkNumber;
        this.chart.reset();
        this.chart.repaint();
        this.chart.addDataPoint(this.yearNumber, this.points);
        this.repaint();
        this.oakStrength = this.oakNumber > 0 ? 0 : -1;
    }

    void evolveSystem() {
        if (this.oakNumber > 0) {
            ++this.yearNumber;
            if (this.oaksTakenCareOff && ++this.oakStrength >= 51) {
                --this.oakStrength;
            }
            if (this.sustainPopulations) {
                this.calculateSustainedGrowth();
                if (this.yearNumber == this.yearSecondGrowth && Math.random() <= 0.2) {
                    this.sustainPopulations = false;
                    this.hawkBornFactor = 0.8;
                    this.hawkSecondGrowth = true;
                }
            } else {
                this.calculateNormalGrowth();
            }
            if (this.yearNumber == this.squirrelStartYear) {
                this.squirrelNumber = this.squirrelStartNumber;
                this.bornSquirrelStack.addElement(new Integer(this.squirrelStartNumber));
            }
            if (this.yearNumber == this.hawkStartYear) {
                this.hawkNumber = this.hawkStartNumber;
                this.bornHawkStack.addElement(new Integer(this.hawkStartNumber));
                if ((double)this.hawkNumber >= 0.5 * (double)this.squirrelNumber) {
                    this.squirrelBornFactor = 1.5;
                } else if (this.squirrelNumber >= 60) {
                    this.sustainPopulations = true;
                    this.squirrelDeathFactor = 0.5;
                    this.hawkDeathFactor = 0.5;
                }
            }
            this.table.addLine(" " + this.yearNumber + "\t" + this.acornNumber + "\t" + this.squirrelNumber + "\t" + this.hawkNumber);
            this.points[0] = this.acornNumber;
            this.points[1] = this.squirrelNumber;
            this.points[2] = this.hawkNumber;
            this.chart.addDataPoint(this.yearNumber, this.points);
            this.chart.repaint();
            if (this.evolvingOn && this.yearNumber > 270) {
                this.evolvingOn = false;
                this.evolveSystemThread.stop();
                this.evolveSystemThread = null;
            }
        }
    }

    void calculateSustainedGrowth() {
        this.acornCalculations();
        if (!this.lockedPopulations) {
            if (this.sustainedHawkTarget == 0) {
                this.sustainedHawkTarget = Math.round(this.squirrelNumber / (6 + this.getRandom(1)));
            }
            this.lowLockSquirrels = (int)Math.floor(2.7 * (double)this.sustainedHawkTarget);
            this.highLockSquirrels = (int)Math.ceil(3.3 * (double)this.sustainedHawkTarget);
            if (this.squirrelNumber <= this.highLockSquirrels && this.squirrelNumber >= this.lowLockSquirrels) {
                this.lowLockHawks = (int)Math.floor(0.9 * (double)this.sustainedHawkTarget);
                this.highLockHawks = (int)Math.ceil(1.1 * (double)this.sustainedHawkTarget);
                this.lockedPopulations = true;
            } else if (this.squirrelNumber >= this.lowLockSquirrels) {
                this.squirrelDeathFactor = 0.4;
            }
            this.calculateNormalGrowth();
        } else {
            if (++this.cycleYearCtr >= this.cycleYear) {
                if (this.cycleUp) {
                    this.lowLockSquirrels += 6;
                    this.highLockSquirrels += 6;
                    this.lowLockHawks += 2;
                    this.highLockHawks += 2;
                    this.squirrelNumber += 3;
                    this.cycleUp = false;
                } else {
                    this.lowLockSquirrels -= 6;
                    this.highLockSquirrels -= 6;
                    this.lowLockHawks -= 2;
                    this.highLockHawks -= 2;
                    this.squirrelNumber -= 3;
                    this.cycleUp = true;
                }
                this.cycleYearCtr = 0;
            }
            int newHawkNumber = this.hawkNumber + this.getRandom(1);
            this.hawkNumber = this.hawkNumber > this.highLockHawks + 6 ? (this.hawkNumber -= 2) : (this.hawkNumber > this.highLockHawks ? --this.hawkNumber : (newHawkNumber < this.lowLockHawks ? this.lowLockHawks : newHawkNumber));
            this.squirrelNumber += this.getRandom(2);
            if (this.squirrelNumber > this.highLockSquirrels) {
                this.squirrelNumber = this.highLockSquirrels;
            }
            if (this.squirrelNumber < this.lowLockSquirrels) {
                this.squirrelNumber = this.lowLockSquirrels;
            }
        }
    }

    void calculateNormalGrowth() {
        Integer tmpInteger;
        int totalDeaths;
        int tmpInt;
        int overlap;
        int newSquirrelNumber = this.squirrelNumber;
        int newHawkNumber = this.hawkNumber;
        int deadSquirrelsFood = 0;
        int deadSquirrelsHawks = 0;
        int deadSquirrelsAge = 0;
        int bornSquirrels = 0;
        int deadHawksFood = 0;
        int deadHawksAge = 0;
        int bornHawks = 0;
        this.acornCalculations();
        int maxSquirrels = Math.round(this.acornNumber / 800);
        int maxHawks = Math.round(maxSquirrels / this.squirrelsPerHawk);
        maxHawks += 2;
        if (this.hawkNumber > 0) {
            deadHawksFood = this.hawkNumber - (int)Math.floor(this.squirrelNumber / this.squirrelsPerHawk);
            if (deadHawksFood > this.hawkNumber) {
                deadHawksFood = this.hawkNumber;
            } else if (deadHawksFood < 0) {
                deadHawksFood = 0;
            }
            if (this.bornHawkStack.size() == 20) {
                tmpInteger = (Integer)this.bornHawkStack.elementAt(0);
                this.bornHawkStack.removeElementAt(0);
                deadHawksAge = tmpInteger;
                if (deadHawksAge > this.hawkNumber) {
                    deadHawksAge = this.hawkNumber;
                }
            }
            if ((deadHawksFood -= (overlap = (int)((double)(deadHawksFood + deadHawksAge) * 0.3))) < 0) {
                deadHawksFood = 0;
            }
            if ((deadHawksAge -= overlap) < 0) {
                deadHawksAge = 0;
            }
            totalDeaths = deadHawksFood + deadHawksAge + overlap;
            bornHawks = (int)Math.floor(((double)this.hawkNumber - (double)totalDeaths / this.hawkBornFactor) / 2.0) * this.hawkBirthRate;
            this.bornHawkStack.addElement(new Integer(bornHawks));
            newHawkNumber = this.hawkNumber + bornHawks - totalDeaths;
            int tmpMaxHawks = newHawkNumber >= 2 ? maxHawks + this.getRandom(1) : maxHawks;
            if (newHawkNumber > tmpMaxHawks) {
                totalDeaths += newHawkNumber - tmpMaxHawks;
                newHawkNumber = tmpMaxHawks;
            }
            if (totalDeaths > 0) {
                this.adjustStack(this.bornHawkStack, totalDeaths);
            }
        }
        if (this.squirrelNumber > 0) {
            deadSquirrelsFood = this.squirrelNumber - (int)Math.floor(this.acornNumber / 800);
            if (deadSquirrelsFood > this.squirrelNumber) {
                deadSquirrelsFood = this.squirrelNumber;
            } else if (deadSquirrelsFood < 0) {
                deadSquirrelsFood = 0;
            }
            if (this.bornSquirrelStack.size() == 12) {
                tmpInteger = (Integer)this.bornSquirrelStack.elementAt(0);
                this.bornSquirrelStack.removeElementAt(0);
                deadSquirrelsAge = tmpInteger;
                if (deadSquirrelsAge > this.squirrelNumber) {
                    deadSquirrelsAge = this.squirrelNumber;
                }
            }
            if ((deadSquirrelsHawks = this.hawkNumber * this.squirrelsPerHawk) > this.squirrelNumber) {
                deadSquirrelsHawks = this.squirrelNumber;
            } else if (deadSquirrelsHawks < 0) {
                deadSquirrelsHawks = 0;
            }
            overlap = (int)((double)(deadSquirrelsFood + deadSquirrelsHawks + deadSquirrelsAge) * 0.3);
            if ((deadSquirrelsFood -= overlap) < 0) {
                deadSquirrelsFood = 0;
            }
            if ((deadSquirrelsHawks -= overlap) < 0) {
                deadSquirrelsHawks = 0;
            }
            if ((deadSquirrelsAge -= overlap) < 0) {
                deadSquirrelsAge = 0;
            }
            totalDeaths = deadSquirrelsFood + deadSquirrelsHawks + deadSquirrelsAge + overlap;
            bornSquirrels = (int)Math.floor(((double)this.squirrelNumber - (double)totalDeaths / this.squirrelBornFactor) / 2.0) * this.squirrelBirthRate;
            this.bornSquirrelStack.addElement(new Integer(bornSquirrels));
            newSquirrelNumber = this.squirrelNumber + bornSquirrels - totalDeaths;
            int adjustNumber = totalDeaths;
            if (newSquirrelNumber > maxSquirrels) {
                adjustNumber += newSquirrelNumber - maxSquirrels;
                newSquirrelNumber = maxSquirrels;
            }
            tmpInt = newSquirrelNumber <= 1 ? 0 : this.getRandom(1);
            if (newSquirrelNumber + tmpInt < 0) {
                adjustNumber += newSquirrelNumber;
                newSquirrelNumber = 0;
            } else {
                newSquirrelNumber += tmpInt;
                adjustNumber -= tmpInt;
            }
            if (adjustNumber > 0) {
                this.adjustStack(this.bornSquirrelStack, adjustNumber);
            }
        }
        double factor = newHawkNumber > 1 ? (double)(newHawkNumber - this.hawkNumber) / (double)this.hawkNumber : 0.0;
        if (this.hawkSecondGrowth && factor > 0.8) {
            tmpInt = (int)Math.ceil((double)(newHawkNumber - this.hawkNumber) * 0.33);
            this.hawkNumber += tmpInt;
            this.adjustStack(this.bornHawkStack, newHawkNumber - this.hawkNumber);
        } else if (factor < -0.2) {
            tmpInt = (int)Math.floor((double)(this.hawkNumber - newHawkNumber) * this.hawkDeathFactor);
            this.hawkNumber -= tmpInt;
            this.ix = this.bornHawkStack.size() - 1;
            tmpInteger = (Integer)this.bornHawkStack.elementAt(this.ix);
            this.bornHawkStack.setElementAt(new Integer(tmpInteger + this.hawkNumber - newHawkNumber), this.ix);
        } else {
            this.hawkNumber = newHawkNumber < 0 ? 0 : newHawkNumber;
        }
        factor = newSquirrelNumber > 1 ? (double)(newSquirrelNumber - this.squirrelNumber) / (double)this.squirrelNumber : 0.0;
        if (factor < -0.2) {
            tmpInt = (int)Math.floor((double)(this.squirrelNumber - newSquirrelNumber) * this.squirrelDeathFactor);
            this.squirrelNumber -= tmpInt;
            this.ix = this.bornSquirrelStack.size() - 1;
            tmpInteger = (Integer)this.bornSquirrelStack.elementAt(this.ix);
            this.bornSquirrelStack.setElementAt(new Integer(tmpInteger + this.squirrelNumber - newSquirrelNumber), this.ix);
        } else {
            this.squirrelNumber = newSquirrelNumber;
        }
    }

    void acornCalculations() {
        this.acornNumber = this.yearNumber * 23 - 483;
        if (this.acornNumber < 0) {
            this.acornNumber = 0;
        } else if (this.acornNumber > this.acornsPerOak) {
            this.acornNumber = this.acornsPerOak;
        }
        this.acornNumber = (int)((long)this.acornNumber * Math.round((double)this.oakNumber * this.acornFactor));
    }

    int getRandom(int limit) {
        if (limit == 1) {
            return (int)Math.ceil(Math.random() * 3.0) - 2;
        }
        return (int)Math.ceil(Math.random() * 5.0) - 3;
    }

    void adjustStack(Vector s, int dead) {
        int removed;
        do {
            removed = 0;
            int ix = 0;
            while (ix < s.size() && dead > 0) {
                Integer cnt = (Integer)s.elementAt(ix);
                if (cnt > 0) {
                    s.setElementAt(new Integer(cnt - 1), ix);
                    --dead;
                    ++removed;
                }
                ++ix;
            }
        } while (removed > 0);
    }

    void captureSetup() {
        this.oakNumberIx = this.oaksChoiceNumber.getSelectedIndex();
        this.oakNumber = Integer.parseInt(this.oaksChoiceNumber.getSelectedItem());
        switch (this.oakNumber) {
            case 0: {
                this.oakNumX = 0;
                this.oakNumY = 0;
                break;
            }
            case 1: {
                this.oakNumX = 1;
                this.oakNumY = 1;
                break;
            }
            case 2: {
                this.oakNumX = 2;
                this.oakNumY = 1;
                break;
            }
            case 3: {
                this.oakNumX = 3;
                this.oakNumY = 1;
                break;
            }
            case 4: {
                this.oakNumX = 2;
                this.oakNumY = 2;
                break;
            }
            case 5: 
            case 6: {
                this.oakNumX = 3;
                this.oakNumY = 2;
                break;
            }
            case 7: 
            case 8: {
                this.oakNumX = 4;
                this.oakNumY = 2;
                break;
            }
            case 9: {
                this.oakNumX = 3;
                this.oakNumY = 3;
                break;
            }
            case 10: {
                this.oakNumX = 5;
                this.oakNumY = 2;
                break;
            }
            case 11: 
            case 12: {
                this.oakNumX = 4;
                this.oakNumY = 3;
                break;
            }
            case 13: 
            case 14: 
            case 15: {
                this.oakNumX = 5;
                this.oakNumY = 3;
                break;
            }
            case 16: {
                this.oakNumX = 4;
                this.oakNumY = 4;
                break;
            }
            case 17: 
            case 18: 
            case 19: 
            case 20: {
                this.oakNumX = 5;
                this.oakNumY = 4;
                break;
            }
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: {
                this.oakNumX = 5;
                this.oakNumY = 5;
                break;
            }
            case 26: 
            case 27: 
            case 28: {
                this.oakNumX = 7;
                this.oakNumY = 4;
                break;
            }
            case 29: 
            case 30: 
            case 31: 
            case 32: {
                this.oakNumX = 8;
                this.oakNumY = 4;
                break;
            }
            case 33: 
            case 34: 
            case 35: 
            case 36: {
                this.oakNumX = 9;
                this.oakNumY = 4;
                break;
            }
            case 37: 
            case 38: 
            case 39: 
            case 40: {
                this.oakNumX = 10;
                this.oakNumY = 4;
                break;
            }
            case 41: 
            case 42: 
            case 43: 
            case 44: {
                this.oakNumX = 11;
                this.oakNumY = 4;
                break;
            }
            case 45: {
                this.oakNumX = 9;
                this.oakNumY = 5;
                break;
            }
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: {
                this.oakNumX = 10;
                this.oakNumY = 5;
                break;
            }
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                this.oakNumX = 11;
                this.oakNumY = 5;
                break;
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: {
                this.oakNumX = 12;
                this.oakNumY = 5;
                break;
            }
            default: {
                this.oakNumX = 0;
                this.oakNumY = 0;
            }
        }
        this.oak_x_min = (int)Math.floor((600 - this.oakNumX * 50) / (this.oakNumX + 1) + 3);
        this.oak_y_min = (int)Math.floor((250 - this.oakNumY * 50) / (this.oakNumY + 1) + 3);
        this.oakSeparationX = this.oak_x_min + 47;
        this.oakSeparationY = this.oak_y_min + 47;
        this.squirrelStartYear = Integer.parseInt(this.squirrelChoiceYear.getSelectedItem());
        this.squirrelStartYearIx = this.squirrelChoiceYear.getSelectedIndex();
        this.squirrelStartNumber = Integer.parseInt(this.squirrelChoiceNumber.getSelectedItem());
        this.squirrelStartNumberIx = this.squirrelChoiceNumber.getSelectedIndex();
        this.squirrelBirthRate = Integer.parseInt(this.squirrelChoiceRate.getSelectedItem());
        this.squirrelBirthRateIx = this.squirrelChoiceRate.getSelectedIndex();
        this.hawkStartYear = Integer.parseInt(this.hawkChoiceYear.getSelectedItem());
        this.hawkStartYearIx = this.hawkChoiceYear.getSelectedIndex();
        this.hawkStartNumber = Integer.parseInt(this.hawkChoiceNumber.getSelectedItem());
        this.hawkStartNumberIx = this.hawkChoiceNumber.getSelectedIndex();
        this.hawkBirthRate = Integer.parseInt(this.hawkChoiceRate.getSelectedItem());
        this.hawkBirthRateIx = this.hawkChoiceRate.getSelectedIndex();
        String[] nutrientsSelected = this.nutrientList.getSelectedItems();
        int ix = 0;
        while (ix < 7) {
            this.nutrientsChosen[ix] = this.nutrientList.isSelected(ix);
            ++ix;
        }
        this.oaksTakenCareOff = false;
        this.acornFactor = 0.0;
        int tmp = 0;
        this.waterChosen = Integer.parseInt(this.waterAmount.getSelectedItem());
        this.waterChosenIx = this.waterAmount.getSelectedIndex();
        if (nutrientsSelected.length >= 4 && this.waterChosen > 1000) {
            this.oaksTakenCareOff = true;
            tmp += nutrientsSelected.length - 3;
            if (this.waterChosen >= 2000) {
                ++tmp;
                if (this.waterChosen >= 5000) {
                    ++tmp;
                    if (this.waterChosen >= 10000) {
                        ++tmp;
                        if (this.waterChosen >= 20000) {
                            ++tmp;
                        }
                    }
                }
            }
        }
        this.acornFactor = 0.125 * (double)tmp;
        this.chart.rescaleY(false);
        double tmp_d = this.acornFactor == 1.0 ? (double)this.acornsPerOak : (this.acornFactor == 0.875 ? (double)this.acornsPerOak * this.acornFactor + 25.0 : (this.acornFactor == 0.75 ? (double)this.acornsPerOak * this.acornFactor : (this.acornFactor == 0.625 ? (double)this.acornsPerOak * this.acornFactor + 25.0 : (this.acornFactor == 0.5 ? (double)this.acornsPerOak * this.acornFactor : (double)this.acornsPerOak * 0.375 + 25.0))));
        tmp_d = (double)this.oakNumber * tmp_d;
        this.chart.maxValue(1, (int)tmp_d);
        tmp = (int)Math.ceil((double)this.oakNumber * 0.55) * 5;
        this.chart.maxValue(2, tmp);
        tmp = (int)Math.ceil(tmp / (5 * this.squirrelsPerHawk)) * 5 + 5;
        this.chart.maxValue(3, tmp);
        if (this.acornFactor >= 0.9) {
            this.oakImages = this.oakImagesGreen;
        } else {
            if (this.acornFactor != this.prevAcornFactor) {
                tmp = 0;
                while (tmp < 51) {
                    FilteredImageSource producer = new FilteredImageSource(this.oakImagesGreen[tmp].getSource(), this.filter);
                    this.oakImagesYellow[tmp] = this.createImage(producer);
                    this.bufGraphics.drawImage(this.oakImagesYellow[tmp], 800, 0, this);
                    ++tmp;
                }
                this.prevAcornFactor = this.acornFactor;
            }
            this.oakImages = this.oakImagesYellow;
        }
    }

    void setSetupPanel() {
        this.oaksChoiceNumber = new Choice();
        this.setNumberRange(this.oaksChoiceNumber, 60);
        this.waterAmount = new Choice();
        this.waterAmount.addItem("0");
        this.waterAmount.addItem("1");
        this.waterAmount.addItem("2");
        this.waterAmount.addItem("5");
        this.waterAmount.addItem("10");
        this.waterAmount.addItem("20");
        this.waterAmount.addItem("50");
        this.waterAmount.addItem("100");
        this.waterAmount.addItem("200");
        this.waterAmount.addItem("500");
        this.waterAmount.addItem("1000");
        this.waterAmount.addItem("2000");
        this.waterAmount.addItem("5000");
        this.waterAmount.addItem("10000");
        this.waterAmount.addItem("20000");
        this.nutrientList = new List(5, true);
        this.nutrientList.addItem("Calcium");
        this.nutrientList.addItem("Carbon Dioxide");
        this.nutrientList.addItem("Magnesium");
        this.nutrientList.addItem("Nitrogen");
        this.nutrientList.addItem("Phosphorus");
        this.nutrientList.addItem("Potassium");
        this.nutrientList.addItem("Sulfur");
        Panel oakPanel1 = new Panel();
        oakPanel1.setLayout(new GridLayout(3, 1));
        Panel oakPanel2 = new Panel();
        oakPanel2.setLayout(new GridLayout(6, 1));
        Panel oakPanel3 = new Panel();
        oakPanel3.setLayout(new GridLayout(1, 1));
        oakPanel1.add(new Label("OAKS SETUP MENU", 1));
        oakPanel1.add(new Label(" Total number of Oak trees:"));
        oakPanel1.add(this.oaksChoiceNumber);
        oakPanel2.add(new Label(""));
        oakPanel2.add(new Label(" Amount of total water per"));
        oakPanel2.add(new Label(" tree (in gallons per year)"));
        oakPanel2.add(this.waterAmount);
        oakPanel2.add(new Label(""));
        oakPanel2.add(new Label(" Nutrients (select all needed):"));
        oakPanel3.add(this.nutrientList);
        Panel oakPanel = new Panel();
        oakPanel.setLayout(new GridLayout(3, 1));
        oakPanel.add(oakPanel1);
        oakPanel.add(oakPanel2);
        oakPanel.add(oakPanel3);
        this.squirrelChoiceYear = new Choice();
        this.setNumberRange(this.squirrelChoiceYear, 200);
        this.squirrelChoiceNumber = new Choice();
        this.setNumberRange(this.squirrelChoiceNumber, 200);
        this.squirrelChoiceRate = new Choice();
        this.setNumberRange(this.squirrelChoiceRate, 1, 4);
        this.squirrelChoiceRate.select(2);
        Panel squirrelPanel = new Panel();
        squirrelPanel.setLayout(new GridLayout(11, 2));
        squirrelPanel.add(new Label("SQUIRRELS ", 2));
        squirrelPanel.add(new Label("SETUP MENU", 0));
        squirrelPanel.add(new Label(""));
        squirrelPanel.add(new Label(""));
        squirrelPanel.add(new Label("Migration to ", 2));
        squirrelPanel.add(new Label("the Area", 0));
        squirrelPanel.add(new Label("Quantity", 1));
        squirrelPanel.add(new Label("Year", 1));
        squirrelPanel.add(this.squirrelChoiceNumber);
        squirrelPanel.add(this.squirrelChoiceYear);
        squirrelPanel.add(new Label(""));
        squirrelPanel.add(new Label(""));
        squirrelPanel.add(new Label("Squirrels ", 2));
        squirrelPanel.add(new Label("Birth Rate", 0));
        squirrelPanel.add(this.squirrelChoiceRate);
        squirrelPanel.add(new Label(""));
        squirrelPanel.add(new Label(""));
        squirrelPanel.add(new Label(""));
        this.hawkChoiceYear = new Choice();
        this.setNumberRange(this.hawkChoiceYear, 200);
        this.hawkChoiceNumber = new Choice();
        this.setNumberRange(this.hawkChoiceNumber, 200);
        this.hawkChoiceRate = new Choice();
        this.setNumberRange(this.hawkChoiceRate, 1, 4);
        this.hawkChoiceRate.select(2);
        Panel hawkPanel = new Panel();
        hawkPanel.setLayout(new GridLayout(11, 2));
        hawkPanel.add(new Label("HAWKS SE", 2));
        hawkPanel.add(new Label("TUP MENU", 0));
        hawkPanel.add(new Label(""));
        hawkPanel.add(new Label(""));
        hawkPanel.add(new Label("Migration to ", 2));
        hawkPanel.add(new Label("the Area", 0));
        hawkPanel.add(new Label("Quantity", 1));
        hawkPanel.add(new Label("Year", 1));
        hawkPanel.add(this.hawkChoiceNumber);
        hawkPanel.add(this.hawkChoiceYear);
        hawkPanel.add(new Label(""));
        hawkPanel.add(new Label(""));
        hawkPanel.add(new Label("Hawks Bi", 2));
        hawkPanel.add(new Label("rth Rate", 0));
        hawkPanel.add(this.hawkChoiceRate);
        hawkPanel.add(new Label(""));
        hawkPanel.add(new Label(""));
        hawkPanel.add(new Label(""));
        Panel choicesPanel = new Panel();
        choicesPanel.setLayout(new GridLayout(0, 3, 40, 10));
        Color darkBlue = new Color(0, 0, 150);
        Color lightBlue = new Color(141, 232, 255);
        oakPanel.setBackground(lightBlue);
        squirrelPanel.setBackground(lightBlue);
        hawkPanel.setBackground(lightBlue);
        choicesPanel.add(oakPanel);
        choicesPanel.add(squirrelPanel);
        choicesPanel.add(hawkPanel);
        choicesPanel.setBackground(Color.white);
        choicesPanel.setForeground(Color.red);
        choicesPanel.validate();
        Panel setupButtons = new Panel();
        Button cancelButton = new Button("Cancel");
        Button resetButton = new Button("Reset");
        Button setButton = new Button("OK");
        setupButtons.add(cancelButton);
        setupButtons.add(resetButton);
        setupButtons.add(setButton);
        this.setupPanel = new Panel();
        this.setupPanel.setLayout(new BorderLayout());
        this.setupPanel.add("North", new Label(" Setup Menus to Define the Initial Conditions ", 1));
        this.setupPanel.add("Center", choicesPanel);
        this.setupPanel.add("South", setupButtons);
        this.setupPanel.add("East", new Label(""));
        this.setupPanel.add("West", new Label(""));
        this.setupPanel.validate();
        this.setBackground(Color.white);
        this.setForeground(Color.red);
        this.setLayout(new BorderLayout());
        this.add("Center", this.setupPanel);
        this.setupPanel.hide();
    }

    void setNumberRange(Choice numberChoice, int max) {
        this.ix = 0;
        while (this.ix <= max) {
            numberChoice.addItem(String.valueOf(this.ix));
            ++this.ix;
        }
    }

    void setNumberRange(Choice numberChoice, int min, int max) {
        this.ix = min;
        while (this.ix <= max) {
            numberChoice.addItem(String.valueOf(this.ix));
            ++this.ix;
        }
    }

    public boolean mouseDown(Event e, int x, int y) {
        this.clicked = true;
        this.curr_x = x;
        this.curr_y = y;
        try {
            if (this.curr_y >= 260 && this.curr_y <= 313 && !this.setupPanelOn) {
                if (this.curr_x <= 2) {
                    this.debug = !this.debug;
                } else if (this.curr_x >= 9 && this.curr_x <= 66) {
                    if (!this.setupPanelOn && !this.evolvingOn) {
                        this.setupPanelOn = true;
                        this.setupPanel.show();
                        this.validate();
                    }
                } else if (this.curr_x >= 71 && this.curr_x <= 128) {
                    if (this.evolvingOn) {
                        this.evolvingOn = false;
                        this.evolveSystemThread.stop();
                        this.evolveSystemThread = null;
                    }
                    this.resetEvolution();
                    if (this.debug) {
                        System.out.println("Reset");
                    }
                } else if (this.curr_x >= 133 && this.curr_x <= 190) {
                    this.evolveSystem();
                    this.repaint();
                    if (this.debug) {
                        System.out.println("Step");
                    }
                } else if (this.curr_x >= 195 && this.curr_x <= 252) {
                    if (this.evolvingOn) {
                        this.evolvingOn = false;
                        this.evolveSystemThread.stop();
                        this.evolveSystemThread = null;
                        if (this.debug) {
                            System.out.println("Stop");
                        }
                    }
                } else if (this.curr_x >= 257 && this.curr_x <= 313) {
                    if (!this.evolvingOn && this.oakNumber > 0) {
                        this.evolvingOn = true;
                        this.evolveSystemThread = new Thread(this);
                        this.evolveSystemThread.start();
                        if (this.debug) {
                            System.out.println("Play");
                        }
                    }
                } else if (this.curr_x >= 318 && this.curr_x <= 375) {
                    if (this.chart.chartOn) {
                        this.chart.chartOn = false;
                        this.chart.hide();
                    } else {
                        this.chart.chartOn = true;
                        this.chart.show();
                    }
                } else if (this.curr_x >= 380 && this.curr_x <= 436) {
                    if (this.table.tableOn) {
                        this.table.tableOn = false;
                        this.table.hide();
                    } else {
                        this.table.tableOn = true;
                        this.table.show();
                    }
                } else if (this.curr_x >= 441 && this.curr_x <= 498) {
                    try {
                        URL infoUrl = new URL(this.getDocumentBase(), "instructions.html");
                        this.getAppletContext().showDocument(infoUrl, "_self");
                    }
                    catch (MalformedURLException v0) {}
                }
            }
        }
        catch (Exception e2) {
            System.out.println("Exception2 detected: " + e2.getMessage());
            e2.printStackTrace();
            this.exception = true;
        }
        return true;
    }

    public boolean handleEvent(Event ev) {
        return super.handleEvent(ev);
    }

    public boolean action(Event e, Object arg) {
        if (e.target instanceof Button) {
            String label = ((Button)e.target).getLabel();
            if (label.equals("Cancel")) {
                if (this.debug) {
                    System.out.println(label);
                }
                this.cancelChoices();
                this.closeSetupPanel();
            } else if (label.equals("Reset")) {
                if (this.debug) {
                    System.out.println("Reset");
                }
                this.resetChoices();
                this.resetEvolution();
            } else if (label.equals("OK")) {
                if (this.debug) {
                    System.out.println("OK");
                }
                this.captureSetup();
                this.resetEvolution();
                this.closeSetupPanel();
            }
        } else if (!(e.target instanceof Choice) && !(e.target instanceof TextField) && this.debug) {
            System.out.println("ETARGET: " + e.target);
        }
        this.repaint();
        return true;
    }

    public void closeSetupPanel() {
        if (this.evolvingOn) {
            this.evolvingOn = false;
            this.evolveSystemThread.stop();
            this.evolveSystemThread = null;
            if (this.debug) {
                System.out.println("Stop");
            }
        }
        this.setupPanelOn = false;
        this.setupPanel.hide();
    }

    public void run() {
        do {
            this.evolveSystem();
            this.repaint();
            try {
                Thread.sleep(300);
                continue;
            }
            catch (InterruptedException v0) {
                continue;
            }
            break;
        } while (true);
    }

    public void update(Graphics g) {
        this.paint(g);
    }

    public void paint(Graphics g) {
        int y;
        int counter;
        int x;
        this.bufGraphics.drawImage(this.backgroundImage, 0, 0, this);
        if (this.oakStrength >= 0) {
            counter = 0;
            this.oakIxX = 1;
            this.ixX = this.oak_x_min;
            while (this.oakIxX <= this.oakNumX) {
                this.oakIxY = 1;
                this.ixY = this.oak_y_min;
                while (this.oakIxY <= this.oakNumY && ++counter <= this.oakNumber) {
                    this.bufGraphics.drawImage(this.oakImages[this.oakStrength], this.ixX, this.ixY, this);
                    ++this.oakIxY;
                    this.ixY += this.oakSeparationY;
                }
                ++this.oakIxX;
                this.ixX += this.oakSeparationX;
            }
        }
        if (this.squirrelNumber > 0) {
            this.bufGraphics.setColor(this.squirrelGray);
            counter = 0;
            while (counter < this.squirrelNumber) {
                x = (int)Math.round(Math.random() * 591.0) + 7;
                y = (int)Math.round(Math.random() * 240.0) + 7;
                this.bufGraphics.drawLine(x, y, x, y);
                ++counter;
            }
        }
        if (this.hawkNumber > 0) {
            counter = 0;
            while (counter < this.hawkNumber) {
                x = (int)Math.round(Math.random() * 591.0) + 7;
                y = (int)Math.round(Math.random() * 240.0) + 7;
                int orient = (int)Math.floor(Math.random() * 4.0);
                switch (orient) {
                    case 0: 
                    case 4: {
                        this.bufGraphics.setColor(Color.black);
                        this.bufGraphics.drawLine(x, y, x, y);
                        this.bufGraphics.drawLine(x - 2, y + 1, x + 2, y + 1);
                        this.bufGraphics.setColor(Color.red);
                        this.bufGraphics.drawLine(x, y + 2, x, y + 2);
                        break;
                    }
                    case 1: {
                        this.bufGraphics.setColor(Color.black);
                        this.bufGraphics.drawLine(x, y, x, y);
                        this.bufGraphics.drawLine(x - 1, y - 2, x - 1, y + 2);
                        this.bufGraphics.setColor(Color.red);
                        this.bufGraphics.drawLine(x - 2, y, x - 2, y);
                        break;
                    }
                    case 2: {
                        this.bufGraphics.setColor(Color.black);
                        this.bufGraphics.drawLine(x, y, x, y);
                        this.bufGraphics.drawLine(x - 2, y - 1, x + 2, y - 1);
                        this.bufGraphics.setColor(Color.red);
                        this.bufGraphics.drawLine(x, y - 2, x, y - 2);
                        break;
                    }
                    case 3: {
                        this.bufGraphics.setColor(Color.black);
                        this.bufGraphics.drawLine(x, y, x, y);
                        this.bufGraphics.drawLine(x + 1, y - 2, x + 1, y + 2);
                        this.bufGraphics.setColor(Color.red);
                        this.bufGraphics.drawLine(x + 2, y, x + 2, y);
                        break;
                    }
                }
                ++counter;
            }
        }
        this.bufGraphics.setColor(Color.white);
        this.bufGraphics.fillRect(509, 258, 94, 58);
        this.bufGraphics.setFont(this.defaultFont);
        this.bufGraphics.setColor(Color.red);
        this.bufGraphics.drawString("Year:", 513, 271);
        this.bufGraphics.drawString(Integer.toString(this.yearNumber), 563, 271);
        this.bufGraphics.drawString("Acorns:", 513, 284);
        this.bufGraphics.drawString(Integer.toString(this.acornNumber), 563, 284);
        this.bufGraphics.drawString("Squirrels:", 513, 297);
        this.bufGraphics.drawString(Integer.toString(this.squirrelNumber), 563, 297);
        this.bufGraphics.drawString("Hawks:", 513, 310);
        this.bufGraphics.drawString(Integer.toString(this.hawkNumber), 563, 310);
        if (this.debug) {
            this.bufGraphics.setColor(Color.white);
            this.bufGraphics.drawString("Mouse: " + this.curr_x + "," + this.curr_y, 5, 295);
        }
        if (this.loadingImages) {
            this.bufGraphics.setColor(Color.red);
            this.bufGraphics.setFont(this.bigFont);
            this.bufGraphics.drawString("Loading images...", 140, 160);
        }
        g.drawImage(this.bufImage, 0, 0, this);
    }

    public Population() {
        this.filter = new YellowFilter(this);
        this.nutrientsChosen = new boolean[7];
    }
}
