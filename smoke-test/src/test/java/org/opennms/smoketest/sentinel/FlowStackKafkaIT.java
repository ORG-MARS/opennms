/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018-2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.smoketest.sentinel;

import org.opennms.test.system.api.TestEnvironmentBuilder;

// Verifies that flows can be processed by a sentinel and are persisted to Elastic communicating via kafka
public class FlowStackKafkaIT extends AbstractFlowIT {

    @Override
    protected void customizeTestEnvironment(TestEnvironmentBuilder builder) {
            builder
                .minion()
                .opennms()
                .kafka()
                .es6()
                .sentinel();

            // Enable Netflow 5 Adapter
            builder.withSentinelEnvironment()
                    .addFile(getClass().getResource("/sentinel/features-kafka.xml"), "deploy/features.xml");

            // Enable Netflow 5 Listener
            builder.withMinionEnvironment()
                    .addFile(getClass().getResource("/featuresBoot.d/kafka.boot"), "etc/featuresBoot.d/kafka.boot");
    }

    @Override
    protected String getSentinelReadyString() {
        return "OpenNMS.Sink.Telemetry-Netflow-5";
    }
}