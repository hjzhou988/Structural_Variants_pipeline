import java.io.*;
import java.util.zip.*;
import java.util.*;

public class ConvertVCFToLinkage {

    public void readPedInfo(String pedinfofile, String ignoreheader, Hashtable<String,String> pedInfo, Hashtable<String,String> sexInfo) throws Exception {
	BufferedReader in = new BufferedReader(new FileReader(pedinfofile));
	String line = in.readLine();
	if (ignoreheader.compareTo("T") == 0) {
	    line = in.readLine();
	}
	while (line != null) {
	    String[] token = line.split("\\s");
	    String famID = token[0];
	    String indID = token[1];
	    String patID = token[2];
	    String matID = token[3];
	    String sex = token[4];
	    if (!indID.startsWith(famID+":")) {
		indID = famID+":"+indID;
	    }
	    if (patID.compareTo("0") != 0 && !patID.startsWith(famID+":")) {
		patID = famID+":"+patID;
	    }
	    if (matID.compareTo("0") != 0 && !matID.startsWith(famID+":")) {
		matID = famID+":"+matID;
	    }
	    if (pedInfo.containsKey(indID)) {
		System.err.println(indID+" appears twice in pedfile");
		System.exit(-1);
	    }
	    pedInfo.put(indID, famID+"\t"+indID+"\t"+patID+"\t"+matID+"\t"+sex);
	    sexInfo.put(indID, sex);
	    line = in.readLine();
	}
	in.close();
    }

    public int getGTIndex(String format) throws Exception {
	int gtIndex = -1;
	String[] token = format.split(":");
	for (int i = 0; i < token.length; i++) {
	    if (token[i].compareTo("GT") == 0) {
		gtIndex = i;
	    }
	}
	if (gtIndex < 0) {
	    System.err.println("cannot find GT index "+format);
	    System.exit(-1);
	}
	return gtIndex;
    }

    public void handleID(int numInd, String[] htoken, ArrayList<ArrayList<String>> genoList, ArrayList<String> indIDList, String ignorelast, ArrayList<String> famList, String pedstructfile, String ignoreheaderped, String seqidindex, String famidindex) throws Exception {
	Hashtable<String,String> seqIDInfo = new Hashtable<String,String>();
	if (ignorelast.compareTo("A") == 0) {
	    int seqindex = Integer.parseInt(seqidindex);
	    String[] ttoken  = famidindex.split(":");
	    int famindex = -1;
	    int indindex = -1;
	    if (ttoken.length == 1) {
		indindex = Integer.parseInt(ttoken[0]);
	    }
	    else if (ttoken.length == 2) {
		famindex = Integer.parseInt(ttoken[0]);
		indindex = Integer.parseInt(ttoken[1]);
	    }
	    else {
		System.err.println("famidindex variant can have 0 or 1 :");
		System.exit(-1);
	    }
	    BufferedReader in = new BufferedReader(new FileReader(pedstructfile));
	    String line = in.readLine();
	    if (ignoreheaderped.compareTo("T") == 0) {
		line = in.readLine();
	    }
	    while (line != null) {
		String[] token = line.split("\\s");
		String indID = "";
		if (famindex >= 0) {
		    indID = token[famindex]+":";
		}
		indID = indID+token[indindex];
		String seqID = token[seqindex];
		if (seqID.compareTo("NA") == 0) {
		    line = in.readLine();
		    continue;
		}
		if (seqIDInfo.containsKey(seqID)) {
		    System.err.println(seqID+" appears twice in seq ID map file");
		    System.exit(-1);
		}
		seqIDInfo.put(seqID, indID);
		line = in.readLine();
	    }
	    in.close();
	}
	Hashtable<String,Integer> famInfo = new Hashtable<String,Integer>();
        for (int i = 0; i < numInd; i++) {
            ArrayList<String> temp = new ArrayList<String>();
            genoList.add(temp);
            String indID = htoken[i+9];
	    if (ignorelast.compareTo("A") == 0) {
		if (!seqIDInfo.containsKey(indID)) {
		    System.err.println(indID+" does not have fam ID info");
		    System.exit(-1);
		}
		indID = seqIDInfo.get(indID);
	    }
	    String[] token = indID.split(":");
	    String famID = token[0];
	    if (Character.isAlphabetic(famID.charAt(famID.length()-1)) && ignorelast.compareTo("T") == 0) {
		famID = famID.substring(0, famID.length()-1);
	    }
	    indID = famID+":"+token[1];
            indIDList.add(indID);
	    if (!famInfo.containsKey(famID)) {
		famInfo.put(famID,0);
		famList.add(famID);
	    }
        }
    }

