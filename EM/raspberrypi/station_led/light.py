import board
import neopixel

LED_COUNT = 16
LED_PIN = board.D18
AUTO_WRITE = True

_pixels = neopixel.NeoPixel(
	LED_PIN,
	LED_COUNT,
	brightness=1.0,
	auto_write=AUTO_WRITE,
	pixel_order=neopixel.GRB
)

def _clamp(v, lo, hi):
	return max(lo, min(hi, v))

def light_on(brightness=100):
	b = _clamp(int(brightness), 0, 100)

	scaled = int(b * 255 / 100)

	# (R,G,B)
	_pixels.fill((scaled, 0, scaled))
	print(f"[LIGHT] ON brightness={b}")


def light_off():
	_pixels.fill((0, 0, 0))
	print("[LIGHT] OFF")


