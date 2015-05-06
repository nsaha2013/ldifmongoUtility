package uk.co.o2.ldifloader;

import com.mongodb.*;

import java.io.IOException;

public class MongoReader {
    protected DB mongodb;


    public MongoReader() throws IOException {

        MongoClientURI uri  = new MongoClientURI((new ReadPropertyFile()).getPropValues("mongoconnectionUrl"));
        MongoClient client = new MongoClient(uri);
        mongodb = client.getDB(uri.getDatabase());

    }


    public boolean searchUIDinMongo(String uid){

        DBCollection identity = mongodb.getCollection("identityV3");
        DBObject record = identity.findOne(new BasicDBObject("uid", uid));


        if(record == null){
            return false;
        }else{
            //System.out.println(record);
            return true;
        }


    }
}
