import java.io.Serializable;
import java.util.*;

public class CampusNavigatorApp implements Serializable {
    static final long serialVersionUID = 99L;

    public HashMap<Station, Station> predecessors = new HashMap<>(); // didnt used
    public HashMap<Set<Station>, Double> times = new HashMap<>(); // didnt used

    public CampusNavigatorNetwork readCampusNavigatorNetwork(String filename) {
        CampusNavigatorNetwork network = new CampusNavigatorNetwork();
        network.readInput(filename);
        return network;
    }

    public List<RouteDirection> getFastestRouteDirections(CampusNavigatorNetwork network) {
        List<RouteDirection> routeDirections = new ArrayList<>();

        // Build the start, destination, and all cart stations
        List<Station> allStations = new ArrayList<>();
        allStations.add(network.startPoint);
        allStations.add(network.destinationPoint);
        for (CartLine line : network.lines) {
            allStations.addAll(line.cartLineStations);
        }

        // Dijkstra algorithm
        Map<Station, Double> dist = new HashMap<>();
        Map<Station, Station> prev = new HashMap<>();
        Map<Station, RouteDirection> routeMap = new HashMap<>();
        for (Station s : allStations) {
            dist.put(s, Double.POSITIVE_INFINITY);
        }
        dist.put(network.startPoint, 0.0);

        PriorityQueue<Station> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(network.startPoint);

        while (!pq.isEmpty()) {
            Station curr = pq.poll();
            double currTime = dist.get(curr);

            // walk to possible stations
            for (Station next : allStations) {
                if (next.equals(curr)) continue;
                double distance = getDistance(curr.coordinates, next.coordinates);
                double walkTime = distance / network.averageWalkingSpeed;
                double newTime = currTime + walkTime;

                if (newTime < dist.get(next)) {
                    dist.put(next, newTime);
                    prev.put(next, curr);
                    routeMap.put(next, new RouteDirection(curr.description, next.description, walkTime, false));
                    pq.add(next);
                }
            }

            // cart rides if possible
            for (CartLine line : network.lines) {
                List<Station> stations = line.cartLineStations;
                for (int i = 0; i < stations.size() - 1; i++) {
                    Station s1 = stations.get(i), s2 = stations.get(i + 1);
                    double distance = getDistance(s1.coordinates, s2.coordinates);
                    double rideTime = distance / network.averageCartSpeed;

                    // from s1 to s2
                    if (curr.equals(s1) && currTime + rideTime < dist.get(s2)) {
                        dist.put(s2, currTime + rideTime);
                        prev.put(s2, s1);
                        routeMap.put(s2, new RouteDirection(s1.description, s2.description, rideTime, true));
                        pq.add(s2);
                    }

                    //from s2 to s1
                    if (curr.equals(s2) && currTime + rideTime < dist.get(s1)) {
                        dist.put(s1, currTime + rideTime);
                        prev.put(s1, s2);
                        routeMap.put(s1, new RouteDirection(s2.description, s1.description, rideTime, true));
                        pq.add(s1);
                    }
                }
            }
        }

        // reeconstruct path from destination to start
        Station curr = network.destinationPoint;
        while (!curr.equals(network.startPoint)) {
            RouteDirection dir = routeMap.get(curr);
            if (dir == null) {
                break;
            }
            routeDirections.add(dir);
            curr = prev.get(curr);
        }

        Collections.reverse(routeDirections);
        return routeDirections;
    }

    public void printRouteDirections(List<RouteDirection> directions) {

        double total = 0.0;
        for (RouteDirection dir: directions) {
            total += dir.duration;
        }

        System.out.printf("The fastest route takes %d minute(s).\n", Math.round(total));
        System.out.println("Directions");
        System.out.println("----------");
        int steps = 1;
        for (RouteDirection dir : directions) {
            String action = dir.cartRide ? "Ride the cart" : "Walk";
            System.out.printf("%d. %s from \"%s\" to \"%s\" for %.2f minutes.\n",
                    steps++, action, dir.startStationName, dir.endStationName, dir.duration);
        }
    }
    private double getDistance(Point a, Point b) {
        int dx = a.x - b.x;
        int dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
