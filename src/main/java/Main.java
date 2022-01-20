import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        String jsonCSV = listToJson(listCSV);
        writeString(jsonCSV, "data.json");

        List<Employee> listXML = parseXML("data.xml");
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "data2.json");


    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategyCSV = new ColumnPositionMappingStrategy<>();
            strategyCSV.setType(Employee.class);
            strategyCSV.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategyCSV)
                    .build();

            list = csv.parse();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    

    public static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> list = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList nodeList = doc.getElementsByTagName("employee");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node employee = nodeList.item(i);
            Element e = (Element) employee;
            String id = e.getElementsByTagName("id").item(0).getTextContent();
            String firstName = e.getElementsByTagName("firstName").item(0).getTextContent();
            String lastName = e.getElementsByTagName("lastName").item(0).getTextContent();
            String country = e.getElementsByTagName("country").item(0).getTextContent();
            String age = e.getElementsByTagName("age").item(0).getTextContent();
            list.add(new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age)));

        }
        return list;
    }


    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

