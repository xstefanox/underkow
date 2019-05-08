package io.github.xstefanox.underkow.dsl

import io.github.xstefanox.underkow.SuspendingHttpHandler
import io.github.xstefanox.underkow.dispatcher.ExchangeDispatcher
import io.undertow.Undertow
import io.undertow.UndertowOptions
import org.xnio.Options

/**
 * The builder used to configure the server routing.
 *
 * @param filters the collection of filters that will be applied to every request received by the routes define by this
 *                builder; they will be applied in order, so be sure to use an ordered collection.
 */
@UndertowDsl
class ServerBuilder(
    private val filters: Collection<SuspendingHttpHandler> = emptyList()
) {

    /**
     * The TCP port used to listen for incoming connections; defaults to [DEFAULT_PORT].
     */
    var port = DEFAULT_PORT

    /**
     * The IP destination address used to listen for incoming requests; defaults to [DEFAULT_HOST].
     */
    var host: String = DEFAULT_HOST

    var dispatcher: ExchangeDispatcher = DEFAULT_DISPATCHER

    var workerIoThreads: Int? = null

    var connectionHighWater: Int? = null

    var connectionLowWater: Int? = null

    var workerTaskCoreThreads: Int? = null

    var workerTaskMaxThreads: Int? = null

    var tcpNoDelay: Boolean? = null

    var cork: Boolean? = null

    var reuseAddresses: Boolean? = null

    var balancingTokens: Int? = null

    var balancingConnections: Int? = null

    var backlog: Int? = null

    var noRequestTimeout: Int? = null

    var onException: SuspendingHttpHandler? = null

    private var routeInitializer: RouteInitializer? = null

    /**
     * Begin the definition of the server routing. Every call to this method overwrites a previously defined routing, if
     * present.
     *
     * @param prefix the path prefix that will be applied to each route defined by the nested builder.
     * @param filters the collection of filters that will be applied to every request received by the server.
     * @param init the lambda function used to configure the routing.
     */
    fun routing(prefix: String, vararg filters: SuspendingHttpHandler, init: RoutingBuilder.() -> Unit) {
        routeInitializer = RouteInitializer(RoutingBuilder(
            prefix,
            filters.toList(),
            dispatcher,
            onException
                ?: UNHANDLED_EXCEPTION_HANDLER
        ), init)
    }

    /**
     * Begin the definition of the server routing. Every call to this method overwrites a previously defined routing, if
     * present.
     *
     * @param filters the collection of filters that will be applied to every request received by the server.
     * @param init the lambda function used to configure the routing.
     */
    fun routing(vararg filters: SuspendingHttpHandler, init: RoutingBuilder.() -> Unit) {
        routeInitializer = RouteInitializer(RoutingBuilder(
            DEFAULT_PREFIX,
            filters.toList(),
            dispatcher,
            onException
                ?: UNHANDLED_EXCEPTION_HANDLER
        ), init)
    }

    /**
     * Build a new [Undertow] instance from this builder.
     *
     * @return an [Undertow] server configured with the DSL defined by this builder.
     */
    fun build(): Undertow {

        val builder = Undertow.builder()
            .setHandler(routeInitializer?.build())
            .addHttpListener(port, host)

        if (workerIoThreads != null) {
            builder.setWorkerOption(Options.WORKER_IO_THREADS, workerIoThreads)
        }

        if (connectionHighWater != null) {
            builder.setWorkerOption(Options.CONNECTION_HIGH_WATER, connectionHighWater)
        }

        if (connectionLowWater != null) {
            builder.setWorkerOption(Options.CONNECTION_LOW_WATER, connectionLowWater)
        }

        if (workerTaskCoreThreads != null) {
            builder.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, workerTaskCoreThreads)
        }

        if (workerTaskMaxThreads != null) {
            builder.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, workerTaskMaxThreads)
        }

        if (tcpNoDelay != null) {
            builder.setWorkerOption(Options.TCP_NODELAY, tcpNoDelay)
        }

        if (cork != null) {
            builder.setWorkerOption(Options.CORK, cork)
        }

        if (reuseAddresses != null) {
            builder.setSocketOption(Options.REUSE_ADDRESSES, reuseAddresses)
        }

        if (balancingTokens != null) {
            builder.setSocketOption(Options.BALANCING_TOKENS, balancingTokens)
        }

        if (balancingConnections != null) {
            builder.setSocketOption(Options.BALANCING_CONNECTIONS, balancingConnections)
        }

        if (backlog != null) {
            builder.setSocketOption(Options.BACKLOG, backlog)
        }

        if (noRequestTimeout != null) {
            builder.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, noRequestTimeout)
        }

        return builder.build()
    }
}
