/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.cassandra.v4_4;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.function.Function;

public class CompletionStageFunction implements Function<Object, Object> {

  @Override
  public Object apply(Object session) {
    if (session == null) {
      return null;
    }
    // This should cover ours and OT's TracingCqlSession
    if (session.getClass().getName().endsWith("cassandra4.TracingCqlSession")) {
      return session;
    }
    return CassandraSingletons.telemetry.wrap((CqlSession) session);
  }
}
