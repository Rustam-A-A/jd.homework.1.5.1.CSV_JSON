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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String csvFileName = "data.csv";
    private static String jsonFileName = "new-data.json";
    private static String xmlFileName = "data.xml";
    private static String jsonFileName2 = "new-data-2.json";

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

        //getting list of emloyees from xml-file
        List<Employee> list2 = parseXML(xmlFileName);

        //creating of json-file and writting out data into it
        jsonFileWriteDown(jsonFileName, list);
        jsonFileWriteDown(jsonFileName2, list2);
    }

    //method to create json-file and fill it out from List<>list
    public static void jsonFileWriteDown(String jsonFileToWrite, List<Employee> list) {
        try (FileWriter file = new FileWriter(jsonFileToWrite)){
            file.write(listToJson(list));
            file.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //method to parse xml-file
    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list2 = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFileName);
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int j = 0; j < nodeList.getLength(); j++){
            Node node = nodeList.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE){
                if (node.getNodeName().equals("employee")) {
                    Element employee = (Element) node;
                    NodeList attributesList = employee.getChildNodes();
                    int k = 0;
                    String[]str = new String[5];
                    for (int l = 0; l < attributesList.getLength(); l++) {
                        if (attributesList.item(l).getNodeType() == Node.ELEMENT_NODE){
                            str[k] = node.getChildNodes().item(l).getTextContent();
                            k++;
                        }
                    }
                    long id = Long.parseLong(str[0]);
                    String firstName = str[1];
                    String lastName = str[2];
                    String country = str[3];
                    int age = Integer.parseInt(str[4]);
                    Employee empl = new Employee(id, firstName, lastName, country, age);
                    list2.add(empl);
                }
            }
        }
        return list2;
    }

    //to create String json
    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    //method to parse csv-file
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
            //staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

}
