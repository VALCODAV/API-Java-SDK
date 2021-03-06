package trackvia.client.examples;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import trackvia.client.TrackviaClient;
import trackvia.client.model.Record;
import trackvia.client.model.RecordData;
import trackvia.client.model.RecordDataBatch;
import trackvia.client.model.RecordSet;
import trackvia.client.model.View;

public class TestCommandLineApp {
	
	public static final String CONFIG_PATH = "src/test/resources/trackvia.config";

	enum MainMenuOptions {EXIT, LIST_VIEWS, LIST_RECORDS, DELETE_RECORD, ADD_RECORD, UPDATE_RECORD};
	TrackviaClient client  = null;

	public static void main(String[] params) throws Exception{
		TestCommandLineApp app = new TestCommandLineApp();
		app.initClient();


		while(true) {
			try {
				switch(app.runMainMenu()) {
				case LIST_VIEWS:
					app.listViews();
					break;
				case LIST_RECORDS:
					app.listRecords();
					break;
				case DELETE_RECORD:
					app.deleteRecords();
					break;
				case UPDATE_RECORD:
					app.updateRecord();
					break;
				case ADD_RECORD:
					app.addRecord();
					break;
				case EXIT:
				default:
					app.println("Goodbye");
					return;
				}
			} catch(Exception e) {
				app.println("\n\n"+e.getMessage());
			}
		}
	}

	/**
	 * Create a new instance of the app
	 */
	public TestCommandLineApp() {

	}

	public void addRecord() {
		println("\n");
		int viewId = -1;
		while(viewId == -1) {
			String viewString = readLine("id of view: ");
			try {
				viewId = Integer.parseInt(viewString);
			} catch(Exception e) {

			}
		}


		RecordData data = new RecordData();

		println("Add data for field. press enter, enter to stop");
		while(true) {
			String key = readLine("Field name: ");
			String value = readLine("Field value: ");
			if(key.equals("") && value.equals("")) {
				break;
			}
			data.put(key, value);
		}
		List<RecordData> recordList = new ArrayList<>(2);
		recordList.add(data);
		RecordDataBatch rdb = new RecordDataBatch(recordList);

		RecordSet records = client.createRecords(viewId, rdb);
		println("New values are:");
		for(RecordData record : records.getData()) {
			println(record.toString());
		}
		println("\n");
	}



	public void updateRecord() {
		println("\n");
		int viewId = -1;
		while(viewId == -1) {
			String viewString = readLine("id of view: ");
			try {
				viewId = Integer.parseInt(viewString);
			} catch(Exception e) {

			}
		}

		int recordId = -1;
		while(recordId == -1) {
			String recordString = readLine("id of record: ");
			try {
				recordId = Integer.parseInt(recordString);
			} catch(Exception e) {

			}
		}
		RecordData data = new RecordData();
		data.put("id", recordId);

		println("Add data for field. press enter, enter to stop");
		while(true) {
			String key = readLine("Field name: ");
			String value = readLine("Field value: ");
			if(key.equals("") && value.equals("")) {
				break;
			}
			data.put(key, value);
		}

		Record record = client.updateRecord(viewId, recordId, data);
		println("New values are:");
		println(record.getData().toString());
		println("\n");
	}


	public void deleteRecords() {
		println("\n");
		int viewId = -1;
		while(viewId == -1) {
			String viewString = readLine("id of view: ");
			try {
				viewId = Integer.parseInt(viewString);
			} catch(Exception e) {

			}
		}

		int recordId = -1;
		while(recordId == -1) {
			String recordString = readLine("id of record: ");
			try {
				recordId = Integer.parseInt(recordString);
			} catch(Exception e) {

			}
		}

		client.deleteRecord(viewId, recordId);
		println("Records deleted:");
		println("\n");
	}

	public void listRecords() {
		println("\n");
		int viewId = -1;
		while(viewId == -1) {
			String viewString = readLine("id of view: ");
			try {
				viewId = Integer.parseInt(viewString);
			} catch(Exception e) {

			}

		}

		RecordSet recordSet = client.getRecords(viewId);
		println("Records:");
		for(RecordData record : recordSet.getData()) {
			println(record.toString());
		}
		println("\n");
	}

	public void listViews() {
		List<View> views = client.getViews();
		println("Views:");
		for(View view : views) {
			println(view.toString());
		}
		println("\n");
	}

	public MainMenuOptions runMainMenu() {
		while(true) {
			println("Choose from the following options:");
			println("* 1 list views");
			println("* 2 list records");
			println("* 3 delete record");
			println("* 4 update record");
			println("* 5 add record");
			println("* 0 exit");

			String choice = readLine();
			try {
				int choiceInt = Integer.parseInt(choice);
				switch(choiceInt) {
				case 1:
					return MainMenuOptions.LIST_VIEWS;
				case 2:
					return MainMenuOptions.LIST_RECORDS;
				case 3:
					return MainMenuOptions.DELETE_RECORD;
				case 4:
					return MainMenuOptions.UPDATE_RECORD;
				case 5:
					return MainMenuOptions.ADD_RECORD;
				case 0:
					return MainMenuOptions.EXIT;
				}
			} catch(Exception e)
			{
				println("I'm sorry, \"" + choice + "\" is not a valid selection. Please try again. \n");
			}
		}
	}


	public void initClient() throws Exception {

		String email;
		String userKey;
		String password;
		String scheme;
		String hostName;
		int port;
		String path;
		
		Properties config = new Properties();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(CONFIG_PATH);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Config file ("+ CONFIG_PATH + ") not found. " + 
					"Did you remember to make a copy from the template file?");
		}
		config.load(inputStream);
		email = config.getProperty("email");
		userKey = config.getProperty("user_key");
		password = config.getProperty("password");
		scheme = config.getProperty("scheme");
		hostName = config.getProperty("hostname");
		port = Integer.parseInt(config.getProperty("port"));
		path = config.getProperty("path");

		client = TrackviaClient.create(path, scheme, hostName, port, email, password, userKey);
	}

	/**
	 * convenience function
	 * @param str
	 */
	protected void println(String str) {
		System.out.println(str);
	}

	protected String readLine() {
		return readLine(null);
	}

	/**
	 * convenience function
	 * @param str
	 * @return
	 */
	protected String readLine(String str) {

			if(str != null) {
				System.out.print(str);
			}
			try {
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String s = bufferRead.readLine();


			    return s;
			} catch(IOException e) {
				e.printStackTrace();
			}
			return null;

	}
}