    public void handleLine(int numInd, String[] token, ArrayList<ArrayList<String>> genoList, String numcoding, ArrayList<String> posList) throws Exception {
	String posID = token[0]+":"+token[1];
	posList.add(posID);
	String refallele = token[3];
	String altallele = token[4];
	int gtIndex = getGTIndex(token[8]);
	for (int i = 0; i < numInd; i++) {
	    String genoinfo = token[i+9];
	    String[] gtoken = genoinfo.split(":");
	    String geno = gtoken[gtIndex];
	    String newgeno = "";
	    if (geno.compareTo(".") == 0) {
		newgeno = "0 0";
	    }
	    else {
		String[] genotoken = geno.split("/|\\|");
		if (numcoding.compareTo("T") == 0) {
		    int firstgeno = -2;
		    int secondgeno = -2;
		    if (genotoken[0].compareTo(".") == 0) {
			firstgeno = 0;
		    }
		    else {
			firstgeno = Integer.parseInt(genotoken[0])+1;
		    }
		    if (genotoken[1].compareTo(".") == 0) {
			secondgeno = 0;
		    }
		    else {
			secondgeno = Integer.parseInt(genotoken[1])+1;
		    }
		    newgeno = firstgeno+" "+secondgeno;
		}
		else {
		    String firstgeno = "-1";
		    String secondgeno = "-2";
		    if (genotoken[0].compareTo(".") == 0) {
			firstgeno = "0";
		    }
		    else {
			if (genotoken[0].compareTo("0") == 0) {
			    firstgeno = refallele;
			}
			else if (genotoken[0].compareTo("1") == 0) {
			    firstgeno = altallele;
			}
			else {
			    System.err.println("only bi-allelic variants are supported for non 1-N coding");
			    System.exit(-1);
			}
		    }
		    if (genotoken[1].compareTo(".") == 0) {
			secondgeno = "0";
		    }
		    else {
			if (genotoken[1].compareTo("0") == 0) {
			    secondgeno = refallele;
			}
			else if (genotoken[1].compareTo("1") == 0) {
			    secondgeno = altallele;
			}
			else {
			    System.err.println("only bi-allelic variants are supported for non 1-N coding");
			    System.exit(-1);
			}
		    }
		    newgeno = firstgeno+" "+secondgeno;
		}
	    }
	    ArrayList<String> temp = genoList.get(i);
	    temp.add(newgeno);
	}
    }

    public void writePed(int numInd, ArrayList<String> famList, ArrayList<String> indIDList, ArrayList<ArrayList<String>> genoList, Hashtable<String,String> pedInfo, Hashtable<String,String> sexInfo, ArrayList<String> posList, String outprefix) throws Exception {
	for (int i = 0; i < famList.size(); i++) {
	    String famID = famList.get(i);
	    writeFamPed(famID, numInd, indIDList, genoList, pedInfo, sexInfo, posList, outprefix+"."+famID+".ped");
	}
    }

