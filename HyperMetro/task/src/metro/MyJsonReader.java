package metro;

import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MyJsonReader {
    JsonReader jsonReader;
    List<MetroStation> metroStationList;

    public MyJsonReader(List<MetroStation> metroStationList, HashSet<TimeLine> timeLineHashSet, String fileName) {
        this.metroStationList = metroStationList;
        readFromFile(fileName,  timeLineHashSet);
    }


    public void readFromFile(String fileName, HashSet<TimeLine> timeLineHashSet) {
        try {
            jsonReader = new JsonReader(new FileReader(fileName));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String lineName = jsonReader.nextName();
                readStationsInLine(jsonReader, timeLineHashSet, lineName);
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            System.out.println("Error! Such a file doesn't exist!");
        }

    }

    private void readStationsInLine(JsonReader jsonReader, HashSet<TimeLine> timeLineHashSet , String lineName) throws IOException {
        String stationName = "";
        List<String> prevStationArray = new ArrayList<>();
        List<String> nextStationArray = new ArrayList<>();
        List<Station> transferArray = new ArrayList<>();
        int time = 0;

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "name" -> stationName = jsonReader.nextString();
                    case "prev" -> prevStationArray = readStationArray(jsonReader);
                    case "next" -> nextStationArray = readStationArray(jsonReader);
                    case "transfer" -> transferArray = readTransferArray(jsonReader);
                    case "time" -> time = jsonReader.nextInt();
                }
            }
            Station stationPlusLine = new Station(lineName, stationName);
            MetroStation metroStation = new MetroStation(stationPlusLine, prevStationArray, nextStationArray, transferArray, time);
            createTimeLineArray(timeLineHashSet, stationPlusLine,prevStationArray,nextStationArray,time);
            metroStationList.add(metroStation);
            jsonReader.endObject();
        }
        jsonReader.endArray();
    }

    private void createTimeLineArray(HashSet<TimeLine> timeLineHashSet, Station stationPlusLine, List<String> prevStationArray, List<String> nextStationArray, int time) {
        for (String st : nextStationArray) {
            TimeLine timeLine = new TimeLine(stationPlusLine.line, stationPlusLine.station, st, time);
            timeLineHashSet.add(timeLine);
            timeLine = new TimeLine(stationPlusLine.line,st, stationPlusLine.station, time);
            timeLineHashSet.add(timeLine);
        }
    }

    private List<Station> readTransferArray(JsonReader jsonReader) throws IOException {
        List<Station> stationsList = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            jsonReader.nextName();
            String line = jsonReader.nextString();
            jsonReader.nextName();
            String station = jsonReader.nextString();
            stationsList.add(new Station(line,station));
            jsonReader.endObject();
        }
        jsonReader.endArray();
        return stationsList;
    }

    private List<String> readStationArray(JsonReader jsonReader) throws IOException {
        List<String> stationsList = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            stationsList.add(jsonReader.nextString());
        }
        jsonReader.endArray();
        return stationsList;
    }
}
