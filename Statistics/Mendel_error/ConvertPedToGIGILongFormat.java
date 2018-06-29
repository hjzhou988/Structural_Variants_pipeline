import java.io.*;
import java.util.zip.*;
import java.util.*;

public class ConvertPedToGIGILongFormat {

    public void readSeqFamFile(String family, String seqfamfile, Hashtable<String,Integer> seqIndInfo) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(seqfamfile));
	String line = in.readLine();
	while (line != null) {
	    String[] token = line.split("\\s");
	    String famID = token[0];
	    if (famID.compareTo(family) == 0) {
		String indID = token[1];
		String[] itoken = indID.split(":");
		if (itoken.length == 2) {
		    indID = itoken[1];
		}
		if (seqIndInfo.containsKey(indID)) {
		    System.err.println(indID+" appears twice in "+seqfamfile);
		    System.exit(-1);
		}
		seqIndInfo.put(indID,0);
	    }
	    line = in.readLine();
	}
	in.close();
    }

    public void readMarkerFile(String markerfile, ArrayList<String> markerList) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(markerfile));
        String line = in.readLine();
	if (line.compareTo("X") != 0) {
	    System.err.println(markerfile+" does not start with X");
	    System.exit(-1);
	}
	line = in.readLine();
        while (line != null) {
	    markerList.add(line);
	    line = in.readLine();
	}
	in.close();
    }

    public void readPedFile(String family, String linkagepedfile, ArrayList<String> indIDList, ArrayList<ArrayList<String>> genoList, Hashtable<String,Integer> seqIndInfo, int numInd, int numMarker) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(linkagepedfile));
        String line = in.readLine();
        while (line != null) {
	    String[] token = line.split("\\s");
	    String famID = token[0];
	    if (famID.compareTo(family) != 0) {
		System.err.println(linkagepedfile+" has indivduals that do not belong to family "+family+" : "+famID);
		System.exit(-1);
	    }
	    String indID = token[1];
	    String[] itoken = indID.split(":");
	    if (itoken.length == 2) {
		indID = itoken[1];
	    }
	    if (!seqIndInfo.containsKey(indID)) {
		line = in.readLine();
		continue;
	    }
	    ArrayList<String> tempList = new ArrayList<String>();
	    for (int i = 6; i < token.length; i=i+2) {
		tempList.add(token[i]+" "+token[i+1]);
	    }
	    if (tempList.size() != numMarker) {
		System.err.println("ERROR: "+indID+" has "+tempList.size()+" markers, but expect "+numMarker+" markers");
		System.exit(-1);
	    }
	    genoList.add(tempList);
	    indIDList.add(indID);
            line = in.readLine();
        }
        in.close();
	if (indIDList.size() != numInd) {
	    System.err.println("ERROR: expect "+numInd+" individuals but observed "+indIDList.size()+" individuals in "+linkagepedfile);
	    System.exit(-1);
	}
    }

    public void writeGIGIFile(ArrayList<String> indIDList, ArrayList<ArrayList<String>> genoList, ArrayList<String> markerList, String outputfile) throws Exception {
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfile)));
	out.print("id");
	for (int i = 0; i < indIDList.size(); i++) {
	    String indID = indIDList.get(i);
	    out.print(" "+indID+" "+indID);
	}
	out.println();
	for (int i = 0; i < markerList.size(); i++) {
	    String markerID = markerList.get(i);
	    out.print(markerID);
	    for (int j = 0; j < genoList.size(); j++) {
		ArrayList<String> tempList = genoList.get(j);
		out.print(" "+tempList.get(i));
	    }
	    out.println();
	}
	out.close();
    }

    public void createFiles(String family, String seqfamfile, String linkagepedfile, String markerfile, String outputfile) throws Exception {
	Hashtable<String,Integer> seqIndInfo = new Hashtable<String,Integer>();
	readSeqFamFile(family, seqfamfile, seqIndInfo);

	ArrayList<String> markerList = new ArrayList<String>();
	readMarkerFile(markerfile, markerList);

	int numInd = seqIndInfo.size();
	int numMarker = markerList.size();
	System.out.println(family+" has "+numInd+" individuals at "+numMarker+" markers");

	ArrayList<ArrayList<String>> genoList = new ArrayList<ArrayList<String>>();
	ArrayList<String> indIDList = new ArrayList<String>();
	readPedFile(family, linkagepedfile, indIDList, genoList, seqIndInfo, numInd, numMarker);

	writeGIGIFile(indIDList, genoList, markerList, outputfile);
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 5) {
	    System.err.println("[usage] java ConvertPedToGIGILongFormat [family name] [seq fam file] [linkage ped file] [marker file] [output file]");
	    System.exit(-1);
	}
	ConvertPedToGIGILongFormat cc = new ConvertPedToGIGILongFormat();
	cc.createFiles(args[0], args[1], args[2], args[3], args[4]);
    }
}

