/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ratis.tracing;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import brave.ScopedSpan;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;
import brave.sampler.Sampler;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

/**
 * Utility class to collect all the tracing helper methods.
 */
public final class TracingUtil {

  private static final Extractor<StringBuilder> extractor =
      Propagation.B3_SINGLE_STRING
          .extractor((carrier, key) -> carrier.toString());

  private static final Injector<StringBuilder> injector =
      Propagation.B3_SINGLE_STRING.injector(
          (carrier, key, value) -> carrier.append(value));

  private static final String NULL_SPAN_AS_STRING = "";

  private TracingUtil() {
  }

  public static <T> CompletableFuture<T> traceFuture(
      CompletableFuture<T> future, String name) {
    return future;
    //    Tracer tracer = Tracing.currentTracer();
    //    ScopedSpan scopedSpan = tracer.startScopedSpan(name);
    //    return future.whenComplete((value, throwable) -> scopedSpan.finish());
  }

  /**
   * Initialize the tracing with the given service name.
   *
   * @param serviceName
   */
  public static void initTracing(String serviceName) {
    URLConnectionSender sender =
        URLConnectionSender.create("http://localhost:9411/api/v2/spans");
    Reporter<Span> spanReporter = AsyncReporter.create(sender);

    // Create a tracing component with the service name you want to see in
    // Zipkin.
    Tracing tracing = Tracing.newBuilder()
        .localServiceName(serviceName)
        .spanReporter(spanReporter)
        .sampler(Sampler.ALWAYS_SAMPLE)
        .build();

    final Tracer tracer = tracing.tracer();
    tracer.startScopedSpan("server-test").finish();

  }

  /**
   * Export the active tracing span as a string.
   *
   * @return encoded tracing context.
   */

  public static ByteString exportCurrentSpan() {
    brave.Span span = Tracing.currentTracer().currentSpan();
    if (span != null) {
      return exportSpan(span.context());
    } else {
      return ByteString.EMPTY;
    }
  }

  /**
   * Export the specific span as a string.
   *
   * @return encoded tracing context.
   */
  public static ByteString exportSpan(TraceContext context) {
    CurrentTraceContext currentTraceContext =
        Tracing.current().currentTraceContext();
    if (context != null) {
      final StringBuilder builder = new StringBuilder();
      injector.inject(context, builder);
      return ByteString
          .copyFromUtf8(
              builder.toString());
    }
    return ByteString.EMPTY;
  }

  /**
   * Creates a proxy of the implementation and trace all the method calls.
   *
   * @param delegate the original class instance
   * @param interfce the interface which should be implemented by the proxy
   * @param <T>      the type of the interface
   * @return A new interface which implements interfce but delegate all the
   * calls to the delegate and also enables tracing.
   */
  public static <T> T createProxy(T delegate, Class<T> interfce) {
    Class<?> aClass = delegate.getClass();
    return (T) Proxy.newProxyInstance(aClass.getClassLoader(),
        new Class<?>[] {interfce},
        new TraceAllMethod<T>(delegate, interfce.getSimpleName()));
  }

  public static brave.Span importSpan(ByteString trace) {
    String encodedParent =
        new String(trace.toByteArray(), StandardCharsets.UTF_8);
    StringBuilder builder = new StringBuilder();
    builder.append(encodedParent);

    if (encodedParent.length() > 0) {
      TraceContextOrSamplingFlags extract = extractor.extract(builder);
      return Tracing.currentTracer().joinSpan(extract.context());
    }
    return null;
  }

  public static void finishCurrentSpan() {
    if (Tracing.currentTracer().currentSpan() != null) {
      Tracing.currentTracer().currentSpan().finish();
    }
  }

  public static void executeWithTrace(String name,
      ExecutionWithIOException task) {
    Tracer tracer = Tracing.currentTracer();
    ScopedSpan scopedSpan = tracer.startScopedSpan(name);
    try {
      task.run();
    } catch (Exception ex) {
      scopedSpan.error(ex);
    } finally {
      scopedSpan.finish();
    }
  }

}
