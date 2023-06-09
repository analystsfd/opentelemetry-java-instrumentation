/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jaxrsclient.v1_1;

import com.sun.jersey.api.client.ClientRequest;
import io.opentelemetry.context.propagation.TextMapSetter;

enum ClientRequestHeaderSetter implements TextMapSetter<ClientRequest> {
  INSTANCE;

  @Override
  public void set(ClientRequest carrier, String key, String value) {
    carrier.getHeaders().putSingle(key, value);
  }
}
