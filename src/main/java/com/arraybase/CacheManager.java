package com.arraybase;

import com.arraybase.tm.GColumn;

import java.util.*;

/**
 * Created by jmilton on 6/12/2015.
 */
public class CacheManager {
    private static final int CACHE_SIZE = 10000;
    private Map collection = null;
    public CacheManager(Map col) {
        collection = col;
        
        init();
        
    }


private void init ()
{

    Thread t = new Thread(new Runnable() {
        public void run() {
            
            while ( true )
            {
                if ( collection.size () > CACHE_SIZE)
                {
                    collection.clear();
                    System.out.println( " cache is clear " );
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    });

    t.start();

}
}

