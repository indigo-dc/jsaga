package org.glite.ce.creamapi.jobmanagement.jdl;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.glite.ce.creamapi.jobmanagement.Job;
import org.glite.jdl.Ad;
import org.glite.jdl.AdParser;
import org.glite.jdl.JobAd;
//import org.glite.jdl.CollectionAd;
//import org.glite.jdl.ParametricAd;
import org.glite.jdl.JobAdException;

public class JobFactory {

    public static Job makeJob(String jdl) throws IllegalArgumentException, Exception {
        if (jdl == null) {
            throw (new IllegalArgumentException("the JDL is null!"));
        }
        
        Ad jad = null;
        try {
            jad = (Ad)AdParser.parseJdl(jdl);
        } catch (JobAdException e) {
            throw new RuntimeException(e);
        }

//        if(jad instanceof CollectionAd) {
//            return new CollectionJob((CollectionAd)jad);
//        } else
//        if(jad instanceof ParametricAd) {
//            return new ParametricJob((ParametricAd)jad);
//        } else
        if(jad instanceof JobAd) {
            return NormalJob.makeJob((JobAd)jad);
        } else {
            throw (new IllegalArgumentException("job type unknown"));
        }        
    }

    
    public static void main(String[] arg) {
        String jdl = "";

        FileReader in = null;
        try {
            in = new FileReader("/tmp/jdl.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        char[] buffer = new char[1024];
        int n = 1;

        while (n > 0) {
            try {
                n = in.read(buffer, 0, buffer.length);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (n > 0) {
                jdl += new String(buffer, 0, n);
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jdl);
        for(int i=0; i<100000; i++) {
            System.out.println("n# " + i);
            try {
                makeJob(jdl);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
