### ConnectableObservable

**Multicasting:**
ConnectableObservable emits items after it's created and acts as a hot observable. All subscribers share the same sequence.

Observable<Integer> source = Observable.range(1, 5);

ConnectableObservable<Integer> connectable = source.publish();

connectable.connect();

connectable.subscribe(i -> System.out.println("Subscriber 1: " + i));
connectable.subscribe(i -> System.out.println("Subscriber 2: " + i));

**AutoConnect:**
`autoConnect()` connects when a specified number of observers subscribe.

ConnectableObservable<Integer> connectable = source.publish().autoConnect(2);

**RefCount:**
Another option to automatically connect when the first observer subscribes.

Observable<Integer> source = Observable.range(1, 5);
ConnectableObservable<Integer> connectable = source.publish();
Observable<Integer> refCounted = connectable.refCount();

### Replay

**Stores and replays to new subscribers:**
Replay caches all items emitted by the source observable and emits them to new subscribers when they subscribe.

Observable<Integer> source = Observable.just(1, 2, 3, 4, 5)
    .subscribeOn(Schedulers.computation())
    .replay(2) // Replay the last 2 items
    .autoConnect();

source.subscribe(item -> System.out.println("Subscriber 1: " + item));

// Delay for a moment
Thread.sleep(2000);

source.subscribe(item -> System.out.println("Subscriber 2: " + item));

### Cache

**Store and reuse emitted items:**
Cache operator stores and reuses emitted items from an observable sequence.

Observable<Integer> observable = Observable.range(1, 5)
    .map(i -> {
        System.out.println("Processing: " + i);
        return i * 2;
    })
    .cache();

observable.subscribe(item -> System.out.println("Subscriber 1: " + item));

// Delay for a moment
Thread.sleep(2000);

observable.subscribe(item -> System.out.println("Subscriber 2: " + item));

### Subjects

**Act as both observers and emitters:**
Subjects, like PublishSubject, BehaviorSubject, ReplaySubject, and AsyncSubject, emit items and act as observers by subscribing to other observables.

// Example using PublishSubject
PublishSubject<String> subject = PublishSubject.create();

subject.subscribe(s -> System.out.println("Observer 1: " + s));
subject.onNext("One");

subject.subscribe(s -> System.out.println("Observer 2: " + s));
subject.onNext("Two");
subject.onComplete();
