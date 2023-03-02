package metro;

import java.sql.Time;

public class TimeLine {
    String line;
    String station1;
    String station2;
    double time;
    //double timeFromStart = 0;

    TimeLine(String line, String station1, String station2, double time) {
        this.line = line;
        this.station1 = station1;
        this.station2 = station2;
        this.time = time;
    }

}
