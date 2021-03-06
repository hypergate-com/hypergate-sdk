package com.hypergate.sdk

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
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
        private const val KEY_INCOMING_AUTH_TOKEN = "incomingAuthToken"
        private const val MANAGED_CONFIG_ACCOUNT_TYPE_KEY =
            "com.android.browser:AuthAndroidNegotiateAccountType"
        private const val KEY_SPNEGO_CONTEXT = "spnegoContext"


        /**
         * Requests the token bundle silently (not showing any ui) which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method.
         *
         * @param context the context to use for token retrieval
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param spnegoContext optional context in case you need to retain the state for context establishment
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSilentlySync(
            context: Context,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray,
            spnegoContext: String
        ): Bundle {
            val accountManager = AccountManager.get(context)
            val accounts = this.getAccounts(context)
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

                if (spnegoContext.isNotEmpty()) {
                    optionsBundle.putString(
                        KEY_SPNEGO_CONTEXT,
                        spnegoContext
                    )
                }

                return accountManager.getAuthToken(
                    account,
                    authToken,
                    optionsBundle,
                    true,
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
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP@myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param spnegoContext optional context in case you need to retain the state for context establishment
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray,
            spnegoContext: String
        ): Bundle {
            val accountManager = AccountManager.get(activity)
            val accounts = this.getAccounts(activity)
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

                if (spnegoContext.isNotEmpty()) {
                    optionsBundle.putString(
                        KEY_SPNEGO_CONTEXT,
                        spnegoContext
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
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray
        ): Bundle {
            return requestTokenBundleSync(activity, servicePrincipalName, incommingAuthToken, "")
        }

        /**
         * Requests the token bundle silently (no ui) which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSilentlySync(
            context: Context,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray
        ): Bundle {
            return requestTokenBundleSilentlySync(
                context,
                servicePrincipalName,
                incommingAuthToken,
                ""
            )
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
         * Requests the token bundle silently (no ui) which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenBundleSilentlySync(
            context: Context,
            servicePrincipalName: String
        ): Bundle {
            return requestTokenBundleSilentlySync(context, servicePrincipalName, ByteArray(0))
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
         * Requests the negotiate token silently (no ui) which you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenSilentlySync(
            context: Context,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0)
        ): String {
            val bundle =
                requestTokenBundleSilentlySync(context, servicePrincipalName, incommingAuthToken)
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
         * Requests the negotiate token silently (no ui) which you have to put in the HTTP
         * headers of your request. This method is synchronous (blocking), you will receive the
         * result in the return of this method
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @throws HypergateException in case something went wrong
         */
        @Throws(HypergateException::class)
        fun requestTokenSilentlySync(
            context: Context,
            servicePrincipalName: String
        ): String {
            return requestTokenSilentlySync(context, servicePrincipalName, ByteArray(0))
        }

        /**
         * Requests the token bundle which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param activity the Activity context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param spnegoContext optional context in case you need to retain the state for context establishment
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenBundleAsync(
            activity: Activity,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0),
            spnegoContext: String,
            successCallback: (result: Bundle) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            Thread {
                try {
                    successCallback(
                        requestTokenBundleSync(
                            activity,
                            servicePrincipalName,
                            incommingAuthToken,
                            spnegoContext
                        )
                    )
                } catch (exception: Exception) {
                    errorCallback(exception)
                }
            }.start()
        }

        /**
         * Requests the token bundle silently (no ui) which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param spnegoContext optional context in case you need to retain the state for context establishment
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenBundleSilentlyAsync(
            context: Context,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0),
            spnegoContext: String,
            successCallback: (result: Bundle) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            Thread {
                try {
                    successCallback(
                        requestTokenBundleSilentlySync(
                            context,
                            servicePrincipalName,
                            incommingAuthToken,
                            spnegoContext
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
            requestTokenBundleAsync(
                activity, servicePrincipalName,
                incommingAuthToken, "", successCallback, errorCallback
            )
        }

        /**
         * Requests the token bundle silently (no ui) which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenBundleSilentlyAsync(
            context: Context,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0),
            successCallback: (result: Bundle) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenBundleSilentlyAsync(
                context, servicePrincipalName,
                incommingAuthToken, "", successCallback, errorCallback
            )
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
         * Requests the token bundle silently (no ui) which includes the negotiate token you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenBundleSilentlyAsync(
            context: Context,
            servicePrincipalName: String,
            successCallback: (result: Bundle) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenBundleSilentlyAsync(
                context, servicePrincipalName,
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
         * Requests the token silently (no ui) which you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param incommingAuthToken optional incomming auth token in case you want to establish a context
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenSilentlyAsync(
            context: Context,
            servicePrincipalName: String,
            incommingAuthToken: ByteArray = ByteArray(0),
            successCallback: (result: String) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenBundleSilentlyAsync(
                context,
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

        /**
         * Requests the token silently (no ui) which you have to put in the HTTP
         * headers of your request. This method is asynchronous (non-blocking), you will receive the
         * result in the success callback you provide
         *
         * @param context the context to use for launching
         * @param servicePrincipalName the service principal you are request a token for e.g. HTTP/myhost.com
         * @param successCallback callback used in case of success
         * @param errorCallback callback used in case of error
         */
        fun requestTokenSilentlyAsync(
            context: Context,
            servicePrincipalName: String,
            successCallback: (result: String) -> Unit,
            errorCallback: (error: Exception) -> Unit
        ) {
            requestTokenSilentlyAsync(
                context,
                servicePrincipalName,
                ByteArray(0),
                successCallback,
                errorCallback
            )
        }

        fun getAccounts(context: Context): Array<Account> {
            val accountType = ManagedConfig.readManagedConfig(context)
                .getString(MANAGED_CONFIG_ACCOUNT_TYPE_KEY, "ch.papers.hypergate")
            val accountManager = AccountManager.get(context)
            return accountManager.getAccountsByType(accountType)
        }

        fun hasAccount(context: Context): Boolean {
            return this.getAccounts(context).isNotEmpty()
        }

        fun getAccountChooserIntent(context: Context): Intent {
            val accountType = ManagedConfig.readManagedConfig(context)
                .getString(MANAGED_CONFIG_ACCOUNT_TYPE_KEY, "ch.papers.hypergate")

            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AccountManager.newChooseAccountIntent(
                    null, null,
                    listOf(accountType).toTypedArray(),
                    null, null, null,
                    null
                )
            } else {
                AccountManager.newChooseAccountIntent(
                    null, null, listOf(accountType).toTypedArray(), false, null,
                    null, null, null
                )
            }
            return intent
        }
    }
}