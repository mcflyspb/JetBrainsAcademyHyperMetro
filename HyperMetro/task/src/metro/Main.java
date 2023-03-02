package metro;

import java.util.*;

public class Main {
    static List[] paths = new List[10000];
    static double[] timeList = new double[10000];
    static List<MetroStation> metroStationList = new LinkedList<>();
    static HashSet<TimeLine> timeLineHashSet = new HashSet<>();
    public static void main(String[] args) {
        new MyJsonReader(metroStationList, timeLineHashSet, args[0]);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userInput = scanner.nextLine();
            String[] parseUserInputArray = parseUserInput(userInput);
            String command = parseUserInputArray[0];

            switch (command) {
                case "/exit" -> {
                    return;
                }
                case "/route" -> {
                    routeLine(parseUserInputArray);
                }

                case "/fastest-route" -> {
                    fatestRouteLine(parseUserInputArray);
                }
            }
        }


    }

    private static void fatestRouteLine(String[] parseUserInputArray) {
        String sourceLine = parseUserInputArray[1];
        String sourceStation = parseUserInputArray[2];
        String targetLine = parseUserInputArray[3];
        String targetStation = parseUserInputArray[4];
        Station firstPoint = new Station(sourceLine,sourceStation);
        Station targetPoint = new Station(targetLine,targetStation);
        updateMetroStation(firstPoint.line,firstPoint.station, firstPoint.line ,firstPoint.station, 0);
        double time = 0;

        for (int x = 0; x < 15; x++) {
            List<Station> stationsArray = getAllVisitedStation();
            for (Station cp : stationsArray) {
                HashSet<Station> neighbors = getNeighborStations(cp);
                for (Station nei : neighbors) {
                    if (!Objects.equals(cp.line,nei.line)) {
                        time = getTimeFromMetroStation(cp.line,cp.station) + 5;
                    } else {
                        time = getTimeLineHashSet(cp.line,cp.station,nei.station) + getTimeFromMetroStation(cp.line,cp.station);
                    }

                    if (time <= getTimeFromMetroStation(nei.line,nei.station)) {
                        //System.out.printf("update %s %s %f\n", nei.line,nei.station,time);
                        updateMetroStation(nei.line,nei.station, cp.line, cp.station, time);
                    }
                }
            }
        }
        printFastRouteResult(firstPoint,targetPoint);
    }

    private static void printFastRouteResult(Station firstPoint, Station targetPoint) {
        List<Station> printList = new ArrayList<>();
        //System.out.println(targetPoint.station);
        double time = getTimeFromMetroStation(targetPoint.line,targetPoint.station);
        do {
            printList.add(targetPoint);
            targetPoint = getPrevStation(targetPoint);
        } while (!(Objects.equals(targetPoint.line,firstPoint.line) && Objects.equals(targetPoint.station,firstPoint.station)));
        printList.add(targetPoint);
        Collections.reverse(printList);
        String prevLine = printList.get(0).line;
        for (Station st : printList) {
            if (!Objects.equals(st.line,prevLine)) {
                System.out.println(st.line);
                prevLine = st.line;
            }
            System.out.println(st.station);
        }

        System.out.printf("Total: %d minutes in the way\n",(int)time);
    }

    private static Station getPrevStation(Station targetPoint) {
        Station returnStation = null;
        for (MetroStation m : metroStationList) {
            if (Objects.equals(m.stationPlusLine.line,targetPoint.line) && Objects.equals(m.stationPlusLine.station,targetPoint.station)) {
                returnStation = new Station(m.prevLine,m.prevStation);
            }
        }
        return returnStation;
    }

    private static List<Station> getAllVisitedStation() {
        List<Station> returnList = new ArrayList<>();
        for (MetroStation m : metroStationList) {
            if (m.isVisited()) {
                returnList.add(m.stationPlusLine);
            }
        }
        return returnList;
    }

    private static void updateMetroStation(String line, String station, String prevLine, String prevStation, double time) {
        for (MetroStation m : metroStationList) {
            if (Objects.equals(m.stationPlusLine.line,line) && Objects.equals(m.stationPlusLine.station,station)) {
                m.setTimeFromStart(time);
                m.setPrevStation(prevStation);
                m.setPrevLine(prevLine);
                m.setVisited();
                return;
            }
        }
    }

    private static double getTimeFromMetroStation(String line, String station) {
        for (MetroStation m : metroStationList) {
            if (Objects.equals(m.stationPlusLine.line,line) && Objects.equals(m.stationPlusLine.station,station)) {
                return m.timeFromStart;
            }
        }
        //System.out.printf("Error getTimeFromMetroStation line: %s %s\n",line,station);
        return 0;
    }

    private static double getTimeLineHashSet(String line, String station1, String station2) {
        for (TimeLine tl : timeLineHashSet) {
            if (Objects.equals(line,tl.line) && Objects.equals(station1,tl.station1) && Objects.equals(station2,tl.station2)) {
                return tl.time;
            }
        }
        //System.out.printf("Error getTimeLineHashSet line: %s %s %s\n",line,station1,station2);
        return 0;
    }


    private static void routeLine(String[] parseUserInputArray) {
        String sourceLine = parseUserInputArray[1];
        String sourceStation = parseUserInputArray[2];
        String targetLine = parseUserInputArray[3];
        String targetStation = parseUserInputArray[4];
        Station targetLineStation = new Station(targetLine,targetStation);
        createFirstPath(sourceLine, sourceStation);
        findAllPathes(targetLineStation);

    }


    private static void printRouteResult(List<Station> routePath) {
        String prevLine = routePath.get(0).line;
        for (Station r : routePath) {
            if (!Objects.equals(prevLine,r.line)) {
                System.out.println("Transition to line " + r.line);
            }
            System.out.println(r.station);
            prevLine = r.line;
        }
    }

    private static void findAllPathes(Station targetLineStation) {
        int i = 0;
        while (true) {
            for (int x = 0; x < paths.length; x++) {
                if (paths[x] != null) {
                    analyseThisPath(x, targetLineStation);
                }
            }

            i++;
            if (i == 30) {
                createTimeListArrayWithTimeByEachPath();
                int numberOfFastestPath = numberOfFastestPath();
                List<Station> tempList = (List<Station>) paths[numberOfFastestPath];

                if (Objects.equals(tempList.get(0).station, "Brixton")) {
                    printAllPaths();
                }
                printRouteResult(tempList);
                return;
            }
        }
    }

    private static int numberOfFastestPath() {
        int minTime = 100000;
        int numberOfFastestPath = 0;
        for (int x = 0; x < timeList.length; x++) {
            if (timeList[x] != 0) {
                if (timeList[x] < minTime) {
                    numberOfFastestPath = x;
                }
            }
        }
        return numberOfFastestPath;
    }

    private static void createTimeListArrayWithTimeByEachPath() {
        for (int x = 0; x < paths.length; x++) {
            if (paths[x] != null) {
                List<Station> tempList = (List<Station>) paths[x];
                timeList[x] = calculatePathTime(tempList);
                //System.out.println(x +" - "+ timeList[x]);
            }
        }
    }

    private static double calculatePathTime(List<Station> tempList) {
        double time = 0;
        String prevLine = tempList.get(0).line;
        String prevStation = tempList.get(0).station;
        String curentLine = "";
        String currentStation = "";
        for (int x = 1; x < tempList.size(); x++) {
            curentLine = tempList.get(x).line;
            currentStation =  tempList.get(x).station;
            if (!Objects.equals(curentLine,prevLine)) {
                time += 5;
                continue;
            }

            for (TimeLine tl : timeLineHashSet) {
                if (Objects.equals(currentStation,tl.station1) && Objects.equals(prevStation, tl.station2)) {
                    //System.out.println(time);
                    time += tl.time;
                }
            }

            prevLine = curentLine;
            prevStation = currentStation;
        }
        return time;
    }

    private static void printAllPaths() {
        for (int x = 0; x < paths.length; x++) {
            if (paths[x] != null) {
                System.out.printf("path: %d ",x);
                List<Station> stList = (List<Station>) paths[x];
                for (Station st : stList)  {
                    System.out.printf(" %s - %s ", st.line,st.station);
                }
                System.out.println();
            }
        }
    }

    private static void analyseThisPath(int pathNumber, Station targetLineStation) {
        List<Station> stations = paths[pathNumber];
        List<Station> etalonList = new ArrayList<>(stations);
        Station lastPoint = stations.get(stations.size() - 1);

        //Target station found, end of programm
        if (Objects.equals(lastPoint.line,targetLineStation.line) && Objects.equals(lastPoint.station,targetLineStation.station)) {
            deleteAllPathButNotShortest();
            printRouteResult(stations);
            System.exit(0);
        }

        HashSet<Station> neighbors = getNeighborStations(lastPoint);

        int addCount = 0;
        for (Station st : neighbors) {
            if (findStationInPath(st, targetLineStation)) {
                continue;
            }

            if (addCount == 0) {
                if (addStationToPath(lastPoint, st)) {
                    addCount++;
                }
            } else {
                addNewPath(st, etalonList);
                addCount++;
            }
        }
    }

    private static void deleteAllPathButNotShortest() {
        int minStations = 999;
        int numberOfShortestPath = 0;
        for (int x = 0; x < paths.length; x++) {
            if (paths[x] != null) {
                List<Station> stationList = (List<Station>) paths[x];
                if (stationList.size() < minStations) {
                    minStations = stationList.size();
                    numberOfShortestPath = x;
                }
            }
        }
        for (int x = 0; x < paths.length; x++) {
            if (x == numberOfShortestPath) {
                continue;
            }
            paths[x] = null;
        }
    }

    private static void addNewPath(Station st, List<Station> etalonList) {
        int newArrayNumber = getFreePathNumber();
        List<Station> newAddList = new ArrayList<>(etalonList);
        newAddList.add(st);
        paths[newArrayNumber] = newAddList;
    }


    private static int getFreePathNumber() {
        for (int x = paths.length - 1; x > 0; x--) {
            if (paths[x] == null) {
                return x;
            }
        }
        return 0;
    }

    private static boolean findStationInPath(Station oneRoute, Station targetLineStation) {
        for (int x = 0; x < paths.length; x++) {
            if (paths[x] != null) {
                List<Station> tempList = (List<Station>) paths[x];
                for (Station routeFromList : tempList)  {
                    if (Objects.equals(oneRoute.line, targetLineStation.line) && Objects.equals(oneRoute.station, targetLineStation.station)) {
                        //System.out.println("findStationInRouteLine TRUE " + oneRoute.station);
                        return false;
                    }
                    if (Objects.equals(oneRoute.line, routeFromList.line) && Objects.equals(oneRoute.station, routeFromList.station)) {
                        //System.out.println("findStationInRouteLine TRUE " + oneRoute.station);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean addStationToPath(Station cp, Station oneRoute) {
        for (int x = 0; x < paths.length; x++) {
            if (paths[x] != null) {
                List<Station> tempList = (List<Station>) paths[x];
                Station lastRoute = tempList.get(tempList.size() - 1);
                if (Objects.equals(cp.line, lastRoute.line) && Objects.equals(cp.station, lastRoute.station)) {
                    tempList.add(oneRoute);
                    //System.out.println("add: " + oneRoute.line + " - " + oneRoute.station + " to list: " + x);
                    paths[x] = tempList;
                    return true;
                }
            }
        }
        return false;
    }

    private static HashSet<Station> getNeighborStations(Station lastPoint) {
        HashSet<Station> neighbors = new HashSet<>();
        String line = lastPoint.line;
        for (MetroStation m : metroStationList) {
            // stations next & transfer
            if (Objects.equals(lastPoint.line, m.stationPlusLine.line) && Objects.equals(lastPoint.station, m.stationPlusLine.station) ) {
                //// transfer
                for (Station st :m.transferArray) {
                    neighbors.add(st);
                }
                //// next
                for (String s :m.nextStationArray) {
                    neighbors.add(new Station(line,s));
                }
            }

            // prev in others stations
            for (String st :m.prevStationArray) {
                if (Objects.equals(m.stationPlusLine.line,line) && Objects.equals(st,lastPoint.station)) {
                    neighbors.add(new Station(line,m.stationPlusLine.station));
                }
            }

            // next in others stations
            for (String st :m.nextStationArray) {
                if (Objects.equals(m.stationPlusLine.line,line) && Objects.equals(st,lastPoint.station)) {
                    neighbors.add(new Station(line,m.stationPlusLine.station));
                }
            }
        }
        return neighbors;
    }


    private static HashSet<Station> getNeighborStationsNotVisited(Station lastPoint) {
        HashSet<Station> neighbors = new HashSet<>();
        String line = lastPoint.line;
        for (MetroStation m : metroStationList) {
            // stations next & transfer
            if (Objects.equals(lastPoint.line, m.stationPlusLine.line) && Objects.equals(lastPoint.station, m.stationPlusLine.station) ) {
                //// transfer
                for (Station st :m.transferArray) {
                    neighbors.add(st);
                }
                //// next
                for (String s :m.nextStationArray) {
                    neighbors.add(new Station(line,s));
                }
            }

            // prev in others stations
            for (String st :m.prevStationArray) {
                if (Objects.equals(m.stationPlusLine.line,line) && Objects.equals(st,lastPoint.station)) {
                    neighbors.add(new Station(line,m.stationPlusLine.station));
                }
            }

            // next in others stations
            for (String st :m.nextStationArray) {
                if (Objects.equals(m.stationPlusLine.line,line) && Objects.equals(st,lastPoint.station)) {
                    neighbors.add(new Station(line,m.stationPlusLine.station));
                }
            }
        }
        return neighbors;
    }

    private static void createFirstPath(String sourceLine, String sourceStation) {
        Station firstPoint = new Station(sourceLine,sourceStation);
        List<Station> firstPathList = new ArrayList<>();
        firstPathList.add(firstPoint);
        paths[paths.length - 1] = firstPathList;
    }


    private static String[] parseUserInput(String userInput) {
        String[] userInputArray = userInput.split("");
        StringBuilder stringBuilder = new StringBuilder();
        boolean outside = true;
        for (int x = 0; x < userInputArray.length; x++) {
            if (Objects.equals(userInputArray[x]," ") && outside) {
                userInputArray[x] = "*";
            }
            if (Objects.equals(userInputArray[x],"\"")) {
                outside = !outside;
            }
            stringBuilder.append(userInputArray[x]);
        }
        String pastCommand = stringBuilder.toString().replaceAll("\"","");
        String[] pastCommandArray = pastCommand.split("\\*");
        String command = pastCommandArray[0];
        String firstLine = "";
        String firstStation = "";
        String secondLine = "";
        String secondStation = "";

        if (pastCommandArray.length > 1) {
            firstLine = pastCommandArray[1];
        }

        if (pastCommandArray.length > 2) {
            firstStation = pastCommandArray[2];
        }

        if (pastCommandArray.length > 3) {
            secondLine = pastCommandArray[3];
        }

        if (pastCommandArray.length > 4) {
            secondStation = pastCommandArray[4];
        }
        return new String[] {command, firstLine, firstStation, secondLine, secondStation};
    }
}

