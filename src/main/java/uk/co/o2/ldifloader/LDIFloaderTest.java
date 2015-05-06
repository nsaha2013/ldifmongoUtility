package uk.co.o2.ldifloader;

import java.io.IOException;

/**
 * Created by ee on 4/30/15.
 */
public class LDIFloaderTest {

    public static int totalRecordVerified = 0;
    public static int invalidRecord = 0;
    public static int recordsNotFound = 0;

    public static void main(String[] args) throws IOException {

        ReadPropertyFile readprop = new ReadPropertyFile();

        GrepCommand grep=new GrepCommand();
        String outputFile = grep.processldifFile(readprop.getPropValues("ldiffile"));

        MongoReader mongoReader=new MongoReader();
        LdifReader ldifreader = new LdifReader(mongoReader);
        ldifreader.readUIDfromFile(outputFile);


        System.out.println("\n");
        System.out.println("\n");
        System.out.println("------------------------- Test Report ---------------------------");
        System.out.println("\n");
        System.out.println("Total number of records in the LDIF file: " +totalRecordVerified);
        System.out.println("Invalid Records in the LDIF file: " +invalidRecord);
        System.out.println("Records not imported to mongodb: " +recordsNotFound);
        System.out.println("Successfully imported records: " +(totalRecordVerified - (invalidRecord + recordsNotFound)));





    }
}
