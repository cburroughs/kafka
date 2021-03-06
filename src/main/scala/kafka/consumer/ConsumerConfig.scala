/*
 * Copyright 2010 LinkedIn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.consumer

import java.util.Properties
import kafka.utils.{ZKConfig, Utils}
import kafka.api.OffsetRequest

object ConsumerConfig {
  val SOCKET_TIMEOUT = 30 * 1000
  val SOCKET_BUFFER_SIZE = 64*1024
  val FETCH_SIZE = 300 * 1024
  val MAX_FETCH_SIZE = 10*FETCH_SIZE
  val BACKOFF_INCREMENT_MS = 1000
  val AUTO_COMMIT = true
  val AUTO_COMMIT_INTERVAL = 10 * 1000
  val MAX_QUEUED_CHUNKS = 100
  val AUTO_OFFSET_RESET = OffsetRequest.SMALLEST_TIME_STRING
  val CONSUMER_TIMEOUT_MS = -1
  val EMBEDDED_CONSUMER_TOPICS = ""
}

class ConsumerConfig(props: Properties) extends ZKConfig(props) {
  import ConsumerConfig._

  /** a string that uniquely identifies a set of consumers within the same consumer group */
  val groupId = Utils.getString(props, "groupid")

  /** consumer id: generated automatically if not set.
   *  Set this explicitly for only testing purpose. */
  val consumerId: Option[String] = /** TODO: can be written better in scala 2.8 */
    if (Utils.getString(props, "consumerid", null) != null) Some(Utils.getString(props, "consumerid")) else None

  /** the socket timeout for network requests */
  val socketTimeoutMs = Utils.getInt(props, "socket.timeout.ms", SOCKET_TIMEOUT)
  
  /** the socket receive buffer for network requests */
  val socketBufferSize = Utils.getInt(props, "socket.buffersize", SOCKET_BUFFER_SIZE)
  
  /** the number of byes of messages to attempt to fetch */
  val fetchSize = Utils.getInt(props, "fetch.size", FETCH_SIZE)
  
  /** the maximum allowable fetch size for a very large message */
  val maxFetchSize: Int = fetchSize * 10
  
  /** to avoid repeatedly polling a broker node which has no new data
      we will backoff every time we get an empty set from the broker*/
  val backoffIncrementMs: Long = Utils.getInt(props, "backoff.increment.ms", BACKOFF_INCREMENT_MS)
  
  /** if true, periodically commit to zookeeper the offset of messages already fetched by the consumer */
  val autoCommit = Utils.getBoolean(props, "autocommit.enable", AUTO_COMMIT)
  
  /** the frequency in ms that the consumer offsets are committed to zookeeper */
  val autoCommitIntervalMs = Utils.getInt(props, "autocommit.interval.ms", AUTO_COMMIT_INTERVAL)

  /** max number of messages buffered for consumption */
  val maxQueuedChunks = Utils.getInt(props, "queuedchunks.max", MAX_QUEUED_CHUNKS)

  /* what to do if an offset is out of range.
     smallest : automatically reset the offset to the smallest offset
     largest : automatically reset the offset to the largest offset
     anything else: throw exception to the consumer */
  val autoOffsetReset = Utils.getString(props, "autooffset.reset", AUTO_OFFSET_RESET)

  /** throw a timeout exception to the consumer if no message is available for consumption after the specified interval */
  val consumerTimeoutMs = Utils.getInt(props, "consumer.timeout.ms", CONSUMER_TIMEOUT_MS)

  /* embed a consumer in the broker. e.g., topic1:1,topic2:1 */
  val embeddedConsumerTopicMap = Utils.getConsumerTopicMap(Utils.getString(props, "embeddedconsumer.topics",
    EMBEDDED_CONSUMER_TOPICS))
}
