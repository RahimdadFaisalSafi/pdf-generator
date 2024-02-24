package software.potthoff.pdfgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;



@SpringBootApplication
public class PdfGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfGeneratorApplication.class, args);
		try {
			List<String> values = findValuesForKeys("/home/safi/pdf-generator/src/main/resources/structure (1).json",
					"formFactoryElementType", "defaultValue", "label", "valueKey", "options", "text", "value", "type");
			if (!values.isEmpty()) {
				System.out.println("Values found:");
				for (String value : values) {
					if(value.startsWith("valueKey")){
						for (String val: findValueForKey("/home/safi/pdf-generator/src/main/resources/values.json", value.substring(10))) {
							System.out.println(val);
						}
					}

					System.out.println(value);
				}
			} else {
				System.out.println("No keys found.");
			}
		} catch (IOException e) {
			System.err.println("Error parsing JSON file: " + e.getMessage());
		}
	}

	public static List<String> findValuesForKeys(String filePath, String... targetKeys) throws IOException {
		ObjectMapper mapper = new ObjectMapper();


		JsonNode rootNode = mapper.readTree(new File(filePath));


		List<String> values = new ArrayList<>();

		findKeys(rootNode, values, targetKeys);

		return values;
	}

	public static void findKeys(JsonNode node, List<String> values, String[] targetKeys) {
		Iterator<Entry<String, JsonNode>> fieldsIterator = node.fields();

		while (fieldsIterator.hasNext()) {
			Entry<String, JsonNode> field = fieldsIterator.next();
			String fieldName = field.getKey();
			JsonNode fieldValue = field.getValue();

			for (String targetKey : targetKeys) {
				if (fieldName.equals(targetKey)) {
					if(fieldName.equals("formFactoryElementType")){
						values.add("-------------------------------------\n");
					}
					values.add(targetKey + ": " + fieldValue.asText());
				}
			}

			if (fieldValue.isArray()) {

				for (JsonNode arrayElement : fieldValue) {

					findKeys(arrayElement, values, targetKeys);
				}
			} else if (fieldValue.isObject()) {
				findKeys(fieldValue, values, targetKeys);
			}
		}
	}
	public static List<String> findValueForKey(String filePath, String... targetKeys) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(new File(filePath));
		List<String> values = new ArrayList<>();
		findKeys(rootNode, values, targetKeys);

		return values;
	}

}