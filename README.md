# Hypergate integration SDK

## Getting Started

Hypergate is an official jcenter Android dependency. You can include it in your project by simply
specifying com.hypergate:sdk:1.0.15 as a gradle dependency:

```gradle
...
dependencies {
   ...
   implementation "com.hypergate:sdk:1.0.15"
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
val token = Hypergate.requestTokenSync(activity,"HTTP/myhost.com")
```

#### Java

```java
final String token = Hypergate.Companion.requestTokenSync(activity, "HTTP/myhost.com");
```

#### Asynchronous Token Request

#### Kotlin

```kotlin
Hypergate.requestTokenAsync(activity, "HTTP/myhost.com",
        { negotiateToken -> Log.d("TOKEN", negotiateToken) },
        { exception -> Log.d("ERROR", exception.message) })
```

#### Java

```java
Hypergate.Companion.requestTokenAsync(activity, "HTTP/myhost.com",
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


## Samples

The following examples showcase how to authenticate HTTP requests using Hypergate for the most common
HTTP Request libraries used on the Android platform. In a nutshell the only thing we do is request
a token and put it into the the request headers. As of now we are cover the requests using Volley
and OkHttp, if there is another HTTP Library you would like us to showcase feel free to reach out.

#### Using Volley

```kotlin
val queue = Volley.newRequestQueue(activityRule.activity)
val url = "https://securedendpoint.com"
val stringRequest = object : StringRequest(
    Method.GET, url,
    Response.Listener<String> { response ->
    },
    Response.ErrorListener { Log.d("ERROR", "That didn't work!") }) {

    override fun getHeaders(): MutableMap<String, String> {
        val headers = super.getHeaders()
        val token = Hypergate.requestTokenSync(
            activityRule.activity,
            "HTTP/${Uri.parse(this.url).host}"
        )
        headers.put("Authorization", "Negotiate ${token}")
        return super.getHeaders()
    }
}
queue.add(stringRequest)
```

#### Using OkHttp

Simply create a interceptor:

```kotlin
internal class HypergateOkHttpInterceptor(private val activity: Activity) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val token = Hypergate.requestTokenSync(
            activity,
            "HTTP/${request.url.host}"
        )
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Negotiate ${token}")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
```

And then use it in your code:

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(HypergateOkHttpInterceptor(activityRule.activity))
    .build()

val request = Request.Builder()
    .url("https://securedendpoint.com")
    .build()

val response = client.newCall(request).execute()
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