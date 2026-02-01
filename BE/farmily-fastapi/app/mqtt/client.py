"""
MQTT Client using paho-mqtt with WebSocket TLS support.
Provides async-compatible client for subscription and publishing.
Based on working implementation from sen-sub.py.
"""
import asyncio
import json
import ssl
import threading
from typing import Callable, Awaitable
from loguru import logger
import paho.mqtt.client as paho_mqtt

from app.core.config import settings


# Type alias for message handlers
MessageHandler = Callable[[str, dict], Awaitable[None]]


class MQTTClient:
    """Async-compatible MQTT client wrapper for paho-mqtt."""
    
    _instance: "MQTTClient | None" = None
    _client: paho_mqtt.Client | None = None
    _handlers: dict[str, list[MessageHandler]] = {}
    _connected: bool = False
    _loop: asyncio.AbstractEventLoop | None = None
    _mqtt_thread: threading.Thread | None = None
    
    def __new__(cls) -> "MQTTClient":
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._handlers = {}
        return cls._instance
    
    async def connect(self) -> None:
        """Connect to MQTT broker."""
        if self._connected:
            logger.warning("MQTT client already connected")
            return
        
        # Store event loop for async callback dispatch
        self._loop = asyncio.get_event_loop()
        
        # Create client with WebSocket transport if WSS enabled
        client_id = f"farmily-fastapi-{id(self)}"
        
        if settings.MQTT_USE_WSS:
            self._client = paho_mqtt.Client(
                client_id=client_id,
                transport="websockets",
                protocol=paho_mqtt.MQTTv311,
            )
            # Set WebSocket path (like sen-sub.py)
            self._client.ws_set_options(path=settings.MQTT_WSS_PATH)
            # Enable TLS for WSS
            self._client.tls_set()
            logger.info(f"MQTT using WebSocket TLS on path: {settings.MQTT_WSS_PATH}")
        else:
            self._client = paho_mqtt.Client(
                client_id=client_id,
                protocol=paho_mqtt.MQTTv311,
            )
        
        # Set callbacks
        self._client.on_connect = self._on_connect
        self._client.on_message = self._on_message
        self._client.on_disconnect = self._on_disconnect
        
        # Set credentials if provided
        if settings.MQTT_USERNAME and settings.MQTT_PASSWORD:
            self._client.username_pw_set(
                settings.MQTT_USERNAME,
                settings.MQTT_PASSWORD
            )
        
        try:
            # Connect to broker
            self._client.connect(
                host=settings.MQTT_HOST,
                port=settings.MQTT_PORT,
                keepalive=60
            )
            
            # Start background thread for MQTT loop
            self._client.loop_start()
            
            # Wait for connection to establish
            for _ in range(50):  # 5 second timeout
                if self._connected:
                    break
                await asyncio.sleep(0.1)
            
            if not self._connected:
                raise ConnectionError("MQTT connection timeout")
            
            logger.info(f"MQTT connected to {settings.MQTT_HOST}:{settings.MQTT_PORT}")
            
        except Exception as e:
            logger.error(f"MQTT connection failed: {e}")
            raise
    
    async def disconnect(self) -> None:
        """Disconnect from MQTT broker."""
        if self._client:
            self._client.loop_stop()
            self._client.disconnect()
            self._connected = False
            logger.info("MQTT disconnected")
    
    def subscribe(self, topic: str, handler: MessageHandler) -> None:
        """Register a handler for a topic.
        
        Args:
            topic: MQTT topic to subscribe to
            handler: Async function to handle messages
        """
        if topic not in self._handlers:
            self._handlers[topic] = []
            if self._client and self._connected:
                self._client.subscribe(topic, qos=1)
                logger.info(f"Subscribed to topic: {topic}")
        
        self._handlers[topic].append(handler)
        logger.debug(f"Handler registered for topic: {topic}")
    
    async def publish(self, topic: str, payload: dict, qos: int = 1) -> None:
        """Publish a message to a topic.
        
        Args:
            topic: MQTT topic to publish to
            payload: Dictionary to serialize as JSON
            qos: Quality of Service (0, 1, or 2)
        """
        if not self._client or not self._connected:
            logger.error("Cannot publish: MQTT client not connected")
            return
        
        message = json.dumps(payload, default=str)
        self._client.publish(topic, message, qos=qos)
        logger.debug(f"Published to {topic}: {message[:100]}...")
    
    def _on_connect(self, client, userdata, flags, rc, properties=None) -> None:
        """Callback when connected to broker."""
        if rc == 0:
            self._connected = True
            logger.info(f"MQTT connected successfully (code: {rc})")
            
            # Subscribe to all registered topics
            for topic in self._handlers.keys():
                client.subscribe(topic, qos=1)
                logger.info(f"Subscribed to topic: {topic}")
        else:
            logger.error(f"MQTT connection failed with code: {rc}")
    
    def _on_message(self, client, userdata, msg) -> None:
        """Callback when message received."""
        try:
            topic = msg.topic
            data = json.loads(msg.payload.decode())
            logger.debug(f"Received message on {topic}: {str(data)[:100]}...")
            
            # Dispatch to handlers using asyncio
            # Support wildcard matching (e.g. farmily/devices/+/event matches farmily/devices/1/event)
            handled = False
            for sub_topic, handlers in self._handlers.items():
                if paho_mqtt.topic_matches_sub(sub_topic, topic):
                    handled = True
                    for handler in handlers:
                        if self._loop and self._loop.is_running():
                            asyncio.run_coroutine_threadsafe(
                                handler(topic, data),
                                self._loop
                            )
                            
            if not handled:
                logger.warning(f"No handler for topic: {topic}")
                
        except json.JSONDecodeError as e:
            logger.error(f"Failed to parse message: {e}")
        except Exception as e:
            logger.error(f"Error handling message: {e}")
    
    def _on_disconnect(self, client, userdata, rc, properties=None) -> None:
        """Callback when disconnected from broker."""
        self._connected = False
        if rc != 0:
            logger.warning(f"MQTT disconnected unexpectedly (code: {rc})")
        else:
            logger.info("MQTT disconnected")
    
    @property
    def is_connected(self) -> bool:
        """Check if client is connected."""
        return self._connected


# Global client instance
mqtt_client = MQTTClient()
