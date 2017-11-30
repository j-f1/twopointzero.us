/*
 * Decompiled with CFR 0_110.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.TextArea;

public class Table
extends Frame {
    boolean tableOn = false;
    TextArea textArea;

    Table(String title) {
        super(title);
        this.setBackground(Color.white);
        this.textArea = new TextArea();
        this.textArea.setEditable(false);
        this.textArea.setText("");
        this.setLayout(new BorderLayout());
        this.add("Center", this.textArea);
        this.pack();
    }

    public void dimension(int x, int y, int width, int height) {
        this.reshape(x, y, width, height);
    }

    public void addLine(String line) {
        this.textArea.appendText(String.valueOf(line) + '\n');
    }

    public void clear() {
        this.textArea.setText("");
    }

    public boolean handleEvent(Event ev) {
        if (ev.id == 201) {
            this.tableOn = false;
            this.hide();
            return true;
        }
        return super.handleEvent(ev);
    }
}
