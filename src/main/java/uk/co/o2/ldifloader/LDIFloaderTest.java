package uk.co.o2.ldifloader;

import java.io.IOException;

/**
 * Created by ee on 4/30/15.
 */
public class LDIFloaderTest {

    public static int totalRecordVerified = 0;
    public static int invalidRecord = 0;
    public static int recordsNotFound = 0;
    public static int recordsError = 0;
    public static String ldiffile;
    public static String logfile;
    public static String mongoconnectionUrl;
    public static boolean sample;
    public static String inputfile;

    public static void main(String[] args) throws IOException {

        ReadPropertyFile readprop = new ReadPropertyFile();
        ldiffile = readprop.getPropValues("ldiffile");
        logfile = readprop.getPropValues("logfile");
        mongoconnectionUrl = readprop.getPropValues("mongoconnectionUrl");
        sample = Boolean.parseBoolean(readprop.getPropValues("sample"));
        inputfile = readprop.getPropValues("inputfile");

        GrepCommand grep=new GrepCommand();
        MongoReader mongoReader=new MongoReader(mongoconnectionUrl);
        LdifReader ldifreader = new LdifReader(mongoReader,grep);

        if(sample){
            System.out.println("specific sample testing");
            ldifreader.readSpecificUID(inputfile,ldiffile);

        }else{

            String outputFile = grep.processldifFile(ldiffile);
            ldifreader.readUIDfromFile(outputFile);
        }



        System.out.println("\n");
        System.out.println("\n");
        System.out.println("------------------------- Test Report ---------------------------");
        System.out.println("\n");
        System.out.println("Total number of records in the LDIF file: " +totalRecordVerified);
        System.out.println("Invalid Records in the LDIF file: " +invalidRecord);
        System.out.println("Records not imported due to some other reason than duplicate or non numeric uid: " +recordsError);
        System.out.println("Records not imported to mongodb: " +recordsNotFound);
        System.out.println("Successfully imported records: " +(totalRecordVerified - (invalidRecord + recordsNotFound)));





    }
}
