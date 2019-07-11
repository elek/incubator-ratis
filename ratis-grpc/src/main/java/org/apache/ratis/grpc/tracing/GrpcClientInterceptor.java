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
package org.apache.ratis.grpc.tracing;

import io.opentracing.util.GlobalTracer;
import jdk.nashorn.internal.objects.Global;
import org.apache.ratis.thirdparty.io.grpc.CallOptions;
import org.apache.ratis.thirdparty.io.grpc.Channel;
import org.apache.ratis.thirdparty.io.grpc.ClientCall;
import org.apache.ratis.thirdparty.io.grpc.ClientInterceptor;
import org.apache.ratis.thirdparty.io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import org.apache.ratis.thirdparty.io.grpc.Metadata;
import org.apache.ratis.thirdparty.io.grpc.Metadata.Key;
import org.apache.ratis.thirdparty.io.grpc.MethodDescriptor;
import org.apache.ratis.tracing.TracingUtil;

/**
 * Interceptor to add the tracing id to the outgoing call header.
 */
public class GrpcClientInterceptor implements ClientInterceptor {

  public static final Key<String> TRACING_HEADER =
      Key.of("Tracing", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions,
      Channel next) {

    return new SimpleForwardingClientCall<ReqT, RespT>(
        next.newCall(method, callOptions)) {

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {

        Metadata tracingHeaders = new Metadata();
        tracingHeaders.put(TRACING_HEADER, TracingUtil.exportCurrentSpan());

        headers.merge(tracingHeaders);

        super.start(responseListener, headers);
      }

      @Override
      public void sendMessage(ReqT message) {
        super.sendMessage(message);
      }
    };
  }
}
