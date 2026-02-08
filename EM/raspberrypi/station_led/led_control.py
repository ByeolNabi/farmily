import board
import neopixel
import threading

LED_COUNT = 16
LED_PIN = board.D18

FIXED_BRIGHTNESS = 0.6   # 0.0 ~ 1.0

_pixels = neopixel.NeoPixel(
	LED_PIN,
	LED_COUNT,
	brightness=FIXED_BRIGHTNESS,
	auto_write=True,
	pixel_order=neopixel.GRB
)

_off_timer = None
_lock = threading.Lock()

def _cancel_timer():
	global _off_timer
	if _off_timer:
		_off_timer.cancel()
		_off_timer = None

def light_on():
    #(R, G, B)
	_pixels.fill((255, 0, 255))
	print("[LIGHT] ON (PURPLE)")


def light_off():
	_pixels.fill((0, 0, 0))
	print("[LIGHT] OFF")

def light_on_for(duration_sec):
	global _off_timer
	with _lock:
		_cancel_timer()
		light_on()

	d = max(0, int(duration_sec))
	if d > 0:
		_off_timer = threading.Timer(d, light_off)
		_off_timer.daemon = True
		_off_timer.start()
		print(f"[LIGHT] auto OFF after {d}s")




