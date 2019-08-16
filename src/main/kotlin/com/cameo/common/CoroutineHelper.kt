package com.cameo.common

import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.experimental.CoroutineContext

private val scrapperContext: CoroutineContext = newFixedThreadPoolContext(2, "scrapper-pool")
private val requestContext: CoroutineContext = newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() - 2, "request-pool")

suspend fun <T> scrapperThread(block: () -> T): T = withContext(scrapperContext) { transaction { block() } }

suspend fun <T> requestThread(block: () -> T): T = withContext(requestContext) { transaction { block() } }