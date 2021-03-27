import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    private static String csvFileName = "data.csv";
    private static String jsonFileName = "new-data.json";
    private static String xmlFileName = "data.xml";
    //private static String xmlFileName = "example.xml";

    public static void main(String[] args) throws DOMException, IOException, SAXException, NullPointerException, ParserConfigurationException, ParserConfigurationException {

        //creating of csv-file and writting out data into it
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName, false))){
            writer.writeNext(employee1);
            writer.writeNext(employee2);
        } catch (IOException e){
            e.printStackTrace();
        }

        //getting list of emloyees from csv-file
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, csvFileName);
        //System.out.println(list.toString());

        //getting list of emloyees from xml-file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFileName);

        Node root = doc.getDocumentElement();
        System.out.println("Root element: " + root.getNodeName());
        NodeList nodeList = root.getChildNodes();
        //System.out.println("nodeList length: " + nodeList.getLength());
        for (int j = 0; j < nodeList.getLength(); j++){
            Node node = nodeList.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE){
                System.out.println("  current element: " + node.getNodeName());

                if (node.getNodeName().equals("employee")) {
                    Element employee = (Element) node;
                    NodeList attributesList = employee.getChildNodes();

                    for (int l = 0; l < attributesList.getLength(); l++) {
                        if (attributesList.item(l).getNodeType() == Node.ELEMENT_NODE){
                            System.out.println("    attribute: " +
                                    node.getChildNodes().item(l).getNodeName() + "  " +
                                    node.getChildNodes().item(l).getNodeValue());
                        }

                    }

                }
            }
        }


        //creating of json-file and writting out data into it
        try (FileWriter file = new FileWriter(jsonFileName)){
            file.write(listToJson(list));
            file.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    private static  List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
            staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

}
