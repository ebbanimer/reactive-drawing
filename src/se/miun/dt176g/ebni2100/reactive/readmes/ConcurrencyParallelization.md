### Concurrency & Parallelization

#### Schedulers
Schedulers in RxJava determine the thread or thread pool on which an observable should operate. For instance, you can specify operations to run on the IO thread for file I/O or on the computation thread for CPU-bound tasks.

Observable.just(1, 2, 3)
    .subscribeOn(Schedulers.io()) // Perform operation on the IO thread
    .observeOn(Schedulers.computation()) // Observe the result on the computation thread
    .subscribe(result -> System.out.println("Result: " + result));


#### Parallelization
While RxJava doesn't inherently support parallelism, you can leverage Java's parallel processing libraries or custom thread pools. For example, using Java's `ForkJoinPool` to parallelize computations within an observable sequence.

Observable.range(1, 1000)
    .flatMap(i -> Observable.just(i).subscribeOn(Schedulers.io()))
    .observeOn(Schedulers.computation())
    .map(this::expensiveComputation)
    .subscribe(result -> System.out.println("Result: " + result));

#### Concurrency Operators
RxJava provides operators like `subscribeOn` and `observeOn` to specify the threading context for specific parts of an observable chain, allowing you to switch between threads for concurrency.

### Buffering

Buffering allows you to group emitted items into batches or chunks for more controlled and efficient processing.

Observable<Integer> source = Observable.range(1, 10);
source.buffer(3).subscribe(buffer -> {
    System.out.println("Buffered items: " + buffer);
});

### Windowing

Windowing is a technique for grouping emitted items into windows or sub-Observables, useful for batch processing and parallel processing of data streams.

Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);

source.window(5, TimeUnit.SECONDS)
    .subscribe(windowedObservable -> {
        System.out.println("New Window Started");
        windowedObservable.subscribe(item -> {
            System.out.println("Item: " + item);
        });
    });

// Sleep to observe the output
Thread.sleep(20000);

### Throttling
Throttling is a technique used to control the rate at which items are emitted by an Observable, useful in scenarios like handling user input events, network requests, or sensor data.

### Switching

Switch-related operators are powerful tools for managing complex scenarios where you need to switch between different data sources.

#### `switchMap`
Used when you have an observable that emits items, each mapping to another observable. It switches to the latest inner observable, emitting its items while disposing of the previous one.

Observable<Integer> source = ...; // Source observable
Observable<String> innerObservable = ...; // Inner observables

source.switchMap(item -> innerObservable) // Switch to the latest inner observable
    .subscribe(result -> {
        // Handle items emitted by the latest inner observable
    });

### `flatMap`

Use `flatMap` when you want to merge emissions from all inner observables, potentially processing them concurrently.

Observable<Integer> source = ...; // Source observable
Observable<String> innerObservable = ...; // Inner observables

source.flatMap(item -> innerObservable) // Merge emissions from inner observables
    .subscribe(result -> {
        // Handle items emitted from inner observables
    });

### `switchMap`

Use `switchMap` when you want to switch to the latest inner observable, ensuring that only one inner observable is active at a time, and you want a deterministic order of emissions.

Observable<Integer> source = ...; // Source observable
Observable<String> innerObservable = ...; // Inner observables

source.switchMap(item -> innerObservable) // Switch to the latest inner observable
    .subscribe(result -> {
        // Handle items emitted from the latest inner observable
    });
