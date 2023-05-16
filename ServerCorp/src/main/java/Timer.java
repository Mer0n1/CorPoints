class Timer {
    private double time_begin;
    private double time_end;

    Timer(double time) {
        time_end = time;
        start();
    }

    public void start() { time_begin = System.currentTimeMillis(); }
    public double getTime() { return (System.currentTimeMillis() - time_begin) / 1000; }
    public boolean isAlive() {
        if ((System.currentTimeMillis() - time_begin) / 1000 > time_end)
            return false;
        else
            return true;
    }
}
