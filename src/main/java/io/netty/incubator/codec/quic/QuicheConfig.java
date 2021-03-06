/*
 * Copyright 2020 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.incubator.codec.quic;

final class QuicheConfig {
    private final String certPath;
    private final String keyPath;
    private final Boolean verifyPeer;
    private final Boolean grease;
    private final  boolean earlyData;
    private final byte[] protos;
    private final Long maxIdleTimeout;
    private final Long maxUdpPayloadSize;
    private final Long initialMaxData;
    private final Long initialMaxStreamDataBidiLocal;
    private final Long initialMaxStreamDataBidiRemote;
    private final Long initialMaxStreamDataUni;
    private final Long initialMaxStreamsBidi;
    private final Long initialMaxStreamsUni;
    private final Long ackDelayExponent;
    private final Long maxAckDelay;
    private final Boolean disableActiveMigration;
    private final Boolean enableHystart;
    private final QuicCongestionControlAlgorithm congestionControlAlgorithm;
    private final Integer recvQueueLen;
    private final Integer sendQueueLen;

    QuicheConfig(String certPath, String keyPath, Boolean verifyPeer, Boolean grease, boolean earlyData,
                        byte[] protos, Long maxIdleTimeout, Long maxUdpPayloadSize, Long initialMaxData,
                        Long initialMaxStreamDataBidiLocal, Long initialMaxStreamDataBidiRemote,
                        Long initialMaxStreamDataUni, Long initialMaxStreamsBidi, Long initialMaxStreamsUni,
                        Long ackDelayExponent, Long maxAckDelay, Boolean disableActiveMigration, Boolean enableHystart,
                        QuicCongestionControlAlgorithm congestionControlAlgorithm,
                 Integer recvQueueLen, Integer sendQueueLen) {
        this.certPath = certPath;
        this.keyPath = keyPath;
        this.verifyPeer = verifyPeer;
        this.grease = grease;
        this.earlyData = earlyData;
        this.protos = protos;
        this.maxIdleTimeout = maxIdleTimeout;
        this.maxUdpPayloadSize = maxUdpPayloadSize;
        this.initialMaxData = initialMaxData;
        this.initialMaxStreamDataBidiLocal = initialMaxStreamDataBidiLocal;
        this.initialMaxStreamDataBidiRemote = initialMaxStreamDataBidiRemote;
        this.initialMaxStreamDataUni = initialMaxStreamDataUni;
        this.initialMaxStreamsBidi = initialMaxStreamsBidi;
        this.initialMaxStreamsUni = initialMaxStreamsUni;
        this.ackDelayExponent = ackDelayExponent;
        this.maxAckDelay = maxAckDelay;
        this.disableActiveMigration = disableActiveMigration;
        this.enableHystart = enableHystart;
        this.congestionControlAlgorithm = congestionControlAlgorithm;
        this.recvQueueLen = recvQueueLen;
        this.sendQueueLen = sendQueueLen;
    }

    boolean isDatagramSupported() {
        return recvQueueLen != null && sendQueueLen != null;
    }

    /**
     * Creates the native config object and return it.
     */
    long createNativeConfig() {
        long config = Quiche.quiche_config_new(Quiche.QUICHE_PROTOCOL_VERSION);
        try {
            if (certPath != null && Quiche.quiche_config_load_cert_chain_from_pem_file(config, certPath) != 0) {
                throw new IllegalArgumentException("Unable to load certificate chain");
            }
            if (keyPath != null && Quiche.quiche_config_load_priv_key_from_pem_file(config, keyPath) != 0) {
                throw new IllegalArgumentException("Unable to load private key");
            }
            if (verifyPeer != null) {
                Quiche.quiche_config_verify_peer(config, verifyPeer);
            }
            if (grease != null) {
                Quiche.quiche_config_grease(config, grease);
            }
            if (earlyData) {
                Quiche.quiche_config_enable_early_data(config);
            }
            if (protos != null) {
                Quiche.quiche_config_set_application_protos(config, protos);
            }
            if (maxIdleTimeout != null) {
                Quiche.quiche_config_set_max_idle_timeout(config, maxIdleTimeout);
            }
            if (maxUdpPayloadSize != null) {
                Quiche.quiche_config_set_max_udp_payload_size(config, maxUdpPayloadSize);
            }
            if (initialMaxData != null) {
                Quiche.quiche_config_set_initial_max_data(config, initialMaxData);
            }
            if (initialMaxStreamDataBidiLocal != null) {
                Quiche.quiche_config_set_initial_max_stream_data_bidi_local(config, initialMaxStreamDataBidiLocal);
            }
            if (initialMaxStreamDataBidiRemote != null) {
                Quiche.quiche_config_set_initial_max_stream_data_bidi_remote(config, initialMaxStreamDataBidiRemote);
            }
            if (initialMaxStreamDataUni != null) {
                Quiche.quiche_config_set_initial_max_stream_data_uni(config, initialMaxStreamDataUni);
            }
            if (initialMaxStreamsBidi != null) {
                Quiche.quiche_config_set_initial_max_streams_bidi(config, initialMaxStreamsBidi);
            }
            if (initialMaxStreamsUni != null) {
                Quiche.quiche_config_set_initial_max_streams_uni(config, initialMaxStreamsUni);
            }
            if (ackDelayExponent != null) {
                Quiche.quiche_config_set_ack_delay_exponent(config, ackDelayExponent);
            }
            if (maxAckDelay != null) {
                Quiche.quiche_config_set_max_ack_delay(config, maxAckDelay);
            }
            if (disableActiveMigration != null) {
                Quiche.quiche_config_set_disable_active_migration(config, disableActiveMigration);
            }
            if (enableHystart != null) {
                Quiche.quiche_config_enable_hystart(config, enableHystart);
            }
            if (congestionControlAlgorithm != null) {
                switch (congestionControlAlgorithm) {
                    case RENO:
                        Quiche.quiche_config_set_cc_algorithm(config, Quiche.QUICHE_CC_RENO);
                        break;
                    case CUBIC:
                        Quiche.quiche_config_set_cc_algorithm(config, Quiche.QUICHE_CC_CUBIC);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unknown congestionControlAlgorithm: " + congestionControlAlgorithm);
                }
            }
            if (isDatagramSupported()) {
                Quiche.quiche_config_enable_dgram(config, true, recvQueueLen, sendQueueLen);
            }
            return config;
        } catch (Throwable cause) {
            Quiche.quiche_config_free(config);
            throw cause;
        }
    }
}
