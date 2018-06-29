import java.io.*;
import java.util.zip.*;
import java.util.*;

public class SetLinkageFileMEMissingPedCheck2 {

    public boolean readNextLevel2ErrorIndID(BufferedReader in, String indivstr, Hashtable<String,ArrayList<String>> indivErrorInfo, boolean expectEmpty, StringBuffer sb, String posID) throws Exception {
	String line = in.readLine().trim();
	boolean found = false;
	String indivID = "";
	while (line != null && !found)  {
	    //System.out.println(line+" "+line.length()+" "+expectEmpty+" "+indivstr);
	    if (line.length() == 0 && expectEmpty) {
		line = in.readLine();
		continue;
	    }
	    else if (line.length() == 0 && !expectEmpty) {
		return true;
	    }
	    StringTokenizer stk = new StringTokenizer(line);
	    stk.nextToken(); // ignore (U) or (T);
	    String tempstr = stk.nextToken();
	    if (tempstr.compareTo(indivstr) == 0) {
		found = true;
		indivID = stk.nextToken();
		indivID = indivID.substring(0, indivID.length()-1);
		if (indivErrorInfo.containsKey(indivID)) {
		    ArrayList<String> tempList = indivErrorInfo.get(indivID);
		    tempList.add(posID);
		    indivErrorInfo.put(indivID, tempList);
		}
		else {
		    ArrayList<String> tempList = new ArrayList<String>();
		    tempList.add(posID);
		    indivErrorInfo.put(indivID, tempList);
		}
		sb.append(indivID+" ");
	    }
	    else {
		line = in.readLine().trim();
	    }
	}
	if (line == null) {
	    return true;
	}
	return false;
    }

    public boolean getNextError(BufferedReader in, Hashtable<String,ArrayList<String>> indivErrorInfo, Hashtable<String,Integer> posMEInfo) throws Exception {
	String line = in.readLine();
	while (line != null && !line.startsWith("##### GENOTYPE ERROR")) {
	    line = in.readLine();
	}
	if (line == null) {
	    return true;
	}
	StringBuffer sb = new StringBuffer();
	StringTokenizer stk = new StringTokenizer(line);
	String token = stk.nextToken();
	while (stk.hasMoreTokens() && token.compareTo("Name") != 0) {
	    token = stk.nextToken();
	}
	if (token.compareTo("Name") != 0) {
	    System.err.println("ERROR: cannot find Name");
	    System.exit(-1);
	}
	String posID = stk.nextToken();
	line = in.readLine().trim();
        while (!(line == null || line.startsWith("ORIGINAL SCORING") || line.startsWith("ORDERED GENOTYPE"))) {
            line = in.readLine().trim();
	}
        if (line == null) {
            System.err.println("ERROR: cannot find ORIGINAL SCORING");
            System.exit(-1);
	}
	sb.append(posID+" ");
	if (line.startsWith("ORIGINAL SCORING")) {
	    line = in.readLine().trim();
	    stk = new StringTokenizer(line);
	    String fatherstr = stk.nextToken();
	    if (fatherstr.compareTo("Father") != 0) {
		System.err.println("ERROR: expecting Father but observed "+fatherstr);
		System.exit(-1);
	    }
	    String patID = stk.nextToken();
	    patID = patID.substring(0, patID.length()-1);
	    if (indivErrorInfo.containsKey(patID)) {
		ArrayList<String> tempList = indivErrorInfo.get(patID);
		tempList.add(posID);
		indivErrorInfo.put(patID, tempList);
	    }
	    else {
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.add(posID);
		indivErrorInfo.put(patID, tempList);
	    }
	    String patGeno = stk.nextToken();
	    sb.append(patID+" ");
	    
	    String motherstr = stk.nextToken();
	    if (motherstr.compareTo("Mother") != 0) {
		System.err.println("ERROR: expecting Mother but observed "+motherstr);
		System.exit(-1);
	    }
	    String matID = stk.nextToken();
	    matID = matID.substring(0, matID.length()-1);
	    if (indivErrorInfo.containsKey(matID)) {
		ArrayList<String> tempList = indivErrorInfo.get(matID);
		tempList.add(posID);
		indivErrorInfo.put(matID, tempList);
	    }
	    else {
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.add(posID);
		indivErrorInfo.put(matID, tempList);
	    }
	    sb.append(matID+" ");
	    
	    line = in.readLine().trim();
	    while (line.startsWith("Child")) {
		stk = new StringTokenizer(line);
		String childstr = stk.nextToken();
		if (childstr.compareTo("Child") != 0) {
		    System.err.println("ERROR: expecting Child but observed "+childstr);
		    System.exit(-1);
		}
		String kidID = stk.nextToken();
		kidID = kidID.substring(0, kidID.length()-1);
		if (indivErrorInfo.containsKey(kidID)) {
		    ArrayList<String> tempList = indivErrorInfo.get(kidID);
		    tempList.add(posID);
		    indivErrorInfo.put(kidID, tempList);
		}
		else {
		    ArrayList<String> tempList = new ArrayList<String>();
		    tempList.add(posID);
		    indivErrorInfo.put(kidID, tempList);
		}
		sb.append(kidID+" ");
		line = in.readLine().trim();
	    }
	}
	else if (line.startsWith("ORDERED GENOTYPE")) {
	    readNextLevel2ErrorIndID(in, "Father", indivErrorInfo, false, sb, posID);
	    readNextLevel2ErrorIndID(in, "Mother", indivErrorInfo, false, sb, posID);
	    boolean done = readNextLevel2ErrorIndID(in, "Child", indivErrorInfo, true, sb, posID);
	    while (!done) {
		done = readNextLevel2ErrorIndID(in, "Child", indivErrorInfo, false, sb, posID);
	    }
	}
	else {
	    System.err.println("ERROR: not error 1 or 2\n"+line);
	    System.exit(-1);
	}
	if (posMEInfo.containsKey(posID)) {
	    int count = posMEInfo.get(posID);
	    count++;
	    posMEInfo.put(posID, count);
	}
	else {
	    posMEInfo.put(posID, 1);
	}
	//System.out.println("Found Mendel inconsistency in "+sb.toString());
	return false;
    }

