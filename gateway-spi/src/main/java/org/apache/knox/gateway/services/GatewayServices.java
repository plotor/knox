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
package org.apache.knox.gateway.services;

import java.util.Collection;

import org.apache.knox.gateway.deploy.ProviderDeploymentContributor;

public interface GatewayServices extends Service,
    ProviderDeploymentContributor {

  String GATEWAY_CLUSTER_ATTRIBUTE = "org.apache.knox.gateway.gateway.cluster";
  String GATEWAY_SERVICES_ATTRIBUTE = "org.apache.knox.gateway.gateway.services";

  String SSL_SERVICE = "SSLService";
  String CRYPTO_SERVICE = "CryptoService";
  String ALIAS_SERVICE = "AliasService";
  String KEYSTORE_SERVICE = "KeystoreService";
  String TOKEN_SERVICE = "TokenService";
  String SERVICE_REGISTRY_SERVICE = "ServiceRegistryService";
  String HOST_MAPPING_SERVICE = "HostMappingService";
  String SERVER_INFO_SERVICE = "ServerInfoService";
  String TOPOLOGY_SERVICE = "TopologyService";
  String SERVICE_DEFINITION_REGISTRY = "ServiceDefinitionRegistry";
  String METRICS_SERVICE = "MetricsService";

  String REMOTE_REGISTRY_CLIENT_SERVICE = "RemoteConfigRegistryClientService";

  String CLUSTER_CONFIGURATION_MONITOR_SERVICE = "ClusterConfigurationMonitorService";

  Collection<String> getServiceNames();

  <T> T getService( String serviceName );
}
