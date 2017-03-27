package com.csv.scrubber.async;


import lombok.extern.apachecommons.CommonsLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@CommonsLog
public class ScrubberMatching<T, U> extends RecursiveTask<Collection<T>> {

    public static final long SIZE = 25;
    private final Collection<T> sourceData;
    private final Collection<U> reductionData;
    private final Collection<BiFunction<T, U, Boolean>> reductionStrategy;
    private final Consumer<String> messageCallback;
    
    //Threads share memory, which means data synchronization is a huge challenge
    //because one Thread and overwrite data from another thread. 
    //AtomicInteger is a class that holds an Integer value, but only one thread
    //can read/write from it at a time. That way we can trust the value stored in this
    //object because it handles data synchronization between threads.
    //We will use this object to report progress to the user
    private AtomicInteger atomicInteger;

    public ScrubberMatching(Collection<T> sourceData, Collection<U> reductionData,
                            Collection<BiFunction<T, U, Boolean>> reductionStrategy,
                            Consumer<String> messageCallback,
                            AtomicInteger atomicInteger) {
        this.sourceData = sourceData;
        this.reductionData = reductionData;
        this.reductionStrategy = reductionStrategy;
        this.messageCallback = messageCallback;
        this.atomicInteger = atomicInteger;
        //log.info("Creating new ScrubberMatchinging...");
    }

    @Override
    protected Collection<T> compute() {
        Collection<T> matches = new ArrayList<>();

        if(sourceData.size() < SIZE){
            log.info("Computing...");

            for(T source : sourceData){
                for(U reduce : reductionData){
                    boolean match = !reductionStrategy.stream().map(func -> func.apply(source, reduce)).collect(toList()).contains(false);
                    if(match){
                        //log.info("Found match...");
                        messageCallback.accept(String.format("Found match %s == > %s", source.toString(), reduce.toString()));
                    }
                }
            }
            log.info("Completed...");
            atomicInteger.incrementAndGet();
        } else {
            log.info("Splitting...");
            matches = split();
        }
        return matches;
    }

    private Collection<T> split(){
        final int mid = sourceData.size() / 2;

        final Collection<T> lhsCol = sourceData.stream().limit(mid).collect(toList());
        final Collection<T> rhsCol = new ArrayList<T>(sourceData);
        rhsCol.removeAll(lhsCol);

        final ScrubberMatching<T, U> lhsTask = new ScrubberMatching<>(lhsCol, reductionData,
                                                                        reductionStrategy, messageCallback, atomicInteger);
        final ScrubberMatching<T, U> rhsTask = new ScrubberMatching<>(rhsCol, reductionData,
                                                                        reductionStrategy, messageCallback, atomicInteger);
        rhsTask.fork();

        final Collection<T> results = new ArrayList<>();
        results.addAll(lhsTask.compute());
        results.addAll(rhsTask.join());

        return results;
    }
}