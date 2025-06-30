import java.io.Serializable;
import java.util.*;

public class Project implements Serializable {
    static final long serialVersionUID = 33L;
    private final String name;
    private final List<Task> tasks;

    public Project(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public int getProjectDuration() {
        int projectDuration = 0;

        int[] schedule = getEarliestSchedule();

        for (int i = 0; i < tasks.size(); i++) {
            int end = schedule[i] + tasks.get(i).getDuration();
            if (end > projectDuration) projectDuration = end;
        }
        return projectDuration;
    }

    public int[] getEarliestSchedule() {

        int n = tasks.size();
        int[] earliestStart = new int[n];

        // create adj list and in degree array for topological sort
        Map<Integer, List<Integer>> adj = new HashMap<>();
        int[] inDegree = new int[n];

        for (Task task : tasks) {
            int id = task.getTaskID();
            adj.putIfAbsent(id, new ArrayList<>());

            for (int dep : task.getDependencies()) {
                adj.computeIfAbsent(dep, k -> new ArrayList<>()).add(id);
                inDegree[id]++;
            }
        }

        // topological sort using Kahns algorithm
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            Task uTask = tasks.get(u);
            int uEndtime = earliestStart[u] + uTask.getDuration();

            for (int v : adj.getOrDefault(u, new ArrayList<>())) {
                earliestStart[v] = Math.max(earliestStart[v], uEndtime);
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }
        return earliestStart;
    }

    public static void printlnDash(int limit, char symbol) {
        for (int i = 0; i < limit; i++) System.out.print(symbol);
        System.out.println();
    }

    public void printSchedule(int[] schedule) {
        int limit = 65;
        char symbol = '-';
        printlnDash(limit, symbol);
        System.out.println(String.format("Project name: %s", name));
        printlnDash(limit, symbol);

        // print  header
        System.out.println(String.format("%-10s%-45s%-7s%-5s","Task ID","Description","Start","End"));
        printlnDash(limit, symbol);
        for (int i = 0; i < schedule.length; i++) {
            Task t = tasks.get(i);
            System.out.println(String.format("%-10d%-45s%-7d%-5d", i, t.getDescription(), schedule[i], schedule[i]+t.getDuration()));
        }
        printlnDash(limit, symbol);
        System.out.println(String.format("Project will be completed in %d days.", tasks.get(schedule.length-1).getDuration() + schedule[schedule.length-1]));
        printlnDash(limit, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;

        int equal = 0;

        for (Task otherTask : ((Project) o).tasks) {
            if (tasks.stream().anyMatch(t -> t.equals(otherTask))) {
                equal++;
            }
        }

        return name.equals(project.name) && equal == tasks.size();
    }

}
