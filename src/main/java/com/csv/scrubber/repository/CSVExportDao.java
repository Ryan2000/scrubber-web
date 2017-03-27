package com.csv.scrubber.repository;


import java.io.OutputStream;
import java.util.Collection;

public interface CSVExportDao {

    <T> void write(OutputStream outputStream, Collection<T> contents, Class<T> clazz);
}
