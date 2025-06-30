import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CampusNavigatorNetwork implements Serializable {
    static final long serialVersionUID = 11L;
    public double averageCartSpeed;
    public final double averageWalkingSpeed = 1000 / 6.0;
    public int numCartLines;
    public Station startPoint;
    public Station destinationPoint;
    public List<CartLine> lines;

    public String getStringVar(String varName, String fileContent) {

        Pattern p = Pattern.compile("(?i)[\\s]*" + varName + "[\\s]*=[\\s]*\"([^\"]+)\"");
        Matcher m = p.matcher(fileContent);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public Double getDoubleVar(String varName, String fileContent) {

        Pattern p = Pattern.compile("[\\s]*" + varName + "[\\s]*=[\\s]*([0-9]+(?:\\.[0-9]+)?)");
        Matcher m = p.matcher(fileContent);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return null;
    }

    public int getIntVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    public Point getPointVar(String varName, String fileContent) {
        Point p = new Point(0, 0);
        Pattern pattern = Pattern.compile("[\\s]*" + varName + "[\\s]*=[\\s]*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new Point(x, y);
        }

        return new Point(0, 0); // fallback
    }

    public List<CartLine> getCartLines(String fileContent) {
        List<CartLine> cartLines = new ArrayList<>();

        Pattern blockP = Pattern.compile("cart_line_name\\s*=\\s*\"([^\"]+)\"[\\s\\S]*?cart_line_stations\\s*=\\s*((?:\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*)+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = blockP.matcher(fileContent);

        while (matcher.find()) {
            String lineName = matcher.group(1);
            String stationBlock = matcher.group(2);

            List<Station> stations = new ArrayList<>();
            Pattern pointP = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            Matcher pointMatcher = pointP.matcher(stationBlock);

            int stIndex = 1;
            while (pointMatcher.find()) {
                int x = Integer.parseInt(pointMatcher.group(1));
                int y = Integer.parseInt(pointMatcher.group(2));
                String label = lineName + " Station " + stIndex++;
                Station station = new Station(new Point(x, y), label);
                stations.add(station);
            }

            cartLines.add(new CartLine(lineName, stations));
        }

        return cartLines;
    }

    public void readInput(String filename) {

        try {
            Scanner scanner = new Scanner(new java.io.File(filename));
            scanner.useDelimiter("\\Z");
            String content = scanner.next();
            scanner.close();

            this.numCartLines = getIntVar("num_cart_lines", content);
            this.startPoint = new Station(getPointVar("starting_point", content), "Starting Point");
            this.destinationPoint = new Station(getPointVar("destination_point", content), "Final Destination");
            // km/h to m/min (1000/60)
            this.averageCartSpeed = getDoubleVar("average_cart_speed", content) * 100.0 / 6.0;
            this.lines = getCartLines(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
