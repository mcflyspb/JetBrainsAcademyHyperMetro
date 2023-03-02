package metro;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

public class MetroStation {
    Station stationPlusLine;
    String prevStation;
    String prevLine;
    boolean visited = false;
    List<String> prevStationArray = new ArrayList<>();
    List<String> nextStationArray = new ArrayList<>();
    List<Station> transferArray = new ArrayList<>();
    int time = 0;
    double timeFromStart = 999999;

    public MetroStation(Station stationPlusLine, List<String> prevStationArray, List<String> nextStationArray, List<Station> transferArray, int time) {
        this.stationPlusLine = stationPlusLine;
        this.prevStationArray = prevStationArray;
        this.nextStationArray = nextStationArray;
        this.transferArray = transferArray;
        this.time = time;
    }

    public void setTimeFromStart (double timeFromStart) {
        this.timeFromStart = timeFromStart;
    }
    public double getTimeFromStart () {
        return timeFromStart;
    }

    public void setVisited () {
        this.visited = true;
    }
    public boolean isVisited () {
        return visited;
    }

    public void setPrevLine(String prevLine) {
        this.prevLine = prevLine;
    }

    public void setPrevStation(String prevStation) {
        this.prevStation = prevStation;
    }
}
