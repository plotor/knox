/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.knox.gateway.hadoopauth.filter;

import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.knox.gateway.security.PrimaryPrincipal;
import org.apache.knox.gateway.audit.api.AuditService;
import org.apache.knox.gateway.audit.api.AuditServiceFactory;
import org.apache.knox.gateway.audit.api.ResourceType;
import org.apache.knox.gateway.audit.log4j.audit.AuditConstants;
import org.apache.knox.gateway.filter.AbstractGatewayFilter;
import org.apache.knox.gateway.hadoopauth.HadoopAuthMessages;
import org.apache.knox.gateway.i18n.messages.MessagesFactory;
import org.apache.knox.gateway.audit.api.Action;
import org.apache.knox.gateway.audit.api.ActionOutcome;
import org.apache.knox.gateway.audit.api.Auditor;

public class HadoopAuthPostFilter implements Filter {

  private static HadoopAuthMessages log = MessagesFactory.get( HadoopAuthMessages.class );
  private static AuditService auditService = AuditServiceFactory.getAuditService();
  private static Auditor auditor = auditService.getAuditor(
      AuditConstants.DEFAULT_AUDITOR_NAME, AuditConstants.KNOX_SERVICE_NAME,
      AuditConstants.KNOX_COMPONENT_NAME );

  @Override
  public void init( FilterConfig filterConfig ) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest)request;
    String principal = httpRequest.getRemoteUser();
    if (principal != null) {
        Subject subject = new Subject();
        subject.getPrincipals().add(new PrimaryPrincipal(principal));
        log.hadoopAuthAssertedPrincipal(principal);
        auditService.getContext().setUsername( principal ); //KM: Audit Fix
        String sourceUri = (String)request.getAttribute( AbstractGatewayFilter.SOURCE_REQUEST_CONTEXT_URL_ATTRIBUTE_NAME );
        auditor.audit( Action.AUTHENTICATION , sourceUri, ResourceType.URI, ActionOutcome.SUCCESS );
        doAs(httpRequest, response, chain, subject);
    } else {
      ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN, "User not authenticated");
    }
  }

  private void doAs(final ServletRequest request, final ServletResponse response, final FilterChain chain, Subject subject)
      throws IOException, ServletException {
    try {
      Subject.doAs(
          subject,
          new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
              chain.doFilter(request, response);
              return null;
            }
          }
      );
    } catch (PrivilegedActionException e) {
      Throwable t = e.getCause();
      if (t instanceof IOException) {
        throw (IOException) t;
      } else if (t instanceof ServletException) {
        throw (ServletException) t;
      } else {
        throw new ServletException(t);
      }
    }
  }
}