    public void readErrorFile(String errorfile, Hashtable<String,ArrayList<String>> indivErrorInfo, Hashtable<String,Integer> posMEInfo) throws Exception {
	BufferedReader in = null;
	if (errorfile.endsWith(".gz")) {
	    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(errorfile))));
	}
	else {
	    in = new BufferedReader(new FileReader(errorfile));
	}
	boolean done = false;
	while (!done) {
	    done = getNextError(in, indivErrorInfo, posMEInfo);
	}
	in.close();
    }

    public void readMarkerFile(String markerfile, Hashtable<String,Integer> markerInfo) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(markerfile));
	String line = in.readLine();
	if (line.compareTo("X") != 0) {
	    System.err.println("marker file must start with X, but observed "+line);
	    System.exit(-1);
	}
	line = in.readLine();
	int index = 0;
	while (line != null) {
	    if (markerInfo.containsKey(line)) {
		System.err.println(line+" appears twice");
		System.exit(-1);
	    }
	    markerInfo.put(line, index);
	    index++;
	    line = in.readLine();
	}
	in.close();
    }

    public void readMarkerFileList(String markerfile, ArrayList<String> markerList) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(markerfile));
	String line = in.readLine();
	if (line.compareTo("X") != 0) {
	    System.err.println("marker file must start with X, but observed "+line);
	    System.exit(-1);
	}
	line = in.readLine();
	while (line != null) {
	    markerList.add(line);
	    line = in.readLine();
	}
	in.close();
    }

    public void setMEMissing(Hashtable<String,ArrayList<String>> indivErrorInfo, Hashtable<String,Integer> markerInfo, String pedfile, String outputfile) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(pedfile));
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfile)));
	String line = in.readLine();
	while (line != null) {
	    String[] token = line.split("\\s");
	    String indID = token[1];
	    if (!indivErrorInfo.containsKey(indID)) {
		out.println(line);
		line = in.readLine();
		continue;
	    }
	    for (int i = 0; i < 6; i++) {
		out.print(token[i]+"\t");
	    }
	    ArrayList<String> posList = indivErrorInfo.get(indID);
	    Hashtable<Integer,Integer> posInfo = new Hashtable<Integer,Integer>();
	    for (int i = 0; i < posList.size(); i++) {
		String posID = posList.get(i);
		if (!markerInfo.containsKey(posID)) {
		    System.err.println("ERROR: cannot find index for "+posID);
		    System.exit(-1);
		}
		int index = markerInfo.get(posID);
		if (!posInfo.containsKey(index)) {
		    posInfo.put(index,0);
		}
	    }
	    int markerIndex = 0;
	    for (int i = 6; i < token.length; i=i+2) {
		if (posInfo.containsKey(markerIndex)) {
		    out.print("0 0");
		}
		else {
		    out.print(token[i]+" "+token[i+1]);
		}
		if (i == token.length-2) {
		    out.println();
		}
		else {
		    out.print("\t");
		}
		markerIndex++;
	    }
	    line = in.readLine();
	}
	in.close();
	out.close();
    }

    public void createFiles(String pedfile, String markerfile, String errorfile, String outputfile, String statoutputfile) throws Exception {
	Hashtable<String,Integer> posMEInfo = new Hashtable<String,Integer>();
	Hashtable<String,ArrayList<String>> indivErrorInfo = new Hashtable<String,ArrayList<String>>();
	readErrorFile(errorfile, indivErrorInfo, posMEInfo);

	Hashtable<String,Integer> markerInfo = new Hashtable<String,Integer>();
	readMarkerFile(markerfile, markerInfo);

	ArrayList<String> markerList = new ArrayList<String>();
	readMarkerFileList(markerfile, markerList);

	setMEMissing(indivErrorInfo, markerInfo, pedfile, outputfile);

	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(statoutputfile)));
	for (int i = 0; i < markerList.size(); i++) {
	    String posid = markerList.get(i);
	    int mecount = 0;
	    if (posMEInfo.containsKey(posid)) {
		mecount = posMEInfo.get(posid);
	    }
	    out.println(posid+" "+mecount);
	}
	out.close();
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 5) {
	    System.err.println("[usage] java SetLinkageFileMEMissingPedCheck2 [ped file] [marker file] [pedcheck.err file] [output file] [stat output file]");
	    System.exit(-1);
	}
	SetLinkageFileMEMissingPedCheck2 cc = new SetLinkageFileMEMissingPedCheck2();
	cc.createFiles(args[0], args[1], args[2], args[3], args[4]);
    }
}

