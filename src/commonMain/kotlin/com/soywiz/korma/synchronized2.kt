package com.soywiz.korma

@PublishedApi
internal inline fun <T> synchronized2(obj: Any, callback: () -> T): T = callback()
