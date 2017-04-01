package com.soywiz.korma.numeric

val Double.niceStr: String get() = if (this.toLong().toDouble() == this) "${this.toLong()}" else "$this"
