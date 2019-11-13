# Hypergate integration SDK

This SDK allows you to enable any Android application to easily support Kerberos based SSO using any
Android Kerberos Authenticator such as the [Hypergate Authenticator](https://hypergate.com).

## Getting Started

Hypergate is an official jcenter Android dependency. You can include it in your project by simply
specifying com.hypergate:sdk:1.1.0 as a gradle dependency:

```gradle
....
dependencies {
   ....
   implementation "com.hypergate:sdk:1.1.0"
   ....
}
....
```

If your application does not have it's own app restrictions, just add the following line to your AndroidManifest.xml:

```xml
....
dependencies {
   <application></application>
   ....
   <meta-data
        android:name="android.content.APP_RESTRICTIONS"
        android:resource="@xml/hypergate_sdk_restrictions" />
   ....
   </application>
}
....
```
        
Otherwise copy the restrictions form the resource file (hypergate_sdk_restrictions.xml) into your own restrictions file.

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

After you included this plugin, your application will receive new managed configurations:

- Account type for HTTP Negotiate authentication: this controls which account type your webview will
 be looking for whenever there is an authentication challenge. This should be set to
 'ch.papers.hypergate'
- Authentication server whitelist: this controls which servers are allowed to request authentication
 tokens from hypergate. This can be either a wildcard (*) or the domains you want to enable
- Whether NTLMv2 authentication is enabled: the title is self explenatory and actually has nothing
 to do with Hypergate.

After you configured the options in your MDM/EMM/UEM (thing that pushes restrictions aka managed
 configurations) the webview will deal with all ajax and native requests on its own transparently
  from your app. This means all your requests will be authenticated automagically.

Note: If you decide not to use the standard ajax request and have a native plugin to perform
 requests, you will need to go with the "Native Token Request" below.

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
    new Function1<Exception, Unit>() {
            @Override
            public Unit invoke(Exception exception) {
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

### Cordova Applications

If you are using Cordova for your hybrid applications, you will can easily enable your project to
support SSO by simply installing a plugin. You can read more about it here:

- [Hypergate Cordova Plugin](https://github.com/hypergate-com/cordova-plugin-hypergate)
- [Hypergate Cordova Sample App](https://github.com/hypergate-com/hypergate-cordova-sample)

## Exceptions
All exception callbacks return a HypergateException object. This object has no logic but holds 2 properties:

- Code: This code is used by the app to switch-case and identify the error.
- Message: A verbose description (in English) of the actual error.

The following table includes a list of all errors you can encounter:

| Code | Message | Description |
|------|---------|-------------|
| 101 | no accounts found |  No accounts found, make sure you added your package name to the hypergate discoverability list |

## Troubleshooting

Since there are various components interoperating (MDM, Hypergate, Your App, Chrome WebView, this SDK)
wrong configuration can lead to issues. Here a list of issues and countermeasures how to solve then:

TODO

## Contributing

This library is licensed under MIT. Feel free to provide pull requests, we'll be happy to review and
include them. If you find any bugs, submit an [issue](../../issues) or open
 [pull-request](../../pulls), helping us catch and fix them.


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

## Support

In case you require support feel free to reach out to us (support@hypergate.com)