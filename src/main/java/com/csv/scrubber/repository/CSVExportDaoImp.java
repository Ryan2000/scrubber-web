package com.csv.scrubber.repository;

import com.opencsv.bean.MappingUtils;
import com.opencsv.bean.StatefulBeanToCsv;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class CSVExportDaoImp implements CSVExportDao {

    @SneakyThrows
    @Override
    public <T> void write(OutputStream outputStream, Collection<T> contents, Class<T> clazz) {
        try (PrintWriter printWriter = new PrintWriter(outputStream)) {
        	//Use StatefulBeanToCsv and MappingUtils.determineMappingStrategy
        	//to tell OpenCSV how to write a CSV file based off of a bean
            StatefulBeanToCsv<T> statefulBeanToCsv = new StatefulBeanToCsv<T>('\\', "\n",
                    MappingUtils.determineMappingStrategy(clazz), '\"', ',', true, printWriter);
            
            //Then we can call the write method to write the file
            statefulBeanToCsv.write(new ArrayList<T>(contents));
        }
    }
}
