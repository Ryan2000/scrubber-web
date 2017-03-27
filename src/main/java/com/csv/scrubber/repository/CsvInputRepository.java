package com.csv.scrubber.repository;

import static java.util.stream.Collectors.toCollection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Repository;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import lombok.SneakyThrows;

@Repository
public class CsvInputRepository<T> implements InputRepository<T>{

    @Override
    @SneakyThrows
    public Collection<T> read(InputStream inputStream, Class<T> entityClazz) {
        try(CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))){
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<T>();
            strategy.setType(entityClazz);
            CsvToBean<T> csvToBean = new CsvToBean<T>();
            return csvToBean.parse(strategy, csvReader).stream().skip(1).collect(toCollection(ArrayList::new));
        }
    }
}
