import java.io.*;
import java.util.*;
import java.util.zip.*;

public class MergeGIGIFile {

    public void printErrorMessage(String error) throws Exception {
        System.err.println("ERROR_in_MergeGIGIFile : "+error);
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

    public void createFiles(String familyfile, String inputprefix) throws Exception {
	ArrayList<String> familyList = new ArrayList<String>();
	readFamilyFile(familyfile, familyList);

	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(inputprefix+".merged.txt")));
	BufferedReader[] in = new BufferedReader[familyList.size()];
	for (int i = 0; i < familyList.size(); i++) {
	    in[i] = new BufferedReader(new FileReader(inputprefix+"."+familyList.get(i)));
	}
	boolean done = false;
	boolean isheader = true;
	while (!done) {
	    String[] line = new String[familyList.size()];
	    String posstr = "";
	    for (int i = 0; i < familyList.size(); i++) {
		line[i] = in[i].readLine();
		if (line[0] == null) {
		    done = true;
		    break;
		}
		String[] token = line[i].split("\\s");
		if (i == 0) {
		    posstr = token[0];
		    out.print(posstr);
		}
		else {
		    if (token[0].compareTo(posstr) != 0) {
			printErrorMessage("inconsistent first column "+token[0]+" "+posstr);
		    }
		}
		for (int j = 1; j < token.length; j++) {
		    if (isheader) {
			out.print(" "+familyList.get(i)+":"+token[j]);
		    }
		    else {
			out.print(" "+token[j]);
		    }
		}
		if (i == familyList.size() - 1) {
		    out.println();
		}
	    }
	    if (isheader) {
		isheader = false;
	    }
	}
	out.close();
	for (int i = 0; i < familyList.size(); i++) {
            in[i].close();
        }
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 2) {
	    System.err.println("[usage] java MergeGIGIFile [family file] [input prefix]");
	    System.exit(-1);
	}
	MergeGIGIFile cc = new MergeGIGIFile();
	cc.createFiles(args[0], args[1]);
    }
}

