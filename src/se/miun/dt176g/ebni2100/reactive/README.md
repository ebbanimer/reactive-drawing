## ConnectableObservable
Multicasting
Emits after it's being created. Hot observable. All subscribers share the same sequence. 

// Create a source observable
Observable<Integer> source = Observable.range(1, 5);

// Create a ConnectableObservable by using the publish operator
ConnectableObservable<Integer> connectable = source.publish();

// Connect the ConnectableObservable to start emitting items
connectable.connect();

// Subscribe multiple times to the same ConnectableObservable
connectable.subscribe(i -> System.out.println("Subscriber 1: " + i));
connectable.subscribe(i -> System.out.println("Subscriber 2: " + i));

// This will emit items to both subscribers


Autoconnect() connects when specified number of observers subscribe.
.publish() // Create a connectable observable
.autoConnect(2); // Start emitting when 2 subscribers are connected
Refcount() another option. 

## Replay
Stores and replays to new subscribers. 

replay() caches all items emitted by the source observable and emits them to new subscribers when they subscribe.
Subscribers receive all previously emitted items, regardless of when they subscribed.

Observable<Integer> source = Observable.just(1, 2, 3, 4, 5)
.subscribeOn(Schedulers.computation())
.replay(2) // Replay the last 2 items
.autoConnect();

// First subscriber
source.subscribe(item -> System.out.println("Subscriber 1: " + item));

// Delay for a moment
Thread.sleep(2000);

// Second subscriber
source.subscribe(item -> System.out.println("Subscriber 2: " + item));

// Output for both subscribers:
// Subscriber 1: 4
// Subscriber 1: 5
// Subscriber 2: 4
// Subscriber 2: 5

## Cache
Store and reuse emitted items from an observable sequence so that subsequent subscribers can receive the same 
items without recomputation. 

Observable<Integer> observable = Observable.range(1, 5)
.map(i -> {
System.out.println("Processing: " + i);
return i * 2;
})
.cache(); // Apply the cache operator

observable.subscribe(item -> System.out.println("Subscriber 1: " + item));

Thread.sleep(2000); // Delay for a moment

observable.subscribe(item -> System.out.println("Subscriber 2: " + item));

// Output:
// Processing: 1
// Processing: 2
// Processing: 3
// Processing: 4
// Processing: 5
// Subscriber 1: 2
// Subscriber 1: 4
// Subscriber 1: 6
// Subscriber 1: 8
// Subscriber 1: 10
// Subscriber 2: 2
// Subscriber 2: 4
// Subscriber 2: 6
// Subscriber 2: 8
// Subscriber 2: 10 

## Subjects
Both emit items and act as observers by subscribing to other observables. 
PublishSubject, BehaviorSubject, ReplaySubject, AsyncSubject.

## Concurrency & Parallelization
- Schedulers: RxJava's Schedulers provide a way to define on which thread or thread pool an observable should 
  operate. For example, you can specify that a certain operation should run on the IO thread to perform file 
  I/O operations or on the computation thread for CPU-bound tasks.
  Observable.just(1, 2, 3)
  .subscribeOn(Schedulers.io()) // Perform operation on the IO thread
  .observeOn(Schedulers.computation()) // Observe the result on the computation thread
  .subscribe(result -> System.out.println("Result: " + result));

- Parallelization: While RxJava doesn't provide native parallelism support, you can use Java's built-in parallel
  processing libraries or custom thread pools to parallelize tasks. For example, you can use Java's ForkJoinPool 
  to parallelize expensive computations within an observable sequence.
  Observable.range(1, 1000)
  .flatMap(i -> Observable.just(i).subscribeOn(Schedulers.io()))
  .observeOn(Schedulers.computation())
  .map(this::expensiveComputation)
  .subscribe(result -> System.out.println("Result: " + result));

- Concurrency Operators: RxJava offers operators like subscribeOn and observeOn that allow you to specify the 
  threading context for certain parts of your observable chain. These operators enable you to switch between 
  threads to achieve concurrency when needed.

## Buffering
Allows you to group emitted items into batches or chunks for more controlled and efficient processing. It's useful for 
rate limiting, batch processing, and managing noisy event streams.
- buffer(int count); collects item into specified size of lists
- buffer(long timespan, TimeUnit unit); collects item into time-span
- buffer(int count, int skip); collects item into size but also skips (allows for overlapping buffers)
- buffer(long timespan, TimeUnit unit, int count); combines all

Observable<Integer> source = Observable.range(1, 10);
// Buffer items into groups of 3 and emit those groups
source.buffer(3).subscribe(buffer -> {
    System.out.println("Buffered items: " + buffer);
});
Output;
Buffered items: [1, 2, 3]
Buffered items: [4, 5, 6]
Buffered items: [7, 8, 9]
Buffered items: [10]

