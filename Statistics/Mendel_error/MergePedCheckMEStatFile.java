import java.io.*;
import java.util.*;
import java.util.zip.*;

public class MergePedCheckMEStatFile {

    public void printErrorMessage(String error) throws Exception {
        System.err.println("ERROR_in_MergePedCheckMEStatFile : "+error);
	System.exit(-1);
    }

    public void readFamilyFile(String familyfile, ArrayList<String> familyList) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(familyfile));
	String line = in.readLine();
	while (line != null) {
	    String[] token = line.split("\\s");
	    familyList.add(token[1]);
	    line = in.readLine();
	}
	in.close();
    }

    public void readMEStatFile(String statfile, ArrayList<String> posList, Hashtable<String,Integer> posMEInfo, boolean isfirst) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(statfile));
	String line = in.readLine();
	int lineindex = 0;
	while (line != null) {
	    String[] token = line.split("\\s");
	    String posid = token[0];
	    int mecount = Integer.parseInt(token[1]);
	    if (isfirst) {
		posList.add(posid);
		posMEInfo.put(posid, mecount);
	    }
	    else {
		String prevposid = posList.get(lineindex);
		if (prevposid.compareTo(posid) != 0) {
		    printErrorMessage("incorrect position id "+prevposid+" "+posid);
		}
		if (!posMEInfo.containsKey(posid)) {
		    printErrorMessage("cannot find ME count for "+posid);
		}
		int curcount = posMEInfo.get(posid);
		curcount = curcount + mecount;
		posMEInfo.put(posid, curcount);
	    }
	    lineindex++;
	    line = in.readLine();
	}
	in.close();
    }

    public void createFiles(String familyfile, String inputprefix, String outputfile) throws Exception {
	ArrayList<String> familyList = new ArrayList<String>();
	readFamilyFile(familyfile, familyList);

	ArrayList<String> posList = new ArrayList<String>();
	Hashtable<String,Integer> posMEInfo = new Hashtable<String,Integer>();
	for (int i = 0; i < familyList.size(); i++) {
	    String famID = familyList.get(i);
	    if (i == 0) {
		readMEStatFile(inputprefix+"."+famID+".level.1.pedcheck.mestat", posList, posMEInfo, true);
		readMEStatFile(inputprefix+"."+famID+".level.2.pedcheck.mestat", posList, posMEInfo, false);
	    }
	    else {
		readMEStatFile(inputprefix+"."+famID+".level.1.pedcheck.mestat", posList, posMEInfo, false);
		readMEStatFile(inputprefix+"."+famID+".level.2.pedcheck.mestat", posList, posMEInfo, false);
	    }
	}
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfile)));
	for (int i = 0; i < posList.size(); i++) {
	    String posid = posList.get(i);
	    int mecount = 0;
	    if (posMEInfo.containsKey(posid)) {
		mecount = posMEInfo.get(posid);
	    }
	    out.println(posid+" "+mecount);
	}
	out.close();
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 3) {
	    System.err.println("[usage] java MergePedCheckMEStatFile [family file] [input prefix] [output file]");
	    System.exit(-1);
	}
	MergePedCheckMEStatFile cc = new MergePedCheckMEStatFile();
	cc.createFiles(args[0], args[1], args[2]);
    }
}

