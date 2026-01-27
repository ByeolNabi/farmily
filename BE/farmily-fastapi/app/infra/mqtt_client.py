"""
MQTT 브로커 연결/구독/발행 래퍼 (gmqtt)
"""
import asyncio
from typing import Callable, Optional
from gmqtt import Client as MQTTClient
from gmqtt.mqtt.constants import MQTTv311

from app.core.config import settings
from app.core.logger import logger


class MQTTClientWrapper:
    """비동기 MQTT 클라이언트 래퍼"""
    
    def __init__(self):
        self._client: Optional[MQTTClient] = None
        self._connected = False
        self._message_handlers: dict[str, Callable] = {}
    
    async def connect(self) -> None:
        """MQTT 브로커 연결"""
        self._client = MQTTClient(client_id="farmily-backend")
        
        # 콜백 설정
        self._client.on_connect = self._on_connect
        self._client.on_disconnect = self._on_disconnect
        self._client.on_message = self._on_message
        
        # 인증 설정
        if settings.MQTT_USERNAME:
            self._client.set_auth_credentials(
                settings.MQTT_USERNAME,
                settings.MQTT_PASSWORD
            )
        
        try:
            await self._client.connect(
                settings.MQTT_HOST,
                settings.MQTT_PORT,
                version=MQTTv311
            )
            logger.info(f"📡 MQTT connected to {settings.MQTT_HOST}:{settings.MQTT_PORT}")
        except Exception as e:
            logger.error(f"❌ MQTT connection failed: {e}")
            raise
    
    async def disconnect(self) -> None:
        """MQTT 연결 해제"""
        if self._client and self._connected:
            await self._client.disconnect()
            logger.info("📡 MQTT disconnected")
    
    async def publish(self, topic: str, payload: str, qos: int = 1) -> None:
        """메시지 발행"""
        if not self._client or not self._connected:
            logger.warning("MQTT not connected, message not sent")
            return
        
        self._client.publish(topic, payload, qos=qos)
        logger.debug(f"📤 Published to {topic}: {payload[:100]}...")
    
    async def subscribe(self, topic: str, handler: Callable, qos: int = 1) -> None:
        """토픽 구독"""
        if not self._client or not self._connected:
            logger.warning("MQTT not connected, cannot subscribe")
            return
        
        self._message_handlers[topic] = handler
        self._client.subscribe(topic, qos=qos)
        logger.info(f"📥 Subscribed to {topic}")
    
    def _on_connect(self, client, flags, rc, properties):
        """연결 콜백"""
        self._connected = True
        logger.info("✅ MQTT connection established")
    
    def _on_disconnect(self, client, packet, exc=None):
        """연결 해제 콜백 - 자동 재접속 로직"""
        self._connected = False
        logger.warning("⚠️ MQTT disconnected, attempting reconnection...")
        asyncio.create_task(self._reconnect())
    
    async def _reconnect(self):
        """재접속 시도"""
        retry_count = 0
        while not self._connected and retry_count < 5:
            try:
                await asyncio.sleep(5)
                await self.connect()
                break
            except Exception as e:
                retry_count += 1
                logger.error(f"Reconnection attempt {retry_count} failed: {e}")
    
    def _on_message(self, client, topic, payload, qos, properties):
        """메시지 수신 콜백"""
        logger.debug(f"📨 Received on {topic}: {payload.decode()}")
        
        handler = self._message_handlers.get(topic)
        if handler:
            asyncio.create_task(handler(topic, payload.decode()))
    
    @property
    def is_connected(self) -> bool:
        return self._connected


# 싱글톤 인스턴스
mqtt_client = MQTTClientWrapper()
