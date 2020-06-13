import java.io.*;
import java.util.*;
import java.util.Map.*;

public class NameGen {
	Scanner input;
	int gender,order, numNames, min, max;
	Map<String,Boolean> names = new Hashtable<String, Boolean>();

	public NameGen() {
		input = new Scanner(System.in);
	}

	public class Names {
		LinkedList<Character> nextChars = new LinkedList<Character>();
		String name = "";
		public Names(String n) {
			this.name = n;
		}
	}

	//Initializes the min/max length of words, markov n-order chain, and n names to generate
	public void init() {
		System.out.print("\nMinimum Name Length: ");
		while(!input.hasNextInt()) {
			input.next();
		}
		this.min = input.nextInt();

		System.out.print("\nMax Name Length: ");
		while(!input.hasNextInt()) {
			input.next();
		}
		this.max = input.nextInt();

		System.out.print("\nOrder: ");
		while(!input.hasNextInt()) {
			input.next();
		}
		this.order = input.nextInt();

		System.out.print("\nNumber of Names: ");
		while(!input.hasNextInt()) {
			input.next();
		}
		this.numNames = input.nextInt();
	}


	//Prompts the user to specify the gender (Choosing numbers 1 or 2)
	//Then loads the corresponding txt file, and adds each name into a names hashtable
	public void inputRead() {
		String fileType, name = "";
		System.out.println("Choose a gender: ");
		System.out.println("    1: Boy");
		System.out.println("    2: Girl");
		System.out.print("    Num: ");
		while(!input.hasNextInt()) {
			input.next();
		}
		gender = input.nextInt();
		if(gender == 1) {
			fileType = "namesBoys.txt";
		} else {
			fileType = "namesGirls.txt";
		}

		try {
			File file = new File(fileType);
			String path = file.getAbsolutePath();
			FileReader fileRead = new FileReader(path);
			BufferedReader buffRead = new BufferedReader(fileRead);
			while(buffRead.readLine() != null) {
				name = "_" + buffRead.readLine() + "_";
				names.put(name, false); 
			}
			buffRead.close();
		} catch(IOException e) {
			System.out.println("Unable to find the specified file");
		}
	}
	//Iterates through the list of names and gets a substring of n chars in the name.
	//Puts that data int a hashtable, then any following possible characters are added too
	public void markovChain() {
		Map<String, Names> chainNames = new Hashtable<String, Names>();
		Iterator<Entry<String, Boolean>> iter = names.entrySet().iterator();
		String orderNames = "";
		Names count;
		while(iter.hasNext()) {
			String name = iter.next().getKey();
			for(int i = 0; i <= name.length() - order; i++) {
				orderNames = name.substring(i, i + order); //A substring of n characters from a given name (n corresponds to markov chain length)
				if(chainNames.containsKey(orderNames)) { 
					count = chainNames.get(orderNames);
					count.name = orderNames;
					if(i+order < name.length()) {
						count.nextChars.add(name.charAt(i+order)); //Takes the next n characters (these are the probable characters of the substring)
					}
					chainNames.put(orderNames, count);
				} else {
					count = new Names(orderNames);
					if(i+order < name.length()) {
						count.nextChars.add(name.charAt(i+order));
					}
					chainNames.put(orderNames, count);
				}
			}
		}
		int namesPrinted = 0;
		Map<Integer, String> nameList = new Hashtable<Integer,String>();
		
		//Loops until n names is generated and printed
		while(namesPrinted != numNames) {
			String genName = createNames(chainNames);
			if(!(names.containsKey(genName)) && !(nameList.containsValue(genName)) && genName.length() >= min && genName.length() <= max) {
				nameList.put(namesPrinted,genName.substring(1, genName.length()-1));
				System.out.println(nameList.get(namesPrinted));
				namesPrinted++;
			}
		}
	}
	//Returns a new name based off of the markov chain
	public String createNames(Map<String, Names> names){
		Collection<Names> beginName = names.values();
		List<Names> nameList = new ArrayList<>(beginName);
		Random rand = new Random();
		String orderName = "";
		String newName = "";
		int randStr = 0;

		//Sets the newName to have a beginning part of a name
		while(nameList.get(randStr).name.charAt(0) != '_') {
			randStr = rand.nextInt(names.size());
		}
		newName = newName + nameList.get(randStr).name;
		randStr = rand.nextInt(names.get(newName).nextChars.size());
		newName = newName + names.get(newName).nextChars.get(randStr);
		orderName = newName.substring(newName.length() - order, newName.length());
		while(newName.charAt(newName.length()-1) != '_') {	
			randStr = rand.nextInt(names.get(orderName).nextChars.size());
			newName = newName + names.get(orderName).nextChars.get(randStr);
			orderName = newName.substring(newName.length() - order, newName.length());
		}
		return newName;
	}

	public static void main(String[] args) {
		NameGen test = new NameGen();
		test.init();
		test.inputRead();
		test.markovChain();
	}
}