    public void writeFamPed(String famID, int numInd, ArrayList<String> indIDList, ArrayList<ArrayList<String>> genoList, Hashtable<String,String> pedInfo, Hashtable<String,String> sexInfo, ArrayList<String> posList, String outfile) throws Exception {
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
	Hashtable<String,Integer> missingInfo = new Hashtable<String,Integer>();
	Hashtable<String,Integer> seqInfo = new Hashtable<String,Integer>();
	for (int i = 0 ; i < numInd; i++) {
	    String indID = indIDList.get(i);
	    if (!indID.startsWith(famID+":")) {
		continue;
	    }
	    ArrayList<String> geno = genoList.get(i);
	    if (!pedInfo.containsKey(indID)) {
		System.err.println(indID+"  does not have ped info");
		System.exit(-1);
	    }
	    String pedline = pedInfo.get(indID);
	    String[] pedtoken = pedline.split("\\s");
	    String patID = pedtoken[2];
	    String matID = pedtoken[3];
	    if (patID.compareTo("0") != 0 && !indIDList.contains(patID)) {
		if (!missingInfo.containsKey(patID)) {
		    missingInfo.put(patID,0);
		}
	    }
	    if (matID.compareTo("0") != 0 && !indIDList.contains(matID)) {
		if (!missingInfo.containsKey(matID)) {
		    missingInfo.put(matID,0);
		}
	    }
	    out.print(pedline+"\t1\t");
	    for (int j = 0; j < geno.size(); j++) {
		out.print(geno.get(j));
		if (j == geno.size() - 1) {
		    out.println();
		}
		else {
		    out.print("\t");
		}
	    }
	    if (geno.size() != posList.size()) {
		System.err.println("incorrect # of variants "+geno.size()+" "+posList.size());
		System.exit(-1);
	    }
	    seqInfo.put(indID,0);
	}
	//System.out.println("# of individuals missing from ped file = "+missingInfo.size());
	Enumeration<String> key = pedInfo.keys();
	while (key.hasMoreElements()) {
	    String indID = key.nextElement();
	    if (!indID.startsWith(famID+":")) {
		continue;
	    }
	    if (seqInfo.containsKey(indID)) {
		continue;
	    }
	    String famline = pedInfo.get(indID);
	    //out.println(famline+"\t1");
	    out.print(famline+"\t1\t");
	    for (int j = 0; j < posList.size(); j++) {
                out.print("0 0");
                if (j == posList.size() - 1) {
                    out.println();
                }
                else {
                    out.print("\t");
                }
            }
	}
	out.close();
    }

    public void createFiles(String pedinfofile, String ignoreheader, String ignorelast, String ignoremulti, String numcoding, String pedstructfile, String ignoreheaderped, String seqidindex, String famidindex, String vcffile, String outprefix) throws Exception {
	Hashtable<String,String> pedInfo = new Hashtable<String,String>();
	Hashtable<String,String> sexInfo = new Hashtable<String,String>();
	readPedInfo(pedinfofile, ignoreheader, pedInfo, sexInfo);

	BufferedReader in = null;
	if (vcffile.endsWith(".gz")) {
	    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(vcffile))));
	}
	else {
	    in = new BufferedReader(new FileReader(vcffile));
	}
	String line = in.readLine();
	while (!line.startsWith("#CHROM")) {
	    line = in.readLine();
	}
	String[] htoken = line.split("\\s");
	int numInd = htoken.length - 9;
	ArrayList<ArrayList<String>> genoList = new ArrayList<ArrayList<String>>();
	ArrayList<String> indIDList = new ArrayList<String>();
	ArrayList<String> famList = new ArrayList<String>();
	handleID(numInd, htoken, genoList, indIDList, ignorelast, famList, pedstructfile, ignoreheaderped, seqidindex, famidindex);

	ArrayList<String> posList = new ArrayList<String>();
	line = in.readLine();
	int numLine = 0;
	while (line != null) {
	    String[] token = line.split("\\s");
	    String[] atoken = token[4].split(",");
	    if (atoken.length > 1 && ignoremulti.compareTo("T") == 0) {
		line = in.readLine();
		continue;
	    }
	    else if (atoken.length == 1 && ignoremulti.compareTo("A") == 0) {
		line = in.readLine();
		continue;
	    }
	    handleLine(numInd, token, genoList, numcoding, posList);
	    line = in.readLine();
	    numLine++;
	    if (numLine % 10000 == 0) {
		System.out.println("finished reading "+numLine+" lines");
	    }
	}
	in.close();

	writePed(numInd, famList, indIDList, genoList, pedInfo, sexInfo, posList, outprefix);

	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outprefix+".marker")));
	out.println("X");
	for (int i = 0; i < posList.size(); i++) {
	    out.println(posList.get(i));
	}
	out.close();
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 11) {
	    System.err.println("[usage] java ConvertVCFToLinkage [ped info file] [T - ignore header in ped file] [T - ignore the last alphabet in famID (from Polymutt), A - change ID to ped format] [T - ignore multi allelic, A - only multi allelic] [T - write 1-N coding] [seq ID map file] [ignore header in seq ID map file] [ID index in VCF] [ID index in seq ID map file possibly separated by :] [vcf file] [output prefix]");
	    System.exit(-1);
	}
	ConvertVCFToLinkage cc = new ConvertVCFToLinkage();
	cc.createFiles(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
    }
}

