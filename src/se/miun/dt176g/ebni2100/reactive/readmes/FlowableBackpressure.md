## Flowable
Extension of observable. Backpressure occurs when an Observable or Flowable produces items at a faster rate 
than the downstream can consume. 

Operators to combat backpressure;
- Buffering: items buffered, allowed to consume them at its own pace.
- Missing: Caches and replays emissions for late subscribers in a Flowable chain, ensuring they receive all emitted items.
- Dropping: new items are dropped if the downstream is not ready to consume them.
- Latest: only the latest item is kept, previous are dropped.
- Error: exception is thrown, indicating backpressure issue. 
- Must use BackpressureStrategy.*operator* as second argument upon creation. 

Example: Flowable produces integers from 0 to 999, and backpressure strategy is set to 'BUFFER', meaning it
will buffer items if the downstream can't keep up. 

Flowable<Integer> flowable = Flowable.create(emitter -> {
    for (int i = 0; i < 1000; i++) {
      emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.BUFFER);

flowable
  .observeOn(Schedulers.io())  // This is just an example, you can replace it with your desired scheduler
  .subscribe(item -> {
    // Simulate slow consumption
    Thread.sleep(10);
    System.out.println(item);
  });

- Good for async and parallel processing.
- Cold observable. 

## When to use Flowable vs Observable
Flowable avoids backpressure, but may operate slower than an observable. 

### Use an Observable if...

- You expect few emissions over the life of the Observable subscription (fewer than 1,000), or emissions are 
  intermittent and far apart.
- Your data processing is strictly synchronous and has limited usage of concurrency, such as simple usage of 
  `subscribeOn()` at the start of an Observable chain.
- You want to emit user interface events like button clicks, ListView selections, or other user inputs. For 
  rapid user inputs, operators like Switching, Throttling, Windowing, and Buffering are often more suitable.

### Use a Flowable if...

- You are dealing with over 10,000 elements, and there is an opportunity for the source to generate emissions 
  in a regulated manner, especially with asynchronous sources pushing large amounts of data.
- You want to emit from IO operations that support blocking while returning results, such as iterating records 
  from files or a `ResultSet` in JDBC.




