/*
 * Decompiled with CFR 0_110.
 */
class Chart.dataPoint {
    int x;
    float[] y;

    Chart.dataPoint(int x, float[] y) {
        Chart.this = Chart.this;
        this.y = new float[3];
        this.x = x;
        int ix = 0;
        while (ix < y.length) {
            this.y[ix] = y[ix];
            ++ix;
        }
    }
}
