import java.io.Serializable;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.io.File;

public class ClubFairSetupPlanner implements Serializable {
    static final long serialVersionUID = 88L;

    public void printSchedule(List<Project> projectList) {

        for (Project project: projectList) {
            int[] sch = project.getEarliestSchedule();
            project.printSchedule(sch);
        }
    }

    public List<Project> readXML(String filename) {
        List<Project> projectList = new ArrayList<>();
        try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList projectNodes = doc.getElementsByTagName("Project");

            for (int i = 0; i < projectNodes.getLength(); i++) {
                Node projectNode = projectNodes.item(i);
                if (projectNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element projectEle = (Element) projectNode;
                    String projectName = projectEle.getElementsByTagName("Name").item(0).getTextContent();

                    List<Task> tasks = new ArrayList<>();
                    NodeList taskNodes = projectEle.getElementsByTagName("Task");

                    for (int j = 0; j < taskNodes.getLength(); j++) {
                        Element taskElement = (Element) taskNodes.item(j);

                        int taskID = Integer.parseInt(taskElement.getElementsByTagName("TaskID").item(0).getTextContent());
                        String description = taskElement.getElementsByTagName("Description").item(0).getTextContent();
                        int duration = Integer.parseInt(taskElement.getElementsByTagName("Duration").item(0).getTextContent());

                        List<Integer> dependencies = new ArrayList<>();
                        NodeList depList = taskElement.getElementsByTagName("DependsOnTaskID");
                        for (int k = 0; k < depList.getLength(); k++) {
                            dependencies.add(Integer.parseInt(depList.item(k).getTextContent()));
                        }

                        Task task = new Task(taskID, description, duration, dependencies);
                        tasks.add(task);
                    }

                    // tasks must be sorted by task id
                    tasks.sort(Comparator.comparingInt(Task::getTaskID));
                    projectList.add(new Project(projectName, tasks));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectList;
    }
}
