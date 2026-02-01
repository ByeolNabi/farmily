"""
MQTT message schemas using Pydantic.
Supports telemetry, command, and event message types.
"""
from datetime import datetime
from typing import Generic, TypeVar, Literal, Any
from pydantic import BaseModel, Field
import uuid


T = TypeVar("T")


class MQTTHeader(BaseModel):
    """Common header for all MQTT messages."""
    msg_id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    type: Literal["telemetry", "command", "event"]
    device_id: str
    timestamp: datetime = Field(default_factory=datetime.now)


class MQTTMessage(BaseModel, Generic[T]):
    """Generic MQTT message wrapper."""
    header: MQTTHeader
    payload: T


# ============================================================
# Telemetry Payloads
# ============================================================

class SensorPayload(BaseModel):
    """Sensor telemetry data from raspi_sensors."""
    temperature: float
    humidity: float
    illuminance: float
    soil_moisture: float


# ============================================================
# Command Payloads
# ============================================================

class CommandPayload(BaseModel):
    """Generic command payload."""
    cmd: str
    params: dict[str, Any] = Field(default_factory=dict)


class WeatherUpdateParams(BaseModel):
    """Parameters for UPDATE_WEATHER command."""
    weather: Literal["SUNNY", "RAINY", "CLOUDY"]


class ConditionUpdateParams(BaseModel):
    """Parameters for UPDATE_CONDITION command."""
    condition: Literal["HEALTHY", "SICK", "THIRSTY"]


# ============================================================
# Event Payloads
# ============================================================

class EventPayload(BaseModel):
    """Generic event payload."""
    event: str
    params: dict[str, Any] = Field(default_factory=dict)






# ============================================================
# Type Aliases for convenience
# ============================================================

SensorMessage = MQTTMessage[SensorPayload]
CommandMessage = MQTTMessage[CommandPayload]
EventMessage = MQTTMessage[EventPayload]
