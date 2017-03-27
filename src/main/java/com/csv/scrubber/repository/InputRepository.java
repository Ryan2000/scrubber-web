package com.csv.scrubber.repository;

import java.io.InputStream;
import java.util.Collection;

public interface InputRepository<T> {

    Collection<T> read(InputStream inputStream, Class<T> entityClazz);
}
