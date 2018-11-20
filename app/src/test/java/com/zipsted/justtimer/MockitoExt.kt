package com.zipsted.justtimer

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

fun <T> any(): T = Mockito.any<T>()

fun <T> eq(obj: T): T = Mockito.eq<T>(obj)