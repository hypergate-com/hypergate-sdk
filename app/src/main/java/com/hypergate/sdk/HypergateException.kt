package com.hypergate.sdk

import java.lang.Exception

class HypergateException(message: String, val code: Int) : Exception(message)