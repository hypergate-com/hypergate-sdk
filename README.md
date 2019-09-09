# Hypergate integration SDK

## Getting Started

Hypergate is an official jcenter Android dependency. You can include it in your project by simply
specifying com.hypergate:sdk:1.0.14 as a gradle dependency:

```gradle
...
dependencies {
   ...
   implementation "com.hypergate:sdk:1.0.14"
   ....
}
...
```

## Usage

The entire project is documented with dokka. The javadoc/dokka can be found here: TODO

There are two ways to use this sdk, which depends on how you plan to consume your kerberized service:

1. You have a hybrid mobile app (i.e. Cordova, Capacitor, etc) or simply use a WebView to consume
you service. See WebView Request below
2. You want to perform the native service requests yourself. See Native Token Request below.

### WebView Request

This is very easy because you don't have to do anything other than include the library as shown in
the Getting Started section. The magic that happens in the background is that your AndroidManifest.xml
will receive the needed Restrictions (hypergate_restrictions.xml) to transparently enable every
WebView in your project.

Make sure to configure your app in you MDM.

### Native Token Request

There are two ways to request tokens either synchronously, which will block until it managed to get
the token or asynchronously, which will return you the token in the success callback you specified.

#### Synchronous Token Request

#### Kotlin

```kotlin
val token = Hypergate.requestTokenSync(appContext,"HTTP/myhost.com")
```

#### Java

```java
final String token = Hypergate.Companion.requestTokenSync(appContext, "HTTP/myhost.com");
```

#### Asynchronous Token Request

#### Kotlin

```kotlin
Hypergate.requestTokenAsync(appContext, "HTTP/myhost.com",
        { negotiateToken -> Log.d("TOKEN", negotiateToken) },
        { exception -> Log.d("ERROR", exception.message) })
```

#### Java

```java
Hypergate.Companion.requestTokenAsync(appContext, "HTTP/myhost.com",
    new Function1<String, Unit>() {
        @Override
        public Unit invoke(String negotiateToken) {
            Log.d("TOKEN", negotiateToken)
            return Unit.INSTANCE;
        }
    },
    new Function1<HypergateException, Unit>() {
            @Override
            public Unit invoke(HypergateException exception) {
                Log.d("ERROR", exception.getMessage);
                return Unit.INSTANCE;
            }
        }
);
```

## Exceptions
All exception callbacks return a HypergateException object. This object has no logic but holds 2 properties:

- Code: This code is used by the app to switch-case and identify the error.
- Message: A verbose description (in English) of the actual error.

The following table includes a list of all errors you can encounter:

| Code | Message | Description |
|------|---------|-------------|
| 101 | key is not inside secure hardware |  No accounts found, make sure you added your package name to the hypergate discoverability list |

## Troubleshooting

Since there are various components interoperating (MDM, Hypergate, Your App, Chrome WebView, this SDK)
wrong configuration can lead to issues. Here a list of issues and countermeasures how to solve then:

TODO

## Contributing

This library is licensed under MIT. Feel free to provide pull requests, we'll be happy to review and
include them.

### Build

```bash
$ git clone https://github.com/hypergate-com/hypergate-sdk
$ cd hypergate-sdk
$ ./gradlew clean build
```

### Test

Since the Hypergate uses various android platform features, all the provided tests are
Android instrumentation tests (androidTest). Other projects would ideally test the non-Android
components with plain JUnit tests (test).

```bash
$ ./gradlew connectedAndroidTest # to run the Android instrumentation tests
```

## JavaDoc

The full java doc documentation can be generated with:

```bash
$ ./gradlew clean dokka
$ open app/build/javadoc/app/index.html
```