## Windowing
Technique for grouping emitted items from an Observable into windows or sub-Observables. It's useful for performing 
operations on batches of data, processing data in fixed time intervals, and enabling parallel processing of data streams.
Similar to buffering, but instead of emitting lists or collections of items, it emits Observable windows.

- window(int count); emits an Observable for each window containing items from the source.
- window(long timespan, TimeUnit unit); It starts a new window after a specified time interval and emits items for that window.
- window(int count, int skip); divides the source Observable into windows of a fixed size (count) but with an overlap of 
  skip items between consecutive windows
- window(Observable<?> boundary) and window(Supplier<? extends Observable<B>> boundarySupplier);  create windows based 
  on a secondary Observable (boundary). When the boundary Observable emits an item, a new window is started.

Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);

// Create time-based windows of 5 seconds and emit items in those windows
source.window(5, TimeUnit.SECONDS)
.subscribe(windowedObservable -> {
    System.out.println("New Window Started");
    windowedObservable.subscribe(item -> {
    System.out.println("Item: " + item);
    });
});

// Sleep to observe the output
Thread.sleep(20000);

New Window Started
Item: 0
Item: 1
Item: 2
Item: 3
Item: 4

New Window Started
Item: 5
Item: 6
Item: 7
Item: 8
Item: 9

## Throttling
Technique used to control the rate at which items are emitted by an Observable, useful in scenarios where you want to 
prevent excessive emissions, such as dealing with user input events, network requests, or sensor data.

## Switching
switching between different inner observables or sources of data. 

These switch-related operators are powerful tools for managing complex scenarios where you need to switch between 
different data sources, handle empty observable cases, or work with observables of observables. They help you control 
which observable you want to listen to based on certain conditions or events.

- switchMap; is used when you have an observable that emits items, but each of these items maps to another observable. 
  It switches to the latest inner observable and emits its items while disposing of the previous inner observable.
  For each emitted item from the source observable, switchMap unsubscribes from the previous inner observable and 
  subscribes to the new one. It emits items from the latest inner observable.

  Observable<Integer> source = ...; // Source observable
  Observable<String> innerObservable = ...; // Inner observables
  source.switchMap(item -> innerObservable) // Switch to the latest inner observable
  .subscribe(result -> {
  // Handle items emitted by the latest inner observable
  });




## FlatMap
Use flatMap when you want to merge emissions from all inner observables, potentially processing them concurrently.

Behavior: For each item emitted by the source observable, flatMap applies a function that maps this item to another
observable. It then merges the emissions from all these inner observables into a single output observable, which emits
items in the order they are received.
Concurrency: By default, flatMap can run these inner observables concurrently, which means they can emit items in a
non-deterministic order. You can control concurrency using an optional parameter.
Use Cases: flatMap is suitable when you want to parallelize the processing of items from the source observable. It's
used when the order of items emitted from inner observables doesn't matter.

Observable<Integer> source = ...; // Source observable
Observable<String> innerObservable = ...; // Inner observables
source.flatMap(item -> innerObservable) // Merge emissions from inner observables
  .subscribe(result -> {
    // Handle items emitted from inner observables
  });


## SwitchMap
Use switchMap when you want to switch to the latest inner observable, ensuring that only one inner observable is active 
at a time, and you want deterministic order of emissions.

Behavior: Similar to flatMap, switchMap maps each item emitted by the source observable to another observable. However,
it only subscribes to the latest inner observable and emits items from that inner observable. When a new item is emitted
by the source observable, it unsubscribes from the previous inner observable and switches to the new one.
Concurrency: switchMap does not run inner observables concurrently. It ensures that only the latest inner observable is
active, so the order of emissions is deterministic.
Use Cases: switchMap is suitable when you want to ensure that only the latest inner observable is considered, and you
don't want concurrent emissions from multiple inner observables.

Observable<Integer> source = ...; // Source observable
Observable<String> innerObservable = ...; // Inner observables
source.switchMap(item -> innerObservable) // Switch to the latest inner observable
  .subscribe(result -> {
    // Handle items emitted from the latest inner observable
  });


## Flowable
designed to handle scenarios where you have a potentially large amount of data or events that need to be processed 
asynchronously, and you want to avoid overwhelming the system with too much data at once. 
- Backpressure - "slow down". 
- Buffer a certain amount of data before delivering to consumer
- Subscribe, defining request - how much data to receive. 
  flowable
  .subscribe(
  data -> process(data), // Consumer
  error -> handleError(error), // Error handler
  () -> onComplete() // Completion handler
  );

  // Request a specific number of items at a time
  subscription.request(10);
- Has different backpressure techniques; BUFFER, DROP, LATEST, ERROR
- Good for async and parallel processing. 
- Cold observable. 