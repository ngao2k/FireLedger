package com.example.fireledger;

import org.junit.Test;

public class ExampleUnitTest {

    @Test
    public void test_spark() throws Exception {
       Spark spark = new Spark();
       String result = spark.request("介绍一下你自己");


    }
}
