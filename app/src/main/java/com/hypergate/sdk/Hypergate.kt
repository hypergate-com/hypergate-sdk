package com.hypergate.sdk

import android.accounts.AccountManager
import android.app.Activity
import android.os.Bundle
import android.util.Base64


/**
 * Created by Alessandro De Carli (@a_d_c_) on 05.09.19.
 * Papers.ch
 * a.decarli@papers.ch
 */

/**
 * Hypergate allows you to request Kerberos Token you can use in your native code. This static class
 * allows you to retrieve those tokens synchronously (blocking) or asynchronously depending on your
 * use case.
 *
 * Furthermore by importing this package your webviews will be enabled automatically and transparently
 * to request tokens from Hypergate if needed.
 */
class Hypergate {
    companion object {
        private val KEY_INCOMING_AUTH_TOKEN = "incomingAuthToken"
        private val MANAGED_CONFIG_ACCOUNT_TYPE_KEY =
            "com.android.browser:AuthAndroidNegotiateAccountType"

        /**
         * Requests the token bundle which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray
        ): Bundle {
            val accountType = ManagedConfig.readManagedConfig(activity)
                .getString(MANAGED_CONFIG_ACCOUNT_TYPE_KEY, "ch.papers.hypergate")
            val accountManager = AccountManager.get(activity)
            val accounts = accountManager.getAccountsByType(accountType)
            val authToken = "SPNEGO:HOSTBASED:${servicePrincipalName}"
            if (accounts.size != 0) {
                val account = accounts.first()
                accountManager.invalidateAuthToken(account.type, authToken)

                val optionsBundle = Bundle()
                if (incommingAuthToken.size > 0) {
                    optionsBundle.putString(
                        KEY_INCOMING_AUTH_TOKEN,
                        Base64.encodeToString(incommingAuthToken, Base64.NO_WRAP)
                    )
                }

                return accountManager.getAuthToken(
                    account,
                    authToken,
                    optionsBundle,
                    activity,
                    null,
                    null
                ).result
            } else {
                throw HypergateException("no accounts found", 101)
            }
        }

        /**
         * Requests the token bundle which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSync(
            activity: Activity,
            servicePrincipalName: String
        ): Bundle {
            return requestTokenBundleSync(activity, servicePrincipalName, ByteArray(0))
        }

        /**
         * Requests the negotiate token which you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenSync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0)
        ): String {
            val bundle = requestTokenBundleSync(activity, servicePrincipalName, incommingAuthToken)
            return bundle.getString(AccountManager.KEY_AUTHTOKEN, "")
        }

        /**
         * Requests the negotiate token which you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenSync(
            activity: Activity,
            servicePrincipalName: String
        ): String {
            return requestTokenSync(activity, servicePrincipalName, ByteArray(0))
        }

        /**
         * Requests the token bundle which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenBundleAsync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0),
            successCallback: (result: Bundle) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            Thread {
                try {
                    successCallback(
                        requestTokenBundleSync(
                            activity,
                            servicePrincipalName,
                            incommingAuthToken
                        )
                    )
                } catch (exception: Exception) {
                    errorCallback(exception)
                }
            }.start()
        }

        /**
         * Requests the token bundle which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenBundleAsync(
            activity: Activity,
            servicePrincipalName: String,
            successCallback: (result: Bundle) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenBundleAsync(
                activity, servicePrincipalName,
                ByteArray(0), successCallback, errorCallback
            )
        }

        /**
         * Requests the token which you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenAsync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0),
            successCallback: (result: String) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenBundleAsync(
                activity,
                servicePrincipalName,
                incommingAuthToken,
                { bundle -> successCallback(bundle.getString(AccountManager.KEY_AUTHTOKEN, "")) },
                errorCallback
            )
        }

        /**
         * Requests the token which you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenAsync(
            activity: Activity,
            servicePrincipalName: String,
            successCallback: (result: String) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenAsync(
                activity,
                servicePrincipalName,
                ByteArray(0),
                successCallback,
                errorCallback
            )
        }
    }
}