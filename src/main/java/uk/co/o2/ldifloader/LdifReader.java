package uk.co.o2.ldifloader;

import com.mongodb.DBObject;
import org.apache.commons.codec.binary.Base64;
import org.grep4j.core.result.GrepResults;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ee on 4/30/15.
 */
public class LdifReader {
    protected MongoReader mongoReader;
    protected GrepCommand grep;

    public LdifReader(MongoReader mongoReader,GrepCommand grep) {
        this.mongoReader = mongoReader;
        this.grep = grep;
    }

    protected void readUIDfromFile(String fileName) throws IOException {

        String line;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {

            while ((line= br.readLine()) != null) {
                String uid = line.substring(line.indexOf("uid=")+4,line.indexOf(",cn"));
                LDIFloaderTest.totalRecordVerified++;

                if(!isNumeric(uid)){
                    LDIFloaderTest.invalidRecord++;
                }else{

                    if(mongoReader.searchUIDinMongo(uid) == null ){

                        if(!grep.searchLogfile(LDIFloaderTest.logfile,uid)){
                            LDIFloaderTest.recordsNotFound++;
                            System.out.println(uid);
                        }else{
                            LDIFloaderTest.recordsError++;
                        }

                    }

                }
            }

        } finally {
            br.close();
        }
    }

    protected void readSpecificUID(String fileName,String ldiffilename) throws IOException {

        String uid;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {

            while ((uid= br.readLine()) != null) {

                LDIFloaderTest.totalRecordVerified++;

                if(!isNumeric(uid)){
                    LDIFloaderTest.invalidRecord++;
                }else{

                    DBObject mongorecord = mongoReader.searchUIDinMongo(uid);

                    if((mongorecord) != null ){

                        GrepResults ldifrecord = grep.getIdentitydetailfromldif(ldiffilename, uid);
                        compareAllField(mongorecord,ldifrecord);


                    }else{
                        if(!grep.searchLogfile(LDIFloaderTest.logfile,uid)){
                            LDIFloaderTest.recordsNotFound++;
                            System.out.println(uid);
                        }else{
                            LDIFloaderTest.recordsError++;
                        }
                    }

                }
            }

        } finally {
            br.close();
        }

    }



    private void compareAllField(DBObject mongorecord,GrepResults ldifrecord){

        String[] fieldLdif;
        String[] arrayldifrecord = ldifrecord.toString().split("\n");

        for(int i=1;i<arrayldifrecord.length;i++){

            if(arrayldifrecord[i].startsWith("dn: uid=")){
                break;
            }

            if(arrayldifrecord[i].contains("::")){
                fieldLdif = arrayldifrecord[i].split("::");
                while(!arrayldifrecord[i+1].contains(":") && i<arrayldifrecord.length){
                    fieldLdif[1]=fieldLdif[1] +arrayldifrecord[i+1].trim();
                    i++;
                }
                byte[] valueDecoded= Base64.decodeBase64(fieldLdif[1]);
                fieldLdif[1] = new String(valueDecoded);

            }else{
                fieldLdif = arrayldifrecord[i].split(":");
                while(!arrayldifrecord[i+1].contains(":") && i<arrayldifrecord.length){
                    fieldLdif[1]=fieldLdif[1] + arrayldifrecord[i+1].trim();
                    i++;
                }
            }



            System.out.println(comparefield(mongorecord, fieldLdif));

        }

    }


    private String comparefield(DBObject mongorecord, String[] fieldLdif){

        String[] LdifFields = {"uid",
                                "cn",
                                "userpassword",
                                "securitychallenges",
                                "securityresponses",
                                "emailvalidated",
                                "regdpasswordexpiredflag",
                                "displayname",
                                "loginportalbfid",
                                "loginportalusername",
                                "creatorsname",
                                "modifiersname"};

        String[] MongoFields = {"uid",
                                "username",
                                "password",
                                "securityQuestion",
                                "securityAnswer",
                                "usernameVerified",
                                "passwordResetByAgent",
                                "displayName",
                                "loginPortalBfid",
                                "loginPortalUsername",
                                "createdBy",
                                "modifiedBy"};



        for(int i=0;i<LdifFields.length;i++){
            if(LdifFields[i].equalsIgnoreCase(fieldLdif[0])){
                System.out.println(fieldLdif[0]);

                if(fieldLdif[0].equalsIgnoreCase("userpassword")){
                    fieldLdif[1] = fieldLdif[1].replace("{SSHA}","");
                }

                if(fieldLdif[1].trim().equalsIgnoreCase(String.valueOf(mongorecord.get(MongoFields[i])))){
                    System.out.println(fieldLdif[1]);
                    return "true";
                }else{
                    System.out.println(fieldLdif[1]);
                    System.out.println(mongorecord.get(MongoFields[i]));
                    return "false";
                }

            }
        }
        return "invalid";
    }

    public boolean isNumeric(String str)
    {
        return str.matches("\\d+");
    }
}
