"""
공통 유틸리티 함수
"""
from datetime import datetime, timezone
from typing import Any
import json


def utc_now() -> datetime:
    """현재 UTC 시간"""
    return datetime.now(timezone.utc)


def kst_now() -> datetime:
    """현재 KST 시간"""
    from datetime import timedelta
    return datetime.now(timezone(timedelta(hours=9)))


def to_timestamp(dt: datetime) -> int:
    """datetime을 Unix timestamp로 변환"""
    return int(dt.timestamp())


def from_timestamp(ts: int) -> datetime:
    """Unix timestamp를 datetime으로 변환"""
    return datetime.fromtimestamp(ts, tz=timezone.utc)


def safe_json_loads(data: str, default: Any = None) -> Any:
    """안전한 JSON 파싱"""
    try:
        return json.loads(data)
    except (json.JSONDecodeError, TypeError):
        return default


def truncate_string(text: str, max_length: int = 100, suffix: str = "...") -> str:
    """문자열 자르기"""
    if len(text) <= max_length:
        return text
    return text[:max_length - len(suffix)] + suffix


def snake_to_camel(snake_str: str) -> str:
    """snake_case를 camelCase로 변환"""
    components = snake_str.split('_')
    return components[0] + ''.join(x.title() for x in components[1:])


def camel_to_snake(camel_str: str) -> str:
    """camelCase를 snake_case로 변환"""
    result = []
    for i, char in enumerate(camel_str):
        if char.isupper() and i > 0:
            result.append('_')
        result.append(char.lower())
    return ''.join(result)
